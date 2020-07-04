package com.sap.slh.tax.maestro.tax.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.sap.slh.tax.attributes.determination.model.request.Country;
import com.sap.slh.tax.attributes.determination.model.request.PartyDetail;
import com.sap.slh.tax.attributes.determination.model.request.TaxAttributesDeterminationRequest;
import com.sap.slh.tax.attributes.determination.model.request.TaxClassification;
import com.sap.slh.tax.attributes.determination.model.request.TaxClassification_;
import com.sap.slh.tax.attributes.determination.model.request.TaxRegistration;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.domain.CurrencyCode;
import com.sap.slh.tax.maestro.api.v1.domain.AmountType;
import com.sap.slh.tax.maestro.api.v1.domain.PartyRole;
import com.sap.slh.tax.maestro.api.v1.domain.ProductType;
import com.sap.slh.tax.maestro.api.v1.domain.TransactionType;
import com.sap.slh.tax.maestro.api.v1.schema.AssignedParty;
import com.sap.slh.tax.maestro.api.v1.schema.CompanyInformation;
import com.sap.slh.tax.maestro.api.v1.schema.CostInformation;
import com.sap.slh.tax.maestro.api.v1.schema.Item;
import com.sap.slh.tax.maestro.api.v1.schema.Party;
import com.sap.slh.tax.maestro.api.v1.schema.PartyTaxClassification;
import com.sap.slh.tax.maestro.api.v1.schema.PartyTaxRegistration;
import com.sap.slh.tax.maestro.api.v1.schema.Product;
import com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument.Builder;
import com.sap.slh.tax.maestro.api.v1.schema.StandardClassification;

import reactor.test.StepVerifier;

public class QuoteDocumentToDetermineRequestMapperTest {

    private QuoteDocumentToDetermineRequestMapper mapper;

    @Before
    public void setup() {
        mapper = QuoteDocumentToDetermineRequestMapper.getInstance();
    }

    @Test
    public void testFullMapping() {
        Builder quoteDocumentBuilder = getDefaultQuoteDocument();
        Date date = getSpecificDate();
        QuoteDocument quoteDocument = quoteDocumentBuilder.withDate(date).build();

        TaxAttributesDeterminationRequest expectedOutput = getExpectedOutput();
        expectedOutput.setDate(date);

        StepVerifier.create(mapper.apply(quoteDocument)).expectNextMatches(request -> {
            assertThat(request).usingRecursiveComparison().isEqualTo(expectedOutput);
            return true;
        }).expectComplete().verify();
    }

    @Test
    public void testMappingWithoutItems() {
        Builder quoteDocumentBuilder = getDefaultQuoteDocument();
        Date date = getSpecificDate();
        QuoteDocument quoteDocument = quoteDocumentBuilder.withDate(date).build();

        TaxAttributesDeterminationRequest expectedOutput = getExpectedOutput();
        expectedOutput.setDate(date);

        quoteDocument.setItems(Collections.emptyList());
        expectedOutput.setItems(Collections.emptyList());

        StepVerifier.create(mapper.apply(quoteDocument)).expectNextMatches(request -> {
            assertThat(request).usingRecursiveComparison().isEqualTo(expectedOutput);
            return true;
        }).expectComplete().verify();
    }

    @Test
    public void testMappingWithoutProducts() {
        Builder quoteDocumentBuilder = getDefaultQuoteDocument();
        Date date = getSpecificDate();
        QuoteDocument quoteDocument = quoteDocumentBuilder.withDate(date).build();

        TaxAttributesDeterminationRequest expectedOutput = getExpectedOutput();
        expectedOutput.setDate(date);

        quoteDocument.setProducts(Collections.emptyList());
        expectedOutput.setProducts(Collections.emptyList());

        StepVerifier.create(mapper.apply(quoteDocument)).expectNextMatches(request -> {
            assertThat(request).usingRecursiveComparison().isEqualTo(expectedOutput);
            return true;
        }).expectComplete().verify();
    }

