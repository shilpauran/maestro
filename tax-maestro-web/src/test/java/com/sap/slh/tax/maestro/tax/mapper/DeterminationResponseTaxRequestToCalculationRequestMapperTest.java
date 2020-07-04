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
import com.sap.slh.tax.maestro.api.v0.domain.GrossOrNet;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest;

public class DeterminationResponseTaxRequestToCalculationRequestMapperTest {

    private DeterminationResponseTaxRequestToCalculationRequestMapper mapper = DeterminationResponseTaxRequestToCalculationRequestMapper
            .getInstance();

    @Test
    public void emptyMapping() {
        TaxAttributesDeterminationResponseModel determineResponse = TaxAttributesDeterminationResponseModelBuilder
                .builder()
                .build();

        TaxRequest taxRequest = TaxRequest.builder().build();

        TaxCalculationRequest expectedOutput = null;

        TaxCalculationRequest actualOutput = mapper.apply(determineResponse, taxRequest);

        assertThat(actualOutput).usingRecursiveComparison().isEqualTo(expectedOutput);
    }

    @Test
    public void singleItemMapping() {
        TaxLine taxLine = TaxLineBuilder.builder()
                .withTaxTypeCode("type")
                .withTaxRate(BigDecimal.TEN)
                .withNonDeductibleTaxRate(BigDecimal.TEN)
                .withDueCategoryCode("PAYABLE")
                .withIsTaxDeferred(false)
                .withIsReverseChargeRelevant(false)
                .build();

        ResponseItem detItem = ItemBuilder.builder()
                .withId("itemId")
                .withCountryRegionCode("BR")
                .withTaxEventLegalPhrase("taxEventLegalPhrase")
                .withTaxes(taxLine)
                .build();

        TaxAttributesDeterminationResponse detResponse = TaxAttributesDeterminationResponseBuilder.builder()
                .withId("id")
                .withItems(detItem)
                .build();

        TaxAttributesDeterminationResponseModel determineResponse = TaxAttributesDeterminationResponseModelBuilder
                .builder()
                .withResult(detResponse)
                .build();

        TaxRequest taxRequest = buildTaxRequestWithItems(
                Arrays.asList(com.sap.slh.tax.maestro.api.v0.schema.Item.builder()
                        .withId("itemId")
                        .withQuantity(BigDecimal.TEN)
                        .withUnitPrice(BigDecimal.TEN)
                        .build()));

        TaxCalculationRequest actualOutput = mapper.apply(determineResponse, taxRequest);

        TaxCalculationRequest expectedOutput = new TaxCalculationRequest();
        expectedOutput.setId("id");
        expectedOutput.setAmountTypeCode(TaxCalculationRequest.AmountTypeCode.GROSS);
        expectedOutput.setCurrencyCode(TaxCalculationRequest.CurrencyCode.BRL);

        Item calcItem1 = new Item();
        calcItem1.setId("itemId");
        calcItem1.setQuantity(BigDecimal.TEN);
        calcItem1.setCountryRegionCode(Item.CountryRegionCode.BR);
        calcItem1.setUnitPrice(BigDecimal.TEN);

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
                .withNonDeductibleTaxRate(BigDecimal.TEN)
                .withDueCategoryCode("RECEIVABLE")
                .withIsTaxDeferred(false)
                .withIsReverseChargeRelevant(null)
                .build();

        TaxLine taxLine2 = TaxLineBuilder.builder()
                .withTaxTypeCode("type2")
                .withTaxRate(BigDecimal.ONE)
                .withNonDeductibleTaxRate(BigDecimal.ONE)
                .withDueCategoryCode("PAYABLE")
                .withIsTaxDeferred(true)
                .withIsReverseChargeRelevant(true)
                .build();

        ResponseItem detItem1 = ItemBuilder.builder()
                .withId("itemId1")
                .withCountryRegionCode("BR")
                .withTaxEventLegalPhrase("taxEventLegalPhrase")
                .withTaxes(taxLine1, taxLine2)
                .build();

        TaxAttributesDeterminationResponse detResponse = TaxAttributesDeterminationResponseBuilder.builder()
                .withId("id")
                .withItems(detItem1)
                .build();

        TaxAttributesDeterminationResponseModel input = TaxAttributesDeterminationResponseModelBuilder.builder()
                .withResult(detResponse)
                .build();

        TaxRequest taxRequest = buildTaxRequestWithItems(
                Arrays.asList(com.sap.slh.tax.maestro.api.v0.schema.Item.builder()
                        .withId("itemId1")
                        .withQuantity(BigDecimal.TEN)
                        .withUnitPrice(BigDecimal.TEN)
                        .build()));

        TaxCalculationRequest actualOutput = mapper.apply(input, taxRequest);

        TaxCalculationRequest expectedOutput = new TaxCalculationRequest();
        expectedOutput.setId("id");
        expectedOutput.setAmountTypeCode(TaxCalculationRequest.AmountTypeCode.GROSS);
        expectedOutput.setCurrencyCode(TaxCalculationRequest.CurrencyCode.BRL);

        Item calcItem1 = new Item();
        calcItem1.setId("itemId1");
        calcItem1.setQuantity(BigDecimal.TEN);
        calcItem1.setCountryRegionCode(Item.CountryRegionCode.BR);
        calcItem1.setUnitPrice(BigDecimal.TEN);

        com.sap.slh.tax.calculation.model.common.TaxLine calcTaxLine1 = new com.sap.slh.tax.calculation.model.common.TaxLine();
        calcTaxLine1.setTaxTypeCode("type");
        calcTaxLine1.setTaxRate(BigDecimal.TEN.doubleValue());
        calcTaxLine1.setDueCategoryCode(com.sap.slh.tax.calculation.model.common.TaxLine.DueCategoryCode.RECEIVABLE);
        calcTaxLine1.setNonDeductibleTaxRate(BigDecimal.TEN.doubleValue());
        calcTaxLine1.setIsReverseChargeRelevant(false);

        com.sap.slh.tax.calculation.model.common.TaxLine calcTaxLine2 = new com.sap.slh.tax.calculation.model.common.TaxLine();
        calcTaxLine2.setTaxTypeCode("type2");
        calcTaxLine2.setTaxRate(BigDecimal.ONE.doubleValue());
        calcTaxLine2.setDueCategoryCode(com.sap.slh.tax.calculation.model.common.TaxLine.DueCategoryCode.PAYABLE);
        calcTaxLine2.setNonDeductibleTaxRate(BigDecimal.ONE.doubleValue());
        calcTaxLine2.setIsReverseChargeRelevant(true);

        calcItem1.setTaxes(Arrays.asList(calcTaxLine1, calcTaxLine2));

        expectedOutput.setItems(Arrays.asList(calcItem1));

        assertThat(actualOutput).usingRecursiveComparison().isEqualTo(expectedOutput);
    }

