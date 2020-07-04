package com.sap.slh.tax.maestro.api.v0.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.v0.domain.DueCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Objects;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = TaxValue.Builder.class)
@ApiModel
public class TaxValue extends BaseModel {
    @JsonIgnore
    @ApiModelProperty(position = 0, hidden = true)
    private String id;

    @ApiModelProperty(position = 1, value = "Returns the tax level in the tax hierarchy for a tax on tax scenario. The highest level is 1. In the case of sales and use tax in US, level 1 indicates State tax, level 2 indicates county and so on.")
    private String level;

    @ApiModelProperty(position = 2, value = "Tax type code.")
    private String taxTypeCode;

    @ApiModelProperty(position = 3, value = "Name of tax type.")
    private String name;

    @ApiModelProperty(position = 4, value = "Tax rate.")
    private String rate;

    @ApiModelProperty(position = 5, value = "Returns the base amount used for tax calculations. In cases where a cash discount percentage is passed, country/region tax determination guidelines can affect the base amount.")
    private String taxable;

    @ApiModelProperty(position = 6, value = "Percentage of base amount exempted.")
    private String exemptedBasePercent;

    @ApiModelProperty(position = 7, value = "Exempted base amount.")
    private String exemptedBaseAmount;

    @ApiModelProperty(position = 8, value = "Other base amount.")
    private String otherBaseAmount;

    @ApiModelProperty(position = 9, value = "Tax amount including the non-deductible amount where applicable.")
    private String value;

    @ApiModelProperty(position = 10, value = "Non-deductible tax rate.")
    private String nonDeductibleTaxRate;

    @ApiModelProperty(position = 11, value = "Non-deductible tax amount.")
    private String nonDeductibleTaxAmount;

    @ApiModelProperty(position = 12, value = "Deductible tax amount.")
    private String deductibleTaxAmount;

    @ApiModelProperty(position = 13, value = "")
    private List<TaxAttribute> taxAttributes;

    @ApiModelProperty(position = 14, value = "Jurisdiction name.")
    private String jurisdiction;

    @ApiModelProperty(position = 15, value = "Returns the unique identifier of tax jurisdiction in the US.")
    private String jurisdictionCode;

    @ApiModelProperty(position = 16, value = "Returns \"P\" (payable) in cases of taxes due to the authorities, and \"R\" (receivable) for input taxes that can be claimed.")
    private DueCategory dueCategory;

    @ApiModelProperty(position = 17, value = "Is tax deferred or not.")
    private Boolean isTaxDeferred;

    @ApiModelProperty(position = 18, value = "Tax value is withholding relevant or not.")
    private Boolean withholdingRelevant;

    private TaxValue(Builder builder) {
        id = builder.id;
        level = builder.level;
        taxTypeCode = builder.taxTypeCode;
        name = builder.name;
        rate = builder.rate;
        taxable = builder.taxable;
        exemptedBasePercent = builder.exemptedBasePercent;
        exemptedBaseAmount = builder.exemptedBaseAmount;
        otherBaseAmount = builder.otherBaseAmount;
        value = builder.value;
        nonDeductibleTaxRate = builder.nonDeductibleTaxRate;
        nonDeductibleTaxAmount = builder.nonDeductibleTaxAmount;
        deductibleTaxAmount = builder.deductibleTaxAmount;
        taxAttributes = builder.taxAttributes;
        jurisdiction = builder.jurisdiction;
        jurisdictionCode = builder.jurisdictionCode;
        dueCategory = builder.dueCategory;
        isTaxDeferred = builder.isTaxDeferred;
        withholdingRelevant = builder.withholdingRelevant;
    }

