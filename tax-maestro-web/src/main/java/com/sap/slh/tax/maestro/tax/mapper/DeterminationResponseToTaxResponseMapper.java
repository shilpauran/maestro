package com.sap.slh.tax.maestro.tax.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.sap.slh.tax.attributes.determination.model.response.ResponseItem;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponse;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.v0.domain.DueCategory;
import com.sap.slh.tax.maestro.api.v0.schema.TaxLine;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse.Builder;
import com.sap.slh.tax.maestro.api.v0.schema.TaxValue;
import com.sap.slh.tax.maestro.tax.exceptions.OutOfBoundsEnumMappingException;

public final class DeterminationResponseToTaxResponseMapper
        implements Function<TaxAttributesDeterminationResponseModel, TaxResponse> {

    private static final Logger logger = LoggerFactory.getLogger(DeterminationResponseToTaxResponseMapper.class);
    private static DeterminationResponseToTaxResponseMapper instance;

    private DeterminationResponseToTaxResponseMapper() {
    }

    public static DeterminationResponseToTaxResponseMapper getInstance() {
        if (instance == null) {
            instance = new DeterminationResponseToTaxResponseMapper();
        }

        return instance;
    }

    @Override
    public TaxResponse apply(TaxAttributesDeterminationResponseModel determinationResponse) {

        Builder responseBuilder = TaxResponse.builder();

        if (determinationResponse != null && determinationResponse.getResult() != null ) {
            TaxAttributesDeterminationResponse detResponse = determinationResponse.getResult();

            if (detResponse != null) {
                CountryCode countryCode = this.mapCountryCodeFromResponse(detResponse.getItems());

                responseBuilder.withCountry(countryCode)
                        .withTaxLines(detResponse.getItems() != null
                                ? detResponse.getItems().stream().map(this::mapDeterminationItemToTaxLine).collect(
                                Collectors.toList())
                                : null)
                        .build();
            }
        }

        return responseBuilder.build();
    }

    private CountryCode mapCountryCodeFromResponse(List<ResponseItem> items) {
        String country = null;

        if (items != null) {
            country = items.stream().findFirst().orElse(new ResponseItem()).getCountryRegionCode();
        }

        return this.mapCountryCode(country);
    }

    private CountryCode mapCountryCode(String country) {
        if (country != null) {
            try {
                return CountryCode.valueOf(country);
            } catch (IllegalArgumentException ex) {
                logger.error(ex.getMessage(), ex);
                throw new OutOfBoundsEnumMappingException(CountryCode.class, country);
            }
        } else {
            return null;
        }
    }

    private TaxLine mapDeterminationItemToTaxLine(com.sap.slh.tax.attributes.determination.model.response.ResponseItem item) {
        if (item != null) {
            return TaxLine.builder()
                    .withId(item.getId())
                    .withCountry(this.mapCountryCode(item.getCountryRegionCode()))
                    .withTaxCode(item.getTaxCode())
                    .withTaxCodeLegalPhrase(item.getTaxEventLegalPhrase())
                    .withTaxValues(this.mapItemtoTaxValues(item))
                    .build();
        } else {
            return TaxLine.builder().build();
        }
    }

    private List<TaxValue> mapItemtoTaxValues(ResponseItem item) {
        if (item != null && !CollectionUtils.isEmpty(item.getTaxes())) {
            return item.getTaxes()
                    .stream()
                    .map(tl -> TaxValue.builder()
                            .withId(tl.getId())
                            .withTaxTypeCode(tl.getTaxTypeCode())
                            .withRate(tl.getTaxRate() != null ? tl.getTaxRate().toString() : null)
                            .withNonDeductibleTaxRate(
                                    tl.getNonDeductibleTaxRate() != null ? tl.getNonDeductibleTaxRate().toString()
                                            : null)
                            .withDueCategory(mapDueCategory(tl.getDueCategoryCode()))
                            .withIsTaxDeferred(tl.getIsTaxDeferred() == null ? Boolean.FALSE : tl.getIsTaxDeferred())
                            .withWithholdingRelevant(Boolean.FALSE)
                            .build())
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private DueCategory mapDueCategory(String dueCategory) {
        if (dueCategory != null) {
            Optional<DueCategory> optDue = Arrays.stream(DueCategory.values())
                    .filter(d -> dueCategory.startsWith(d.toString())).findFirst();
            if (!optDue.isPresent()) {
                throw new OutOfBoundsEnumMappingException(DueCategory.class, dueCategory);
            }
            return optDue.get();
        } else {
            return null;
        }
    }
}
