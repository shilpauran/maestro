package com.sap.slh.tax.maestro.tax.mapper;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.sap.slh.tax.attributes.determination.model.response.ResponseItem;
import com.sap.slh.tax.attributes.determination.model.response.ItemBuilder;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponse;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseBuilder;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModelBuilder;
import com.sap.slh.tax.attributes.determination.model.response.TaxLine;
import com.sap.slh.tax.attributes.determination.model.response.TaxLineBuilder;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.v1.domain.DueCategory;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteResultDocument;
import com.sap.slh.tax.maestro.api.v1.schema.ResultDocumentItem;
import com.sap.slh.tax.maestro.api.v1.schema.TaxResultItem;
import com.sap.slh.tax.maestro.tax.exceptions.OutOfBoundsEnumMappingException;

public class DeterminationResponseToQuoteResultDocumentMapperTest {

    DeterminationResponseToQuoteResultDocumentMapper mapper = DeterminationResponseToQuoteResultDocumentMapper
            .getInstance();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void basicDocument() {
        TaxLine taxLine = TaxLineBuilder.builder()
                .withTaxTypeCode("taxType")
                .withTaxRate(BigDecimal.valueOf(10))
                .withDueCategoryCode("PAYABLE")
                .withIsTaxDeferred(Boolean.TRUE)
                .withNonDeductibleTaxRate(BigDecimal.valueOf(20))
                .withId("taxId")
                .build();
        ResponseItem item = ItemBuilder.builder()
                .withCountryRegionCode("BR")
                .withSubdivisionCode("SUBDIVISION_CODE")
                .withTaxEventTypeCode("A_CODE")
                .withTaxEventLegalPhrase("This is a legal phrase")
                .withTaxes(taxLine)
                .withTaxCode("TAX_CODE")
                .withId("itemId")
                .build();
        TaxAttributesDeterminationResponse taxAttributesDeterminationResponse = TaxAttributesDeterminationResponseBuilder
                .builder()
                .withItems(item)
                .build();
        TaxAttributesDeterminationResponseModel taxAttributesDeterminationResponseModel = TaxAttributesDeterminationResponseModelBuilder
                .builder()
                .withResult(taxAttributesDeterminationResponse)
                .build();

        TaxResultItem taxResultItem = TaxResultItem.builder()
                .withTaxTypeCode("taxType")
                .withTaxRate(BigDecimal.valueOf(10))
                .withDueCategoryCode(DueCategory.PAYABLE)
                .withIsTaxDeferred(Boolean.TRUE)
                .withNonDeductibleTaxRate(BigDecimal.valueOf(20))
                .withId("taxId")
                .build();
        ResultDocumentItem resultDocumentItem = ResultDocumentItem.builder()
                .withCountryRegionCode(CountryCode.BR)
                .withSubdivisionCode("SUBDIVISION_CODE")
                .withTaxes(taxResultItem)
                .withTaxEventCode("A_CODE")
                .withTaxEventLegalPhrase("This is a legal phrase")
                .addAdditionalInformation("taxCode", "TAX_CODE")
                .withId("itemId")
                .build();
        QuoteResultDocument expectedResult = QuoteResultDocument.builder().withItems(resultDocumentItem).build();

        QuoteResultDocument resultDocument = mapper.apply(taxAttributesDeterminationResponseModel);
        assertEquals(expectedResult, resultDocument);
    }

