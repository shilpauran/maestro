package com.sap.slh.tax.maestro.tax.mapper;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.sap.slh.tax.calculation.model.common.ItemResult;
import com.sap.slh.tax.calculation.model.common.TaxCalculationResponse;
import com.sap.slh.tax.calculation.model.common.TaxCalculationResponseLine;
import com.sap.slh.tax.calculation.model.common.TaxResult;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteResultDocument;
import com.sap.slh.tax.maestro.api.v1.schema.ResultDocumentItem;
import com.sap.slh.tax.maestro.api.v1.schema.TaxResultItem;

public final class CalculationResponseToQuoteResultDocumentMapper
        implements Function<TaxCalculationResponse, QuoteResultDocument> {

    private static CalculationResponseToQuoteResultDocumentMapper instance;

    private Function<TaxResult, TaxResultItem> convertTaxResultToTaxResultItem = taxResult -> TaxResultItem.builder()
            .withId(taxResult.getId())
            .withTaxableBaseAmount(taxResult.getTaxableBaseAmount())
            .withTaxAmount(taxResult.getTaxAmount())
            .withDeductibleTaxAmount(taxResult.getDeductibleTaxAmount())
            .withNonDeductibleTaxAmount(taxResult.getNonDeductibleTaxAmount())
            .build();

    private Function<ItemResult, ResultDocumentItem> convertItemResultToResultDocumentItem = itemResult -> ResultDocumentItem
            .builder()
            .withId(itemResult.getId())
            .withTaxes(itemResult.getTaxes().stream().map(convertTaxResultToTaxResultItem).collect(Collectors.toList()))
            .build();

    private CalculationResponseToQuoteResultDocumentMapper() {

    }

    public static CalculationResponseToQuoteResultDocumentMapper getInstance() {
        if (instance == null) {
            instance = new CalculationResponseToQuoteResultDocumentMapper();
        }
        return instance;
    }

    @Override
    public QuoteResultDocument apply(TaxCalculationResponse taxCalculationResponse) {
        QuoteResultDocument.Builder builder = QuoteResultDocument.builder();

        if (taxCalculationResponse != null && taxCalculationResponse.getResult() != null) {
            TaxCalculationResponseLine calResult = taxCalculationResponse.getResult();
            if (!CollectionUtils.isEmpty(calResult.getItems())) {
                builder.withItems(taxCalculationResponse.getResult()
                        .getItems()
                        .stream()
                        .filter(this::itemHasTaxes)
                        .map(convertItemResultToResultDocumentItem)
                        .collect(Collectors.toList()));
            }
        }
        return builder.build();
    }

    private boolean itemHasTaxes(ItemResult itemResult) {
        return itemResult != null && !CollectionUtils.isEmpty(itemResult.getTaxes());
    }
}