    @Test
    public void multipleItemsMapping() {
        TaxLine taxLine1 = TaxLineBuilder.builder()
                .withTaxTypeCode("type")
                .withTaxRate(BigDecimal.TEN)
                .withNonDeductibleTaxRate(BigDecimal.TEN)
                .withDueCategoryCode("PAYABLE")
                .withIsTaxDeferred(false)
                .withIsReverseChargeRelevant(false)
                .build();

        TaxLine taxLine2 = TaxLineBuilder.builder()
                .withTaxTypeCode("type2")
                .withTaxRate(BigDecimal.ONE)
                .withNonDeductibleTaxRate(BigDecimal.ONE)
                .withDueCategoryCode("RECEIVABLE")
                .withIsTaxDeferred(true)
                .build();

        ResponseItem detItem1 = ItemBuilder.builder()
                .withId("itemId1")
                .withCountryRegionCode("BR")
                .withTaxEventLegalPhrase("taxEventLegalPhrase")
                .withTaxes(taxLine1)
                .build();

        ResponseItem detItem2 = ItemBuilder.builder()
                .withId("itemId2")
                .withCountryRegionCode("BR")
                .withTaxEventLegalPhrase("taxEventLegalPhrase")
                .withTaxes(taxLine2)
                .build();

        TaxAttributesDeterminationResponse detResponse = TaxAttributesDeterminationResponseBuilder.builder()
                .withId("id")
                .withItems(detItem1, detItem2)
                .build();

        TaxAttributesDeterminationResponseModel input = TaxAttributesDeterminationResponseModelBuilder.builder()
                .withResult(detResponse)
                .build();

        TaxRequest taxRequest = buildTaxRequestWithItems(Arrays.asList(
                com.sap.slh.tax.maestro.api.v0.schema.Item.builder()
                        .withId("itemId1")
                        .withQuantity(BigDecimal.TEN)
                        .withUnitPrice(BigDecimal.TEN)
                        .build(),
                com.sap.slh.tax.maestro.api.v0.schema.Item.builder()
                        .withId("itemId2")
                        .withQuantity(BigDecimal.TEN)
                        .withUnitPrice(BigDecimal.TEN)
                        .build()));

        TaxCalculationRequest actualOutput = mapper.apply(input, taxRequest);

        TaxCalculationRequest expectedOutput = new TaxCalculationRequest();
        expectedOutput.setId("id");
        expectedOutput.setAmountTypeCode(TaxCalculationRequest.AmountTypeCode.GROSS);
        expectedOutput.setCurrencyCode(TaxCalculationRequest.CurrencyCode.BRL);

        Item calcItem1 = new Item();
        calcItem1.setId("itemId1");
        calcItem1.setQuantity(BigDecimal.TEN);
        calcItem1.setCountryRegionCode(Item.CountryRegionCode.BR);
        calcItem1.setUnitPrice(BigDecimal.TEN);

        com.sap.slh.tax.calculation.model.common.TaxLine calcTaxLine1 = new com.sap.slh.tax.calculation.model.common.TaxLine();
        calcTaxLine1.setTaxTypeCode("type");
        calcTaxLine1.setTaxRate(BigDecimal.TEN.doubleValue());
        calcTaxLine1.setDueCategoryCode(com.sap.slh.tax.calculation.model.common.TaxLine.DueCategoryCode.PAYABLE);
        calcTaxLine1.setNonDeductibleTaxRate(BigDecimal.TEN.doubleValue());
        calcTaxLine1.setIsReverseChargeRelevant(false);

        calcItem1.setTaxes(Arrays.asList(calcTaxLine1));

        Item calcItem2 = new Item();
        calcItem2.setId("itemId2");
        calcItem2.setQuantity(BigDecimal.TEN);
        calcItem2.setCountryRegionCode(Item.CountryRegionCode.BR);
        calcItem2.setUnitPrice(BigDecimal.TEN);

        com.sap.slh.tax.calculation.model.common.TaxLine calcTaxLine2 = new com.sap.slh.tax.calculation.model.common.TaxLine();
        calcTaxLine2.setTaxTypeCode("type2");
        calcTaxLine2.setTaxRate(BigDecimal.ONE.doubleValue());
        calcTaxLine2.setDueCategoryCode(com.sap.slh.tax.calculation.model.common.TaxLine.DueCategoryCode.RECEIVABLE);
        calcTaxLine2.setNonDeductibleTaxRate(BigDecimal.ONE.doubleValue());

        calcItem2.setTaxes(Arrays.asList(calcTaxLine2));

        expectedOutput.setItems(Arrays.asList(calcItem1, calcItem2));

        assertThat(actualOutput).usingRecursiveComparison().isEqualTo(expectedOutput);
    }

    private TaxRequest buildTaxRequestWithItems(List<com.sap.slh.tax.maestro.api.v0.schema.Item> items) {
        return TaxRequest.builder()
                .withCurrency(CurrencyCode.BRL)
                .withCashDiscountPercent(BigDecimal.TEN.toString())
                .withGrossOrNet(GrossOrNet.G)
                .withItems(items)
                .build();
    }
}
