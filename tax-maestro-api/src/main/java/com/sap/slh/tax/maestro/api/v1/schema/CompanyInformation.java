package com.sap.slh.tax.maestro.api.v1.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

@ApiModel(description = "The information on the company that triggers the business transaction.")
@JsonDeserialize(builder = CompanyInformation.Builder.class)
@JsonInclude(Include.NON_NULL)
public class CompanyInformation extends BaseModel {

    @ApiModelProperty(position = 0, required = true, value = "The ID of the party that is the company triggering the business transaction.")
    @JsonProperty(JSONParameter.ASSIGNED_PARTY_ID)
    private String assignedPartyId;

    @ApiModelProperty(position = 1, value = "Determines if the tax calculation service applies deferred tax rules to this company. These rules apply when the company has the required authorizations for tax deferral.", example = "false")
    @JsonProperty(JSONParameter.IS_DEFERRED_TAX_ENABLED)
    private Boolean isDeferredTaxEnabled;

    @ApiModelProperty(position = 2, value = "The parameter that contains dynamic key/value pairs related to additional information on the company level.", example = "{\"key1\":\"value1\",\"key2\":\"value2\"}")
    @JsonProperty(JSONParameter.ADDITIONAL_INFORMATION)
    private Map<String, String> additionalInformation;

    private CompanyInformation(Builder builder) {
        this.assignedPartyId = builder.assignedPartyId;
        this.isDeferredTaxEnabled = builder.isDeferredTaxEnabled;
        this.additionalInformation = builder.additionalInformation;
    }

    @Override
    public String getIndentifierValue() {
        return this.assignedPartyId;
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingAttributes = new ArrayList<>();
        validateMandatoryProperty(this.assignedPartyId, JSONParameter.ASSIGNED_PARTY_ID, missingAttributes);
        InvalidModelException.checkExceptions(missingAttributes);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String assignedPartyId;
        private Boolean isDeferredTaxEnabled;
        private Map<String, String> additionalInformation;

        public Builder withAssignedPartyId(String assignedPartyId) {
            this.assignedPartyId = assignedPartyId;
            return this;
        }

        public Builder withIsDeferredTaxEnabled(Boolean isDeferredTaxEnabled) {
            this.isDeferredTaxEnabled = isDeferredTaxEnabled;
            return this;
        }

        public Builder withAdditionalInformation(Map<String, String> additionalInformation) {
            this.additionalInformation = additionalInformation;
            return this;
        }

        public CompanyInformation build() {
            return new CompanyInformation(this);
        }

    }

    public String getAssignedPartyId() {
        return assignedPartyId;
    }

    public Boolean getIsDeferredTaxEnabled() {
        return isDeferredTaxEnabled;
    }

    public Map<String, String> getAdditionalInformation() {
        return additionalInformation;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CompanyInformation{");
        sb.append("assignedPartyId='").append(assignedPartyId).append('\'');
        sb.append(", isDeferredTaxEnabled=").append(isDeferredTaxEnabled);
        sb.append(", additionalInformation=").append(additionalInformation);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompanyInformation)) {
            return false;
        }
        CompanyInformation that = (CompanyInformation)o;
        return Objects.equals(getAssignedPartyId(), that.getAssignedPartyId()) &&
            Objects.equals(getIsDeferredTaxEnabled(), that.getIsDeferredTaxEnabled()) &&
            Objects.equals(getAdditionalInformation(), that.getAdditionalInformation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAssignedPartyId(), getIsDeferredTaxEnabled(), getAdditionalInformation());
    }

    public static class JSONParameter {
        private JSONParameter() {
        }

        public static final String ASSIGNED_PARTY_ID = "assignedPartyId";
        public static final String IS_DEFERRED_TAX_ENABLED = "isDeferredTaxEnabled";
        public static final String ADDITIONAL_INFORMATION = "additionalInformation";
    }

}