    @Test
    public void testMappingWithoutParties() {
        Builder quoteDocumentBuilder = getDefaultQuoteDocument();
        Date date = getSpecificDate();
        QuoteDocument quoteDocument = quoteDocumentBuilder.withDate(date).build();

        TaxAttributesDeterminationRequest expectedOutput = getExpectedOutput();
        expectedOutput.setDate(date);

        quoteDocument.setParties(Collections.emptyList());
        expectedOutput.setParties(Collections.emptyList());

        StepVerifier.create(mapper.apply(quoteDocument)).expectNextMatches(request -> {
            assertThat(request).usingRecursiveComparison().isEqualTo(expectedOutput);
            return true;
        }).expectComplete().verify();
    }

    @Test
    public void testMappingWithoutCompanyInformation() {
        Builder quoteDocumentBuilder = getDefaultQuoteDocument();
        Date date = getSpecificDate();
        QuoteDocument quoteDocument = quoteDocumentBuilder.withDate(date).build();

        TaxAttributesDeterminationRequest expectedOutput = getExpectedOutput();
        expectedOutput.setDate(date);

        quoteDocument.setCompanyInformation(null);
        expectedOutput
                .setCompanyInformation(new com.sap.slh.tax.attributes.determination.model.request.CompanyInformation());

        StepVerifier.create(mapper.apply(quoteDocument)).expectNextMatches(request -> {
            assertThat(request).usingRecursiveComparison().isEqualTo(expectedOutput);
            return true;
        }).expectComplete().verify();
    }

