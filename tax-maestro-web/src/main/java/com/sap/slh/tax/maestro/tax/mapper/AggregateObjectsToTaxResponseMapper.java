package com.sap.slh.tax.maestro.tax.mapper;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.calculation.model.common.TaxCalculationResponse;
import com.sap.slh.tax.maestro.api.v0.schema.TaxLine;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse;
import com.sap.slh.tax.maestro.api.v0.schema.TaxValue;

public class AggregateObjectsToTaxResponseMapper implements Function<Object[], TaxResponse> {

    private static AggregateObjectsToTaxResponseMapper instance;

    private AggregateObjectsToTaxResponseMapper() {
    }

    public static AggregateObjectsToTaxResponseMapper getInstance() {
        if (instance == null) {
            instance = new AggregateObjectsToTaxResponseMapper();
        }
        return instance;
    }

    private TaxValue getTaxValueFromId(List<TaxValue> taxes, String id) {
        Optional<TaxValue> tax = taxes.stream().filter(t -> id.equals(t.getId())).findFirst();

        return tax.isPresent() ? tax.get() : null;
    }

    private TaxLine getTaxLineFromId(List<TaxLine> taxLines, String id) {
        Optional<TaxLine> taxLine = taxLines.stream().filter(it -> id.equals(it.getId())).findFirst();

        return taxLine.isPresent() ? taxLine.get() : null;
    }

    @Override
    public TaxResponse apply(Object[] objects) {

        TaxResponse taxResponseFromRequest = null;
        TaxResponse taxResponseFromDetermine = null;
        TaxResponse taxResponseFromCalculate = null;

        for (Object obj : objects) {
            if (obj instanceof TaxRequest) {
                taxResponseFromRequest = TaxRequestToTaxResponseMapper.getInstance().apply((TaxRequest)obj);
            } else if (obj instanceof TaxCalculationResponse) {
                taxResponseFromCalculate = CalculationResponseToTaxResponseMapper.getInstance()
                        .apply((TaxCalculationResponse)obj);
            } else {
                taxResponseFromDetermine = DeterminationResponseToTaxResponseMapper.getInstance()
                        .apply((TaxAttributesDeterminationResponseModel)obj);
            }
        }

        return getAggregatedResponse(taxResponseFromRequest, taxResponseFromDetermine, taxResponseFromCalculate);
    }

    private TaxResponse getAggregatedResponse(TaxResponse taxResponseFromRequest, TaxResponse taxResponseFromDetermine,
            TaxResponse taxResponseFromCalculate) {
        taxResponseFromRequest.setCountry(taxResponseFromDetermine.getCountry());

        for (TaxLine taxLine : taxResponseFromRequest.getTaxLines()) {
            TaxLine detItem = getTaxLineFromId(taxResponseFromDetermine.getTaxLines(), taxLine.getId());
            TaxLine calcItem = getTaxLineFromId(taxResponseFromCalculate.getTaxLines(), taxLine.getId());

            if (detItem != null && calcItem != null) {
                taxLine.setCountry(detItem.getCountry());
                taxLine.setTaxCode(detItem.getTaxCode());
                taxLine.setTaxCodeLegalPhrase(detItem.getTaxCodeLegalPhrase());
                taxLine.setTaxValues(detItem.getTaxValues().stream().map(detTax -> {
                    TaxValue calcTax = getTaxValueFromId(calcItem.getTaxValues(), detTax.getId());

                    detTax.setTaxable(calcTax.getTaxable());
                    detTax.setValue(calcTax.getValue());
                    detTax.setDeductibleTaxAmount(calcTax.getDeductibleTaxAmount());
                    detTax.setNonDeductibleTaxAmount(calcTax.getNonDeductibleTaxAmount());
                    return detTax;
                }).collect(Collectors.toList()));
            }

        }
        return taxResponseFromRequest;
    }
}
