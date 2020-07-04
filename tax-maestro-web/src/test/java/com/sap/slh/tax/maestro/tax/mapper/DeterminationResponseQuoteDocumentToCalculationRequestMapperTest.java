package com.sap.slh.tax.maestro.tax.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.sap.slh.tax.attributes.determination.model.response.ItemBuilder;
import com.sap.slh.tax.attributes.determination.model.response.ResponseItem;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponse;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseBuilder;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModelBuilder;
import com.sap.slh.tax.attributes.determination.model.response.TaxLine;
import com.sap.slh.tax.attributes.determination.model.response.TaxLineBuilder;
import com.sap.slh.tax.calculation.model.common.Item;
import com.sap.slh.tax.calculation.model.common.TaxCalculationRequest;
import com.sap.slh.tax.maestro.api.common.domain.CurrencyCode;
import com.sap.slh.tax.maestro.api.v1.domain.AmountType;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;

public class DeterminationResponseQuoteDocumentToCalculationRequestMapperTest {

    private DeterminationResponseQuoteDocumentToCalculationRequestMapper mapper = DeterminationResponseQuoteDocumentToCalculationRequestMapper
            .getInstance();

    @Test
    public void emptyMapping() {
        TaxAttributesDeterminationResponseModel determineResponse = TaxAttributesDeterminationResponseModelBuilder
                .builder()
                .withResult(TaxAttributesDeterminationResponseBuilder.builder().build())
                .build();

        QuoteDocument quoteDocument = QuoteDocument.builder().build();

        TaxCalculationRequest actualOutput = mapper.apply(determineResponse, quoteDocument);

        TaxCalculationRequest expectedOutput = new TaxCalculationRequest();

        assertThat(actualOutput).usingRecursiveComparison().isEqualTo(expectedOutput);
    }

    @Test
    public void singleItemMapping() {
        TaxLine taxLine = TaxLineBuilder.builder()
                .withTaxTypeCode("type")
                .withTaxRate(BigDecimal.TEN)
                .withDueCategoryCode("PAYABLE")
                .withNonDeductibleTaxRate(BigDecimal.TEN)
                .withIsReverseChargeRelevant(false)
                .build();

        ResponseItem detItem = ItemBuilder.builder()
                .withId("itemId")
                .withCountryRegionCode("BR")
                .withTaxEventTypeCode("type")
                .withIsTaxEventNonTaxable(false)
                .withTaxes(taxLine)
                .build();

        TaxAttributesDeterminationResponse detResponse = TaxAttributesDeterminationResponseBuilder.builder()
                .withId("id")
                .withItems(detItem)
                .build();

        TaxAttributesDeterminationResponseModel determineResponse = new TaxAttributesDeterminationResponseModel();
        determineResponse.setResult(detResponse);

        QuoteDocument quoteDocument = buildQuoteDocumentWithItems(Arrays.asList(com.sap.slh.tax.maestro.api.v1.schema.Item.builder()
                .withId("itemId")
                .withUnitPrice(BigDecimal.TEN)
                .withQuantity(BigDecimal.TEN)
                .build()));

        TaxCalculationRequest actualOutput = mapper.apply(determineResponse, quoteDocument);

        TaxCalculationRequest expectedOutput = new TaxCalculationRequest();
        expectedOutput.setId("id");
        expectedOutput.setAmountTypeCode(TaxCalculationRequest.AmountTypeCode.GROSS);
        expectedOutput.setCurrencyCode(TaxCalculationRequest.CurrencyCode.BRL);

        Item calcItem1 = new Item();
        calcItem1.setId("itemId");
        calcItem1.setQuantity(BigDecimal.TEN);
        calcItem1.setCountryRegionCode(Item.CountryRegionCode.BR);
        calcItem1.setUnitPrice(BigDecimal.TEN);
        calcItem1.setTaxEventCode("type");

        com.sap.slh.tax.calculation.model.common.TaxLine calcTaxLine1 = new com.sap.slh.tax.calculation.model.common.TaxLine();
        calcTaxLine1.setTaxTypeCode("type");
        calcTaxLine1.setTaxRate(BigDecimal.TEN.doubleValue());
        calcTaxLine1.setDueCategoryCode(com.sap.slh.tax.calculation.model.common.TaxLine.DueCategoryCode.PAYABLE);
        calcTaxLine1.setNonDeductibleTaxRate(BigDecimal.TEN.doubleValue());
        calcTaxLine1.setIsReverseChargeRelevant(false);

        calcItem1.setTaxes(Arrays.asList(calcTaxLine1));

        expectedOutput.setItems(Arrays.asList(calcItem1));

        assertThat(actualOutput).usingRecursiveComparison().isEqualTo(expectedOutput);
    }

