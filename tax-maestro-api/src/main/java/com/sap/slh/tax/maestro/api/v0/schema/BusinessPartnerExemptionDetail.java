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

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = BusinessPartnerExemptionDetail.Builder.class)
@ApiModel(description = "The exemption details of a business partner.")
public class BusinessPartnerExemptionDetail extends BaseModel {
    @ApiModelProperty(position = 0, value = "The location at which the product has the specified exemption.")
    private LocationType locationType;

    @ApiModelProperty(position = 1, value = "The exemption code classification for the product.")
    @JsonProperty("exemptionreasoncode")
    private String exemptionReasonCode;

    @ApiModelProperty(position = 2, value = "The identification of the tax rate type on which you apply the exemption.")
    private String taxType;

    private BusinessPartnerExemptionDetail(Builder builder) {
        locationType = builder.locationType;
        exemptionReasonCode = builder.exemptionReasonCode;
        taxType = builder.taxType;
    }

    /**
     * Builder for {@link BusinessPartnerExemptionDetail}.
     * 
     * @return Builder for {@link BusinessPartnerExemptionDetail}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocationType locationType = null;
        private String exemptionReasonCode = null;
        private String taxType = null;

        public Builder withLocationType(LocationType locationType) {
            this.locationType = locationType;
            return this;
        }

        @JsonProperty("exemptionreasoncode")
        public Builder withExemptionReasonCode(String exemptionReasonCode) {
            this.exemptionReasonCode = exemptionReasonCode;
            return this;
        }

        public Builder withTaxType(String taxType) {
            this.taxType = taxType;
            return this;
        }

        public BusinessPartnerExemptionDetail build() {
            return new BusinessPartnerExemptionDetail(this);
        }
    }

    /**
     * @return the locationType
     */
    public LocationType getLocationType() {
        return locationType;
    }

    /**
     * @return the exemptionReasonCode
     */
    public String getExemptionReasonCode() {
        return exemptionReasonCode;
    }

    /**
     * @return the taxType
     */
    public String getTaxType() {
        return taxType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BusinessPartnerExemptionDetail{");
        sb.append("locationType=").append(locationType);
        sb.append(", exemptionReasonCode='").append(exemptionReasonCode).append('\'');
        sb.append(", taxType='").append(taxType).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BusinessPartnerExemptionDetail)) {
            return false;
        }
        BusinessPartnerExemptionDetail that = (BusinessPartnerExemptionDetail)o;
        return getLocationType() == that.getLocationType() &&
            Objects.equals(getExemptionReasonCode(), that.getExemptionReasonCode()) &&
            Objects.equals(getTaxType(), that.getTaxType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocationType(), getExemptionReasonCode(), getTaxType());
    }
}