    @Test
    public void documentWithThreeItemsAndThreeTaxLines() {
        TaxLine taxLine32 = TaxLineBuilder.builder()
                .withTaxTypeCode("32")
                .withTaxRate(BigDecimal.valueOf(32))
                .withDueCategoryCode("PAYABLE")
                .withIsTaxDeferred(Boolean.FALSE)
                .withNonDeductibleTaxRate(BigDecimal.valueOf(320))
                .build();
        TaxLine taxLine31 = TaxLineBuilder.builder()
                .withTaxTypeCode("31")
                .withTaxRate(BigDecimal.valueOf(31))
                .withDueCategoryCode("RECEIVABLE")
                .withIsTaxDeferred(Boolean.FALSE)
                .withNonDeductibleTaxRate(BigDecimal.valueOf(310))
                .build();
        ResponseItem item3 = ItemBuilder.builder()
                .withCountryRegionCode("US")
                .withTaxEventTypeCode("3")
                .withTaxEventLegalPhrase("This is a legal phrase for item 3")
                .withTaxes(taxLine31, taxLine32)
                .build();        
        TaxLine taxLine22 = TaxLineBuilder.builder()
                .withTaxTypeCode("22")
                .withTaxRate(BigDecimal.valueOf(22))
                .withDueCategoryCode("PAYABLE")
                .withIsTaxDeferred(Boolean.FALSE)
                .withNonDeductibleTaxRate(BigDecimal.valueOf(220))
                .build();
        TaxLine taxLine21 = TaxLineBuilder.builder()
                .withTaxTypeCode("21")
                .withTaxRate(BigDecimal.valueOf(21))
                .withDueCategoryCode("RECEIVABLE")
                .withIsTaxDeferred(Boolean.FALSE)
                .withNonDeductibleTaxRate(BigDecimal.valueOf(210))
                .build();
        ResponseItem item2 = ItemBuilder.builder()
                .withCountryRegionCode("US")
                .withTaxEventTypeCode("2")
                .withTaxEventLegalPhrase("This is a legal phrase for item 2")
                .withTaxes(taxLine21, taxLine22)
                .withTaxCode("")
                .build();
        TaxLine taxLine11 = TaxLineBuilder.builder()
                .withTaxTypeCode("11")
                .withTaxRate(BigDecimal.valueOf(11))
                .withDueCategoryCode("PAYABLE")
                .withIsTaxDeferred(Boolean.TRUE)
                .withNonDeductibleTaxRate(BigDecimal.valueOf(110))
                .build();
        ResponseItem item1 = ItemBuilder.builder()
                .withCountryRegionCode("BR")
                .withTaxEventTypeCode("1")
                .withTaxEventLegalPhrase("This is a legal phrase for item 1")
                .withTaxes(taxLine11)
                .withTaxCode("1")
                .build();
        TaxAttributesDeterminationResponse taxAttributesDeterminationResponse = TaxAttributesDeterminationResponseBuilder
                .builder()
                .withItems(item1, item2, item3)
                .build();
        TaxAttributesDeterminationResponseModel determinationResponse = TaxAttributesDeterminationResponseModelBuilder
                .builder()
                .withResult(taxAttributesDeterminationResponse)
                .build();

        TaxResultItem taxResultItem32 = TaxResultItem.builder()
                .withTaxTypeCode("32")
                .withTaxRate(BigDecimal.valueOf(32))
                .withDueCategoryCode(DueCategory.PAYABLE)
                .withIsTaxDeferred(Boolean.FALSE)
                .withNonDeductibleTaxRate(BigDecimal.valueOf(320))
                .build();
        TaxResultItem taxResultItem31 = TaxResultItem.builder()
                .withTaxTypeCode("31")
                .withTaxRate(BigDecimal.valueOf(31))
                .withDueCategoryCode(DueCategory.RECEIVABLE)
                .withIsTaxDeferred(Boolean.FALSE)
                .withNonDeductibleTaxRate(BigDecimal.valueOf(310))
                .build();
        ResultDocumentItem resultDocumentItem3 = ResultDocumentItem.builder()
                .withCountryRegionCode(CountryCode.US)
                .withTaxes(taxResultItem31, taxResultItem32)
                .withTaxEventCode("3")
                .withTaxEventLegalPhrase("This is a legal phrase for item 3")
                .build();
        TaxResultItem taxResultItem22 = TaxResultItem.builder()
                .withTaxTypeCode("22")
                .withTaxRate(BigDecimal.valueOf(22))
                .withDueCategoryCode(DueCategory.PAYABLE)
                .withIsTaxDeferred(Boolean.FALSE)
                .withNonDeductibleTaxRate(BigDecimal.valueOf(220))
                .build();
        TaxResultItem taxResultItem21 = TaxResultItem.builder()
                .withTaxTypeCode("21")
                .withTaxRate(BigDecimal.valueOf(21))
                .withDueCategoryCode(DueCategory.RECEIVABLE)
                .withIsTaxDeferred(Boolean.FALSE)
                .withNonDeductibleTaxRate(BigDecimal.valueOf(210))
                .build();
        ResultDocumentItem resultDocumentItem2 = ResultDocumentItem.builder()
                .withCountryRegionCode(CountryCode.US)
                .withTaxes(taxResultItem21, taxResultItem22)
                .withTaxEventCode("2")
                .withTaxEventLegalPhrase("This is a legal phrase for item 2")
                .addAdditionalInformation("taxCode", "")
                .build();
        TaxResultItem taxResultItem11 = TaxResultItem.builder()
                .withTaxTypeCode("11")
                .withTaxRate(BigDecimal.valueOf(11))
                .withDueCategoryCode(DueCategory.PAYABLE)
                .withIsTaxDeferred(Boolean.TRUE)
                .withNonDeductibleTaxRate(BigDecimal.valueOf(110))
                .build();
        ResultDocumentItem resultDocumentItem1 = ResultDocumentItem.builder()
                .withCountryRegionCode(CountryCode.BR)
                .withTaxes(taxResultItem11)
                .withTaxEventCode("1")
                .withTaxEventLegalPhrase("This is a legal phrase for item 1")
                .addAdditionalInformation("taxCode", "1")
                .build();
        QuoteResultDocument expectedResult = QuoteResultDocument.builder()
                .withItems(resultDocumentItem1, resultDocumentItem2, resultDocumentItem3)
                .build();

        QuoteResultDocument resultDocument = mapper.apply(determinationResponse);
        assertEquals(expectedResult, resultDocument);
    }

