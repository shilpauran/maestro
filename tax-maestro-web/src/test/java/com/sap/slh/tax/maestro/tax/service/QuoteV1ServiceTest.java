package com.sap.slh.tax.maestro.tax.service;

import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
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
import com.sap.slh.tax.maestro.api.v1.domain.DueCategory;
import com.sap.slh.tax.maestro.api.v1.domain.ProductType;
import com.sap.slh.tax.maestro.api.v1.schema.Item;
import com.sap.slh.tax.maestro.api.v1.schema.Product;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteResultDocument;
import com.sap.slh.tax.maestro.tax.service.integration.CalculateQueueIntegration;
import com.sap.slh.tax.maestro.tax.service.integration.DetermineQueueIntegration;
import com.sap.slh.tax.maestro.tax.service.integration.EnhanceQueueIntegration;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class QuoteV1ServiceTest {
    private final EnhanceQueueIntegration enhanceIntegration = Mockito.mock(EnhanceQueueIntegration.class);
    private final DetermineQueueIntegration determineIntegration = Mockito.mock(DetermineQueueIntegration.class);
    private final CalculateQueueIntegration calculateIntegration = Mockito.mock(CalculateQueueIntegration.class);

    private QuoteV1Service orchestration;

    private String defaultItemtId = "itemId1";
    private String defaultDocumentId = "010";
    private String defaultProductMasterDataCode = "001";
    private String defaultProductId = "001";

    @Before
    public void setup() {
        orchestration = new QuoteV1Service(enhanceIntegration, determineIntegration, calculateIntegration);
    }

    @Test
    public void testAMQPCallToEnhance() {
        QuoteDocument enhancedDocument = getQuoteDocumentWithId(defaultDocumentId);
        enhancedDocument.setProducts(
                Arrays.asList(Product.builder().withMasterDataProductId(defaultProductMasterDataCode).build()));

        this.mockEnhanceIntegration(getEnhancedQuoteDocument());
        this.mockDetermineIntegration(getDeterminationResponse());
        this.mockCalculateIntegration(getCalculationResponse());

        StepVerifier.create(orchestration.call(Mono.just(enhancedDocument))).consumeNextWith(quoteResponse -> {
            Assert.assertNotNull(quoteResponse);
            Assert.assertTrue(quoteResponse instanceof QuoteResultDocument);
            this.verifyEnhanceIntegrationCall(1);
            this.verifyDetermineIntegrationCall(1);
            this.verifyCalculateIntegrationCall(1);
        }).verifyComplete();
    }

    @Test
    public void testAMQPCallNoEnhance() {
        QuoteDocument quoteDocument = getQuoteDocumentWithId(defaultDocumentId);
        quoteDocument.setProducts(Arrays.asList(Product.builder().build()));

        this.mockDetermineIntegration(getDeterminationResponse());
        this.mockCalculateIntegration(getCalculationResponse());

        StepVerifier.create(orchestration.call(Mono.just(quoteDocument))).consumeNextWith(quoteResponse -> {
            this.verifyEnhanceIntegrationCall(0);
            this.verifyDetermineIntegrationCall(1);
            this.verifyCalculateIntegrationCall(1);
        }).verifyComplete();
    }

    @SuppressWarnings("unchecked")
    private void mockEnhanceIntegration(QuoteDocument response) {
        Mockito.when(enhanceIntegration.call(any(Mono.class), any(Function.class), any(BiFunction.class)))
                .thenReturn(Mono.just(response));
    }

    @SuppressWarnings("unchecked")
    private void verifyEnhanceIntegrationCall(int times) {
        Mockito.verify(enhanceIntegration, Mockito.times(times)).call(any(Mono.class), any(Function.class),
                any(BiFunction.class));
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
    private void verifyDetermineIntegrationCall(int times) {
        Mockito.verify(determineIntegration, Mockito.times(times)).call(any(Mono.class), any(Function.class),
                any(Function.class));
    }

    @SuppressWarnings("unchecked")
    private void verifyCalculateIntegrationCall(int times) {
        Mockito.verify(calculateIntegration, Mockito.times(times)).call(any(Mono.class), any(Mono.class),
                any(BiFunction.class));
    }

    private QuoteDocument getQuoteDocumentWithId(String documentId) {
        return QuoteDocument.builder().withDate(new Date()).withId(documentId)
                .withItems(Item.builder().withId(defaultItemtId).build()).build();
    }

    private QuoteDocument getEnhancedQuoteDocument() {
        return QuoteDocument.builder().withDate(new Date()).withId(defaultDocumentId)
                .withItems(Item.builder().withId(defaultItemtId).build())
                .withProducts(Product.builder().withId(defaultProductId).withTypeCode(ProductType.MATERIAL).build())
                .build();
    }

    private TaxAttributesDeterminationResponseModel getDeterminationResponse() {
        TaxAttributesDeterminationResponseModel responseModel = new TaxAttributesDeterminationResponseModel();

        TaxLine taxLine = new TaxLine();
        taxLine.setId("taxId");
        taxLine.setDueCategoryCode(DueCategory.PAYABLE.toString());

        com.sap.slh.tax.attributes.determination.model.response.ResponseItem detItem = new com.sap.slh.tax.attributes.determination.model.response.ResponseItem();
        detItem.setId(defaultItemtId);
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
        item.setId(defaultItemtId);
        item.setTaxes(Arrays.asList(tax));

        TaxCalculationResponseLine responseLine = new TaxCalculationResponseLine();
        responseLine.setItems(Arrays.asList(item));

        calcResponse.setResult(responseLine);
        return calcResponse;
    }
}
