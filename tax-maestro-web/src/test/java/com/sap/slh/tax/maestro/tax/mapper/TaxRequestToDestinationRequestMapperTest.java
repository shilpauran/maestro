package com.sap.slh.tax.maestro.tax.mapper;

import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationRequest;
import com.sap.slh.tax.destinationconfiguration.destinations.models.CountryRegionCode;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.domain.CurrencyCode;
import com.sap.slh.tax.maestro.api.v0.domain.GrossOrNet;
import com.sap.slh.tax.maestro.api.v0.domain.LocationType;
import com.sap.slh.tax.maestro.api.v0.domain.SaleOrPurchase;
import com.sap.slh.tax.maestro.api.v0.domain.TaxCategory;
import com.sap.slh.tax.maestro.api.v0.schema.Item;
import com.sap.slh.tax.maestro.api.v0.schema.Location;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest;
import com.sap.slh.tax.maestro.tax.exceptions.NoRelevantCountryForDestinationException;

import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TaxRequestToDestinationRequestMapperTest {

    @Test
    public void saleWithShipFrom() {

        DestinationRequest expectedDestinationRequest = DestinationRequest.builder()
                .withCountryRegionCode(CountryRegionCode.valueOf("GB"))
                .build();

        List<Location> locations = new ArrayList<>();
        Location location;
        location = Location.builder()
                .withType(LocationType.SHIP_FROM)
                .withAddressLine1("9 High Street")
                .withZipCode("SW1A 2AA")
                .withCity("London")
                .withState("LND")
                .withCountry(CountryCode.GB)
                .build();
        locations.add(location);
        location = Location.builder()
                .withType(LocationType.SHIP_TO)
                .withAddressLine1("Avenue des Olympiades 2")
                .withZipCode("1140")
                .withCity("Brussels")
                .withCountry(CountryCode.BE)
                .build();
        locations.add(location);

        TaxRequest request = TaxRequest.builder()
                .withId("1")
                .withDate(new Date())
                .withSaleOrPurchase(SaleOrPurchase.s)
                .withGrossOrNet(GrossOrNet.n)
                .withCurrency(CurrencyCode.GBP)
                .withLocations(locations)
                .build();

        StepVerifier.create(TaxRequestToDestinationRequestMapper.getInstance().apply(request))
                .assertNext(destinationRequest -> {
                    assertEquals(expectedDestinationRequest, destinationRequest);
                })
                .verifyComplete();
    }

    @Test
    public void saleWithContractFrom() {

        DestinationRequest expectedDestinationRequest = DestinationRequest.builder()
                .withCountryRegionCode(CountryRegionCode.valueOf("GB"))
                .build();

        List<Location> locations = new ArrayList<>();
        Location location;
        location = Location.builder()
                .withType(LocationType.CONTRACT_FROM)
                .withAddressLine1("9 High Street")
                .withZipCode("SW1A 2AA")
                .withCity("London")
                .withState("LND")
                .withCountry(CountryCode.GB)
                .build();
        locations.add(location);
        location = Location.builder()
                .withType(LocationType.CONTRACT_TO)
                .withAddressLine1("Avenue des Olympiades 2")
                .withZipCode("1140")
                .withCity("Brussels")
                .withCountry(CountryCode.BE)
                .build();
        locations.add(location);

        TaxRequest request = TaxRequest.builder()
                .withId("1")
                .withDate(new Date())
                .withSaleOrPurchase(SaleOrPurchase.S)
                .withGrossOrNet(GrossOrNet.n)
                .withCurrency(CurrencyCode.GBP)
                .withLocations(locations)
                .build();

        StepVerifier.create(TaxRequestToDestinationRequestMapper.getInstance().apply(request))
                .assertNext(destinationRequest -> {
                    assertEquals(expectedDestinationRequest, destinationRequest);
                })
                .verifyComplete();
    }

    @Test
    public void saleWithShipFromAndContractFrom() {

        DestinationRequest expectedDestinationRequest = DestinationRequest.builder()
                .withCountryRegionCode(CountryRegionCode.valueOf("GB"))
                .build();

        List<Location> locations = new ArrayList<>();
        Location location;
        location = Location.builder()
                .withType(LocationType.SHIP_TO)
                .withAddressLine1("Avenue des Olympiades 2")
                .withZipCode("1140")
                .withCity("Brussels")
                .withCountry(CountryCode.BE)
                .build();
        locations.add(location);
        location = Location.builder()
                .withType(LocationType.CONTRACT_FROM)
                .withAddressLine1("Generaal Lemanstraat 67")
                .withZipCode("2018")
                .withCity("Antwerp")
                .withCountry(CountryCode.BE)
                .build();
        locations.add(location);
        location = Location.builder()
                .withType(LocationType.SHIP_FROM)
                .withAddressLine1("9 High Street")
                .withZipCode("SW1A 2AA")
                .withCity("London")
                .withState("LND")
                .withCountry(CountryCode.GB)
                .build();
        locations.add(location);

        TaxRequest request = TaxRequest.builder()
                .withId("1")
                .withDate(new Date())
                .withSaleOrPurchase(SaleOrPurchase.s)
                .withGrossOrNet(GrossOrNet.n)
                .withCurrency(CurrencyCode.GBP)
                .withLocations(locations)
                .build();

        StepVerifier.create(TaxRequestToDestinationRequestMapper.getInstance().apply(request))
                .assertNext(destinationRequest -> {
                    assertEquals(expectedDestinationRequest, destinationRequest);
                })
                .verifyComplete();
    }

    @Test
    public void purchaseWithShipTo() {

        DestinationRequest expectedDestinationRequest = DestinationRequest.builder()
                .withCountryRegionCode(CountryRegionCode.valueOf("BE"))
                .build();

        List<Location> locations = new ArrayList<>();
        Location location;
        location = Location.builder()
                .withType(LocationType.SHIP_FROM)
                .withAddressLine1("9 High Street")
                .withZipCode("SW1A 2AA")
                .withCity("London")
                .withState("LND")
                .withCountry(CountryCode.GB)
                .build();
        locations.add(location);
        location = Location.builder()
                .withType(LocationType.SHIP_TO)
                .withAddressLine1("Avenue des Olympiades 2")
                .withZipCode("1140")
                .withCity("Brussels")
                .withCountry(CountryCode.BE)
                .build();
        locations.add(location);

        TaxRequest request = TaxRequest.builder()
                .withId("1")
                .withDate(new Date())
                .withSaleOrPurchase(SaleOrPurchase.p)
                .withGrossOrNet(GrossOrNet.n)
                .withCurrency(CurrencyCode.EUR)
                .withLocations(locations)
                .build();

        StepVerifier.create(TaxRequestToDestinationRequestMapper.getInstance().apply(request))
                .assertNext(destinationRequest -> {
                    assertEquals(expectedDestinationRequest, destinationRequest);
                })
                .verifyComplete();
    }

    @Test
    public void purchaseWithContractTo() {

        DestinationRequest expectedDestinationRequest = DestinationRequest.builder()
                .withCountryRegionCode(CountryRegionCode.valueOf("BE"))
                .build();

        List<Location> locations = new ArrayList<>();
        Location location;
        location = Location.builder()
                .withType(LocationType.CONTRACT_FROM)
                .withAddressLine1("9 High Street")
                .withZipCode("SW1A 2AA")
                .withCity("London")
                .withState("LND")
                .withCountry(CountryCode.GB)
                .build();
        locations.add(location);
        location = Location.builder()
                .withType(LocationType.CONTRACT_TO)
                .withAddressLine1("Avenue des Olympiades 2")
                .withZipCode("1140")
                .withCity("Brussels")
                .withCountry(CountryCode.BE)
                .build();
        locations.add(location);

        TaxRequest request = TaxRequest.builder()
                .withId("1")
                .withDate(new Date())
                .withSaleOrPurchase(SaleOrPurchase.P)
                .withGrossOrNet(GrossOrNet.n)
                .withCurrency(CurrencyCode.EUR)
                .withLocations(locations)
                .build();

        StepVerifier.create(TaxRequestToDestinationRequestMapper.getInstance().apply(request))
                .assertNext(destinationRequest -> {
                    assertEquals(expectedDestinationRequest, destinationRequest);
                })
                .verifyComplete();
    }

    @Test
    public void purchaseWithShipToAndContractTo() {

        DestinationRequest expectedDestinationRequest = DestinationRequest.builder()
                .withCountryRegionCode(CountryRegionCode.valueOf("BE"))
                .build();

        List<Location> locations = new ArrayList<>();
        Location location;
        location = Location.builder()
                .withType(LocationType.SHIP_FROM)
                .withAddressLine1("9 High Street")
                .withZipCode("SW1A 2AA")
                .withCity("London")
                .withState("LND")
                .withCountry(CountryCode.GB)
                .build();
        locations.add(location);
        location = Location.builder()
                .withType(LocationType.CONTRACT_TO)
                .withAddressLine1("47 King William St")
                .withZipCode("EC4R 9AF")
                .withCity("London")
                .withState("LND")
                .withCountry(CountryCode.GB)
                .build();
        locations.add(location);
        location = Location.builder()
                .withType(LocationType.SHIP_TO)
                .withAddressLine1("Avenue des Olympiades 2")
                .withZipCode("1140")
                .withCity("Brussels")
                .withCountry(CountryCode.BE)
                .build();
        locations.add(location);

        TaxRequest request = TaxRequest.builder()
                .withId("1")
                .withDate(new Date())
                .withSaleOrPurchase(SaleOrPurchase.p)
                .withGrossOrNet(GrossOrNet.n)
                .withCurrency(CurrencyCode.EUR)
                .withLocations(locations)
                .build();

        StepVerifier.create(TaxRequestToDestinationRequestMapper.getInstance().apply(request))
                .assertNext(destinationRequest -> {
                    assertEquals(expectedDestinationRequest, destinationRequest);
                })
                .verifyComplete();
    }

    @Test
    public void purchaseWithEmptyLocations() {

        TaxRequest request = TaxRequest.builder()
                .withId("1")
                .withDate(new Date())
                .withSaleOrPurchase(SaleOrPurchase.P)
                .withGrossOrNet(GrossOrNet.n)
                .withCurrency(CurrencyCode.EUR)
                .withLocations(Collections.emptyList())
                .build();
        StepVerifier.create(Mono.just(request).flatMap(TaxRequestToDestinationRequestMapper.getInstance()))
                .expectError(NoRelevantCountryForDestinationException.class)
                .verify();

    }

    @Test
    public void purchaseWithNullLocations() {

        TaxRequest request = TaxRequest.builder()
                .withId("1")
                .withDate(new Date())
                .withSaleOrPurchase(SaleOrPurchase.P)
                .withGrossOrNet(GrossOrNet.n)
                .withCurrency(CurrencyCode.EUR)
                .build();
        StepVerifier.create(Mono.just(request).flatMap(TaxRequestToDestinationRequestMapper.getInstance()))
                .expectError(NoRelevantCountryForDestinationException.class)
                .verify();

    }

    @Test
    public void directPayload() {
        DestinationRequest expectedDestinationRequest = DestinationRequest.builder()
                .withCountryRegionCode(CountryRegionCode.valueOf("GB"))
                .build();

        List<Item> items = new ArrayList<Item>();
        items.add(Item.builder()
                .withId("id1")
                .withItemCode("itemCode")
                .withQuantity(new BigDecimal(1))
                .withUnitPrice(new BigDecimal(2))
                .withTaxCategory(TaxCategory.PRODUCT_TAXES)
                .withTaxCodeRegion("taxCodeRegion")
                .withTaxCode("taxCode")
                .withTaxCodeCountry(CountryCode.GB)
                .build());

        TaxRequest request = TaxRequest.builder()
                .withId("1")
                .withDate(new Date())
                .withSaleOrPurchase(SaleOrPurchase.P)
                .withGrossOrNet(GrossOrNet.n)
                .withCurrency(CurrencyCode.EUR)
                .withItems(items)
                .withLocations(Collections.emptyList())
                .build();

        StepVerifier.create(TaxRequestToDestinationRequestMapper.getInstance().apply(request))
                .assertNext(destinationRequest -> {
                    assertEquals(expectedDestinationRequest, destinationRequest);
                })
                .verifyComplete();
    }

    @Test
    public void directPayloadWithoutTaxCodeCountry() {
        List<Item> items = new ArrayList<Item>();
        items.add(Item.builder()
                .withId("id1")
                .withItemCode("itemCode")
                .withQuantity(new BigDecimal(1))
                .withUnitPrice(new BigDecimal(2))
                .withTaxCategory(TaxCategory.PRODUCT_TAXES)
                .withTaxCode("taxCode")
                .withTaxCodeRegion("LDN")
                .build());

        TaxRequest request = TaxRequest.builder()
                .withId("1")
                .withDate(new Date())
                .withSaleOrPurchase(SaleOrPurchase.P)
                .withGrossOrNet(GrossOrNet.n)
                .withCurrency(CurrencyCode.EUR)
                .withItems(items)
                .withLocations(Collections.emptyList())
                .build();

        StepVerifier.create(Mono.just(request).flatMap(TaxRequestToDestinationRequestMapper.getInstance()))
                .expectError(NoRelevantCountryForDestinationException.class)
                .verify();
    }

}