    private Date getSpecificDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2019, 05, 10);
        return cal.getTime();
    }

    private Builder getDefaultQuoteDocument() {
        Map<String, String> additional = new HashMap<>();
        additional.put("key1", "value1");
        additional.put("key2", "value2");

        Item item = Item.builder()
                .withAdditionalInformation(additional)
                .withId("ID")
                .withAssignedProductId("ID")
                .withQuantity(BigDecimal.ONE)
                .withUnitPrice(BigDecimal.TEN)
                .withCosts(CostInformation.builder().withType("COST").withAmount(BigDecimal.ONE).build())
                .withAssignedParties(AssignedParty.builder().withId("Party").withRole(PartyRole.BILL_FROM).build())
                .build();

        ProductTaxClassification prodTaxClassification = ProductTaxClassification.builder()
                .withCountryRegionCode(CountryCode.AI)
                .withIsServicePointTaxable(Boolean.TRUE)
                .withIsSoldElectronically(Boolean.TRUE)
                .withSubdivisionCode("SUBDIVISION")
                .withExemptionReasonCode("EXEMPTION")
                .withTaxRateTypeCode("RATE_TYPE")
                .withTaxTypeCode("TYPE")
                .build();

        Product product = Product.builder()
                .withId("ID")
                .withAdditionalInformation(additional)
                .withMasterDataProductId("productCode")
                .withTypeCode(ProductType.MATERIAL)
                .withTaxClassifications(prodTaxClassification)
                .withStandardClassifications(StandardClassification.builder()
                        .withClassificationSystem("SYS_CODE")
                        .withProductCode("CLASS_CODE")
                        .build())
                .build();

        PartyTaxClassification taxClassification = PartyTaxClassification.builder()
                .withExemptionReasonCode("EXEMPTION")
                .withTaxTypeCode("TYPE_CODE")
                .withCountryRegionCode(CountryCode.CU)
                .withSubdivisionCode("SUBDIVISION")
                .build();

        PartyTaxRegistration taxRegistration = PartyTaxRegistration.builder()
                .withCountryRegionCode(CountryCode.CU)
                .withSubdivisionCode("SUBDIVISION")
                .withType("TYPE")
                .build();

        Party party = Party.builder()
                .withId("ID")
                .withAddress("ADDRESS")
                .withCity("CITY")
                .withCountryRegionCode(CountryCode.AI)
                .withCounty("COUNTY")
                .withSubdivisionCode("SUBDIVISION")
                .withZipCode("ZIPCODE")
                .withTaxClassifications(taxClassification)
                .withTaxRegistrations(taxRegistration)
                .withAdditionalInformation(additional)
                .build();

        CompanyInformation companyInformation = CompanyInformation.builder()
                .withAssignedPartyId("ID")
                .withIsDeferredTaxEnabled(false)
                .withAdditionalInformation(null)
                .build();

        return QuoteDocument.builder()
                .withId("ID")
                .withDate(getSpecificDate())
                .withCompanyInformation(companyInformation)
                .withTransactionTypeCode(TransactionType.PURCHASE)
                .withAmountTypeCode(AmountType.GROSS)
                .withCurrencyCode(CurrencyCode.BRL)
                .withIsTransactionWithinTaxReportingGroup(Boolean.TRUE)
                .withItems(item)
                .withAdditionalInformation(additional)
                .withProducts(product)
                .withParties(party);
    }

    private TaxAttributesDeterminationRequest getExpectedOutput() {
        TaxAttributesDeterminationRequest expectedOutput = new TaxAttributesDeterminationRequest();
        expectedOutput.setId("ID");
        expectedOutput.setIsTransactionWithinTaxReportingGroup(Boolean.TRUE);
        expectedOutput.setTransactionTypeCode(TaxAttributesDeterminationRequest.TransactionType.PURCHASE);

        PartyDetail detPartyDetail = new PartyDetail();
        detPartyDetail.setId("Party");
        detPartyDetail.setRole(PartyDetail.Role.BILL_FROM);

        com.sap.slh.tax.attributes.determination.model.request.Item detItem = new com.sap.slh.tax.attributes.determination.model.request.Item();
        detItem.setId("ID");
        detItem.setAssignedProductId("ID");
        detItem.setAssignedParties(Arrays.asList(detPartyDetail));

        expectedOutput.setItems(Arrays.asList(detItem));

        com.sap.slh.tax.attributes.determination.model.request.StandardClassification detStandardClassification = new com.sap.slh.tax.attributes.determination.model.request.StandardClassification();
        detStandardClassification.setClassificationSystem("SYS_CODE");
        detStandardClassification.setProductCode("CLASS_CODE");

        TaxClassification detProdTaxClassification = new TaxClassification();
        detProdTaxClassification.setCountryRegionCode(Country.AI);
        detProdTaxClassification.setExemptionReasonCode("EXEMPTION");
        detProdTaxClassification.setIsServicePointTaxable(Boolean.TRUE);
        detProdTaxClassification.setIsSoldElectronically(Boolean.TRUE);
        detProdTaxClassification.setSubdivisionCode("SUBDIVISION");
        detProdTaxClassification.setTaxRateTypeCode("RATE_TYPE");
        detProdTaxClassification.setTaxTypeCode("TYPE");

        com.sap.slh.tax.attributes.determination.model.request.Product detProduct = new com.sap.slh.tax.attributes.determination.model.request.Product();
        detProduct.setId("ID");
        detProduct.setTypeCode(com.sap.slh.tax.attributes.determination.model.request.Product.Type.MATERIAL);
        detProduct.setTaxClassifications(Arrays.asList(detProdTaxClassification));
        detProduct.setStandardClassifications(Arrays.asList(detStandardClassification));

        expectedOutput.setProducts(Arrays.asList(detProduct));

        TaxClassification_ detPartyTaxClassification = new TaxClassification_();
        detPartyTaxClassification.setCountryRegionCode(Country.CU);
        detPartyTaxClassification.setExemptionReasonCode("EXEMPTION");
        detPartyTaxClassification.setSubdivisionCode("SUBDIVISION");
        detPartyTaxClassification.setTaxTypeCode("TYPE_CODE");

        TaxRegistration detTaxRegistration = new TaxRegistration();
        detTaxRegistration.setCountryRegionCode(Country.CU);
        detTaxRegistration.setSubdivisionCode("SUBDIVISION");
        detTaxRegistration.setType("TYPE");

        com.sap.slh.tax.attributes.determination.model.request.Party detParty = new com.sap.slh.tax.attributes.determination.model.request.Party();
        detParty.setId("ID");
        detParty.setCountryRegionCode(Country.AI);
        detParty.setSubdivisionCode("SUBDIVISION");
        detParty.setTaxClassification(Arrays.asList(detPartyTaxClassification));
        detParty.setTaxRegistrations(Arrays.asList(detTaxRegistration));

        expectedOutput.setParties(Arrays.asList(detParty));

        com.sap.slh.tax.attributes.determination.model.request.CompanyInformation detCompanyInformation = new com.sap.slh.tax.attributes.determination.model.request.CompanyInformation();
        detCompanyInformation.setAssignedPartyId("ID");
        detCompanyInformation.setIsDeferredTaxEnabled(Boolean.FALSE);

        expectedOutput.setCompanyInformation(detCompanyInformation);

        return expectedOutput;
    }

}
