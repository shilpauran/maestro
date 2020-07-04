package com.sap.slh.tax.maestro.tax.mapper;

import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;

import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationResponse;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse;

public final class DestinationResponsePartnerResponseToTaxResponseMapper
        implements BiFunction<DestinationResponse, TaxResponse, TaxResponse> {

    private static DestinationResponsePartnerResponseToTaxResponseMapper instance;

    private DestinationResponsePartnerResponseToTaxResponseMapper() {

    }

    public static DestinationResponsePartnerResponseToTaxResponseMapper getInstance() {
        if (instance == null) {
            return new DestinationResponsePartnerResponseToTaxResponseMapper();
        } else {
            return instance;
        }
    }

    @Override
    public TaxResponse apply(DestinationResponse destinationResponse, TaxResponse partnerResponse) {
        if (StringUtils.isEmpty(partnerResponse.getPartnerName())) {
            partnerResponse.setPartnerName(destinationResponse.getName());
        }

        return partnerResponse;
    }
}
