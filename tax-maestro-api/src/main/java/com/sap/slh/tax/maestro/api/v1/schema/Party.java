package com.sap.slh.tax.maestro.api.v1.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

@ApiModel(description = "The information on one of the parts involved in a business transaction. You can assign a party to one or more items.")
@JsonDeserialize(builder = Party.Builder.class)
@JsonInclude(Include.NON_NULL)
public class Party extends BaseModel {

    @ApiModelProperty(position = 0, required = true, value = "The identifier of each party in the business transaction.")
    @JsonProperty(JSONParameter.ID)
    private String id;

    @ApiModelProperty(position = 1, required = true, value = "The code that identifies the country or region where the party is located. This code follows the standards of ISO 3166-1 alpha-2. Pattern: [A-Z]{2}.", example = "CA")
    @JsonProperty(JSONParameter.COUNTRY_REGION_CODE)
    private CountryCode countryRegionCode;

    @ApiModelProperty(position = 2, value = "The code of the subdivision in which the party is located. The subdivision can be, for example, a state or province. For this code, use the standard of the latter part of the complete ISO 3166-2 code element (after the hyphen). Pattern: [a-zA-Z]{1,3}.", example = "BC")
    @JsonProperty(JSONParameter.SUBDIVISION_CODE)
    private String subdivisionCode;

    @ApiModelProperty(position = 3, value = "The name of the city where the party is located.")
    @JsonProperty(JSONParameter.CITY)
    private String city;

    @ApiModelProperty(position = 4, value = "The name of the county where the party is located.")
    @JsonProperty(JSONParameter.COUNTY)
    private String county;

    @ApiModelProperty(position = 5, value = "The ZIP or postal code of the party.")
    @JsonProperty(JSONParameter.ZIP_CODE)
    private String zipCode;

    @ApiModelProperty(position = 6, value = "The address of the party. You can add the street name or any other relevant complement.")
    @JsonProperty(JSONParameter.ADDRESS)
    private String address;

    @ApiModelProperty(position = 7, value = "A list with the tax classifications assigned to this party.")
    @JsonProperty(JSONParameter.TAX_CLASSIFICATIONS)
    private List<PartyTaxClassification> taxClassifications;

    @ApiModelProperty(position = 8, value = "A list with the tax registrations assigned to this party.")
    @JsonProperty(JSONParameter.TAX_REGISTRATIONS)
    private List<PartyTaxRegistration> taxRegistrations;

    @ApiModelProperty(position = 9, value = "The parameter that contains dynamic key/value pairs related to additional information on party level.", example = "{\"key1\":\"value1\",\"key2\":\"value2\"}")
    @JsonProperty(JSONParameter.ADDITIONAL_INFORMATION)
    private Map<String, String> additionalInformation;

    private Party(Builder builder) {
        this.id = builder.id;
        this.countryRegionCode = builder.countryRegionCode;
        this.subdivisionCode = builder.subdivisionCode;
        this.city = builder.city;
        this.county = builder.county;
        this.zipCode = builder.zipCode;
        this.address = builder.address;
        this.taxClassifications = builder.taxClassifications;
        this.taxRegistrations = builder.taxRegistrations;
        this.additionalInformation = builder.additionalInformation;
    }