    /**
     * Builder for {@link TaxValue}.
     *
     * @return Builder for {@link TaxValue}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        @JsonIgnore
        private String id = null;
        private String level = null;
        private String taxTypeCode = null;
        private String name = null;
        private String rate = null;
        private String taxable = null;
        private String exemptedBasePercent = null;
        private String exemptedBaseAmount = null;
        private String otherBaseAmount = null;
        private String value = null;
        private String nonDeductibleTaxRate = null;
        private String nonDeductibleTaxAmount = null;
        private String deductibleTaxAmount = null;
        private List<TaxAttribute> taxAttributes = null;
        private String jurisdiction = null;
        private String jurisdictionCode = null;
        private DueCategory dueCategory = null;
        private Boolean isTaxDeferred = null;
        private Boolean withholdingRelevant = null;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withLevel(String level) {
            this.level = level;
            return this;
        }

        public Builder withTaxTypeCode(String taxTypeCode) {
            this.taxTypeCode = taxTypeCode;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withRate(String rate) {
            this.rate = rate;
            return this;
        }

        public Builder withTaxable(String taxable) {
            this.taxable = taxable;
            return this;
        }

        public Builder withExemptedBasePercent(String exemptedBasePercent) {
            this.exemptedBasePercent = exemptedBasePercent;
            return this;
        }

        public Builder withExemptedBaseAmount(String exemptedBaseAmount) {
            this.exemptedBaseAmount = exemptedBaseAmount;
            return this;
        }

        public Builder withOtherBaseAmount(String otherBaseAmount) {
            this.otherBaseAmount = otherBaseAmount;
            return this;
        }

        public Builder withValue(String value) {
            this.value = value;
            return this;
        }

        public Builder withNonDeductibleTaxRate(String nonDeductibleTaxRate) {
            this.nonDeductibleTaxRate = nonDeductibleTaxRate;
            return this;
        }

        public Builder withNonDeductibleTaxAmount(String nonDeductibleTaxAmount) {
            this.nonDeductibleTaxAmount = nonDeductibleTaxAmount;
            return this;
        }

        public Builder withDeductibleTaxAmount(String deductibleTaxAmount) {
            this.deductibleTaxAmount = deductibleTaxAmount;
            return this;
        }

        public Builder withTaxAttributes(List<TaxAttribute> taxAttributes) {
            this.taxAttributes = taxAttributes;
            return this;
        }

        public Builder withJurisdiction(String jurisdiction) {
            this.jurisdiction = jurisdiction;
            return this;
        }

        public Builder withJurisdictionCode(String jurisdictionCode) {
            this.jurisdictionCode = jurisdictionCode;
            return this;
        }

        public Builder withDueCategory(DueCategory dueCategory) {
            this.dueCategory = dueCategory;
            return this;
        }

        public Builder withIsTaxDeferred(Boolean isTaxDeferred) {
            this.isTaxDeferred = isTaxDeferred;
            return this;
        }

        public Builder withWithholdingRelevant(Boolean withholdingRelevant) {
            this.withholdingRelevant = withholdingRelevant;
            return this;
        }

        public TaxValue build() {
            return new TaxValue(this);
        }
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the level
     */
    public String getLevel() {
        return level;
    }

