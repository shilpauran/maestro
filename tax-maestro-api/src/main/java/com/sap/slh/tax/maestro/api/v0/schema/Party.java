package com.sap.slh.tax.maestro.api.v0.schema;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;
import com.sap.slh.tax.maestro.api.v0.domain.LocationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = Party.Builder.class)
@ApiModel(description = "Details about other parties.")
public class Party extends BaseModel {
    @ApiModelProperty(position = 0, value = "The unique identifier of the party.")
    private String id;

    @ApiModelProperty(position = 1, value = "The role played by the party in the transaction.")
    private LocationType role;

    @ApiModelProperty(position = 2)
    private List<TaxRegistration> taxRegistration;

    @ApiModelProperty(hidden = true)
    private List<TaxRegistration> removedTaxRegistrations;

    private Party(Builder builder) {
        id = builder.id;
        role = builder.role;
        taxRegistration = builder.taxRegistration;
        removedTaxRegistrations = builder.removedTaxRegistrations;
    }

    /**
     * Builder for {@link Party}.
     * 
     * @return Builder for {@link Party}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id = null;
        private LocationType role = null;
        private List<TaxRegistration> taxRegistration = null;
        private List<TaxRegistration> removedTaxRegistrations = null;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withRole(LocationType role) {
            this.role = role;
            return this;
        }

        public Builder withTaxRegistration(List<TaxRegistration> taxRegistration) {
            this.taxRegistration = taxRegistration;
            return this;
        }

        public Builder withRemovedTaxRegistrations(List<TaxRegistration> removedTaxRegistrations) {
            this.removedTaxRegistrations = removedTaxRegistrations;
            return this;
        }

        public Party build() {
            return new Party(this);
        }
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingAttributes = new ArrayList<>();
        validateListItems(taxRegistration, "taxRegistration", missingAttributes);
        validateListItems(removedTaxRegistrations, "removedTaxRegistrations", missingAttributes);
        InvalidModelException.checkExceptions(missingAttributes);
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the role
     */
    public LocationType getRole() {
        return role;
    }

    /**
     * @return the taxRegistration
     */
    public List<TaxRegistration> getTaxRegistration() {
        return taxRegistration;
    }

    /**
     * @return the removedTaxRegistrations
     */
    public List<TaxRegistration> getRemovedTaxRegistrations() {
        return removedTaxRegistrations;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Party{");
        sb.append("id='").append(id).append('\'');
        sb.append(", role=").append(role);
        sb.append(", taxRegistration=").append(taxRegistration);
        sb.append(", removedTaxRegistrations=").append(removedTaxRegistrations);
        sb.append('}');
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
        return Objects.equals(getId(), party.getId()) &&
            getRole() == party.getRole() &&
            Objects.equals(getTaxRegistration(), party.getTaxRegistration()) &&
            Objects.equals(getRemovedTaxRegistrations(), party.getRemovedTaxRegistrations());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getRole(), getTaxRegistration(), getRemovedTaxRegistrations());
    }
}
