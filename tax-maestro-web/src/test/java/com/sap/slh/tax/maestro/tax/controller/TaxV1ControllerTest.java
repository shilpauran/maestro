package com.sap.slh.tax.maestro.tax.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.domain.CurrencyCode;
import com.sap.slh.tax.maestro.api.v1.domain.AmountType;
import com.sap.slh.tax.maestro.api.v1.domain.PartyRole;
import com.sap.slh.tax.maestro.api.v1.domain.TransactionType;
import com.sap.slh.tax.maestro.api.v1.schema.AssignedParty;
import com.sap.slh.tax.maestro.api.v1.schema.CompanyInformation;
import com.sap.slh.tax.maestro.api.v1.schema.Item;
import com.sap.slh.tax.maestro.api.v1.schema.Party;
import com.sap.slh.tax.maestro.api.v1.schema.Product;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteResultDocument;
import com.sap.slh.tax.maestro.config.JacksonDecoderConfig;
import com.sap.slh.tax.maestro.context.RequestContextService;
import com.sap.slh.tax.maestro.tax.service.QuoteV1Service;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class TaxV1ControllerTest {

    private QuoteV1Service quoteService = Mockito.mock(QuoteV1Service.class);
    private ObjectMapper mapper = new JacksonDecoderConfig().objectMapper();
    private RequestContextService requestContextService = Mockito.mock(RequestContextService.class);

    private TaxV1Controller taxController;

    private String request = getDefaultRequest();
    private QuoteResultDocument response = QuoteResultDocument.builder().withId("1").build();
    private HttpHeaders httpHeaders = new HttpHeaders();

    public TaxV1ControllerTest() throws JsonProcessingException {
    }

    @Test
    public void testValidRequest() throws Exception {
        mockQuoteService();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "test");
        taxController = new TaxV1Controller(mapper, requestContextService, quoteService);
        StepVerifier.create(taxController.quote(request)).expectNext(ResponseEntity.ok().body(response))
                .verifyComplete();
    }

    @SuppressWarnings("unchecked")
    private void mockQuoteService() {
        when(quoteService.call(any(Mono.class))).thenReturn(Mono.just(response));
    }

    private String getDefaultRequest() throws JsonProcessingException {
        Party partyFrom = Party.builder().withId("1").withCountryRegionCode(CountryCode.GB).withSubdivisionCode("LDN")
                .build();
        Party partyTo = Party.builder().withId("2").withCountryRegionCode(CountryCode.GB).withSubdivisionCode("LDN")
                .build();
        Product product = Product.builder().withId("1").withMasterDataProductId("ENHANCED").build();

        AssignedParty assignedPartyFrom = AssignedParty.builder().withId("1").withRole(PartyRole.SHIP_FROM).build();
        AssignedParty assignedPartyTo = AssignedParty.builder().withId("2").withRole(PartyRole.SHIP_TO).build();
        CompanyInformation companyInformation = CompanyInformation.builder().withAssignedPartyId("1").build();

        Item item = Item.builder().withId("1").withQuantity(new BigDecimal(1)).withUnitPrice(new BigDecimal(100))
                .withAssignedProductId("1").withAssignedParties(assignedPartyFrom, assignedPartyTo).build();

        return mapper.writeValueAsString(QuoteDocument.builder().withId("1").withDate(new Date()).withTransactionTypeCode(TransactionType.SALE)
                .withAmountTypeCode(AmountType.GROSS).withCurrencyCode(CurrencyCode.BRL)
                .withIsTransactionWithinTaxReportingGroup(Boolean.FALSE).withItems(item)
                .withCompanyInformation(companyInformation).withAdditionalInformation(Collections.emptyMap())
                .withProducts(product).withParties(partyFrom, partyTo).build());
    }
}
