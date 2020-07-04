package com.sap.slh.tax.maestro.api.v1.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApiModel(description = "The tax classification information of a product.")
@JsonDeserialize(builder = ProductTaxClassification.Builder.class)
@JsonInclude(Include.NON_NULL)
public class ProductTaxClassification extends BaseModel {

    @ApiModelProperty(position = 0, required = true, value = "The country or region for which the tax classification is valid. This code follows the standards of ISO 3166-1 alpha-2. Pattern: [A-Z]{2}.", example = "CA")
    @JsonProperty(JSONParameter.COUNTRY_REGION_CODE)
    private CountryCode countryRegionCode;

    @ApiModelProperty(position = 1, value = "The code of the subdivision for which the tax classification is valid. The subdivision can be, for example, a state or province. For this code, use the standard of the latter part of the complete ISO 3166-2 code element (after the hyphen). Pattern: [a-zA-Z]{1,3}.", example = "BC")
    @JsonProperty(JSONParameter.SUBDIVISION_CODE)
    private String subdivisionCode;

    @ApiModelProperty(position = 2, required = true, value = "The code of the tax type for which you apply the exemption or the tax rate type. For example, 1 represents GST.", example = "1")
    @JsonProperty(JSONParameter.TAX_TYPE_CODE)
    private String taxTypeCode;

    @ApiModelProperty(position = 3, value = "The type of tax rate as defined by national tax legislation. For example, 3 represents Reduced.", example = "3")
    @JsonProperty(JSONParameter.TAX_RATE_TYPE_CODE)
    private String taxRateTypeCode;

    @ApiModelProperty(position = 4, value = "The code of the reason for the tax exemption. For example, 2 represents Exempted Product.", example = "2")
    @JsonProperty(JSONParameter.EXEMPTION_REASON_CODE)
    private String exemptionReasonCode;

    @ApiModelProperty(position = 5, value = "Determines if the product is sold via electronic medium in the specified country or region.")
    @JsonProperty(JSONParameter.IS_SOLD_ELECTRONICALLY)
    private Boolean isSoldElectronically;

    @ApiModelProperty(position = 6, value = "Determines if the service is taxable in the country or region where service is offered.")
    @JsonProperty(JSONParameter.IS_SERVICE_POINT_TAXABLE)
    private Boolean isServicePointTaxable;

    private ProductTaxClassification(Builder builder) {
        this.countryRegionCode = builder.countryRegionCode;
        this.subdivisionCode = builder.subdivisionCode;
        this.taxTypeCode = builder.taxTypeCode;
        this.taxRateTypeCode = builder.taxRateTypeCode;
        this.exemptionReasonCode = builder.exemptionReasonCode;
        this.isSoldElectronically = builder.isSoldElectronically;
        this.isServicePointTaxable = builder.isServicePointTaxable;
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingAttributes = new ArrayList<>();

        validateMandatoryProperty(this.countryRegionCode, JSONParameter.COUNTRY_REGION_CODE, missingAttributes);
        validateMandatoryProperty(this.taxTypeCode, JSONParameter.TAX_TYPE_CODE, missingAttributes);

        InvalidModelException.checkExceptions(missingAttributes);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private CountryCode countryRegionCode;
        private String subdivisionCode;
        private String taxTypeCode;
        private String taxRateTypeCode;
        private String exemptionReasonCode;
        private Boolean isSoldElectronically;
        private Boolean isServicePointTaxable;

        private Builder() {
        }

        public Builder withCountryRegionCode(CountryCode countryRegionCode) {
            this.countryRegionCode = countryRegionCode;
            return this;
        }

        public Builder withSubdivisionCode(String subdivisionCode) {
            this.subdivisionCode = subdivisionCode;
            return this;
        }

        public Builder withTaxTypeCode(String taxTypeCode) {
            this.taxTypeCode = taxTypeCode;
            return this;
        }

        public Builder withTaxRateTypeCode(String taxRateTypeCode) {
            this.taxRateTypeCode = taxRateTypeCode;
            return this;
        }

        public Builder withExemptionReasonCode(String exemptionReasonCode) {
            this.exemptionReasonCode = exemptionReasonCode;
            return this;
        }

        public Builder withIsSoldElectronically(Boolean isSoldElectronically) {
            this.isSoldElectronically = isSoldElectronically;
            return this;
        }

        public Builder withIsServicePointTaxable(Boolean isServicePointTaxable) {
            this.isServicePointTaxable = isServicePointTaxable;
            return this;
        }

        public ProductTaxClassification build() {
            return new ProductTaxClassification(this);
        }
    }

    public CountryCode getCountryRegionCode() {
        return countryRegionCode;
    }

    public String getSubdivisionCode() {
        return subdivisionCode;
    }

    public String getTaxTypeCode() {
        return taxTypeCode;
    }

    public String getTaxRateTypeCode() {
        return taxRateTypeCode;
    }

    public String getExemptionReasonCode() {
        return exemptionReasonCode;
    }

    public Boolean getIsSoldElectronically() {
        return isSoldElectronically;
    }

    public Boolean getIsServicePointTaxable() {
        return isServicePointTaxable;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ProductTaxClassification [countryRegionCode=");
        sb.append(countryRegionCode);
        sb.append(", subdivisionCode=");
        sb.append(subdivisionCode);
        sb.append(", taxTypeCode=");
        sb.append(taxTypeCode);
        sb.append(", taxRateTypeCode=");
        sb.append(taxRateTypeCode);
        sb.append(", exemptionReasonCode=");
        sb.append(exemptionReasonCode);
        sb.append(", isSoldElectronically=");
        sb.append(isSoldElectronically);
        sb.append(", isServicePointTaxable=");
        sb.append(isServicePointTaxable);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductTaxClassification)) {
            return false;
        }
        ProductTaxClassification that = (ProductTaxClassification)o;
        return getCountryRegionCode() == that.getCountryRegionCode() &&
            Objects.equals(getSubdivisionCode(), that.getSubdivisionCode()) &&
            Objects.equals(getTaxTypeCode(), that.getTaxTypeCode()) &&
            Objects.equals(getTaxRateTypeCode(), that.getTaxRateTypeCode()) &&
            Objects.equals(getExemptionReasonCode(), that.getExemptionReasonCode()) &&
            Objects.equals(getIsSoldElectronically(), that.getIsSoldElectronically()) &&
            Objects.equals(getIsServicePointTaxable(), that.getIsServicePointTaxable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCountryRegionCode(), getSubdivisionCode(), getTaxTypeCode(), getTaxRateTypeCode(),
            getExemptionReasonCode(), getIsSoldElectronically(), getIsServicePointTaxable());
    }

    public static class JSONParameter {
        private JSONParameter() {
        }

        public static final String COUNTRY_REGION_CODE = "countryRegionCode";
        public static final String SUBDIVISION_CODE = "subdivisionCode";
        public static final String TAX_TYPE_CODE = "taxTypeCode";
        public static final String TAX_RATE_TYPE_CODE = "taxRateTypeCode";
        public static final String EXEMPTION_REASON_CODE = "exemptionReasonCode";
        public static final String IS_SOLD_ELECTRONICALLY = "isSoldElectronically";
        public static final String IS_SERVICE_POINT_TAXABLE = "isServicePointTaxable";
    }

}
