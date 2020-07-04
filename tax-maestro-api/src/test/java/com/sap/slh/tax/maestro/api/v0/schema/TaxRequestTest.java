package com.sap.slh.tax.maestro.api.v0.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.domain.CurrencyCode;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.v0.domain.BooleanValue;
import com.sap.slh.tax.maestro.api.v0.domain.GrossOrNet;
import com.sap.slh.tax.maestro.api.v0.domain.ItemType;
import com.sap.slh.tax.maestro.api.v0.domain.LocationType;
import com.sap.slh.tax.maestro.api.v0.domain.SaleOrPurchase;
import com.sap.slh.tax.maestro.api.v0.domain.TaxCategory;
import com.sap.slh.tax.maestro.api.v0.schema.AdditionalItemInformation;
import com.sap.slh.tax.maestro.api.v0.schema.BusinessPartnerExemptionDetail;
import com.sap.slh.tax.maestro.api.v0.schema.CostInformation;
import com.sap.slh.tax.maestro.api.v0.schema.ExemptionDetail;
import com.sap.slh.tax.maestro.api.v0.schema.Item;
import com.sap.slh.tax.maestro.api.v0.schema.ItemClassification;
import com.sap.slh.tax.maestro.api.v0.schema.Location;
import com.sap.slh.tax.maestro.api.v0.schema.Party;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class TaxRequestTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ObjectMapper mapper = new ObjectMapper();

    private String mandatoryPropertyMessageFormat = "Mandatory property missing: '%s'";
    private String mandatoryPropertyDetailedMessageFormat = "Mandatory property missing: '%s' under %s";
    private String mandatoryPropertyFullyDetailedMessageFormat = "Mandatory property missing: '%s' under %s %s";
    private String mandatoryPropertyValueDetailedMessageFormat = "Mandatory property missing: '%s' with value: '%s' under %s";
    private String propertyWithMultipleValues = "You cannot have different values for property '%s' in a request under %s";

    @Test
    public void serializationAndDeserialization()
            throws JsonParseException, JsonMappingException, IOException, ParseException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        TaxRequest actualObject = new ObjectMapper().readValue(actualJson, TaxRequest.class);

        List<Item> items = new ArrayList<Item>();
        items.add(Item.builder().withId("id").withItemCode("itemCode").withQuantity(new BigDecimal(1))
                .withUnitPrice(new BigDecimal(2)).withShippingCost("shippingCost").withItemType(ItemType.M)
                .withExemptionDetails(new ArrayList<ExemptionDetail>()).withCertificateId("1")
                .withItemClassifications(new ArrayList<ItemClassification>())
                .withAdditionalItemInformation(new ArrayList<AdditionalItemInformation>())
                .withCostInformation(new ArrayList<CostInformation>()).build());

        List<Location> locations = new ArrayList<Location>();
        locations.add(Location.builder().withType(LocationType.CONTRACT_FROM).withZipCode("zipCode").withState("state")
                .withCountry(CountryCode.BR).build());

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = fmt.parse("2016-06-11T05:27:00.000Z");

        TaxRequest expectedObject = TaxRequest.builder().withId("id").withCompanyId("companyId")
                .withBusinessPartnerId("businessPartnerId").withDate(date).withCurrency(CurrencyCode.BRL)
                .withIsTransactionWithinTaxReportingGroup(BooleanValue.y).withCashDiscountPercent("cashDiscountPercent")
                .withIsCashDiscountPlanned(BooleanValue.n).withGrossOrNet(GrossOrNet.G)
                .withSaleOrPurchase(SaleOrPurchase.S).withOperationNatureCode("operationNatureCode")
                .withIsCompanyDeferredTaxEnabled(BooleanValue.Y).withItems(items).withLocations(locations)
                .withBusinessPartnerExemptionDetails(new ArrayList<BusinessPartnerExemptionDetail>())
                .withParty(new ArrayList<Party>()).withRemovedParties(new ArrayList<Party>())
                .withIsTraceRequired(BooleanValue.N).withTteDocumentId("tteDocumentId").build();

        assertEquals(expectedObject, actualObject);
        assertEquals(actualObject,
                new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(expectedObject), TaxRequest.class));
    }

    @Test
    public void serializationAndDeserializationForDirectPayload() throws ParseException, IOException {
        List<Item> items = new ArrayList<Item>();
        items.add(Item.builder().withId("id").withItemCode("itemCode").withQuantity(new BigDecimal(1))
                .withUnitPrice(new BigDecimal(2)).withTaxCategory(TaxCategory.PRODUCT_TAXES)
                .withTaxCode("taxcode").withTaxCodeCountry(CountryCode.CA).withTaxCodeRegion("ON").build());

        TaxRequest actualObject = TaxRequest.builder().withId("id").withDate(new Date()).withCurrency(CurrencyCode.GBP)
                .withGrossOrNet(GrossOrNet.G).withSaleOrPurchase(SaleOrPurchase.S).withItems(items).build();

        String serialized = mapper.writeValueAsString(actualObject);

        TaxRequest deserialized = mapper.readValue(serialized, TaxRequest.class);

        assertEquals(actualObject, deserialized);
    }

    @Test
    public void objectValidation() {
        try {
            TaxRequest.builder().build().validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage0 = String.format(mandatoryPropertyMessageFormat, "id");
            String expectedMessage1 = String.format(mandatoryPropertyMessageFormat, "date");
            String expectedMessage2 = String.format(mandatoryPropertyMessageFormat, "currency");
            String expectedMessage3 = String.format(mandatoryPropertyMessageFormat, "Items");
            String expectedMessage4 = String.format(mandatoryPropertyMessageFormat, "Locations");

            assertEquals(5, ex.getPropertyErrors().size());
            assertEquals(expectedMessage0, ex.getPropertyErrors().get(0).getMessage());
            assertEquals(expectedMessage1, ex.getPropertyErrors().get(1).getMessage());
            assertEquals(expectedMessage2, ex.getPropertyErrors().get(2).getMessage());
            assertEquals(expectedMessage3, ex.getPropertyErrors().get(3).getMessage());
            assertEquals(expectedMessage4, ex.getPropertyErrors().get(4).getMessage());
        }
    }

    @Test
    public void validateInvalidItem() throws ParseException {
        List<Item> items = new ArrayList<Item>();
        items.add(Item.builder().withId(null).withItemCode("itemCode").withQuantity(new BigDecimal(1))
                .withUnitPrice(new BigDecimal(2)).withShippingCost("shippingCost")
                .withExemptionDetails(new ArrayList<ExemptionDetail>()).withCertificateId("1")
                .withItemClassifications(new ArrayList<ItemClassification>())
                .withAdditionalItemInformation(new ArrayList<AdditionalItemInformation>())
                .withCostInformation(new ArrayList<CostInformation>()).build());
        items.add(Item.builder().withId("id").withItemCode("itemCode").withQuantity(new BigDecimal(1))
                .withUnitPrice(new BigDecimal(2)).withShippingCost("shippingCost")
                .withExemptionDetails(new ArrayList<ExemptionDetail>()).withCertificateId("1")
                .withItemClassifications(new ArrayList<ItemClassification>())
                .withAdditionalItemInformation(new ArrayList<AdditionalItemInformation>())
                .withCostInformation(new ArrayList<CostInformation>()).build());

        List<Location> locations = new ArrayList<Location>();
        locations.add(Location.builder().withType(LocationType.CONTRACT_FROM).withZipCode("zipCode").withState("state")
                .withCountry(CountryCode.BR).build());

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = fmt.parse("2016-06-11T05:27:00.000Z");

        TaxRequest expectedObject = TaxRequest.builder().withId("id").withCompanyId("companyId")
                .withBusinessPartnerId("businessPartnerId").withDate(date).withCurrency(CurrencyCode.BRL)
                .withIsTransactionWithinTaxReportingGroup(BooleanValue.y).withCashDiscountPercent("cashDiscountPercent")
                .withIsCashDiscountPlanned(BooleanValue.n).withGrossOrNet(GrossOrNet.G)
                .withSaleOrPurchase(SaleOrPurchase.S).withOperationNatureCode("operationNatureCode")
                .withIsCompanyDeferredTaxEnabled(BooleanValue.Y).withItems(items).withLocations(locations)
                .withBusinessPartnerExemptionDetails(new ArrayList<BusinessPartnerExemptionDetail>())
                .withParty(new ArrayList<Party>()).withRemovedParties(new ArrayList<Party>())
                .withIsTraceRequired(BooleanValue.N).withTteDocumentId("tteDocumentId").build();

        try {
            expectedObject.validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyDetailedMessageFormat, "id", "Items");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateInvalidItemQuantity() throws ParseException {
        List<Item> items = new ArrayList<Item>();
        items.add(Item.builder().withId("id1").withItemCode("itemCode").withQuantity(new BigDecimal(1))
                .withUnitPrice(new BigDecimal(2)).withShippingCost("shippingCost")
                .withExemptionDetails(new ArrayList<ExemptionDetail>()).withCertificateId("1")
                .withItemClassifications(new ArrayList<ItemClassification>())
                .withAdditionalItemInformation(new ArrayList<AdditionalItemInformation>())
                .withCostInformation(new ArrayList<CostInformation>()).build());
        items.add(Item.builder().withId("id2").withItemCode("itemCode").withQuantity(null)
                .withUnitPrice(new BigDecimal(2)).withShippingCost("shippingCost")
                .withExemptionDetails(new ArrayList<ExemptionDetail>()).withCertificateId("1")
                .withItemClassifications(new ArrayList<ItemClassification>())
                .withAdditionalItemInformation(new ArrayList<AdditionalItemInformation>())
                .withCostInformation(new ArrayList<CostInformation>()).build());

        List<Location> locations = new ArrayList<Location>();
        locations.add(Location.builder().withType(LocationType.CONTRACT_FROM).withZipCode("zipCode").withState("state")
                .withCountry(CountryCode.BR).build());

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = fmt.parse("2016-06-11T05:27:00.000Z");

        TaxRequest expectedObject = TaxRequest.builder().withId("id").withCompanyId("companyId")
                .withBusinessPartnerId("businessPartnerId").withDate(date).withCurrency(CurrencyCode.BRL)
                .withIsTransactionWithinTaxReportingGroup(BooleanValue.y).withCashDiscountPercent("cashDiscountPercent")
                .withIsCashDiscountPlanned(BooleanValue.n).withGrossOrNet(GrossOrNet.G)
                .withSaleOrPurchase(SaleOrPurchase.S).withOperationNatureCode("operationNatureCode")
                .withIsCompanyDeferredTaxEnabled(BooleanValue.Y).withItems(items).withLocations(locations)
                .withBusinessPartnerExemptionDetails(new ArrayList<BusinessPartnerExemptionDetail>())
                .withParty(new ArrayList<Party>()).withRemovedParties(new ArrayList<Party>())
                .withIsTraceRequired(BooleanValue.N).withTteDocumentId("tteDocumentId").build();

        try {
            expectedObject.validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyFullyDetailedMessageFormat, "quantity", "Items",
                    "id2");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateMultipleTaxCodeCountryDirectPayload() throws ParseException {
        List<Item> items = new ArrayList<Item>();
        items.add(Item.builder().withId("id1").withItemCode("itemCode").withQuantity(new BigDecimal(1))
                .withUnitPrice(new BigDecimal(2)).withTaxCategory(TaxCategory.PRODUCT_TAXES)
                .withTaxCode("taxcode").withTaxCodeCountry(CountryCode.CA).withTaxCodeRegion("ON").build());
        items.add(Item.builder().withId("id2").withItemCode("itemCode").withQuantity(new BigDecimal(2))
                .withUnitPrice(new BigDecimal(2)).withTaxCategory(TaxCategory.PRODUCT_TAXES)
                .withTaxCode("taxcode").withTaxCodeCountry(CountryCode.GB).withTaxCodeRegion("LDN").build());

        TaxRequest expectedObject = TaxRequest.builder().withId("id").withDate(new Date()).withCurrency(CurrencyCode.GBP)
                .withGrossOrNet(GrossOrNet.G).withSaleOrPurchase(SaleOrPurchase.S).withItems(items)
                .withIsTraceRequired(BooleanValue.N).withTteDocumentId("tteDocumentId").build();

        try {
            expectedObject.validate();
            fail("Expected Exception InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(propertyWithMultipleValues, "taxCodeCountry", "Items");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateMissingTaxCodeCountryDirectPayload() throws ParseException {
        List<Item> items = new ArrayList<Item>();
        items.add(Item.builder().withId("id1").withItemCode("itemCode").withQuantity(new BigDecimal(1))
                .withUnitPrice(new BigDecimal(2)).withTaxCategory(TaxCategory.PRODUCT_TAXES)
                .withTaxCode("taxcode").withTaxCodeRegion("LDN").build());
        items.add(Item.builder().withId("id2").withItemCode("itemCode").withQuantity(new BigDecimal(2))
                .withUnitPrice(new BigDecimal(2)).withTaxCategory(TaxCategory.PRODUCT_TAXES)
                .withTaxCode("taxcode").withTaxCodeCountry(CountryCode.GB).withTaxCodeRegion("LDN").build());

        TaxRequest expectedObject = TaxRequest.builder().withId("id").withDate(new Date()).withCurrency(CurrencyCode.GBP)
                .withGrossOrNet(GrossOrNet.G).withSaleOrPurchase(SaleOrPurchase.S).withItems(items)
                .withIsTraceRequired(BooleanValue.N).withTteDocumentId("tteDocumentId").build();

        try {
            expectedObject.validate();
            fail("Expected Exception InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyFullyDetailedMessageFormat, "taxCodeCountry",
                    "Items", "id1");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateMissingTaxCodeDirectPayload() throws ParseException {
        List<Item> items = new ArrayList<Item>();
        items.add(Item.builder().withId("id1").withItemCode("itemCode").withQuantity(new BigDecimal(1))
                .withUnitPrice(new BigDecimal(2)).withTaxCategory(TaxCategory.PRODUCT_TAXES)
                .withTaxCode("taxcode").withTaxCodeCountry(CountryCode.CA).withTaxCodeRegion("ON").build());
        items.add(Item.builder().withId("id2").withItemCode("itemCode").withQuantity(new BigDecimal(2))
                .withUnitPrice(new BigDecimal(2)).withTaxCategory(TaxCategory.PRODUCT_TAXES)
                .withTaxCodeCountry(CountryCode.CA).withTaxCodeRegion("BC").build());

        TaxRequest expectedObject = TaxRequest.builder().withId("id").withDate(new Date()).withCurrency(CurrencyCode.CAD)
                .withGrossOrNet(GrossOrNet.G).withSaleOrPurchase(SaleOrPurchase.S).withItems(items)
                .withIsTraceRequired(BooleanValue.N).withTteDocumentId("tteDocumentId").build();

        try {
            expectedObject.validate();
            fail("Expected Exception InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyFullyDetailedMessageFormat, "taxCode", "Items",
                    "id2");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateMissingTaxCategoryDirectPayload() throws ParseException {
        List<Item> items = new ArrayList<Item>();
        items.add(Item.builder().withId("id1").withItemCode("itemCode").withQuantity(new BigDecimal(1))
                .withUnitPrice(new BigDecimal(2)).withTaxCode("taxcode")
                .withTaxCodeCountry(CountryCode.CA).withTaxCodeRegion("BC").build());
        items.add(Item.builder().withId("id2").withItemCode("itemCode").withQuantity(new BigDecimal(2))
                .withUnitPrice(new BigDecimal(2)).withTaxCategory(TaxCategory.PRODUCT_TAXES)
                .withTaxCode("taxcode").withTaxCodeCountry(CountryCode.CA).withTaxCodeRegion("ON").build());

        TaxRequest expectedObject = TaxRequest.builder().withId("id").withDate(new Date()).withCurrency(CurrencyCode.CAD)
                .withGrossOrNet(GrossOrNet.G).withSaleOrPurchase(SaleOrPurchase.S).withItems(items)
                .withIsTraceRequired(BooleanValue.N).withTteDocumentId("tteDocumentId").build();

        try {
            expectedObject.validate();
            fail("Expected Exception InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyFullyDetailedMessageFormat, "taxCategory", "Items",
                    "id1");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateInvalidPurchaseLocationContractFrom()
            throws JsonParseException, JsonMappingException, InvalidModelException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"P\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";

        try {
            new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyValueDetailedMessageFormat, "type",
                    "CONTRACT_TO, SHIP_TO", "Locations");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateInvalidPurchaseLocationShipFrom()
            throws JsonParseException, JsonMappingException, InvalidModelException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"P\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"SHIP_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";

        try {
            new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyValueDetailedMessageFormat, "type",
                    "CONTRACT_TO, SHIP_TO", "Locations");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateValidPurchaseLocationContractToShipTo()
            throws JsonParseException, JsonMappingException, InvalidModelException, IOException {

        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"P\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_TO\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();

        actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"P\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"SHIP_TO\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
    }

    @Test
    public void validateInvalidSaleLocationContractTo()
            throws JsonParseException, JsonMappingException, InvalidModelException, IOException {

        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_TO\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";

        try {
            new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyValueDetailedMessageFormat, "type",
                    "CONTRACT_FROM, SHIP_FROM", "Locations");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateInvalidSaleLocationShipTo()
            throws JsonParseException, JsonMappingException, InvalidModelException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"SHIP_TO\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        try {
            new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyValueDetailedMessageFormat, "type",
                    "CONTRACT_FROM, SHIP_FROM", "Locations");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateValidSaleLocationContractFromShipFrom()
            throws JsonParseException, JsonMappingException, InvalidModelException, IOException {

        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();

        actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"SHIP_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
    }

    @Test
    public void validCurrencyCodeDomain() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        TaxRequest actualObject = new ObjectMapper().readValue(actualJson, TaxRequest.class);

        assertEquals(actualObject.getCurrency(), CurrencyCode.BRL);
    }

    @Test
    public void invalidCurrencyCodeDomain() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("CurrencyCode");

        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"invalidCurrency\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        new ObjectMapper().readValue(actualJson, TaxRequest.class);
    }

    @Test
    public void validGrossOrNetDomain() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        TaxRequest actualObject = new ObjectMapper().readValue(actualJson, TaxRequest.class);

        assertEquals(actualObject.getGrossOrNet(), GrossOrNet.G);
    }

    @Test
    public void invalidGrossOrNetDomain() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("GrossOrNet");

        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"m\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        new ObjectMapper().readValue(actualJson, TaxRequest.class);
    }

    @Test
    public void validSaleOrPurchaseDomain() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        TaxRequest actualObject = new ObjectMapper().readValue(actualJson, TaxRequest.class);

        assertEquals(actualObject.getSaleOrPurchase(), SaleOrPurchase.S);
    }

    @Test
    public void invalidSaleOrPurchaseDomain() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("SaleOrPurchase");

        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"X\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        new ObjectMapper().readValue(actualJson, TaxRequest.class);
    }

    @Test
    public void validIsCompanyDeferredTaxEnabledDomain() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        TaxRequest actualObject = new ObjectMapper().readValue(actualJson, TaxRequest.class);

        assertEquals(actualObject.getIsCompanyDeferredTaxEnabled(), BooleanValue.Y);
    }

    @Test
    public void invalidIsCompanyDeferredTaxEnabledDomain()
            throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("BooleanValue");

        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"P\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        new ObjectMapper().readValue(actualJson, TaxRequest.class);
    }

    @Test
    public void validIsTraceRequiredDomain() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        TaxRequest actualObject = new ObjectMapper().readValue(actualJson, TaxRequest.class);

        assertEquals(actualObject.getIsTraceRequired(), BooleanValue.N);
    }

    @Test
    public void invalidIsTraceRequiredDomain() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("BooleanValue");

        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"123\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        new ObjectMapper().readValue(actualJson, TaxRequest.class);
    }

    @Test
    public void validIsTransactionWithinTaxReportingGroupDomain()
            throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        TaxRequest actualObject = new ObjectMapper().readValue(actualJson, TaxRequest.class);

        assertEquals(actualObject.getIsTransactionWithinTaxReportingGroup(), BooleanValue.y);
    }

    @Test
    public void invalidIsTransactionWithinTaxReportingGroupDomain()
            throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("BooleanValue");

        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"1a\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        new ObjectMapper().readValue(actualJson, TaxRequest.class);
    }

    @Test
    public void validIsCashDiscountPlannedDomain() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        TaxRequest actualObject = new ObjectMapper().readValue(actualJson, TaxRequest.class);

        assertEquals(actualObject.getIsCashDiscountPlanned(), BooleanValue.n);
    }

    @Test
    public void invalidIsCashDiscountPlannedDomain() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("BooleanValue");

        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"no\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";
        new ObjectMapper().readValue(actualJson, TaxRequest.class);
    }

    @Test
    public void validateMissingId() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";

        try {
            new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "id");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateNullId() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":null,\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";

        try {
            new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "id");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateEmptyId() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";

        try {
            new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "id");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateMissingDate() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";

        try {
            new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "date");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateNullDate() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\",\"date\":null,"
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";

        try {
            new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "date");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateEmptyDate() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\",\"date\":\"\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";

        try {
            new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "date");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateDateFormat() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("\"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\"");

        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016.06.11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";

        new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
    }

    @Test
    public void validateDateFormatWrongMonth() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("\"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\"");

        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-13-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";

        new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
    }

    @Test
    public void validateMissingItem() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\"," + "\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";

        try {
            new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "Items");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateNullItem() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":null,\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";

        try {
            new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "Items");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateEmptyItem() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":\"BRL\",\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";

        try {
            new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "Items");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateMissingCurrency() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";

        try {
            new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "currency");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateNullCurrency() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"cashDiscountPercent\":\"cashDiscountPercent\","
                + "\"companyId\":\"companyId\",\"businessPartnerId\":\"businessPartnerId\","
                + "\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"isTransactionWithinTaxReportingGroup\":\"y\",\"isCompanyDeferredTaxEnabled\":\"Y\","
                + "\"saleorPurchase\":\"S\",\"operationNatureCode\":\"operationNatureCode\",\"grossOrNet\":\"G\","
                + "\"currency\":null,\"isCashDiscountPlanned\":\"n\",\"isTraceRequired\":\"N\","
                + "\"Items\":[{\"id\":\"id\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}],\"Locations\":"
                + "[{\"type\":\"CONTRACT_FROM\",\"zipCode\":\"zipCode\",\"state\":\"state\",\"country\":\"BR\"}],"
                + "\"BusinessPartnerExemptionDetails\":[],\"Party\":[],\"removedParties\":[],"
                + "\"tteDocumentId\":\"tteDocumentId\"}";

        try {
            new ObjectMapper().readValue(actualJson, TaxRequest.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "currency");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(TaxRequest.class).suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
                .verify();
    }
}
