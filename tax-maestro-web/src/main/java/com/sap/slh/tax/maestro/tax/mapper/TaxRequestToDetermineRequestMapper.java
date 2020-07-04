package com.sap.slh.tax.maestro.tax.mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.sap.slh.tax.attributes.determination.model.request.CompanyInformation;
import com.sap.slh.tax.attributes.determination.model.request.Country;
import com.sap.slh.tax.attributes.determination.model.request.Party;
import com.sap.slh.tax.attributes.determination.model.request.PartyDetail;
import com.sap.slh.tax.attributes.determination.model.request.Product;
import com.sap.slh.tax.attributes.determination.model.request.StandardClassification;
import com.sap.slh.tax.attributes.determination.model.request.TaxAttributesDeterminationRequest;
import com.sap.slh.tax.attributes.determination.model.request.TaxClassification;
import com.sap.slh.tax.attributes.determination.model.request.TaxClassification_;
import com.sap.slh.tax.attributes.determination.model.request.TaxRegistration;
import com.sap.slh.tax.maestro.api.v0.domain.BooleanValue;
import com.sap.slh.tax.maestro.api.v0.domain.ItemType;
import com.sap.slh.tax.maestro.api.v0.domain.LocationType;
import com.sap.slh.tax.maestro.api.v0.domain.SaleOrPurchase;
import com.sap.slh.tax.maestro.api.v0.schema.BusinessPartnerExemptionDetail;
import com.sap.slh.tax.maestro.api.v0.schema.ExemptionDetail;
import com.sap.slh.tax.maestro.api.v0.schema.Item;
import com.sap.slh.tax.maestro.api.v0.schema.ItemClassification;
import com.sap.slh.tax.maestro.api.v0.schema.Location;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest;
import com.sap.slh.tax.maestro.tax.exceptions.DirectPayloadNotSupportedException;

import reactor.core.publisher.Mono;

