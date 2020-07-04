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

@ApiModel(description = "The tax registrations information of a party.")
@JsonDeserialize(builder = PartyTaxRegistration.Builder.class)
@JsonInclude(Include.NON_NULL)
public class PartyTaxRegistration extends BaseModel {

    @ApiModelProperty(position = 0, required = true, value = "The code that represents a tax registration type for a party. For example, CNPJ, TIN, VAT identification number, etc.")
    @JsonProperty(JSONParameter.TYPE)
    private String type;

    @ApiModelProperty(position = 1, value = "The tax registration number for the registration type.")
    @JsonProperty(JSONParameter.NUMBER)
    private String number;    
    
    @ApiModelProperty(position = 2, value = "The country or region for which the tax registration is valid. This code follows the standards of ISO 3166-1 alpha-2. Pattern: [A-Z]{2}.", example = "CA")
    @JsonProperty(JSONParameter.COUNTRY_REGION_CODE)
    private CountryCode countryRegionCode;

    @ApiModelProperty(position = 3, value = "The code of the subdivision for which the tax registration is valid. The subdivision can be, for example, a state or province. For this code, use the standard of the latter part of the complete ISO 3166-2 code element (after the hyphen). Pattern: [a-zA-Z]{1,3}.", example = "BC")
    @JsonProperty(JSONParameter.SUBDIVISION_CODE)
    private String subdivisionCode;

    private PartyTaxRegistration(Builder builder) {
        this.type = builder.type;
        this.number = builder.number;
        this.countryRegionCode = builder.countryRegionCode;
        this.subdivisionCode = builder.subdivisionCode;
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingAttributes = new ArrayList<>();
        validateMandatoryProperty(this.type, JSONParameter.TYPE, missingAttributes);
        InvalidModelException.checkExceptions(missingAttributes);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String type;
        private String number;
        private CountryCode countryRegionCode;
        private String subdivisionCode;


        private Builder() {
        }

        public Builder withType(String type) {
            this.type = type;
            return this;
        }
        
        public Builder withNumber(String number) {
            this.number = number;
            return this;
        }        
        
        public Builder withCountryRegionCode(CountryCode countryRegionCode) {
            this.countryRegionCode = countryRegionCode;
            return this;
        }

        public Builder withSubdivisionCode(String subdivisionCode) {
            this.subdivisionCode = subdivisionCode;
            return this;
        }

        public PartyTaxRegistration build() {
            return new PartyTaxRegistration(this);
        }
    }

    public CountryCode getCountryRegionCode() {
        return countryRegionCode;
    }

    public String getSubdivisionCode() {
        return subdivisionCode;
    }

    public String getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PartyTaxRegistration [type=");
        sb.append(type);
        sb.append(", number=");
        sb.append(number);        
        sb.append(", countryRegionCode=");
        sb.append(countryRegionCode);
        sb.append(", subdivisionCode=");
        sb.append(subdivisionCode);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartyTaxRegistration)) {
            return false;
        }
        PartyTaxRegistration that = (PartyTaxRegistration)o;
        return Objects.equals(getType(), that.getType()) &&
            Objects.equals(getNumber(), that.getNumber()) &&
            getCountryRegionCode() == that.getCountryRegionCode() &&
            Objects.equals(getSubdivisionCode(), that.getSubdivisionCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getNumber(), getCountryRegionCode(), getSubdivisionCode());
    }

    public static class JSONParameter {
        private JSONParameter() {
        }

        public static final String TYPE = "type";
        public static final String NUMBER = "number";        
        public static final String COUNTRY_REGION_CODE = "countryRegionCode";
        public static final String SUBDIVISION_CODE = "subdivisionCode";
    }
}
