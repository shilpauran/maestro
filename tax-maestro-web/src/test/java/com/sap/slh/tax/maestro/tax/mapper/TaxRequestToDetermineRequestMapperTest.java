package com.sap.slh.tax.maestro.tax.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import com.sap.slh.tax.maestro.api.v0.domain.BooleanValue;
import com.sap.slh.tax.maestro.api.v0.domain.GrossOrNet;
import com.sap.slh.tax.maestro.api.v0.domain.ItemType;
import com.sap.slh.tax.maestro.api.v0.domain.LocationType;
import com.sap.slh.tax.maestro.api.v0.domain.SaleOrPurchase;
import com.sap.slh.tax.maestro.api.v0.domain.TaxCategory;
import com.sap.slh.tax.maestro.api.v0.schema.AdditionalItemInformation;
import com.sap.slh.tax.maestro.api.v0.schema.BusinessPartnerExemptionDetail;
import com.sap.slh.tax.maestro.api.v0.schema.ExemptionDetail;
import com.sap.slh.tax.maestro.api.v0.schema.Item;
import com.sap.slh.tax.maestro.api.v0.schema.ItemClassification;
import com.sap.slh.tax.maestro.api.v0.schema.Location;
import com.sap.slh.tax.maestro.api.v0.schema.Party;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest.Builder;
import com.sap.slh.tax.maestro.tax.exceptions.DirectPayloadNotSupportedException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class TaxRequestToDetermineRequestMapperTest {

    private TaxRequestToDetermineRequestMapper mapper;

    @Before
    public void setup() {
        mapper = TaxRequestToDetermineRequestMapper.getInstance();
    }

    @Test
    public void testFullMapping() {
        Builder taxRequestBuilder = getDefaultTaxRequest();
        Date date = getSpecificDate();
        TaxRequest taxRequest = taxRequestBuilder.withDate(date).build();

        TaxAttributesDeterminationRequest expectedOutput = getExpectedOutput();
        expectedOutput.setDate(date);

        StepVerifier.create(mapper.apply(taxRequest)).expectNextMatches(tdsRequest -> {
            assertThat(tdsRequest).usingRecursiveComparison().isEqualTo(expectedOutput);
            return true;
        }).expectComplete().verify();
    }

    @Test
    public void testMappingWithoutBooleanFields() {
        Builder taxRequestBuilder = getTaxRequestWithoutBooleanFields();
        Date date = getSpecificDate();
        TaxRequest taxRequest = taxRequestBuilder.withDate(date).build();

        TaxAttributesDeterminationRequest expectedOutput = getExpectedOutput();
        expectedOutput.setDate(date);
        expectedOutput.setIsCompanyDeferredTaxEnabled(Boolean.FALSE);
        expectedOutput.setIsTransactionWithinTaxReportingGroup(Boolean.FALSE);
        expectedOutput.getCompanyInformation().setIsDeferredTaxEnabled(Boolean.FALSE);

        StepVerifier.create(mapper.apply(taxRequest)).expectNextMatches(tdsRequest -> {
            assertThat(tdsRequest).usingRecursiveComparison().isEqualTo(expectedOutput);
            return true;
        }).expectComplete().verify();
    }

    @Test
    public void testDirectPayload() {
        Builder taxRequestBuilder = buildDirectPayloadTaxRequest();
        Date date = getSpecificDate();
        TaxRequest taxRequest = taxRequestBuilder.withDate(date).build();

        StepVerifier.create(Mono.just(taxRequest).flatMap(mapper))
                .expectError(DirectPayloadNotSupportedException.class)
                .verify();
    }

    private Date getSpecificDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2019, 05, 10);
        return cal.getTime();
    }

    private Builder buildDirectPayloadTaxRequest() {
        List<Item> items = new ArrayList<Item>();
        items.add(Item.builder()
                .withId("id1")
                .withItemCode("itemCode")
                .withQuantity(new BigDecimal(1))
                .withUnitPrice(new BigDecimal(2))
                .withTaxCategory(TaxCategory.PRODUCT_TAXES)
                .withTaxCode("taxCode")
                .withTaxCodeRegion("LDN")
                .build());

        return TaxRequest.builder()
                .withId("1")
                .withSaleOrPurchase(SaleOrPurchase.P)
                .withGrossOrNet(GrossOrNet.n)
                .withCurrency(CurrencyCode.EUR)
                .withItems(items)
                .withLocations(Collections.emptyList());
    }

    private Builder getDefaultTaxRequest() {
        Item item = buildItem();
        Location location = buildLocation();
        Party party = buildParty();
        BusinessPartnerExemptionDetail businessPartnerExemption = buildBusinessPartnerExemptionDetail();

        return TaxRequest.builder()
                .withId("ID")
                .withBusinessPartnerExemptionDetails(Arrays.asList(businessPartnerExemption))
                .withBusinessPartnerId("businessPartnerId")
                .withCompanyId("companyId")
                .withIsCompanyDeferredTaxEnabled(BooleanValue.Y)
                .withIsTraceRequired(BooleanValue.Y)
                .withIsTransactionWithinTaxReportingGroup(BooleanValue.Y)
                .withItems(Arrays.asList(item))
                .withLocations(Arrays.asList(location))
                .withOperationNatureCode("natureCode")
                .withRemovedParties(Arrays.asList(Party.builder().build()))
                .withParty(Arrays.asList(party))
                .withSaleOrPurchase(SaleOrPurchase.P)
                .withTteDocumentId("tteID");
    }

    private Builder getTaxRequestWithoutBooleanFields() {
        Item item = buildItem();
        Location location = buildLocation();
        Party party = buildParty();
        BusinessPartnerExemptionDetail businessPartnerExemption = buildBusinessPartnerExemptionDetail();

        return TaxRequest.builder()
                .withId("ID")
                .withBusinessPartnerExemptionDetails(Arrays.asList(businessPartnerExemption))
                .withBusinessPartnerId("businessPartnerId")
                .withCompanyId("companyId")
                .withItems(Arrays.asList(item))
                .withLocations(Arrays.asList(location))
                .withOperationNatureCode("natureCode")
                .withRemovedParties(Arrays.asList(Party.builder().build()))
                .withParty(Arrays.asList(party))
                .withSaleOrPurchase(SaleOrPurchase.P)
                .withTteDocumentId("tteID");
    }

    private BusinessPartnerExemptionDetail buildBusinessPartnerExemptionDetail() {
        BusinessPartnerExemptionDetail businessPartnerExemption = BusinessPartnerExemptionDetail.builder()
                .withLocationType(LocationType.SHIP_FROM)
                .build();
        return businessPartnerExemption;
    }

    private Party buildParty() {
        Party party = Party.builder()
                .withId("id")
                .withRole(LocationType.SHIP_FROM)
                .withTaxRegistration(Arrays.asList(com.sap.slh.tax.maestro.api.v0.schema.TaxRegistration.builder()
                        .withLocationType(LocationType.SHIP_FROM)
                        .build()))
                .build();
        return party;
    }

    private Location buildLocation() {
        Location location = Location.builder().withCountry(CountryCode.AD).withType(LocationType.SHIP_FROM).build();
        return location;
    }

    private Item buildItem() {
        AdditionalItemInformation additionalItemInformation = AdditionalItemInformation.builder()
                .withInformation("information")
                .withType("type")
                .build();

        ItemClassification itemClassification = ItemClassification.builder()
                .withItemStandardClassificationCode("code")
                .withItemStandardClassificationSystemCode("systemcode")
                .build();

        Item item = Item.builder()
                .withExemptionDetails(
                        Arrays.asList(ExemptionDetail.builder().withLocationType(LocationType.SHIP_FROM).build()))
                .withId("id")
                .withItemType(ItemType.S)
                .withAdditionalItemInformation(Arrays.asList(additionalItemInformation))
                .withItemClassifications(Arrays.asList(itemClassification))
                .withItemCode("code")
                .build();
        return item;
    }

    private TaxAttributesDeterminationRequest getExpectedOutput() {
        TaxAttributesDeterminationRequest expectedOutput = new TaxAttributesDeterminationRequest();
        expectedOutput.setId("ID");
        expectedOutput.setIsCompanyDeferredTaxEnabled(Boolean.TRUE);
        expectedOutput.setIsTransactionWithinTaxReportingGroup(Boolean.TRUE);
        expectedOutput.setTransactionTypeCode(TaxAttributesDeterminationRequest.TransactionType.PURCHASE);

        PartyDetail detPartyDetail = new PartyDetail();
        detPartyDetail.setId("1");
        detPartyDetail.setRole(PartyDetail.Role.SHIP_FROM);

        com.sap.slh.tax.attributes.determination.model.request.Item detItem = new com.sap.slh.tax.attributes.determination.model.request.Item();
        detItem.setId("id");
        detItem.setAssignedProductId("1");
        detItem.setAssignedParties(Arrays.asList(detPartyDetail));

        expectedOutput.setItems(Arrays.asList(detItem));

        com.sap.slh.tax.attributes.determination.model.request.StandardClassification detStandardClassification = new com.sap.slh.tax.attributes.determination.model.request.StandardClassification();
        detStandardClassification.setClassificationSystem("systemcode");
        detStandardClassification.setProductCode("code");

        TaxClassification detProdTaxClassification = new TaxClassification();
        detProdTaxClassification.setCountryRegionCode(Country.AD);

        com.sap.slh.tax.attributes.determination.model.request.Product detProduct = new com.sap.slh.tax.attributes.determination.model.request.Product();
        detProduct.setId("1");
        detProduct.setTypeCode(com.sap.slh.tax.attributes.determination.model.request.Product.Type.SERVICE);
        detProduct.setTaxClassifications(Arrays.asList(detProdTaxClassification));
        detProduct.setStandardClassifications(Arrays.asList(detStandardClassification));

        expectedOutput.setProducts(Arrays.asList(detProduct));

        TaxClassification_ detPartyTaxClassification = new TaxClassification_();
        detPartyTaxClassification.setCountryRegionCode(Country.AD);

        TaxRegistration detTaxRegistration = new TaxRegistration();
        detTaxRegistration.setCountryRegionCode(Country.AD);

        com.sap.slh.tax.attributes.determination.model.request.Party detParty = new com.sap.slh.tax.attributes.determination.model.request.Party();
        detParty.setId("1");
        detParty.setCountryRegionCode(Country.AD);
        detParty.setTaxClassification(Arrays.asList(detPartyTaxClassification));
        detParty.setTaxRegistrations(Arrays.asList(detTaxRegistration));

        expectedOutput.setParties(Arrays.asList(detParty));

        com.sap.slh.tax.attributes.determination.model.request.CompanyInformation detCompanyInformation = new com.sap.slh.tax.attributes.determination.model.request.CompanyInformation();
        detCompanyInformation.setIsDeferredTaxEnabled(Boolean.TRUE);

        expectedOutput.setCompanyInformation(detCompanyInformation);

        return expectedOutput;
    }
}
