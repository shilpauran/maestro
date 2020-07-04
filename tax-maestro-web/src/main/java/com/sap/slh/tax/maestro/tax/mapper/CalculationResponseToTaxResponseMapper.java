package com.sap.slh.tax.maestro.tax.mapper;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.sap.slh.tax.calculation.model.common.ItemResult;
import com.sap.slh.tax.calculation.model.common.TaxCalculationResponse;
import com.sap.slh.tax.calculation.model.common.TaxCalculationResponseLine;
import com.sap.slh.tax.calculation.model.common.TaxResult;
import com.sap.slh.tax.maestro.api.v0.schema.TaxLine;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse;
import com.sap.slh.tax.maestro.api.v0.schema.TaxValue;

public final class CalculationResponseToTaxResponseMapper implements Function<TaxCalculationResponse, TaxResponse> {

    private static CalculationResponseToTaxResponseMapper instance;

    private Function<TaxResult, TaxValue> convertTaxResultToTaxValue = taxResult -> TaxValue.builder()
            .withId(taxResult.getId())
            .withTaxable(getNotNull(taxResult.getTaxableBaseAmount()))
            .withValue(getNotNull(taxResult.getTaxAmount()))
            .withDeductibleTaxAmount(getNotNull(taxResult.getDeductibleTaxAmount()))
            .withNonDeductibleTaxAmount(getNotNull(taxResult.getNonDeductibleTaxAmount()))
            .build();

    private Function<ItemResult, TaxLine> convertItemResultToTaxLine = itemResult -> TaxLine.builder()
            .withId(itemResult.getId())
            .withTaxValues(itemResult.getTaxes().stream().map(convertTaxResultToTaxValue).collect(Collectors.toList()))
            .build();

    private CalculationResponseToTaxResponseMapper() {

    }

    public static CalculationResponseToTaxResponseMapper getInstance() {
        if (instance == null) {
            instance = new CalculationResponseToTaxResponseMapper();
        }
        return instance;
    }

    @Override
    public TaxResponse apply(TaxCalculationResponse taxCalculationResponse) {
        TaxResponse.Builder builder = TaxResponse.builder();

        if (taxCalculationResponse != null && taxCalculationResponse.getResult() != null) {
            TaxCalculationResponseLine calResult = taxCalculationResponse.getResult();
            if (!CollectionUtils.isEmpty(calResult.getItems())) {
                builder.withTaxLines(taxCalculationResponse.getResult()
                        .getItems()
                        .stream()
                        .filter(this::itemHasTaxes)
                        .map(convertItemResultToTaxLine)
                        .collect(Collectors.toList()));
            }
        }
        return builder.build();
    }

    private String getNotNull(BigDecimal obj) {
        return obj != null ? obj.toString() : null;
    }

    private boolean itemHasTaxes(ItemResult itemResult) {
        return itemResult != null && !CollectionUtils.isEmpty(itemResult.getTaxes());
    }
}