    @Test
    public void noResults() {
        TaxAttributesDeterminationResponseModel determinationResponse =
                TaxAttributesDeterminationResponseModelBuilder
                        .builder()
                        .build();

        QuoteResultDocument expectedResult = QuoteResultDocument.builder().build();

        QuoteResultDocument resultDocument = mapper.apply(determinationResponse);
        assertEquals(expectedResult, resultDocument);
    }

    @Test
    public void noTaxLinesAndAdditionalAttributesAndTaxCountry() {
        TaxAttributesDeterminationResponse taxAttributesDeterminationResponse =
                TaxAttributesDeterminationResponseBuilder
                        .builder()
                        .build();
        TaxAttributesDeterminationResponseModel determinationResponse =
                TaxAttributesDeterminationResponseModelBuilder
                        .builder()
                        .withResult(taxAttributesDeterminationResponse)
                        .build();

        QuoteResultDocument expectedResult = QuoteResultDocument.builder().build();

        QuoteResultDocument resultDocument = mapper.apply(determinationResponse);
        assertEquals(expectedResult, resultDocument);
    }

    @Test
    public void noItem() {
        ResponseItem item = ItemBuilder.builder().build();
        TaxAttributesDeterminationResponse taxAttributesDeterminationResponse =
                TaxAttributesDeterminationResponseBuilder
                        .builder()
                        .withItems(item)
                        .build();
        TaxAttributesDeterminationResponseModel determinationResponse =
                TaxAttributesDeterminationResponseModelBuilder
                        .builder()
                        .withResult(taxAttributesDeterminationResponse)
                        .build();

        ResultDocumentItem resultDocumentItem = ResultDocumentItem.builder().build();
        QuoteResultDocument expectedResult = QuoteResultDocument.builder().withItems(resultDocumentItem).build();

        QuoteResultDocument resultDocument = mapper.apply(determinationResponse);
        assertEquals(expectedResult, resultDocument);
    }

    @Test
    public void invalidCountryRegionCode() {
        ResponseItem item = ItemBuilder.builder().withCountryRegionCode("THIS_IS_NOT_A_VALID_CONTRYREGION_CODE").build();
        TaxAttributesDeterminationResponse taxAttributesDeterminationResponse =
                TaxAttributesDeterminationResponseBuilder
                        .builder()
                        .withItems(item)
                        .build();
        TaxAttributesDeterminationResponseModel determinationResponse =
                TaxAttributesDeterminationResponseModelBuilder
                        .builder()
                        .withResult(taxAttributesDeterminationResponse)
                        .build();

        thrown.expect(OutOfBoundsEnumMappingException.class);
        thrown.expectMessage("Mapping error while transforming enum value THIS_IS_NOT_A_VALID_CONTRYREGION_CODE to enum com.sap.slh.tax.maestro.api.common.domain.CountryCode");
        mapper.apply(determinationResponse);
    }

    @Test
    public void noDueCategory() {
        TaxLine taxLine = TaxLineBuilder.builder().build();
        ResponseItem item = ItemBuilder.builder().withTaxes(taxLine).build();
        TaxAttributesDeterminationResponse taxAttributesDeterminationResponse =
                TaxAttributesDeterminationResponseBuilder
                        .builder()
                        .withItems(item)
                        .build();
        TaxAttributesDeterminationResponseModel determinationResponse =
                TaxAttributesDeterminationResponseModelBuilder
                        .builder()
                        .withResult(taxAttributesDeterminationResponse)
                        .build();

        TaxResultItem taxResultItem = TaxResultItem.builder().withIsTaxDeferred(Boolean.FALSE).build();
        ResultDocumentItem resultDocumentItem = ResultDocumentItem.builder().withTaxes(taxResultItem).build();
        QuoteResultDocument expectedResult = QuoteResultDocument.builder().withItems(resultDocumentItem).build();

        QuoteResultDocument resultDocument = mapper.apply(determinationResponse);
        assertEquals(expectedResult, resultDocument);
    }

    @Test
    public void invalidDueCategory() {
        TaxLine taxLine = TaxLineBuilder.builder().withDueCategoryCode("THIS_IS_NOT_A_DUE_CATEGORY").build();
        ResponseItem item = ItemBuilder.builder().withTaxes(taxLine).build();
        TaxAttributesDeterminationResponse taxAttributesDeterminationResponse =
                TaxAttributesDeterminationResponseBuilder
                        .builder()
                        .withItems(item)
                        .build();
        TaxAttributesDeterminationResponseModel determinationResponse =
                TaxAttributesDeterminationResponseModelBuilder
                        .builder()
                        .withResult(taxAttributesDeterminationResponse)
                        .build();

        thrown.expect(OutOfBoundsEnumMappingException.class);
        thrown.expectMessage("Mapping error while transforming enum value THIS_IS_NOT_A_DUE_CATEGORY to enum com.sap.slh.tax.maestro.api.v1.domain.DueCategory");
        mapper.apply(determinationResponse);
    }
}
