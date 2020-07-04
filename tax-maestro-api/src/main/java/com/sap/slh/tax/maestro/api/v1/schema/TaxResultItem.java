package com.sap.slh.tax.maestro.api.v1.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.v1.domain.DueCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@ApiModel(description = "The detailed information about the tax that the tax service calculates for an item.", parent = ResultDocumentItem.class)
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = TaxResultItem.Builder.class)
public class TaxResultItem extends BaseModel {

    @JsonIgnore
    private String id;

    @ApiModelProperty(position = 0, value = "The coded representation of the type of tax. For example, 1 represents GST.", example = "1")
    private String taxTypeCode;

    @ApiModelProperty(position = 1, value = "The percentage rate that the tax service uses to calculate the tax amount", example = "13")
    private BigDecimal taxRate;

    @ApiModelProperty(position = 2, value = "The base amount that the tax service uses to calculate the tax amount.", example = "100")
    private BigDecimal taxableBaseAmount;

    @ApiModelProperty(position = 3, value = "The amount of the tax that the tax service calculates referring to a tax type. This amount includes the non-deductible value when applicable.", example = "13")
    private BigDecimal taxAmount;

    @ApiModelProperty(position = 4, value = "The code that specifies whether the product tax represents a receivable or a payable to the tax authority.", allowableValues = "PAYABLE,RECEIVABLE", example = "PAYABLE")
    private DueCategory dueCategoryCode;

    @ApiModelProperty(position = 5, value = "Determines whether the tax is deferred or not.", example = "false")
    private Boolean isTaxDeferred;

    @ApiModelProperty(position = 6, value = "The percentage rate of the tax that is non-deductible.", example = "100")
    private BigDecimal nonDeductibleTaxRate;

    @ApiModelProperty(position = 7, value = "The amount of the tax that is deductible.", example = "13")
    private BigDecimal deductibleTaxAmount;

    @ApiModelProperty(position = 8, value = "The amount of the tax that is non-deductible.", example = "0")
    private BigDecimal nonDeductibleTaxAmount;

    @ApiModelProperty(position = 9, value = "The parameter that contains dynamic key/value pairs related to additional information on tax type level.", example = "{\"key1\":\"value1\",\"key2\":\"value2\"}")
    private Map<String, String> additionalInformation;

    private TaxResultItem(Builder builder) {
        id = builder.id;
        taxTypeCode = builder.taxTypeCode;
        taxRate = builder.taxRate;
        taxableBaseAmount = builder.taxableBaseAmount;
        taxAmount = builder.taxAmount;
        dueCategoryCode = builder.dueCategoryCode;
        isTaxDeferred = builder.isTaxDeferred;
        nonDeductibleTaxRate = builder.nonDeductibleTaxRate;
        deductibleTaxAmount = builder.deductibleTaxAmount;
        nonDeductibleTaxAmount = builder.nonDeductibleTaxAmount;
        additionalInformation = builder.additionalInformation;
    }

    /**
     * Builder for {@link TaxResultItem}.
     *
     * @return Builder for {@link TaxResultItem}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String taxTypeCode;
        private BigDecimal taxRate;
        private BigDecimal taxableBaseAmount;
        private BigDecimal taxAmount;
        private DueCategory dueCategoryCode;
        private Boolean isTaxDeferred;
        private BigDecimal nonDeductibleTaxRate;
        private BigDecimal deductibleTaxAmount;
        private BigDecimal nonDeductibleTaxAmount;
        private Map<String, String> additionalInformation;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withTaxTypeCode(String taxTypeCode) {
            this.taxTypeCode = taxTypeCode;
            return this;
        }

        public Builder withTaxRate(BigDecimal taxRate) {
            this.taxRate = taxRate;
            return this;
        }

        public Builder withTaxableBaseAmount(BigDecimal taxableBaseAmount) {
            this.taxableBaseAmount = taxableBaseAmount;
            return this;
        }

        public Builder withTaxAmount(BigDecimal taxAmount) {
            this.taxAmount = taxAmount;
            return this;
        }

        public Builder withDueCategoryCode(DueCategory dueCategoryCode) {
            this.dueCategoryCode = dueCategoryCode;
            return this;
        }

        public Builder withIsTaxDeferred(Boolean isTaxDeferred) {
            this.isTaxDeferred = isTaxDeferred;
            return this;
        }

        public Builder withNonDeductibleTaxRate(BigDecimal nonDeductibleTaxRate) {
            this.nonDeductibleTaxRate = nonDeductibleTaxRate;
            return this;
        }

        public Builder withDeductibleTaxAmount(BigDecimal deductibleTaxAmount) {
            this.deductibleTaxAmount = deductibleTaxAmount;
            return this;
        }

        public Builder withNonDeductibleTaxAmount(BigDecimal nonDeductibleTaxAmount) {
            this.nonDeductibleTaxAmount = nonDeductibleTaxAmount;
            return this;
        }

        public Builder withAdditionalInformation(Map<String, String> additionalInformation) {
            this.additionalInformation = additionalInformation;
            return this;
        }

        public TaxResultItem build() {
            return new TaxResultItem(this);
        }
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the taxTypeCode
     */
    public String getTaxTypeCode() {
        return taxTypeCode;
    }