    /**
     * @return the taxTypeCode
     */
    public String getTaxTypeCode() {
        return taxTypeCode;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the rate
     */
    public String getRate() {
        return rate;
    }

    /**
     * @return the taxable
     */
    public String getTaxable() {
        return taxable;
    }

    /**
     * @param taxable the taxable to set
     */
    public void setTaxable(String taxable) {
        this.taxable = taxable;
    }

    /**
     * @return the exemptedBasePercent
     */
    public String getExemptedBasePercent() {
        return exemptedBasePercent;
    }

    /**
     * @return the exemptedBaseAmount
     */
    public String getExemptedBaseAmount() {
        return exemptedBaseAmount;
    }

    /**
     * @return the otherBaseAmount
     */
    public String getOtherBaseAmount() {
        return otherBaseAmount;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the nonDeductibleTaxRate
     */
    public String getNonDeductibleTaxRate() {
        return nonDeductibleTaxRate;
    }

    /**
     * @return the nonDeductibleTaxAmount
     */
    public String getNonDeductibleTaxAmount() {
        return nonDeductibleTaxAmount;
    }

    /**
     * @param nonDeductibleTaxAmount the nonDeductibleTaxAmount to set
     */
    public void setNonDeductibleTaxAmount(String nonDeductibleTaxAmount) {
        this.nonDeductibleTaxAmount = nonDeductibleTaxAmount;
    }

    /**
     * @return the deductibleTaxAmount
     */
    public String getDeductibleTaxAmount() {
        return deductibleTaxAmount;
    }

    /**
     * @param deductibleTaxAmount the deductibleTaxAmount to set
     */
    public void setDeductibleTaxAmount(String deductibleTaxAmount) {
        this.deductibleTaxAmount = deductibleTaxAmount;
    }

    /**
     * @return the taxAttributes
     */
    public List<TaxAttribute> getTaxAttributes() {
        return taxAttributes;
    }

    /**
     * @return the jurisdiction
     */
    public String getJurisdiction() {
        return jurisdiction;
    }

    /**
     * @return the jurisdictionCode
     */
    public String getJurisdictionCode() {
        return jurisdictionCode;
    }

    /**
     * @return the dueCategory
     */
    public DueCategory getDueCategory() {
        return dueCategory;
    }

    /**
     * @return the isTaxDeferred
     */
    public Boolean getIsTaxDeferred() {
        return isTaxDeferred;
    }

    /**
     * @return the withholdingRelevant
     */
    public Boolean getWithholdingRelevant() {
        return withholdingRelevant;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaxValue{");
        sb.append("id='").append(id).append('\'');
        sb.append(", level='").append(level).append('\'');
        sb.append(", taxTypeCode='").append(taxTypeCode).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", rate='").append(rate).append('\'');
        sb.append(", taxable='").append(taxable).append('\'');
        sb.append(", exemptedBasePercent='").append(exemptedBasePercent).append('\'');
        sb.append(", exemptedBaseAmount='").append(exemptedBaseAmount).append('\'');
        sb.append(", otherBaseAmount='").append(otherBaseAmount).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append(", nonDeductibleTaxRate='").append(nonDeductibleTaxRate).append('\'');
        sb.append(", nonDeductibleTaxAmount='").append(nonDeductibleTaxAmount).append('\'');
        sb.append(", deductibleTaxAmount='").append(deductibleTaxAmount).append('\'');
        sb.append(", taxAttributes=").append(taxAttributes);
        sb.append(", jurisdiction='").append(jurisdiction).append('\'');
        sb.append(", jurisdictionCode='").append(jurisdictionCode).append('\'');
        sb.append(", dueCategory=").append(dueCategory);
        sb.append(", isTaxDeferred=").append(isTaxDeferred);
        sb.append(", withholdingRelevant=").append(withholdingRelevant);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaxValue)) {
            return false;
        }
        TaxValue taxValue = (TaxValue)o;
        return Objects.equals(getId(), taxValue.getId()) &&
            Objects.equals(getLevel(), taxValue.getLevel()) &&
            Objects.equals(getTaxTypeCode(), taxValue.getTaxTypeCode()) &&
            Objects.equals(getName(), taxValue.getName()) &&
            Objects.equals(getRate(), taxValue.getRate()) &&
            Objects.equals(getTaxable(), taxValue.getTaxable()) &&
            Objects.equals(getExemptedBasePercent(), taxValue.getExemptedBasePercent()) &&
            Objects.equals(getExemptedBaseAmount(), taxValue.getExemptedBaseAmount()) &&
            Objects.equals(getOtherBaseAmount(), taxValue.getOtherBaseAmount()) &&
            Objects.equals(getValue(), taxValue.getValue()) &&
            Objects.equals(getNonDeductibleTaxRate(), taxValue.getNonDeductibleTaxRate()) &&
            Objects.equals(getNonDeductibleTaxAmount(), taxValue.getNonDeductibleTaxAmount()) &&
            Objects.equals(getDeductibleTaxAmount(), taxValue.getDeductibleTaxAmount()) &&
            Objects.equals(getTaxAttributes(), taxValue.getTaxAttributes()) &&
            Objects.equals(getJurisdiction(), taxValue.getJurisdiction()) &&
            Objects.equals(getJurisdictionCode(), taxValue.getJurisdictionCode()) &&
            getDueCategory() == taxValue.getDueCategory() &&
            Objects.equals(getIsTaxDeferred(), taxValue.getIsTaxDeferred()) &&
            Objects.equals(getWithholdingRelevant(), taxValue.getWithholdingRelevant());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLevel(), getTaxTypeCode(), getName(), getRate(), getTaxable(),
            getExemptedBasePercent(), getExemptedBaseAmount(), getOtherBaseAmount(), getValue(), getJurisdiction(),
            getNonDeductibleTaxRate(), getNonDeductibleTaxAmount(), getDeductibleTaxAmount(), getTaxAttributes(),
            getJurisdictionCode(), getDueCategory(), getIsTaxDeferred(), getWithholdingRelevant());
    }
}
