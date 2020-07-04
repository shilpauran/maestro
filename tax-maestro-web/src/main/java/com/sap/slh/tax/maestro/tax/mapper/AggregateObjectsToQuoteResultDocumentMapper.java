package com.sap.slh.tax.maestro.tax.mapper;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.calculation.model.common.TaxCalculationResponse;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteResultDocument;
import com.sap.slh.tax.maestro.api.v1.schema.ResultDocumentItem;
import com.sap.slh.tax.maestro.api.v1.schema.TaxResultItem;

public class AggregateObjectsToQuoteResultDocumentMapper implements Function<Object[], QuoteResultDocument> {

    private static AggregateObjectsToQuoteResultDocumentMapper instance;

    private AggregateObjectsToQuoteResultDocumentMapper() {
    }

    public static AggregateObjectsToQuoteResultDocumentMapper getInstance() {
        if (instance == null) {
            instance = new AggregateObjectsToQuoteResultDocumentMapper();
        }
        return instance;
    }

    private TaxResultItem getTaxFromId(List<TaxResultItem> taxes, String id) {
        Optional<TaxResultItem> tax = taxes.stream().filter(t -> id.equals(t.getId())).findFirst();

        return tax.isPresent() ? tax.get() : null;
    }

    private ResultDocumentItem getItemFromId(List<ResultDocumentItem> items, String id) {
        Optional<ResultDocumentItem> item = items.stream().filter(it -> id.equals(it.getId())).findFirst();

        return item.isPresent() ? item.get() : null;
    }

    @Override
    public QuoteResultDocument apply(Object[] objects) {

        QuoteResultDocument quoteResultFromRequest = null;
        QuoteResultDocument quoteResultFromDetermine = null;
        QuoteResultDocument quoteResultFromCalculate = null;

        for (Object obj : objects) {
            if (obj instanceof QuoteDocument) {
                quoteResultFromRequest = QuoteDocumentToQuoteResultDocumentMapper.getInstance()
                        .apply((QuoteDocument)obj);
            } else if (obj instanceof TaxCalculationResponse) {
                quoteResultFromCalculate = CalculationResponseToQuoteResultDocumentMapper.getInstance()
                        .apply((TaxCalculationResponse)obj);
            } else {
                quoteResultFromDetermine = DeterminationResponseToQuoteResultDocumentMapper.getInstance()
                        .apply((TaxAttributesDeterminationResponseModel)obj);
            }
        }

        return getAggregatedResponse(quoteResultFromRequest, quoteResultFromDetermine, quoteResultFromCalculate);
    }

    private QuoteResultDocument getAggregatedResponse(QuoteResultDocument quoteResultFromRequest,
            QuoteResultDocument quoteResultFromDetermine, QuoteResultDocument quoteResultFromCalculate) {
        for (ResultDocumentItem quoteItem : quoteResultFromRequest.getItems()) {
            ResultDocumentItem detItem = getItemFromId(quoteResultFromDetermine.getItems(), quoteItem.getId());
            ResultDocumentItem calcItem = getItemFromId(quoteResultFromCalculate.getItems(), quoteItem.getId());

            if (detItem != null && calcItem != null) {
                quoteItem.setCountryRegionCode(detItem.getCountryRegionCode());
                quoteItem.setSubdivisionCode(detItem.getSubdivisionCode());
                quoteItem.setTaxEventCode(detItem.getTaxEventCode());
                quoteItem.setTaxEventLegalPhrase(detItem.getTaxEventLegalPhrase());

                quoteItem.setAdditionalInformation(detItem.getAdditionalInformation());
                if (!CollectionUtils.isEmpty(calcItem.getAdditionalInformation())) {
                    quoteItem.getAdditionalInformation().putAll(calcItem.getAdditionalInformation());
                }

                quoteItem.setTaxes(detItem.getTaxes().stream().map(detTax -> {
                    TaxResultItem calcTax = getTaxFromId(calcItem.getTaxes(), detTax.getId());

                    detTax.setTaxAmount(calcTax.getTaxAmount());
                    detTax.setNonDeductibleTaxAmount(calcTax.getNonDeductibleTaxAmount());
                    detTax.setTaxableBaseAmount(calcTax.getTaxableBaseAmount());
                    detTax.setDeductibleTaxAmount(calcTax.getDeductibleTaxAmount());

                    return detTax;
                }).collect(Collectors.toList()));
            }

        }
        return quoteResultFromRequest;
    }
}
