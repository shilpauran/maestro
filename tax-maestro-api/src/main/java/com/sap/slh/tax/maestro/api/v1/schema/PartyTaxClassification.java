package com.sap.slh.tax.maestro.api.v1.schema;

import java.util.ArrayList;
import java.util.List;

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
import java.util.Objects;

@ApiModel(description = "The tax classification information of a party.")
@JsonDeserialize(builder = PartyTaxClassification.Builder.class)
@JsonInclude(Include.NON_NULL)
public class PartyTaxClassification extends BaseModel {

    @ApiModelProperty(position = 0, required = true, value = "The code of the reason for the tax exemption. For example, 1 represents Exempted Business Partner.", example = "1")
    @JsonProperty(JSONParameter.EXEMPTION_REASON_CODE)
    private String exemptionReasonCode = null;

    @ApiModelProperty(position = 1, value = "The country or region for which the tax classification is valid. This code follows the standards of ISO 3166-1 alpha-2. Pattern: [A-Z]{2}.", example = "CA")
    @JsonProperty(JSONParameter.COUNTRY_REGION_CODE)
    private CountryCode countryRegionCode = null;

    @ApiModelProperty(position = 2, value = "The code of the subdivision for which the tax classification is valid. The subdivision can be, for example, a state or province. For this code, use the standard of the latter part of the complete ISO 3166-2 code element (after the hyphen). Pattern: [a-zA-Z]{1,3}.", example = "BC")
    @JsonProperty(JSONParameter.SUBDIVISION_CODE)
    private String subdivisionCode = null;

    @ApiModelProperty(position = 3, value = "The code of the tax type for which you apply the exemption. For example, 1 represents GST.", example = "1")
    @JsonProperty(JSONParameter.TAX_TYPE_CODE)
    private String taxTypeCode = null;

    private PartyTaxClassification(Builder builder) {
        this.countryRegionCode = builder.countryRegionCode;
        this.subdivisionCode = builder.subdivisionCode;
        this.taxTypeCode = builder.taxTypeCode;
        this.exemptionReasonCode = builder.exemptionReasonCode;
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingAttributes = new ArrayList<>();
        validateMandatoryProperty(this.exemptionReasonCode, JSONParameter.EXEMPTION_REASON_CODE, missingAttributes);
        InvalidModelException.checkExceptions(missingAttributes);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CountryCode countryRegionCode;
        private String subdivisionCode;
        private String taxTypeCode;
        private String exemptionReasonCode;

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

        public Builder withExemptionReasonCode(String exemptionReasonCode) {
            this.exemptionReasonCode = exemptionReasonCode;
            return this;
        }

        public PartyTaxClassification build() {
            return new PartyTaxClassification(this);
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

    public String getExemptionReasonCode() {
        return exemptionReasonCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PartyTaxClassification [exemptionReasonCode=");
        sb.append(exemptionReasonCode);
        sb.append(", countryRegionCode=");
        sb.append(countryRegionCode);
        sb.append(", subdivisionCode=");
        sb.append(subdivisionCode);
        sb.append(", taxTypeCode=");
        sb.append(taxTypeCode);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartyTaxClassification)) {
            return false;
        }
        PartyTaxClassification that = (PartyTaxClassification)o;
        return Objects.equals(getExemptionReasonCode(), that.getExemptionReasonCode()) &&
            getCountryRegionCode() == that.getCountryRegionCode() &&
            Objects.equals(getSubdivisionCode(), that.getSubdivisionCode()) &&
            Objects.equals(getTaxTypeCode(), that.getTaxTypeCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getExemptionReasonCode(), getCountryRegionCode(), getSubdivisionCode(), getTaxTypeCode());
    }

    public static class JSONParameter {
        private JSONParameter() {
        }

        public static final String EXEMPTION_REASON_CODE = "exemptionReasonCode";
        public static final String COUNTRY_REGION_CODE = "countryRegionCode";
        public static final String SUBDIVISION_CODE = "subdivisionCode";
        public static final String TAX_TYPE_CODE = "taxTypeCode";
    }
}
