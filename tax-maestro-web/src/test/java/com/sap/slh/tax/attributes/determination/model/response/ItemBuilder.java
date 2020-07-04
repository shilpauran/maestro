package com.sap.slh.tax.attributes.determination.model.response;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {
    private ResponseItem instance;

    private ItemBuilder() {
        this.instance = new ResponseItem();
    }

    public static ItemBuilder builder() {
        return new ItemBuilder();
    }
    
    public ItemBuilder withId(String id) {
        instance.setId(id);
        return this;
    }

    public ItemBuilder withCountryRegionCode(String countryRegionCode) {
        instance.setCountryRegionCode(countryRegionCode);
        return this;
    }

    public ItemBuilder withSubdivisionCode(String subdivisionCode) {
        instance.setSubdivisionCode(subdivisionCode);
        return this;
    }

    public ItemBuilder withTaxEventTypeCode(String taxEventTypeCode) {
        instance.setTaxEventCode(taxEventTypeCode);
        return this;
    }

    public ItemBuilder withTaxEventLegalPhrase(String taxEventLegalPhrase) {
        instance.setTaxEventLegalPhrase(taxEventLegalPhrase);
        return this;
    }

    public ItemBuilder withIsTaxEventNonTaxable(Boolean isTaxEventTaxable) {
        instance.setIsTaxEventNonTaxable(isTaxEventTaxable);
        return this;
    }

    public ItemBuilder withTaxCode(String taxCode) {
        instance.setTaxCode(taxCode);
        return this;
    }

    public ItemBuilder withTaxes(List<TaxLine> taxLines) {
        this.instance.setTaxes(taxLines);
        return this;
    }

    public ItemBuilder withTaxes(TaxLine... taxLines) {
        this.instance.setTaxes(Arrays.asList(taxLines));
        return this;
    }

    public ItemBuilder withTaxDeterminationDetails(String taxDeterminationDetails) {
        instance.setTaxDeterminationDetails(taxDeterminationDetails);
        return this;
    }
    
    public ResponseItem build() {
        return this.instance;
    }
}