public final class TaxRequestToDetermineRequestMapper
        implements Function<TaxRequest, Mono<TaxAttributesDeterminationRequest>> {

    private static TaxRequestToDetermineRequestMapper instance;

    private TaxRequestToDetermineRequestMapper() {

    }

    public static TaxRequestToDetermineRequestMapper getInstance() {
        if (instance == null) {
            instance = new TaxRequestToDetermineRequestMapper();
        }
        return instance;
    }

    private Map<LocationType, Location> initializeLocations(TaxRequest taxRequest) {
        Map<LocationType, Location> locations = new LinkedHashMap<>();
        if (hasElements(taxRequest.getLocations())) {
            taxRequest.getLocations().forEach(loc -> locations.put(loc.getType(), loc));
        }
        return locations;
    }

    private Map<LocationType, com.sap.slh.tax.maestro.api.v0.schema.Party> initializeParties(TaxRequest taxRequest) {
        Map<LocationType, com.sap.slh.tax.maestro.api.v0.schema.Party> parties = new LinkedHashMap<>();
        if (hasElements(taxRequest.getParty())) {
            taxRequest.getParty().forEach(party -> parties.put(party.getRole(), party));
        }
        return parties;
    }

    private Function<ItemClassification, StandardClassification> convertItemClassificationToStandardClassification = taxItemClass -> {
        StandardClassification standClass = new StandardClassification();

        Optional.ofNullable(taxItemClass.getItemStandardClassificationSystemCode())
                .filter(StringUtils::isNotEmpty)
                .ifPresent(standClass::setClassificationSystem);

        Optional.ofNullable(taxItemClass.getItemStandardClassificationCode()).filter(StringUtils::isNotEmpty).ifPresent(
                standClass::setProductCode);

        return standClass;
    };

    private List<TaxClassification> convertTaxExemptionDetailToTaxClassification(List<ExemptionDetail> exemptionDetails,
            Map<LocationType, Location> locations) {

        List<TaxClassification> taxClassifications = new ArrayList<>();
        for (ExemptionDetail exemptionDetail : exemptionDetails) {
            Location location = locations.get(exemptionDetail.getLocationType());
            TaxClassification taxClass = new TaxClassification();

            Optional.ofNullable(location.getCountry())
                    .ifPresent(value -> taxClass.setCountryRegionCode(Country.fromValue(value.name())));

            Optional.ofNullable(exemptionDetail.getRegion()).filter(StringUtils::isNotEmpty).ifPresent(
                    taxClass::setSubdivisionCode);

            Optional.ofNullable(exemptionDetail.getTaxType()).filter(StringUtils::isNotEmpty).ifPresent(
                    taxClass::setTaxTypeCode);

            Optional.ofNullable(exemptionDetail.getTaxRateType()).filter(StringUtils::isNotEmpty).ifPresent(
                    taxClass::setTaxRateTypeCode);

            Optional.ofNullable(exemptionDetail.getExemptionCode()).filter(StringUtils::isNotEmpty).ifPresent(
                    taxClass::setExemptionReasonCode);

            taxClassifications.add(taxClass);
        }

        return taxClassifications;
    }

    private List<TaxClassification_> convertExemptionDetailToPartyTaxClassification(
            List<BusinessPartnerExemptionDetail> exemptionDetails, Map<LocationType, Location> locations) {

        List<TaxClassification_> taxClassifications = new ArrayList<>();
        for (BusinessPartnerExemptionDetail exemptionDetail : exemptionDetails) {
            Location exemptionLocation = locations.get(exemptionDetail.getLocationType());
            TaxClassification_ partyClassification = new TaxClassification_();

            Optional.ofNullable(exemptionDetail.getExemptionReasonCode()).filter(StringUtils::isNotEmpty).ifPresent(
                    partyClassification::setExemptionReasonCode);

            Optional.ofNullable(exemptionLocation.getCountry())
                    .ifPresent(value -> partyClassification.setCountryRegionCode(Country.fromValue(value.name())));

            Optional.ofNullable(exemptionLocation.getState()).filter(StringUtils::isNotEmpty).ifPresent(
                    partyClassification::setSubdivisionCode);

            Optional.ofNullable(exemptionDetail.getTaxType()).filter(StringUtils::isNotEmpty).ifPresent(
                    partyClassification::setTaxTypeCode);

            taxClassifications.add(partyClassification);
        }

        return taxClassifications;
    }

    private List<TaxRegistration> convertTaxRegistrationToPartyTaxRegistration(
            List<com.sap.slh.tax.maestro.api.v0.schema.TaxRegistration> partyTaxRegistrations,
            Map<LocationType, Location> locations) {

        List<TaxRegistration> taxRegistrations = new ArrayList<>();
        for (com.sap.slh.tax.maestro.api.v0.schema.TaxRegistration partyTaxRegistration : partyTaxRegistrations) {
            Location location = locations.get(partyTaxRegistration.getLocationType());
            TaxRegistration tdsTaxRegistration = new TaxRegistration();

            Optional.ofNullable(partyTaxRegistration.getTaxNumberTypeCode()).filter(StringUtils::isNotEmpty).ifPresent(
                    tdsTaxRegistration::setType);

            Optional.ofNullable(location.getCountry())
                    .ifPresent(value -> tdsTaxRegistration.setCountryRegionCode(Country.fromValue(value.name())));

            Optional.ofNullable(location.getState()).filter(StringUtils::isNotEmpty).ifPresent(
                    tdsTaxRegistration::setSubdivisionCode);

            taxRegistrations.add(tdsTaxRegistration);
        }

        return taxRegistrations;
    }

    private void addItemsAndProductsToDetermineRequest(TaxRequest taxRequest,
            TaxAttributesDeterminationRequest tdsRequest, Map<LocationType, Location> locations,
            Map<LocationType, com.sap.slh.tax.maestro.api.v0.schema.Party> parties) {
        List<Product> tdsProducts = new ArrayList<>();
        AtomicInteger productId = new AtomicInteger(1);

        tdsRequest.setItems(taxRequest.getItems().stream().map(taxItem -> {
            com.sap.slh.tax.attributes.determination.model.request.Item tdsItem = new com.sap.slh.tax.attributes.determination.model.request.Item();

            Optional.ofNullable(taxItem.getId()).filter(StringUtils::isNotEmpty).ifPresent(tdsItem::setId);

            tdsItem.setAssignedProductId(String.valueOf(productId.get()));

            if (hasElements(locations)) {
                tdsItem.setAssignedParties(convertTaxLocationsToAssignedParties(locations));
            }

            tdsProducts.add(createDetermineProduct(taxItem, productId.getAndIncrement(), locations));

            return tdsItem;
        }).collect(Collectors.toList()));

        tdsRequest.setProducts(tdsProducts);
    }

    private List<PartyDetail> convertTaxLocationsToAssignedParties(Map<LocationType, Location> locations) {
        AtomicInteger partyId = new AtomicInteger(1);
        return locations.keySet().stream().map(locationType -> {
            PartyDetail assignedParty = new PartyDetail();
            assignedParty.setId(String.valueOf(partyId.getAndIncrement()));
            assignedParty.setRole(convertLocationTypeToRole(locationType));
            return assignedParty;
        }).collect(Collectors.toList());
    }

    private Product createDetermineProduct(Item taxItem, int productId, Map<LocationType, Location> locations) {
        Product tdsProduct = new Product();

        tdsProduct.setId(String.valueOf(productId));

        Optional.ofNullable(taxItem.getItemType())
                .ifPresent(value -> tdsProduct.setTypeCode(convertItemTypeToProductType(value)));

        if (hasElements(taxItem.getExemptionDetails()))
            tdsProduct.setTaxClassifications(
                    convertTaxExemptionDetailToTaxClassification(taxItem.getExemptionDetails(), locations));

        if (hasElements(taxItem.getItemClassifications())) {
            tdsProduct.setStandardClassifications(taxItem.getItemClassifications()
                    .stream()
                    .map(convertItemClassificationToStandardClassification)
                    .collect(Collectors.toList()));
        }

        return tdsProduct;
    }

    private List<Party> convertTaxLocationsToDetermineParties(TaxRequest taxRequest,
            TaxAttributesDeterminationRequest tdsRequest, Map<LocationType, Location> locations,
            Map<LocationType, com.sap.slh.tax.maestro.api.v0.schema.Party> parties) {
        com.sap.slh.tax.attributes.determination.model.request.Item tdsItem = tdsRequest.getItems().get(0);

        return tdsItem.getAssignedParties().stream().map(assignedParty -> {
            Location taxLocation = locations.get(LocationType.valueOf(assignedParty.getRole().toString()));

            Party tdsParty = new Party();

            tdsParty.setId(String.valueOf(assignedParty.getId()));

            Optional.ofNullable(taxLocation.getCountry())
                    .ifPresent(value -> tdsParty.setCountryRegionCode(Country.fromValue(value.name())));

            Optional.ofNullable(taxLocation.getState()).filter(StringUtils::isNotEmpty).ifPresent(
                    tdsParty::setSubdivisionCode);

            if (hasElements(taxRequest.getBusinessPartnerExemptionDetails())) {
                tdsParty.setTaxClassification(convertExemptionDetailToPartyTaxClassification(
                        taxRequest.getBusinessPartnerExemptionDetails(), locations));
            }

            com.sap.slh.tax.maestro.api.v0.schema.Party taxParty;
            if (hasElements(parties) && (taxParty = parties.get(taxLocation.getType())) != null
                    && hasElements(taxParty.getTaxRegistration())) {
                tdsParty.setTaxRegistrations(
                        convertTaxRegistrationToPartyTaxRegistration(taxParty.getTaxRegistration(), locations));
            }

            return tdsParty;
        }).collect(Collectors.toList());
    }

    private CompanyInformation getCompanyInformation(TaxRequest taxRequest) {
        CompanyInformation companyInformation = new CompanyInformation();
        companyInformation
                .setIsDeferredTaxEnabled(convertBooleanValueToBoolean(taxRequest.getIsCompanyDeferredTaxEnabled()));
        return companyInformation;
    }

    private TaxAttributesDeterminationRequest.TransactionType convertToTransactionType(SaleOrPurchase saleOrPurchase) {
        if (saleOrPurchase.isPurchase()) {
            return TaxAttributesDeterminationRequest.TransactionType.PURCHASE;
        }
        return TaxAttributesDeterminationRequest.TransactionType.SALE;
    }

    private Boolean convertBooleanValueToBoolean(BooleanValue booleanValue) {
        if (BooleanValue.Y.equals(booleanValue) || BooleanValue.y.equals(booleanValue)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private PartyDetail.Role convertLocationTypeToRole(LocationType type) {
        return PartyDetail.Role.fromValue(type.name());
    }

    private Product.Type convertItemTypeToProductType(ItemType itemType) {
        if (ItemType.M.equals(itemType) || ItemType.m.equals(itemType)) {
            return Product.Type.MATERIAL;
        }
        return Product.Type.SERVICE;
    }

    private boolean hasElements(List<?> list) {
        return list != null && !list.isEmpty();
    }

    private boolean hasElements(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    private void throwErrorIfDirectPayload(TaxRequest taxRequest) {
        if (taxRequest.isDirectPayload()) {
            throw new DirectPayloadNotSupportedException("Direct payload is not supported");
        }
    }

    @Override
    public Mono<TaxAttributesDeterminationRequest> apply(TaxRequest taxRequest) {

        throwErrorIfDirectPayload(taxRequest);

        Map<LocationType, Location> locations = initializeLocations(taxRequest);
        Map<LocationType, com.sap.slh.tax.maestro.api.v0.schema.Party> parties = initializeParties(taxRequest);

        TaxAttributesDeterminationRequest determinationRequest = new TaxAttributesDeterminationRequest();

        Optional.ofNullable(taxRequest.getId()).filter(StringUtils::isNotEmpty).ifPresent(determinationRequest::setId);

        Optional.ofNullable(taxRequest.getDate()).ifPresent(determinationRequest::setDate);

        Optional.ofNullable(taxRequest.getSaleOrPurchase())
                .ifPresent(value -> determinationRequest.setTransactionTypeCode(convertToTransactionType(value)));

        Optional.ofNullable(taxRequest.getIsTransactionWithinTaxReportingGroup())
                .ifPresent(value -> determinationRequest
                        .setIsTransactionWithinTaxReportingGroup(convertBooleanValueToBoolean(value)));

        Optional.ofNullable(taxRequest.getIsCompanyDeferredTaxEnabled()).ifPresent(value -> {
            determinationRequest.setIsCompanyDeferredTaxEnabled(convertBooleanValueToBoolean(value));
            determinationRequest.setCompanyInformation(getCompanyInformation(taxRequest));
        });

        if (hasElements(taxRequest.getItems()))
            addItemsAndProductsToDetermineRequest(taxRequest, determinationRequest, locations, parties);

        if (hasElements(taxRequest.getLocations()))
            determinationRequest.setParties(
                    convertTaxLocationsToDetermineParties(taxRequest, determinationRequest, locations, parties));

        return Mono.just(determinationRequest);
    }

}
