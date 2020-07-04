package com.sap.slh.tax.maestro.api.v1.schema;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;
import com.sap.slh.tax.maestro.api.v1.domain.PartyRole;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

@ApiModel(description = "The party that is assigned to this item.")
@JsonDeserialize(builder = AssignedParty.Builder.class)
@JsonInclude(Include.NON_NULL)
public class AssignedParty extends BaseModel {

    @ApiModelProperty(position = 0, required = true, value = "The identifier of the assigned party.")
    @JsonProperty(JSONParameter.ID)
    private String id;

    @ApiModelProperty(position = 1, required = true, value = "The role played by the party in this business transaction for a specific item.")
    @JsonProperty(JSONParameter.ROLE)
    private PartyRole role;

    private AssignedParty(Builder builder) {
        this.id = builder.id;
        this.role = builder.role;
    }
    
    @Override
    public String getIndentifierValue() {
        return this.id;
    }
    
    @Override
    public void validate() {
        List<PropertyErrorDetail> missingAttributes = new ArrayList<>();
        validateMandatoryProperty(this.id, JSONParameter.ID, missingAttributes);
        validateMandatoryProperty(this.role, JSONParameter.ROLE, missingAttributes);
        InvalidModelException.checkExceptions(missingAttributes);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private PartyRole role;
        
        public Builder withId(String id) {
            this.id = id;
            return this;
        }
        
        public Builder withRole(PartyRole role) {
            this.role = role;
            return this;
        }        
        
        public AssignedParty build() {
            return new AssignedParty(this);
        }
        
    }
    
    public String getId() {
        return id;
    }

    public PartyRole getRole() {
        return role;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AssignedParty{");
        sb.append("id='").append(id).append('\'');
        sb.append(", role=").append(role);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssignedParty)) {
            return false;
        }
        AssignedParty that = (AssignedParty)o;
        return Objects.equals(getId(), that.getId()) &&
            getRole() == that.getRole();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getRole());
    }

    public static class JSONParameter {
        private JSONParameter() {
        }

        public static final String ID = "id";
        public static final String ROLE = "role";
    }

}