    @Test
    public void multipleTaxLinesMapping() {
        TaxLine taxLine1 = TaxLineBuilder.builder()
                .withTaxTypeCode("type")
                .withTaxRate(BigDecimal.TEN)
                .withDueCategoryCode("PAYABLE")
                .withNonDeductibleTaxRate(BigDecimal.TEN)
                .withIsReverseChargeRelevant(true)
                .build();

        TaxLine taxLine2 = TaxLineBuilder.builder()
                .withTaxTypeCode("type2")
                .withTaxRate(BigDecimal.ONE)
                .withDueCategoryCode("RECEIVABLE")
                .withNonDeductibleTaxRate(BigDecimal.ONE)
                .withIsReverseChargeRelevant(null)
                .build();

        ResponseItem detItem = ItemBuilder.builder()
                .withId("itemId")
                .withCountryRegionCode("BR")
                .withTaxEventTypeCode("type")
                .withIsTaxEventNonTaxable(false)
                .withTaxes(taxLine1, taxLine2)
                .build();

        TaxAttributesDeterminationResponse detResponse = TaxAttributesDeterminationResponseBuilder.builder()
                .withId("id1")
                .withItems(detItem)
                .build();

        TaxAttributesDeterminationResponseModel input = TaxAttributesDeterminationResponseModelBuilder.builder()
                .withResult(detResponse)
                .build();

        QuoteDocument quoteDocument = buildQuoteDocumentWithItems(Arrays.asList(com.sap.slh.tax.maestro.api.v1.schema.Item.builder()
                .withId("itemId")
                .withUnitPrice(BigDecimal.TEN)
                .withQuantity(BigDecimal.TEN)
                .build()));

        TaxCalculationRequest actualOutput = mapper.apply(input, quoteDocument);

        TaxCalculationRequest expectedOutput = new TaxCalculationRequest();
        expectedOutput.setId("id1");
        expectedOutput.setAmountTypeCode(TaxCalculationRequest.AmountTypeCode.GROSS);
        expectedOutput.setCurrencyCode(TaxCalculationRequest.CurrencyCode.BRL);

        Item calcItem1 = new Item();
        calcItem1.setId("itemId");
        calcItem1.setQuantity(BigDecimal.TEN);
        calcItem1.setCountryRegionCode(Item.CountryRegionCode.BR);
        calcItem1.setUnitPrice(BigDecimal.TEN);
        calcItem1.setTaxEventCode("type");

        com.sap.slh.tax.calculation.model.common.TaxLine calcTaxLine1 = new com.sap.slh.tax.calculation.model.common.TaxLine();
        calcTaxLine1.setTaxTypeCode("type");
        calcTaxLine1.setTaxRate(BigDecimal.TEN.doubleValue());
        calcTaxLine1.setDueCategoryCode(com.sap.slh.tax.calculation.model.common.TaxLine.DueCategoryCode.PAYABLE);
        calcTaxLine1.setNonDeductibleTaxRate(BigDecimal.TEN.doubleValue());
        calcTaxLine1.setIsReverseChargeRelevant(true);

        com.sap.slh.tax.calculation.model.common.TaxLine calcTaxLine2 = new com.sap.slh.tax.calculation.model.common.TaxLine();
        calcTaxLine2.setTaxTypeCode("type2");
        calcTaxLine2.setTaxRate(BigDecimal.ONE.doubleValue());
        calcTaxLine2.setDueCategoryCode(com.sap.slh.tax.calculation.model.common.TaxLine.DueCategoryCode.RECEIVABLE);
        calcTaxLine2.setNonDeductibleTaxRate(BigDecimal.ONE.doubleValue());

        calcItem1.setTaxes(Arrays.asList(calcTaxLine1, calcTaxLine2));

        expectedOutput.setItems(Arrays.asList(calcItem1));

        assertThat(actualOutput).usingRecursiveComparison().isEqualTo(expectedOutput);
    }

