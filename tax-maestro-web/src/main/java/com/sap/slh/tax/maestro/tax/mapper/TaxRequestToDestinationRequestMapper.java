package com.sap.slh.tax.maestro.tax.mapper;

import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationRequest;
import com.sap.slh.tax.destinationconfiguration.destinations.models.CountryRegionCode;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.v0.domain.LocationType;
import com.sap.slh.tax.maestro.api.v0.domain.SaleOrPurchase;
import com.sap.slh.tax.maestro.api.v0.schema.Location;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest;
import com.sap.slh.tax.maestro.tax.exceptions.NoRelevantCountryForDestinationException;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public final class TaxRequestToDestinationRequestMapper implements Function<TaxRequest, Mono<DestinationRequest>> {

    private static TaxRequestToDestinationRequestMapper instance;

    private TaxRequestToDestinationRequestMapper() {

    }

    public static TaxRequestToDestinationRequestMapper getInstance() {
        if (instance == null) {
            instance = new TaxRequestToDestinationRequestMapper();
        }
        return instance;
    }

    @Override
    public Mono<DestinationRequest> apply(TaxRequest taxRequest) {
        return Mono.just(DestinationRequest.builder().withCountryRegionCode(findRelevantCountry(taxRequest)).build());
    }

    private CountryRegionCode findRelevantCountry(TaxRequest taxRequest) {
        if (taxRequest.isDirectPayload()) {
            return findRelevantCountryDirectPayload(taxRequest);
        } else {
            return findRelevantCountryFullPayload(taxRequest);
        }
    }

    private CountryRegionCode findRelevantCountryDirectPayload(TaxRequest taxRequest) {
        if (!CollectionUtils.isEmpty(taxRequest.getItems())) {
            CountryCode taxCodeCountry = taxRequest.getItems().get(0).getTaxCodeCountry();
            if (taxCodeCountry != null) {
                return CountryRegionCode.valueOf(taxCodeCountry.toString());
            }
        }

        throw new NoRelevantCountryForDestinationException(
                "Not able to find a relevant tax code country on direct payload");
    }

    private CountryRegionCode findRelevantCountryFullPayload(TaxRequest taxRequest) {
        List<LocationType> relevantLocations = getRelevantLocationsOrderedByPriority(taxRequest);
        List<Location> locationList = taxRequest.getLocations();

        if (locationList != null) {
            for (LocationType relevantLocation : relevantLocations) {
                for (Location location : locationList) {
                    if (location.getType().equals(relevantLocation)) {
                        return CountryRegionCode.valueOf(location.getCountry().toString());
                    }
                }
            }
        }

        throw new NoRelevantCountryForDestinationException(
                "Not able to find a relevant country for locations " + relevantLocations.toString());
    }

    private List<LocationType> getRelevantLocationsOrderedByPriority(TaxRequest taxRequest) {
        SaleOrPurchase operation = taxRequest.getSaleOrPurchase();
        if (operation.isSale()) {
            return Arrays.asList(LocationType.SHIP_FROM, LocationType.CONTRACT_FROM);
        } else {
            return Arrays.asList(LocationType.SHIP_TO, LocationType.CONTRACT_TO);
        }
    }
}
