package com.sap.slh.tax.maestro.tax.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.domain.CurrencyCode;
import com.sap.slh.tax.maestro.api.v0.domain.GrossOrNet;
import com.sap.slh.tax.maestro.api.v0.domain.ItemType;
import com.sap.slh.tax.maestro.api.v0.domain.LocationType;
import com.sap.slh.tax.maestro.api.v0.domain.SaleOrPurchase;
import com.sap.slh.tax.maestro.api.v0.schema.ExemptionDetail;
import com.sap.slh.tax.maestro.api.v0.schema.Item;
import com.sap.slh.tax.maestro.api.v0.schema.Location;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse;
import com.sap.slh.tax.maestro.config.JacksonDecoderConfig;
import com.sap.slh.tax.maestro.context.RequestContextService;
import com.sap.slh.tax.maestro.tax.service.QuoteV0Service;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class TaxV0ControllerTest {

    private QuoteV0Service quoteService = Mockito.mock(QuoteV0Service.class);
    private ObjectMapper mapper = new JacksonDecoderConfig().objectMapper();
    private RequestContextService requestContextService = Mockito.mock(RequestContextService.class);

    private TaxV0Controller taxController;

    private String request = getDefaultRequest();
    private TaxResponse response = TaxResponse.builder().withPartnerName("TEST").build();
    private HttpHeaders httpHeaders = new HttpHeaders();

    public TaxV0ControllerTest() throws JsonProcessingException {
    }

    @Test
    public void testValidRequest() throws Exception {
        mockQuoteService();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "test");
        taxController = new TaxV0Controller(mapper, requestContextService, quoteService);
        StepVerifier.create(taxController.quote(request)).expectNext(ResponseEntity.ok().body(response))
                .verifyComplete();
    }

    @SuppressWarnings("unchecked")
    private void mockQuoteService() {
        when(quoteService.call(any(Mono.class))).thenReturn(Mono.just(response));
    }

    private String getDefaultRequest() throws JsonProcessingException {

        List<Location> locations = new ArrayList<>();
        Location location;
        location = Location.builder().withType(LocationType.SHIP_FROM).withAddressLine1("9 High Street")
                .withZipCode("SW1A 2AA").withCity("London").withState("LND").withCountry(CountryCode.GB).build();
        locations.add(location);
        location = Location.builder().withType(LocationType.SHIP_TO).withAddressLine1("47 King William St")
                .withZipCode("EC4R 9AF").withCity("London").withState("LND").withCountry(CountryCode.GB).build();
        locations.add(location);

        ExemptionDetail exemptionDetail = ExemptionDetail.builder().build();
        List<ExemptionDetail> exemptionDetails = new ArrayList<>();
        exemptionDetails.add(exemptionDetail);

        Item item = Item.builder().withId("1").withItemCode("P1234353453").withItemType(ItemType.m)
                .withQuantity(new BigDecimal(1)).withUnitPrice(new BigDecimal(100))
                .withExemptionDetails(exemptionDetails).build();

        return mapper.writeValueAsString(TaxRequest.builder().withId("1").withDate(new Date()).withSaleOrPurchase(SaleOrPurchase.s)
                .withGrossOrNet(GrossOrNet.n).withCurrency(CurrencyCode.GBP).withItems(item).withLocations(locations)
                .build());

    }
}
