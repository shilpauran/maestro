package com.sap.slh.tax.maestro.api.v0.schema;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;
import com.sap.slh.tax.maestro.api.v0.domain.BooleanValue;
import com.sap.slh.tax.maestro.api.v0.domain.LocationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@ApiModel(description = "List of Addresses.")
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = Location.Builder.class)
public class Location extends BaseModel {
    @ApiModelProperty(required = true, position = 0, value = "The type of address.")
    private LocationType type;

    @ApiModelProperty(position = 1, value = "The first line of the address.")
    private String addressLine1;

    @ApiModelProperty(position = 2, value = "The second line of the address.")
    private String addressLine2;

    @ApiModelProperty(position = 3, value = "The third line of the address.")
    private String addressLine3;

    @ApiModelProperty(required = true, position = 4, value = "The ZIP or postal code.")
    private String zipCode;

    @ApiModelProperty(position = 5, value = "The name of the city.")
    private String city;

    @ApiModelProperty(position = 6, value = "The code of the state or province; do not add the country/region as a prefix or suffix.")
    private String state;

    @ApiModelProperty(position = 7, value = "The name of the county.")
    private String county;

    @ApiModelProperty(position = 8, value = "The ID of the address, as maintained in the tax configuration application for the various location addresses. The 'addressID' parameter is relevant only when location addresses are maintained in the tax configuration application for a company, customer, or supplier. For sale transactions and the location type 'SHIP_FROM', the API checks for an address maintained for the company ID that is specified in the request payload. For sale transactions and the location type 'SHIP_TO', the API checks for an address maintained for the business partner ID that is specified in the request payload. For purchase transactions and the location type 'SHIP_TO', the API checks for an address maintained for the company ID that is specified in the request payload. For sale transactions and the location type 'SHIP_FROM', the API checks for an address maintained for the business partner ID that is specified in the request payload. Note: if you specify an address ID in the request, you must also specify a company ID and business partner ID.")
    private String addressId;

    @ApiModelProperty(required = true, position = 9, value = "The name of the country/region. Enter the 2-character country/region code according to the standards of ISO 3166-1 alpha-2.")
    private CountryCode country;

    @ApiModelProperty(position = 10, value = "Shows whether the company is registered with the tax authorities at this location. The application calling the API must identify whether a company has a VAT establishment for tax purposes in the selling country/region. If the company has a VAT establishment, the calling application must specify 'Y' as the value for this parameter \"isCompanyTaxRegistered\" for the selling location. If the company is not established for VAT purposes, the calling application must specify 'N' as the value for this parameter \"isCompanyTaxRegistered\" for the selling location.")
    private BooleanValue isCompanyTaxRegistered;

    @ApiModelProperty(position = 11, value = "Shows whether the business partner is registered for tax purposes. The tax service does not differentiate between business-to-business (B2B) and business-to-consumer (B2C) transactions. B2B transactions: the calling application must specify 'Y' as the value of this parameter. B2C transactions: the calling application must specify 'N' as the value of this parameter.")
    private BooleanValue isBusinessPartnerTaxRegistered;


    private Location(Builder builder) {
        type = builder.type;
        addressLine1 = builder.addressLine1;
        addressLine2 = builder.addressLine2;
        addressLine3 = builder.addressLine3;
        zipCode = builder.zipCode;
        city = builder.city;
        state = builder.state;
        county = builder.county;
        addressId = builder.addressId;
        country = builder.country;
        isCompanyTaxRegistered = builder.isCompanyTaxRegistered;
        isBusinessPartnerTaxRegistered = builder.isBusinessPartnerTaxRegistered;
    }

