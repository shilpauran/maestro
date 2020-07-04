package com.sap.slh.tax.maestro.api.v1.schema;

import static com.sap.slh.tax.maestro.api.assertion.HasMandatoryAttribute.hasMandatoryItems;
import static com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument.JSONParameter.AMOUNT_TYPE_CODE;
import static com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument.JSONParameter.COMPANY_INFORMATION;
import static com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument.JSONParameter.CURRENCY_CODE;
import static com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument.JSONParameter.DATE;
import static com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument.JSONParameter.ID;
import static com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument.JSONParameter.ITEMS;
import static com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument.JSONParameter.PARTIES;
import static com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument.JSONParameter.PRODUCTS;
import static com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument.JSONParameter.TRANSACTION_TYPE_CODE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.domain.CurrencyCode;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.v1.domain.AmountType;
import com.sap.slh.tax.maestro.api.v1.domain.PartyRole;
import com.sap.slh.tax.maestro.api.v1.domain.ProductType;
import com.sap.slh.tax.maestro.api.v1.domain.TransactionType;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class QuoteDocumentTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        mapper.disable(MapperFeature.ALLOW_COERCION_OF_SCALARS);
    }

    @Test
    public void nullFieldsBuilder() {
        QuoteDocument document = QuoteDocument.builder().build();

        assertNull(document.getAmountTypeCode());
        assertNull(document.getId());
        assertNull(document.getDate());
        assertNull(document.getTransactionTypeCode());
        assertNull(document.getCurrencyCode());
        assertNull(document.getIsTransactionWithinTaxReportingGroup());
        assertNull(document.getCompanyInformation());
        assertNull(document.getItems());
        assertNull(document.getAdditionalInformation());
        assertNull(document.getParties());
        assertNull(document.getProducts());
    }

    @Test
    public void initializeFields() {
        String id = "1";
        Date date = new Date();
        CompanyInformation companyInformation = CompanyInformation.builder().build();

        QuoteDocument document = QuoteDocument.builder().withId(id).withDate(date)
                .withTransactionTypeCode(TransactionType.SALE).withAmountTypeCode(AmountType.GROSS)
                .withCurrencyCode(CurrencyCode.BRL).withIsTransactionWithinTaxReportingGroup(Boolean.TRUE)
                .withCompanyInformation(companyInformation).withItems(Collections.emptyList())
                .withAdditionalInformation(Collections.emptyMap()).withProducts(Collections.emptyList())
                .withParties(Collections.emptyList()).build();

        assertEquals(id, document.getId());
        assertEquals(date, document.getDate());
        assertEquals(TransactionType.SALE, document.getTransactionTypeCode());
        assertEquals(AmountType.GROSS, document.getAmountTypeCode());
        assertEquals(CurrencyCode.BRL, document.getCurrencyCode());
        assertTrue(document.getIsTransactionWithinTaxReportingGroup());
        assertEquals(companyInformation, document.getCompanyInformation());
        assertThat(document.getItems(), is(empty()));
        assertEquals(0, document.getAdditionalInformation().size());
        assertThat(document.getProducts(), is(empty()));
        assertThat(document.getParties(), is(empty()));
    }

    @Test
    public void serializeAndDeserialize() throws IOException {
        QuoteDocument quoteDocument = this.getDefaultQuoteDocument();
        String serialized = mapper.writeValueAsString(quoteDocument);
        QuoteDocument deserialized = mapper.readValue(serialized, QuoteDocument.class);

        assertEquals(quoteDocument, deserialized);
    }

    @Test
    public void initializeItemsWithVarArg() {
        QuoteDocument document = QuoteDocument.builder().withItems(Item.builder().build())
                .withParties(Party.builder().build()).withProducts(Product.builder().build()).build();
        assertThat(document.getItems(), hasSize(1));
        assertThat(document.getProducts(), hasSize(1));
        assertThat(document.getParties(), hasSize(1));
    }

    @Test
    public void nullValidation() {
        try {
            QuoteDocument.builder().build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat("must not be null", e.getPropertyErrors(), hasMandatoryItems(ID, DATE, TRANSACTION_TYPE_CODE,
                    AMOUNT_TYPE_CODE, CURRENCY_CODE, COMPANY_INFORMATION, ITEMS, PARTIES, PRODUCTS));
        }
    }

    @Test
    public void emptyValidation() {
        try {
            QuoteDocument.builder().withId("").withItems(Collections.emptyList()).withProducts(Collections.emptyList())
                    .withParties(Collections.emptyList()).build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat("must not be empty", e.getPropertyErrors(), hasMandatoryItems(ID, ITEMS, PRODUCTS, PARTIES));
        }
    }

    @Test
    public void idValidation() {
        try {
            QuoteDocument.builder().withId("     ").build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat("must not be blank", e.getPropertyErrors(), hasMandatoryItems(ID));
        }
    }

    @Test
    public void itemsNullElementsValidation() {
        try {
            QuoteDocument.builder().withCompanyInformation(null).withItems(null, null).withProducts(null, null)
                    .withParties(null, null).build().validate();
            fail();

        } catch (InvalidModelException e) {
            assertThat("elements must not be null", e.getPropertyErrors(),
                    hasMandatoryItems(COMPANY_INFORMATION, ITEMS, PARTIES, PRODUCTS));
        }
    }

    @Test
    public void validateWithoutErrors() {
        CompanyInformation companyInformation = CompanyInformation.builder().withAssignedPartyId("1").build();

        Item item = Item.builder().withId("id").withAssignedProductId("010").withQuantity(BigDecimal.ZERO)
                .withUnitPrice(BigDecimal.TEN)
                .withAssignedParties(AssignedParty.builder().withId("1").withRole(PartyRole.SHIP_FROM).build(),
                        AssignedParty.builder().withId("2").withRole(PartyRole.SHIP_TO).build())
                .build();

        Product product = Product.builder().withId("010").withTypeCode(ProductType.MATERIAL).build();

        Party firstParty = Party.builder().withId("1").withCountryRegionCode(CountryCode.US).build();
        Party secondParty = Party.builder().withId("2").withCountryRegionCode(CountryCode.US).build();

        QuoteDocument.builder().withId("1").withDate(new Date()).withTransactionTypeCode(TransactionType.SALE)
                .withAmountTypeCode(AmountType.NET).withCurrencyCode(CurrencyCode.USD)
                .withCompanyInformation(companyInformation).withItems(item).withProducts(product)
                .withParties(firstParty, secondParty).build().validate();

        assertTrue(true);
    }

    @Test
    public void validateAssignedProductIdReference() {
        CompanyInformation companyInformation = CompanyInformation.builder().withAssignedPartyId("1").build();

        Item firstItem = Item.builder().withId("10").withAssignedProductId("1").withQuantity(BigDecimal.ZERO)
                .withUnitPrice(BigDecimal.TEN)
                .withAssignedParties(AssignedParty.builder().withId("1").withRole(PartyRole.SHIP_FROM).build(),
                        AssignedParty.builder().withId("2").withRole(PartyRole.SHIP_TO).build())
                .build();

        Item secondItem = Item.builder().withId("20").withAssignedProductId("2").withQuantity(BigDecimal.ZERO)
                .withUnitPrice(BigDecimal.TEN)
                .withAssignedParties(AssignedParty.builder().withId("1").withRole(PartyRole.SHIP_FROM).build(),
                        AssignedParty.builder().withId("2").withRole(PartyRole.SHIP_TO).build())
                .build();

        Product dummyProduct = Product.builder().withId("10").withTypeCode(ProductType.MATERIAL).build();

        Party firstParty = Party.builder().withId("1").withCountryRegionCode(CountryCode.US).build();
        Party secondParty = Party.builder().withId("2").withCountryRegionCode(CountryCode.US).build();

        try {
            QuoteDocument.builder().withId("1").withDate(new Date()).withTransactionTypeCode(TransactionType.SALE)
                    .withAmountTypeCode(AmountType.NET).withCurrencyCode(CurrencyCode.USD)
                    .withCompanyInformation(companyInformation).withItems(firstItem, secondItem)
                    .withProducts(dummyProduct).withParties(firstParty, secondParty).build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertEquals(2, e.getPropertyErrors().size());
        }

    }

    @Test
    public void validateNullProductsAndNullAssignedParty() {
        CompanyInformation companyInformation = CompanyInformation.builder().withAssignedPartyId("2").build();

        Item item = Item.builder()
                .withId("1")
                .withAssignedProductId("1")
                .withQuantity(new BigDecimal(2.987897686))
                .withUnitPrice(new BigDecimal(100.123213123123))
                .build();

        Party party1 = Party.builder().withId("1").withCountryRegionCode(CountryCode.GB).build();
        Party party2 = Party.builder().withId("2").withCountryRegionCode(CountryCode.GB).build();

        try {
            QuoteDocument.builder()
                    .withId("1111110164532745327")
                    .withDate(new Date())
                    .withTransactionTypeCode(TransactionType.PURCHASE)
                    .withAmountTypeCode(AmountType.NET)
                    .withCurrencyCode(CurrencyCode.CAD)
                    .withCompanyInformation(companyInformation)
                    .withItems(item)
                    .withParties(party1, party2)
                    .build()
                    .validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat("must not be null", e.getPropertyErrors(), hasMandatoryItems(PRODUCTS));
        }
    }    
    
    @Test
    public void validateAssignedPartiesReference() {
        CompanyInformation companyInformation = CompanyInformation.builder().withAssignedPartyId("1").build();

        Item firstItem = Item.builder().withId("10").withAssignedProductId("1").withQuantity(BigDecimal.ZERO)
                .withUnitPrice(BigDecimal.TEN)
                .withAssignedParties(AssignedParty.builder().withId("1").withRole(PartyRole.SHIP_FROM).build(),
                        AssignedParty.builder().withId("2").withRole(PartyRole.SHIP_TO).build())
                .build();

        Item secondItem = Item.builder().withId("20").withAssignedProductId("2").withQuantity(BigDecimal.ZERO)
                .withUnitPrice(BigDecimal.TEN)
                .withAssignedParties(AssignedParty.builder().withId("1").withRole(PartyRole.SHIP_FROM).build(),
                        AssignedParty.builder().withId("2").withRole(PartyRole.SHIP_TO).build())
                .build();

        Product firstProduct = Product.builder().withId("1").withTypeCode(ProductType.MATERIAL).build();
        Product secondProduct = Product.builder().withId("2").withTypeCode(ProductType.MATERIAL).build();

        Party party = Party.builder().withId("10").withCountryRegionCode(CountryCode.US).build();

        try {
            QuoteDocument document = QuoteDocument.builder().withId("1").withDate(new Date()).withTransactionTypeCode(TransactionType.SALE)
                    .withAmountTypeCode(AmountType.NET).withCurrencyCode(CurrencyCode.USD)
                    .withCompanyInformation(companyInformation).withItems(firstItem, secondItem)
                    .withProducts(firstProduct, secondProduct).withParties(party).build();
            document.validate();
            fail();
        } catch (InvalidModelException e) {
            assertEquals(5, e.getPropertyErrors().size());
        }

    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(QuoteDocument.class)
            .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
            .verify();
    }

    @Test
    public void testNumberInBooleanDeserialization() throws IOException {
        expectedException.expect(MismatchedInputException.class);
        String json = "{\"isTransactionWithinTaxReportingGroup\":100}";
        mapper.readValue(json, QuoteDocument.class);
    }

    @Test
    public void testUnknowPropertyError() throws IOException {
        expectedException.expect(UnrecognizedPropertyException.class);
        String json = "{\"taxEventCode\":\"abc\"}";
        mapper.readValue(json, QuoteDocument.class);
    }

    @Test
    public void testUnknowPropertyWithoutError() throws IOException {
        String json = "{\"taxEventCode\":\"abc\"}";
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        QuoteDocument quote = mapper.readValue(json, QuoteDocument.class);
        assertNull(quote.getTransactionTypeCode());
    }

    private QuoteDocument getDefaultQuoteDocument() {
        return QuoteDocument.builder().withId("id").withDate(new Date()).withTransactionTypeCode(TransactionType.SALE)
                .withAmountTypeCode(AmountType.GROSS).withCurrencyCode(CurrencyCode.AED)
                .withIsTransactionWithinTaxReportingGroup(false).withCompanyInformation(null)
                .withItems(Collections.emptyList()).withAdditionalInformation(null)
                .withProducts(Collections.emptyList()).withParties(Collections.emptyList()).build();
    }
}
