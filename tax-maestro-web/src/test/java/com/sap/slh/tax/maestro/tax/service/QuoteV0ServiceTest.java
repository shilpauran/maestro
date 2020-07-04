package com.sap.slh.tax.maestro.tax.service;

import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponse;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.attributes.determination.model.response.TaxLine;
import com.sap.slh.tax.calculation.model.common.ItemResult;
import com.sap.slh.tax.calculation.model.common.TaxCalculationResponse;
import com.sap.slh.tax.calculation.model.common.TaxCalculationResponseLine;
import com.sap.slh.tax.calculation.model.common.TaxResult;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationResponse;
import com.sap.slh.tax.maestro.api.v0.schema.Item;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse;
import com.sap.slh.tax.maestro.tax.service.integration.CalculateQueueIntegration;
import com.sap.slh.tax.maestro.tax.service.integration.DestinationQueueIntegration;
import com.sap.slh.tax.maestro.tax.service.integration.DetermineQueueIntegration;
import com.sap.slh.tax.maestro.tax.service.integration.QuoteV0PartnerHttpIntegration;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class QuoteV0ServiceTest {
    private final DetermineQueueIntegration determineIntegration = Mockito.mock(DetermineQueueIntegration.class);
    private final CalculateQueueIntegration calculateIntegration = Mockito.mock(CalculateQueueIntegration.class);
    private final QuoteV0PartnerHttpIntegration quoteV0PartnerHttpIntegration = Mockito
            .mock(QuoteV0PartnerHttpIntegration.class);
    private final DestinationQueueIntegration destinationIntegration = Mockito.mock(DestinationQueueIntegration.class);

    private QuoteV0Service orchestration;

    @Before
    public void setup() {
        orchestration = new QuoteV0Service(determineIntegration, calculateIntegration, quoteV0PartnerHttpIntegration,
                destinationIntegration);
    }

    @Test
    public void testAMQPCall() {
        TaxRequest taxRequest = getDefaultRequest();
        this.mockDestinationIntegration(getDestinationResponse("tax-service"));
        this.mockDetermineIntegration(getDeterminationResponse());
        this.mockCalculateIntegration(getCalculationResponse());

        StepVerifier.create(orchestration.call(Mono.just(taxRequest))).consumeNextWith(taxResponse -> {
            Assert.assertNotNull(taxResponse);
            this.verifyDestinationIntegrationCall(1);
            this.verifyDetermineIntegrationCall(1);
            this.verifyCalculateIntegrationCall(1);
        }).verifyComplete();
    }

    @Test
    public void testHTTPCall() {
        TaxRequest taxRequest = getDefaultRequest();
        this.mockDestinationIntegration(getDestinationResponse("TAXWEB-BR"));
        this.mockHttpIntegration(getTaxResponse("TAXWEB-BR"));

        StepVerifier.create(orchestration.call(Mono.just(taxRequest))).consumeNextWith(taxResponse -> {
            Assert.assertNotNull(taxResponse);
            Assert.assertTrue(taxResponse instanceof TaxResponse);
            Assert.assertEquals(getTaxResponse("TAXWEB-BR"), taxResponse);
            this.verifyDestinationIntegrationCall(1);
            this.verifyHttpIntegrationCall(1);
            this.verifyDetermineIntegrationCall(0);
            this.verifyCalculateIntegrationCall(0);
        }).verifyComplete();
    }

    @SuppressWarnings("unchecked")
    private void mockDestinationIntegration(DestinationResponse response) {
        Mockito.when(destinationIntegration.call(any(Mono.class), any(Function.class), any(Function.class)))
                .thenReturn(Mono.just(response));
    }

    @SuppressWarnings("unchecked")
    private void mockHttpIntegration(TaxResponse response) {
        Mockito.when(quoteV0PartnerHttpIntegration.call(any(DestinationResponse.class), any(Mono.class),
                any(Function.class), any(Function.class))).thenReturn(Mono.just(response));
    }

    @SuppressWarnings("unchecked")
    private void mockDetermineIntegration(TaxAttributesDeterminationResponseModel response) {
        Mockito.when(determineIntegration.call(any(Mono.class), any(Function.class), any(Function.class)))
                .thenReturn(Mono.just(response));
    }

    @SuppressWarnings("unchecked")
    private void mockCalculateIntegration(TaxCalculationResponse response) {
        Mockito.when(calculateIntegration.call(any(Mono.class), any(Mono.class), any(BiFunction.class)))
                .thenReturn(Mono.just(response));
    }

    @SuppressWarnings("unchecked")
    private void verifyDestinationIntegrationCall(int times) {
        Mockito.verify(destinationIntegration, Mockito.times(times)).call(any(Mono.class), any(Function.class),
                any(Function.class));
    }

    @SuppressWarnings("unchecked")
    private void verifyHttpIntegrationCall(int times) {
        Mockito.verify(quoteV0PartnerHttpIntegration, Mockito.times(times)).call(any(DestinationResponse.class),
                any(Mono.class), any(Function.class), any(Function.class));
    }

    @SuppressWarnings("unchecked")
    private void verifyDetermineIntegrationCall(int times) {
        Mockito.verify(determineIntegration, Mockito.times(times)).call(any(Mono.class), any(Function.class),
                any(Function.class));
    }

    @SuppressWarnings({ "unchecked" })
    private void verifyCalculateIntegrationCall(int times) {
        Mockito.verify(calculateIntegration, Mockito.times(times)).call(any(Mono.class), any(Mono.class),
                any(BiFunction.class));
    }

    private DestinationResponse getDestinationResponse(String name) {
        return DestinationResponse.builder().withName(name).build();
    }

    private TaxResponse getTaxResponse(String partnerName) {
        return TaxResponse.builder().withPartnerName(partnerName).build();
    }

    private TaxAttributesDeterminationResponseModel getDeterminationResponse() {
        TaxAttributesDeterminationResponseModel responseModel = new TaxAttributesDeterminationResponseModel();

        TaxLine taxLine = new TaxLine();
        taxLine.setId("taxId");
        taxLine.setTaxTypeCode("taxTypeCode");

        com.sap.slh.tax.attributes.determination.model.response.ResponseItem detItem = new com.sap.slh.tax.attributes.determination.model.response.ResponseItem();
        detItem.setId("itemId1");
        detItem.setTaxes(Arrays.asList(taxLine));

        TaxAttributesDeterminationResponse response = new TaxAttributesDeterminationResponse();
        response.setItems(Arrays.asList(detItem));

        responseModel.setResult(response);

        return responseModel;
    }

    private TaxCalculationResponse getCalculationResponse() {
        TaxCalculationResponse calcResponse = new TaxCalculationResponse();

        TaxResult tax = new TaxResult();
        tax.setId("taxId");
        tax.setTaxAmount(BigDecimal.TEN);
        tax.setNonDeductibleTaxAmount(BigDecimal.ONE);

        ItemResult item = new ItemResult();
        item.setId("itemId1");
        item.setTaxes(Arrays.asList(tax));

        TaxCalculationResponseLine responseLine = new TaxCalculationResponseLine();
        responseLine.setItems(Arrays.asList(item));

        calcResponse.setResult(responseLine);
        return calcResponse;
    }

    private TaxRequest getDefaultRequest() {
        return TaxRequest.builder().withItems(Item.builder().withId("itemId").build()).build();
    }
}
