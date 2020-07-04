package com.sap.slh.tax.maestro.tax.mapper;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponse;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.calculation.model.common.Item;
import com.sap.slh.tax.calculation.model.common.TaxCalculationRequest;
import com.sap.slh.tax.calculation.model.common.TaxLine;
import com.sap.slh.tax.maestro.api.v0.domain.GrossOrNet;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest;
import com.sap.slh.tax.maestro.api.v1.domain.AmountType;

public final class DeterminationResponseTaxRequestToCalculationRequestMapper
        implements BiFunction<TaxAttributesDeterminationResponseModel, TaxRequest, TaxCalculationRequest> {

    private static DeterminationResponseTaxRequestToCalculationRequestMapper instance;

    private DeterminationResponseTaxRequestToCalculationRequestMapper() {

    }

    public static DeterminationResponseTaxRequestToCalculationRequestMapper getInstance() {
        if (instance == null) {
            instance = new DeterminationResponseTaxRequestToCalculationRequestMapper();
        }
        return instance;
    }

    @Override
    public TaxCalculationRequest apply(TaxAttributesDeterminationResponseModel determineResponseModel,
            TaxRequest taxRequest) {
        if (hasResponse(determineResponseModel)) {
            TaxAttributesDeterminationResponse detResponse = determineResponseModel.getResult();
            TaxCalculationRequest calcRequest = new TaxCalculationRequest();

            Optional.ofNullable(detResponse.getId()).filter(StringUtils::isNotEmpty).ifPresent(calcRequest::setId);

            Optional.ofNullable(taxRequest.getDate()).ifPresent(calcRequest::setDate);

            Optional.ofNullable(taxRequest.getGrossOrNet())
                    .ifPresent(value -> calcRequest.setAmountTypeCode(this.convertToGrossOrNet(value)));

            Optional.ofNullable(taxRequest.getCurrency()).ifPresent(
                    value -> calcRequest.setCurrencyCode(TaxCalculationRequest.CurrencyCode.fromValue(value.name())));

            calcRequest.setItems(detResponse.getItems().stream().map(detItem -> {
                Item item = new Item();
                com.sap.slh.tax.maestro.api.v0.schema.Item taxRequestItem = this.getQuoteItemById(taxRequest,
                        detItem.getId());

                Optional.ofNullable(detItem.getId()).filter(StringUtils::isNotEmpty).ifPresent(item::setId);

                Optional.ofNullable(taxRequestItem).ifPresent(value -> {
                    Optional.ofNullable(value.getQuantity()).ifPresent(item::setQuantity);
                    Optional.ofNullable(value.getUnitPrice()).ifPresent(item::setUnitPrice);
                });

                Optional.ofNullable(detItem.getCountryRegionCode()).filter(StringUtils::isNotEmpty).ifPresent(
                        value -> item.setCountryRegionCode(Item.CountryRegionCode.fromValue(value)));

                Optional.ofNullable(detItem.getTaxEventCode()).filter(StringUtils::isNotEmpty).ifPresent(
                        item::setTaxEventCode);

                Optional.ofNullable(detItem.getIsTaxEventNonTaxable()).ifPresent(item::setIsTaxEventNonTaxable);

                item.setTaxes(this.getCalculationItemTaxesFromDetermineResponseItemTaxes(detItem.getTaxes()));

                return item;
            }).collect(Collectors.toList()));

            return calcRequest;
        }
        return null;
    }

    private com.sap.slh.tax.maestro.api.v0.schema.Item getQuoteItemById(TaxRequest taxRequest, String id) {
        return taxRequest.getItems().stream().filter(item -> id.equals(item.getId())).findFirst().orElse(null);
    }

    private List<TaxLine> getCalculationItemTaxesFromDetermineResponseItemTaxes(
            List<com.sap.slh.tax.attributes.determination.model.response.TaxLine> detItemTaxes) {
        return detItemTaxes.stream().map(DetermineMapperFunction.convertDetermineTaxLineToCalculateTaxLine).collect(
                Collectors.toList());
    }

    private TaxCalculationRequest.AmountTypeCode convertToGrossOrNet(GrossOrNet grossOrNet) {
        if (GrossOrNet.G.equals(grossOrNet) || GrossOrNet.g.equals(grossOrNet)) {
            return TaxCalculationRequest.AmountTypeCode.fromValue(AmountType.GROSS.name());
        }
        return TaxCalculationRequest.AmountTypeCode.fromValue(AmountType.NET.name());
    }

    private boolean hasResponse(TaxAttributesDeterminationResponseModel response) {
        return response != null && response.getResult() != null;
    }
}