    /**
     * @return the taxRate
     */
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    /**
     * @return the taxableBaseAmount
     */
    public BigDecimal getTaxableBaseAmount() {
        return taxableBaseAmount;
    }

    /**
     * @param taxableBaseAmount the taxableBaseAmount to set
     */
    public void setTaxableBaseAmount(BigDecimal taxableBaseAmount) {
        this.taxableBaseAmount = taxableBaseAmount;
    }

    /**
     * @return the taxAmount
     */
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    /**
     * @param taxAmount the taxAmount to set
     */
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    /**
     * @return the dueCategoryCode
     */
    public DueCategory getDueCategoryCode() {
        return dueCategoryCode;
    }

    /**
     * @return the isTaxDeferred
     */
    public Boolean getIsTaxDeferred() {
        return isTaxDeferred;
    }

    /**
     * @return the nonDeductibleTaxRate
     */
    public BigDecimal getNonDeductibleTaxRate() {
        return nonDeductibleTaxRate;
    }

    /**
     * @return the deductibleTaxAmount
     */
    public BigDecimal getDeductibleTaxAmount() {
        return deductibleTaxAmount;
    }

    /**
     * @param deductibleTaxAmount the deductibleTaxAmount to set
     */
    public void setDeductibleTaxAmount(BigDecimal deductibleTaxAmount) {
        this.deductibleTaxAmount = deductibleTaxAmount;
    }

    /**
     * @return the nonDeductibleTaxAmount
     */
    public BigDecimal getNonDeductibleTaxAmount() {
        return nonDeductibleTaxAmount;
    }

    /**
     * @param nonDeductibleTaxAmount the nonDeductibleTaxAmount to set
     */
    public void setNonDeductibleTaxAmount(BigDecimal nonDeductibleTaxAmount) {
        this.nonDeductibleTaxAmount = nonDeductibleTaxAmount;
    }

    /**
     * @return the additionalInformation
     */
    public Map<String, String> getAdditionalInformation() {
        return additionalInformation;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TaxResultItem [id=");
        sb.append(id);
        sb.append(", taxTypeCode=");
        sb.append(taxTypeCode);
        sb.append(", taxRate=");
        sb.append(taxRate);
        sb.append(", taxableBaseAmount=");
        sb.append(taxableBaseAmount);
        sb.append(", taxAmount=");
        sb.append(taxAmount);
        sb.append(", dueCategoryCode=");
        sb.append(dueCategoryCode);
        sb.append(", isTaxDeferred=");
        sb.append(isTaxDeferred);
        sb.append(", nonDeductibleTaxRate=");
        sb.append(nonDeductibleTaxRate);
        sb.append(", deductibleTaxAmount=");
        sb.append(deductibleTaxAmount);
        sb.append(", nonDeductibleTaxAmount=");
        sb.append(nonDeductibleTaxAmount);
        sb.append(", additionalInformation=");
        sb.append(additionalInformation);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaxResultItem)) return false;
        TaxResultItem that = (TaxResultItem)o;
        return Objects.equals(getId(), that.getId()) &&
            Objects.equals(getTaxTypeCode(), that.getTaxTypeCode()) &&
            Objects.equals(getTaxRate(), that.getTaxRate()) &&
            Objects.equals(getTaxableBaseAmount(), that.getTaxableBaseAmount()) &&
            Objects.equals(getTaxAmount(), that.getTaxAmount()) &&
            getDueCategoryCode() == that.getDueCategoryCode() &&
            Objects.equals(getIsTaxDeferred(), that.getIsTaxDeferred()) &&
            Objects.equals(getNonDeductibleTaxRate(), that.getNonDeductibleTaxRate()) &&
            Objects.equals(getDeductibleTaxAmount(), that.getDeductibleTaxAmount()) &&
            Objects.equals(getNonDeductibleTaxAmount(), that.getNonDeductibleTaxAmount()) &&
            Objects.equals(getAdditionalInformation(), that.getAdditionalInformation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTaxTypeCode(), getTaxRate(), getTaxableBaseAmount(), getTaxAmount(), getDueCategoryCode(), getIsTaxDeferred(), getNonDeductibleTaxRate(), getDeductibleTaxAmount(), getNonDeductibleTaxAmount(), getAdditionalInformation());
    }
}
