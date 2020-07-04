package com.sap.slh.tax.maestro.tax.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.sap.slh.tax.attributes.determination.model.request.Country;
import com.sap.slh.tax.attributes.determination.model.request.Item;
import com.sap.slh.tax.attributes.determination.model.request.Party;
import com.sap.slh.tax.attributes.determination.model.request.PartyDetail;
import com.sap.slh.tax.attributes.determination.model.request.Product;
import com.sap.slh.tax.attributes.determination.model.request.StandardClassification;
import com.sap.slh.tax.attributes.determination.model.request.TaxClassification;
import com.sap.slh.tax.attributes.determination.model.request.TaxClassification_;
import com.sap.slh.tax.attributes.determination.model.request.TaxRegistration;
import com.sap.slh.tax.attributes.determination.model.response.TaxLine;
import com.sap.slh.tax.maestro.api.v1.schema.AssignedParty;
import com.sap.slh.tax.maestro.api.v1.schema.PartyTaxClassification;
import com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification;

public final class DetermineMapperFunction {

    public static final Function<Map<String, String>, Map<String, Object>> convertMap = Collections::<String, Object>unmodifiableMap;

    public static final Function<AssignedParty, PartyDetail> convertAssignedPartyToPartyDetail = assignedParty -> {
        PartyDetail partyDetail = new PartyDetail();

        Optional.ofNullable(assignedParty.getId()).filter(StringUtils::isNotEmpty).ifPresent(partyDetail::setId);

        Optional.ofNullable(assignedParty.getRole())
                .ifPresent(value -> partyDetail.setRole(PartyDetail.Role.fromValue(value.name())));

        return partyDetail;
    };

    public static final Function<ProductTaxClassification, TaxClassification> convertProductTaxClassificationToTaxClassification = productTaxClassification -> {
        TaxClassification taxClassification = new TaxClassification();

        Optional.ofNullable(productTaxClassification.getCountryRegionCode()).ifPresent(
                value -> taxClassification.setCountryRegionCode(Country.fromValue(value.name())));

        Optional.ofNullable(productTaxClassification.getSubdivisionCode()).filter(StringUtils::isNotEmpty).ifPresent(
                taxClassification::setSubdivisionCode);

        Optional.ofNullable(productTaxClassification.getTaxTypeCode()).filter(StringUtils::isNotEmpty).ifPresent(
                taxClassification::setTaxTypeCode);

        Optional.ofNullable(productTaxClassification.getTaxRateTypeCode()).filter(StringUtils::isNotEmpty).ifPresent(
                taxClassification::setTaxRateTypeCode);

        Optional.ofNullable(productTaxClassification.getExemptionReasonCode())
                .filter(StringUtils::isNotEmpty)
                .ifPresent(taxClassification::setExemptionReasonCode);

        Optional.ofNullable(productTaxClassification.getIsSoldElectronically())
                .ifPresent(taxClassification::setIsSoldElectronically);

        Optional.ofNullable(productTaxClassification.getIsServicePointTaxable())
                .ifPresent(taxClassification::setIsServicePointTaxable);

        return taxClassification;
    };

    public static final Function<com.sap.slh.tax.maestro.api.v1.schema.StandardClassification, StandardClassification> convertStandardClassification = productStandardClassification -> {
        StandardClassification standardClassification = new StandardClassification();

        Optional.ofNullable(productStandardClassification.getClassificationSystem())
                .filter(StringUtils::isNotEmpty)
                .ifPresent(standardClassification::setClassificationSystem);

        Optional.ofNullable(productStandardClassification.getProductCode()).filter(StringUtils::isNotEmpty).ifPresent(
                standardClassification::setProductCode);

        return standardClassification;
    };

    public static final Function<PartyTaxClassification, TaxClassification_> convertPartyTaxClassificationToTaxClassification = partyTaxClassification -> {
        TaxClassification_ taxClassification = new TaxClassification_();

        Optional.ofNullable(partyTaxClassification.getExemptionReasonCode()).filter(StringUtils::isNotEmpty).ifPresent(
                taxClassification::setExemptionReasonCode);

        Optional.ofNullable(partyTaxClassification.getCountryRegionCode()).ifPresent(
                value -> taxClassification.setCountryRegionCode(Country.fromValue(value.name())));

        Optional.ofNullable(partyTaxClassification.getSubdivisionCode()).filter(StringUtils::isNotEmpty).ifPresent(
                taxClassification::setSubdivisionCode);

        Optional.ofNullable(partyTaxClassification.getTaxTypeCode()).filter(StringUtils::isNotEmpty).ifPresent(
                taxClassification::setTaxTypeCode);

        return taxClassification;
    };

