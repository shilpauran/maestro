package com.sap.slh.tax.attributes.determination.model.response;

import java.math.BigDecimal;

public class TaxLineBuilder {
    private TaxLine instance;

    private TaxLineBuilder() {
        this.instance = new TaxLine();
    }

    public static TaxLineBuilder builder() {
        return new TaxLineBuilder();
    }

    public TaxLineBuilder withId(String id) {
        instance.setId(id);
        return this;
    }

    public TaxLineBuilder withTaxTypeCode(String taxTypeCode) {
        instance.setTaxTypeCode(taxTypeCode);
        return this;
    }

    public TaxLineBuilder withTaxRate(BigDecimal taxRate) {
        instance.setTaxRate(taxRate);
        return this;
    }

    public TaxLineBuilder withNonDeductibleTaxRate(BigDecimal nonDeductibleTaxRate) {
        instance.setNonDeductibleTaxRate(nonDeductibleTaxRate);
        return this;
    }

    public TaxLineBuilder withDueCategoryCode(String dueCategoryCode) {
        this.instance.setDueCategoryCode(dueCategoryCode);
        return this;
    }

    public TaxLineBuilder withIsTaxDeferred(Boolean isTaxDeferred) {
        this.instance.setIsTaxDeferred(isTaxDeferred);
        return this;
    }
    
    public TaxLineBuilder withIsReverseChargeRelevant(Boolean isReverseChargeRelevant) {
        this.instance.setIsReverseChargeRelevant(isReverseChargeRelevant);
        return this;
    }

    public TaxLine build() {
        return this.instance;
    }
}