    /**
     * Builder for {@link Location}.
     * 
     * @return Builder for {@link Location}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocationType type = null;
        private String addressLine1 = null;
        private String addressLine2 = null;
        private String addressLine3 = null;
        private String zipCode = null;
        private String city = null;
        private String state = null;
        private String county = null;
        private String addressId = null;
        private CountryCode country = null;
        private BooleanValue isCompanyTaxRegistered = null;
        private BooleanValue isBusinessPartnerTaxRegistered = null;

        public Builder withType(LocationType type) {
            this.type = type;
            return this;
        }

        public Builder withAddressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
            return this;
        }

        public Builder withAddressLine2(String addressLine2) {
            this.addressLine2 = addressLine2;
            return this;
        }

        public Builder withAddressLine3(String addressLine3) {
            this.addressLine3 = addressLine3;
            return this;
        }

        public Builder withZipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public Builder withCity(String city) {
            this.city = city;
            return this;
        }

        public Builder withState(String state) {
            this.state = state;
            return this;
        }

        public Builder withCounty(String county) {
            this.county = county;
            return this;
        }

        public Builder withAddressId(String addressId) {
            this.addressId = addressId;
            return this;
        }

        public Builder withCountry(CountryCode country) {
            this.country = country;
            return this;
        }

        public Builder withIsCompanyTaxRegistered(BooleanValue isCompanyTaxRegistered) {
            this.isCompanyTaxRegistered = isCompanyTaxRegistered;
            return this;
        }

        public Builder withIsBusinessPartnerTaxRegistered(BooleanValue isBusinessPartnerTaxRegistered) {
            this.isBusinessPartnerTaxRegistered = isBusinessPartnerTaxRegistered;
            return this;
        }

        public Location build() {
            return new Location(this);
        }
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingProperties = new ArrayList<>();
        validateMandatoryProperty(type, "type", missingProperties);
        validateMandatoryProperty(zipCode, "zipCode", missingProperties);
        validateMandatoryProperty(country, "country", missingProperties);
        InvalidModelException.checkExceptions(missingProperties);
    }

    /**
     * @return the type
     */
    public LocationType getType() {
        return type;
    }

    /**
     * @return the addressLine1
     */
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     * @return the addressLine2
     */
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     * @return the addressLine3
     */
    public String getAddressLine3() {
        return addressLine3;
    }

    /**
     * @return the zipCode
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @return the county
     */
    public String getCounty() {
        return county;
    }

    /**
     * @return the addressId
     */
    public String getAddressId() {
        return addressId;
    }

    /**
     * @return the country
     */
    public CountryCode getCountry() {
        return country;
    }

    /**
     * @return the isCompanyTaxRegistered
     */
    public BooleanValue getIsCompanyTaxRegistered() {
        return isCompanyTaxRegistered;
    }

    /**
     * @return the isBusinessPartnerTaxRegistered
     */
    public BooleanValue getIsBusinessPartnerTaxRegistered() {
        return isBusinessPartnerTaxRegistered;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Location{");
        sb.append("type=").append(type);
        sb.append(", addressLine1='").append(addressLine1).append('\'');
        sb.append(", addressLine2='").append(addressLine2).append('\'');
        sb.append(", addressLine3='").append(addressLine3).append('\'');
        sb.append(", zipCode='").append(zipCode).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append(", county='").append(county).append('\'');
        sb.append(", addressId='").append(addressId).append('\'');
        sb.append(", country=").append(country);
        sb.append(", isCompanyTaxRegistered=").append(isCompanyTaxRegistered);
        sb.append(", isBusinessPartnerTaxRegistered=").append(isBusinessPartnerTaxRegistered);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Location)) {
            return false;
        }
        Location location = (Location)o;
        return getType() == location.getType() &&
            Objects.equals(getAddressLine1(), location.getAddressLine1()) &&
            Objects.equals(getAddressLine2(), location.getAddressLine2()) &&
            Objects.equals(getAddressLine3(), location.getAddressLine3()) &&
            Objects.equals(getZipCode(), location.getZipCode()) &&
            Objects.equals(getCity(), location.getCity()) &&
            Objects.equals(getState(), location.getState()) &&
            Objects.equals(getCounty(), location.getCounty()) &&
            Objects.equals(getAddressId(), location.getAddressId()) &&
            getCountry() == location.getCountry() &&
            getIsCompanyTaxRegistered() == location.getIsCompanyTaxRegistered() &&
            getIsBusinessPartnerTaxRegistered() == location.getIsBusinessPartnerTaxRegistered();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getAddressLine1(), getAddressLine2(), getAddressLine3(), getZipCode(), getCity(),
            getState(), getCounty(), getAddressId(), getCountry(), getIsCompanyTaxRegistered(), getIsBusinessPartnerTaxRegistered());
    }
}