    public static final Function<com.sap.slh.tax.maestro.api.v1.schema.PartyTaxRegistration, TaxRegistration> convertPartyTaxRegistrationToTaxRegistration = partyTaxRegistration -> {
        TaxRegistration taxRegistration = new TaxRegistration();

        Optional.ofNullable(partyTaxRegistration.getType()).filter(StringUtils::isNotEmpty).ifPresent(
                taxRegistration::setType);

        Optional.ofNullable(partyTaxRegistration.getCountryRegionCode()).ifPresent(
                value -> taxRegistration.setCountryRegionCode(Country.fromValue(value.name())));

        Optional.ofNullable(partyTaxRegistration.getSubdivisionCode()).filter(StringUtils::isNotEmpty).ifPresent(
                taxRegistration::setSubdivisionCode);

        return taxRegistration;
    };

    public static final Function<com.sap.slh.tax.maestro.api.v1.schema.Item, Item> convertDocumentItemToDetermineItem = documentItem -> {
        Item item = new Item();

        Optional.ofNullable(documentItem.getId()).filter(StringUtils::isNotEmpty).ifPresent(item::setId);

        Optional.ofNullable(documentItem.getAssignedProductId()).filter(StringUtils::isNotEmpty).ifPresent(
                item::setAssignedProductId);

        if (hasElements(documentItem.getAssignedParties()))
            item.setAssignedParties(convertList(documentItem.getAssignedParties(), convertAssignedPartyToPartyDetail));

        return item;
    };

    public static final Function<com.sap.slh.tax.maestro.api.v1.schema.Product, Product> convertDocumentProductToDetermineProduct = documentProduct -> {
        Product product = new Product();

        Optional.ofNullable(documentProduct.getId()).filter(StringUtils::isNotEmpty).ifPresent(product::setId);

        Optional.ofNullable(documentProduct.getTypeCode())
                .ifPresent(value -> product.setTypeCode(Product.Type.fromValue(value.name())));

        if (hasElements(documentProduct.getTaxClassifications())) {
            product.setTaxClassifications(convertList(documentProduct.getTaxClassifications(),
                    convertProductTaxClassificationToTaxClassification));
        }

        if (hasElements(documentProduct.getStandardClassifications())) {
            product.setStandardClassifications(
                    convertList(documentProduct.getStandardClassifications(), convertStandardClassification));
        }

        return product;
    };

    public static final Function<com.sap.slh.tax.maestro.api.v1.schema.Party, Party> convertDocumentPartyToDetermineParty = documentParty -> {
        Party party = new Party();

        Optional.ofNullable(documentParty.getId()).filter(StringUtils::isNotEmpty).ifPresent(party::setId);

        Optional.ofNullable(documentParty.getCountryRegionCode())
                .ifPresent(value -> party.setCountryRegionCode(Country.fromValue(value.name())));

        Optional.ofNullable(documentParty.getSubdivisionCode()).filter(StringUtils::isNotEmpty).ifPresent(
                party::setSubdivisionCode);

        if (hasElements(documentParty.getTaxClassifications())) {
            party.setTaxClassification(convertList(documentParty.getTaxClassifications(),
                    convertPartyTaxClassificationToTaxClassification));
        }

        if (hasElements(documentParty.getTaxRegistrations())) {
            party.setTaxRegistrations(
                    convertList(documentParty.getTaxRegistrations(), convertPartyTaxRegistrationToTaxRegistration));
        }

        return party;
    };

    public static final Function<TaxLine, com.sap.slh.tax.calculation.model.common.TaxLine> convertDetermineTaxLineToCalculateTaxLine = detTaxLine -> {
        com.sap.slh.tax.calculation.model.common.TaxLine calcTaxLine = new com.sap.slh.tax.calculation.model.common.TaxLine();

        Optional.ofNullable(detTaxLine.getId()).filter(StringUtils::isNotEmpty).ifPresent(
                value -> calcTaxLine.setId(value));

        Optional.ofNullable(detTaxLine.getTaxTypeCode()).filter(StringUtils::isNotEmpty).ifPresent(
                value -> calcTaxLine.setTaxTypeCode(value));

        Optional.ofNullable(detTaxLine.getTaxRate()).ifPresent(value -> calcTaxLine.setTaxRate(value.doubleValue()));

        Optional.ofNullable(detTaxLine.getDueCategoryCode()).filter(StringUtils::isNotEmpty).ifPresent(
                value -> calcTaxLine.setDueCategoryCode(
                        com.sap.slh.tax.calculation.model.common.TaxLine.DueCategoryCode.fromValue(value)));

        Optional.ofNullable(detTaxLine.getNonDeductibleTaxRate())
                .ifPresent(value -> calcTaxLine.setNonDeductibleTaxRate(Double.valueOf(value.toString())));

        Optional.ofNullable(detTaxLine.getIsReverseChargeRelevant())
                .ifPresent(value -> calcTaxLine.setIsReverseChargeRelevant(value));

        return calcTaxLine;
    };

    private DetermineMapperFunction() {
    }

    public static final <I, O> List<O> convertList(List<I> list, Function<I, O> converter) {
        return list.stream().map(converter).collect(Collectors.toList());
    }

    private static boolean hasElements(List<?> list) {
        return list != null && !list.isEmpty();
    }

}
