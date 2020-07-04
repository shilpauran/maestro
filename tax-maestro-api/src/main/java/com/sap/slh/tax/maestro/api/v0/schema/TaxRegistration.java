package com.sap.slh.tax.maestro.api.v0.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.v0.domain.LocationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@ApiModel(description = "The tax registration details.")
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = TaxRegistration.Builder.class)
public class TaxRegistration extends BaseModel {
    @ApiModelProperty(position = 0, value = "The type of location.")
    private LocationType locationType;

    @ApiModelProperty(position = 0, value = "The code for the tax number type.")
    private String taxNumberTypeCode;

    @ApiModelProperty(position = 0, value = "The corresponding tax number for the tax number type code.")
    private String taxNumber;

    private TaxRegistration(Builder builder) {
        locationType = builder.locationType;
        taxNumberTypeCode = builder.taxNumberTypeCode;
        taxNumber = builder.taxNumber;
    }

    /**
     * Builder for {@link TaxRegistration}.
     *
     * @return Builder for {@link TaxRegistration}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocationType locationType = null;
        private String taxNumberTypeCode = null;
        private String taxNumber = null;

        public Builder withLocationType(LocationType locationType) {
            this.locationType = locationType;
            return this;
        }

        public Builder withTaxNumberTypeCode(String taxNumberTypeCode) {
            this.taxNumberTypeCode = taxNumberTypeCode;
            return this;
        }

        public Builder withTaxNumber(String taxNumber) {
            this.taxNumber = taxNumber;
            return this;
        }

        public TaxRegistration build() {
            return new TaxRegistration(this);
        }
    }

    /**
     * @return the locationType
     */
    public LocationType getLocationType() {
        return locationType;
    }

    /**
     * @return the taxNumberTypeCode
     */
    public String getTaxNumberTypeCode() {
        return taxNumberTypeCode;
    }

    /**
     * @return the taxNumber
     */
    public String getTaxNumber() {
        return taxNumber;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaxRegistration{");
        sb.append("locationType=").append(locationType);
        sb.append(", taxNumberTypeCode='").append(taxNumberTypeCode).append('\'');
        sb.append(", taxNumber='").append(taxNumber).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaxRegistration)) {
            return false;
        }
        TaxRegistration that = (TaxRegistration)o;
        return getLocationType() == that.getLocationType() &&
            Objects.equals(getTaxNumberTypeCode(), that.getTaxNumberTypeCode()) &&
            Objects.equals(getTaxNumber(), that.getTaxNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocationType(), getTaxNumberTypeCode(), getTaxNumber());
    }
}
