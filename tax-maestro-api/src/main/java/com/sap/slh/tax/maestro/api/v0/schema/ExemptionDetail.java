package com.sap.slh.tax.maestro.api.v0.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.v0.domain.LocationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@ApiModel(description = "The list of details on the tax exemption.")
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ExemptionDetail.Builder.class)
public class ExemptionDetail extends BaseModel {
    @ApiModelProperty(position = 0, value = "The location at which the product has the specified exemption.")
    @JsonProperty("LocationType")
    private LocationType locationType;

    @ApiModelProperty(position = 1, value = "The Exemption Code classification for the product.")
    private String exemptionCode;

    @ApiModelProperty(position = 2, value = "The classification code for a tax rate. The tax authorities of the country/region in which the the tax is applicable determine these rates.")
    private String tariffId;

    @ApiModelProperty(position = 3, value = "The identification of the tax rate type on which you apply the exemption.")
    private String taxType;

    @ApiModelProperty(position = 4, value = "The identification of the tax rate type.")
    private String taxRateType;

    @ApiModelProperty(position = 5, value = "The code of the region in which the product exemption is valid.")
    private String region;

    private ExemptionDetail(Builder builder) {
        locationType = builder.locationType;
        exemptionCode = builder.exemptionCode;
        tariffId = builder.tariffId;
        taxType = builder.taxType;
        taxRateType = builder.taxRateType;
        region = builder.region;
    }

    /**
     * Builder for {@link ExemptionDetail}.
     * 
     * @return Builder for {@link ExemptionDetail}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocationType locationType = null;
        private String exemptionCode = null;
        private String tariffId = null;
        private String taxType = null;
        private String taxRateType = null;
        private String region = null;

        @JsonProperty("LocationType")
        public Builder withLocationType(LocationType locationType) {
            this.locationType = locationType;
            return this;
        }

        public Builder withExemptionCode(String exemptionCode) {
            this.exemptionCode = exemptionCode;
            return this;
        }

        public Builder withTariffId(String tariffId) {
            this.tariffId = tariffId;
            return this;
        }

        public Builder withTaxType(String taxType) {
            this.taxType = taxType;
            return this;
        }

        public Builder withTaxRateType(String taxRateType) {
            this.taxRateType = taxRateType;
            return this;
        }

        public Builder withRegion(String region) {
            this.region = region;
            return this;
        }

        public ExemptionDetail build() {
            return new ExemptionDetail(this);
        }
    }

    /**
     * @return the locationType
     */
    public LocationType getLocationType() {
        return locationType;
    }

    /**
     * @return the exemptionCode
     */
    public String getExemptionCode() {
        return exemptionCode;
    }

    /**
     * @return the tariffId
     */
    public String getTariffId() {
        return tariffId;
    }

    /**
     * @return the taxType
     */
    public String getTaxType() {
        return taxType;
    }

    /**
     * @return the taxRateType
     */
    public String getTaxRateType() {
        return taxRateType;
    }

    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExemptionDetail{");
        sb.append("locationType=").append(locationType);
        sb.append(", exemptionCode='").append(exemptionCode).append('\'');
        sb.append(", tariffId='").append(tariffId).append('\'');
        sb.append(", taxType='").append(taxType).append('\'');
        sb.append(", taxRateType='").append(taxRateType).append('\'');
        sb.append(", region='").append(region).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExemptionDetail)) {
            return false;
        }
        ExemptionDetail that = (ExemptionDetail)o;
        return getLocationType() == that.getLocationType() &&
            Objects.equals(getExemptionCode(), that.getExemptionCode()) &&
            Objects.equals(getTariffId(), that.getTariffId()) &&
            Objects.equals(getTaxType(), that.getTaxType()) &&
            Objects.equals(getTaxRateType(), that.getTaxRateType()) &&
            Objects.equals(getRegion(), that.getRegion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocationType(), getExemptionCode(), getTariffId(), getTaxType(), getTaxRateType(), getRegion());
    }
}