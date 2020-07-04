package com.sap.slh.tax.maestro.tax.mapper;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponse;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.calculation.model.common.Item;
import com.sap.slh.tax.calculation.model.common.TaxCalculationRequest;
import com.sap.slh.tax.calculation.model.common.TaxLine;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;

public final class DeterminationResponseQuoteDocumentToCalculationRequestMapper
        implements BiFunction<TaxAttributesDeterminationResponseModel, QuoteDocument, TaxCalculationRequest> {

    private static DeterminationResponseQuoteDocumentToCalculationRequestMapper instance;

    private DeterminationResponseQuoteDocumentToCalculationRequestMapper() {

    }

    public static DeterminationResponseQuoteDocumentToCalculationRequestMapper getInstance() {
        if (instance == null) {
            instance = new DeterminationResponseQuoteDocumentToCalculationRequestMapper();
        }
        return instance;
    }

    @Override
    public TaxCalculationRequest apply(TaxAttributesDeterminationResponseModel determineResponseModel,
            QuoteDocument quoteDocument) {
        if (hasResponse(determineResponseModel)) {
            TaxAttributesDeterminationResponse detResponse = determineResponseModel.getResult();

            TaxCalculationRequest calcRequest = new TaxCalculationRequest();

            Optional.ofNullable(detResponse.getId()).filter(StringUtils::isNotEmpty).ifPresent(calcRequest::setId);

            Optional.ofNullable(quoteDocument.getDate()).ifPresent(calcRequest::setDate);

            Optional.ofNullable(quoteDocument.getAmountTypeCode()).ifPresent(value -> calcRequest
                    .setAmountTypeCode(TaxCalculationRequest.AmountTypeCode.fromValue(value.name())));

            Optional.ofNullable(quoteDocument.getCurrencyCode()).ifPresent(
                    value -> calcRequest.setCurrencyCode(TaxCalculationRequest.CurrencyCode.fromValue(value.name())));

            calcRequest.setItems(CollectionUtils.isEmpty(detResponse.getItems()) ? null
                    : detResponse.getItems().stream().map(detItem -> {
                        Item item = new Item();
                        com.sap.slh.tax.maestro.api.v1.schema.Item quoteItem = this.getQuoteItemById(quoteDocument,
                                detItem.getId());

                        Optional.ofNullable(detItem.getId()).filter(StringUtils::isNotEmpty).ifPresent(item::setId);

                        Optional.ofNullable(quoteItem).ifPresent(value -> {
                            Optional.ofNullable(value.getQuantity()).ifPresent(item::setQuantity);
                            Optional.ofNullable(value.getUnitPrice()).ifPresent(item::setUnitPrice);
                        });

                        Optional.ofNullable(detItem.getCountryRegionCode()).filter(StringUtils::isNotEmpty).ifPresent(
                                value -> item.setCountryRegionCode(Item.CountryRegionCode.fromValue(value)));

                        Optional.ofNullable(detItem.getTaxEventCode()).filter(StringUtils::isNotEmpty).ifPresent(
                                item::setTaxEventCode);

                        Optional.ofNullable(detItem.getIsTaxEventNonTaxable()).ifPresent(item::setIsTaxEventNonTaxable);

                        item.setTaxes(getCalculationItemTaxesFromDetermineResponseItemTaxes(detItem.getTaxes()));

                        return item;
                    }).collect(Collectors.toList()));

            return calcRequest;
        }
        return null;
    }

    private com.sap.slh.tax.maestro.api.v1.schema.Item getQuoteItemById(QuoteDocument quoteDocument, String id) {
        return quoteDocument.getItems().stream().filter(item -> id.equals(item.getId())).findFirst().orElse(null);
    }

    private List<TaxLine> getCalculationItemTaxesFromDetermineResponseItemTaxes(
            List<com.sap.slh.tax.attributes.determination.model.response.TaxLine> detItemTaxes) {
        return detItemTaxes.stream().map(DetermineMapperFunction.convertDetermineTaxLineToCalculateTaxLine).collect(
                Collectors.toList());
    }

    private boolean hasResponse(TaxAttributesDeterminationResponseModel response) {
        return response != null && response.getResult() != null;
    }
}