    @Override
    public String getIndentifierValue() {
        return this.id;
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingAttributes = new ArrayList<>();

        validateMandatoryProperty(this.id, JSONParameter.ID, missingAttributes);
        validateMandatoryProperty(this.countryRegionCode, JSONParameter.COUNTRY_REGION_CODE, missingAttributes);
        validateListItems(this.taxClassifications, JSONParameter.TAX_CLASSIFICATIONS, missingAttributes);
        validateListItems(this.taxRegistrations, JSONParameter.TAX_REGISTRATIONS, missingAttributes);

        InvalidModelException.checkExceptions(missingAttributes);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private CountryCode countryRegionCode;
        private String subdivisionCode;
        private String city;
        private String county;
        private String zipCode;
        private String address;
        private List<PartyTaxClassification> taxClassifications;
        private List<PartyTaxRegistration> taxRegistrations;
        private Map<String, String> additionalInformation;

        public Builder withId(String id) {
            this.id = id;
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

        public Builder withCity(String city) {
            this.city = city;
            return this;
        }

        public Builder withCounty(String county) {
            this.county = county;
            return this;
        }

        public Builder withZipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public Builder withAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder withTaxClassifications(PartyTaxClassification... taxClassification) {
            this.taxClassifications = Arrays.asList(taxClassification);
            return this;
        }

        @JsonProperty(JSONParameter.TAX_CLASSIFICATIONS)
        public Builder withTaxClassifications(List<PartyTaxClassification> taxClassifications) {
            this.taxClassifications = taxClassifications;
            return this;
        }

        public Builder withTaxRegistrations(PartyTaxRegistration... taxRegistrations) {
            this.taxRegistrations = Arrays.asList(taxRegistrations);
            return this;
        }

        @JsonProperty(JSONParameter.TAX_REGISTRATIONS)
        public Builder withTaxRegistrations(List<PartyTaxRegistration> taxRegistrations) {
            this.taxRegistrations = taxRegistrations;
            return this;
        }

        public Builder withAdditionalInformation(Map<String, String> additionalInformation) {
            this.additionalInformation = additionalInformation;
            return this;
        }

        public Party build() {
            return new Party(this);
        }
    }

    public String getId() {
        return id;
    }

    public CountryCode getCountryRegionCode() {
        return countryRegionCode;
    }

    public String getSubdivisionCode() {
        return subdivisionCode;
    }

    public String getCity() {
        return city;
    }

    public String getCounty() {
        return county;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getAddress() {
        return address;
    }

    public List<PartyTaxClassification> getTaxClassifications() {
        return taxClassifications;
    }

    public List<PartyTaxRegistration> getTaxRegistrations() {
        return taxRegistrations;
    }

    public Map<String, String> getAdditionalInformation() {
        return additionalInformation;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Party [id=");
        sb.append(id);
        sb.append(", countryRegionCode=");
        sb.append(countryRegionCode);
        sb.append(", subdivisionCode=");
        sb.append(subdivisionCode);
        sb.append(", city=");
        sb.append(city);
        sb.append(", county=");
        sb.append(county);
        sb.append(", zipCode=");
        sb.append(zipCode);
        sb.append(", address=");
        sb.append(address);
        sb.append(", taxClassifications=");
        sb.append(taxClassifications);
        sb.append(", taxRegistrations=");
        sb.append(taxRegistrations);
        sb.append(", additionalInformation=");
        sb.append(additionalInformation);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Party)) {
            return false;
        }
        Party party = (Party)o;
        return Objects.equals(getId(), party.getId()) && getCountryRegionCode() == party.getCountryRegionCode()
                && Objects.equals(getSubdivisionCode(), party.getSubdivisionCode())
                && Objects.equals(getCity(), party.getCity()) && Objects.equals(getCounty(), party.getCounty())
                && Objects.equals(getZipCode(), party.getZipCode()) && Objects.equals(getAddress(), party.getAddress())
                && Objects.equals(getTaxClassifications(), party.getTaxClassifications())
                && Objects.equals(getTaxRegistrations(), party.getTaxRegistrations())
                && Objects.equals(getAdditionalInformation(), party.getAdditionalInformation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCountryRegionCode(), getSubdivisionCode(), getCity(), getCounty(), getZipCode(),
                getAddress(), getTaxClassifications(), getTaxRegistrations(), getAdditionalInformation());
    }

    public static class JSONParameter {
        private JSONParameter() {
        }

        public static final String ID = "id";
        public static final String COUNTRY_REGION_CODE = "countryRegionCode";
        public static final String SUBDIVISION_CODE = "subdivisionCode";
        public static final String CITY = "city";
        public static final String COUNTY = "county";
        public static final String ZIP_CODE = "zipCode";
        public static final String ADDRESS = "address";
        public static final String TAX_CLASSIFICATIONS = "taxClassifications";
        public static final String TAX_REGISTRATIONS = "taxRegistrations";
        public static final String ADDITIONAL_INFORMATION = "additionalInformation";
    }
}