    @Test
    public void multipleItemsMapping() {
        TaxLine taxLine1 = TaxLineBuilder.builder()
                .withTaxTypeCode("type")
                .withTaxRate(BigDecimal.TEN)
                .withDueCategoryCode("PAYABLE")
                .withNonDeductibleTaxRate(BigDecimal.TEN)
                .withIsReverseChargeRelevant(true)
                .build();

        TaxLine taxLine2 = TaxLineBuilder.builder()
                .withTaxTypeCode("type2")
                .withTaxRate(BigDecimal.ONE)
                .withDueCategoryCode("RECEIVABLE")
                .withNonDeductibleTaxRate(BigDecimal.ONE)
                .build();

        ResponseItem detItem1 = ItemBuilder.builder()
                .withId("itemId1")
                .withCountryRegionCode("BR")
                .withTaxEventTypeCode("type")
                .withIsTaxEventNonTaxable(false)
                .withTaxes(taxLine1)
                .build();

        ResponseItem detItem2 = ItemBuilder.builder()
                .withId("itemId2")
                .withCountryRegionCode("BE")
                .withTaxEventTypeCode("type2")
                .withIsTaxEventNonTaxable(true)
                .withTaxes(taxLine2)
                .build();

        TaxAttributesDeterminationResponse detResponse = TaxAttributesDeterminationResponseBuilder.builder()
                .withId("id")
                .withItems(detItem1, detItem2)
                .build();

        TaxAttributesDeterminationResponseModel input = TaxAttributesDeterminationResponseModelBuilder.builder()
                .withResult(detResponse)
                .build();

        QuoteDocument quoteDocument = buildQuoteDocumentWithItems(Arrays.asList(
                com.sap.slh.tax.maestro.api.v1.schema.Item.builder()
                        .withId("itemId1")
                        .withUnitPrice(BigDecimal.TEN)
                        .withQuantity(BigDecimal.TEN)
                        .build(),
                com.sap.slh.tax.maestro.api.v1.schema.Item.builder()
                        .withId("itemId2")
                        .withUnitPrice(BigDecimal.TEN)
                        .withQuantity(BigDecimal.TEN)
                        .build()));

        TaxCalculationRequest actualOutput = mapper.apply(input, quoteDocument);

        TaxCalculationRequest expectedOutput = new TaxCalculationRequest();
        expectedOutput.setId("id");
        expectedOutput.setAmountTypeCode(TaxCalculationRequest.AmountTypeCode.GROSS);
        expectedOutput.setCurrencyCode(TaxCalculationRequest.CurrencyCode.BRL);

        Item calcItem1 = new Item();
        calcItem1.setId("itemId1");
        calcItem1.setQuantity(BigDecimal.TEN);
        calcItem1.setCountryRegionCode(Item.CountryRegionCode.BR);
        calcItem1.setUnitPrice(BigDecimal.TEN);
        calcItem1.setTaxEventCode("type");

        com.sap.slh.tax.calculation.model.common.TaxLine calcTaxLine1 = new com.sap.slh.tax.calculation.model.common.TaxLine();
        calcTaxLine1.setTaxTypeCode("type");
        calcTaxLine1.setTaxRate(BigDecimal.TEN.doubleValue());
        calcTaxLine1.setDueCategoryCode(com.sap.slh.tax.calculation.model.common.TaxLine.DueCategoryCode.PAYABLE);
        calcTaxLine1.setNonDeductibleTaxRate(BigDecimal.TEN.doubleValue());
        calcTaxLine1.setIsReverseChargeRelevant(true);

        calcItem1.setTaxes(Arrays.asList(calcTaxLine1));

        Item calcItem2 = new Item();
        calcItem2.setId("itemId2");
        calcItem2.setQuantity(BigDecimal.TEN);
        calcItem2.setCountryRegionCode(Item.CountryRegionCode.BE);
        calcItem2.setUnitPrice(BigDecimal.TEN);
        calcItem2.setIsTaxEventNonTaxable(true);
        calcItem2.setTaxEventCode("type2");

        com.sap.slh.tax.calculation.model.common.TaxLine calcTaxLine2 = new com.sap.slh.tax.calculation.model.common.TaxLine();
        calcTaxLine2.setTaxTypeCode("type2");
        calcTaxLine2.setTaxRate(BigDecimal.ONE.doubleValue());
        calcTaxLine2.setDueCategoryCode(com.sap.slh.tax.calculation.model.common.TaxLine.DueCategoryCode.RECEIVABLE);
        calcTaxLine2.setNonDeductibleTaxRate(BigDecimal.ONE.doubleValue());

        calcItem2.setTaxes(Arrays.asList(calcTaxLine2));

        expectedOutput.setItems(Arrays.asList(calcItem1, calcItem2));

        assertThat(actualOutput).usingRecursiveComparison().isEqualTo(expectedOutput);
    }

    private QuoteDocument buildQuoteDocumentWithItems(List<com.sap.slh.tax.maestro.api.v1.schema.Item> items) {
        return QuoteDocument.builder()
                .withCurrencyCode(CurrencyCode.BRL)
                .withAmountTypeCode(AmountType.GROSS)
                .withItems(items)
                .build();
    }

}
