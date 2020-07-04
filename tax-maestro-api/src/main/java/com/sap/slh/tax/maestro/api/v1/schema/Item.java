package com.sap.slh.tax.maestro.api.v1.schema;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

@ApiModel(description = "The item information in a business transaction.")
@JsonDeserialize(builder = Item.Builder.class)
@JsonInclude(Include.NON_NULL)
public class Item extends BaseModel {

    @ApiModelProperty(position = 0, required = true, value = "The identifier of each item in the business transaction.")
    @JsonProperty(JSONParameter.ID)
    private String id;

    @ApiModelProperty(position = 1, required = true, value = "The identifier of the product that you specify in the product list.")
    @JsonProperty(JSONParameter.ASSIGNED_PRODUCT_ID)
    private String assignedProductId;

    @ApiModelProperty(position = 2, required = true, value = "The quantity of this item. This can include fractions expressed using decimal notation.")
    @JsonProperty(JSONParameter.QUANTITY)
    private BigDecimal quantity;

    @ApiModelProperty(position = 3, required = true, value = "The unit price of the item.")
    @JsonProperty(JSONParameter.UNIT_PRICE)
    private BigDecimal unitPrice;

    @ApiModelProperty(position = 4, required = true, value = "A list with all the parties assigned to this item.")
    @JsonProperty(JSONParameter.ASSIGNED_PARTIES)
    private List<AssignedParty> assignedParties;

    @ApiModelProperty(position = 5, value = "A list with the costs assigned to this item.")
    @JsonProperty(JSONParameter.COSTS)
    private List<CostInformation> costs;

    @ApiModelProperty(position = 6, value = "The parameter that contains dynamic key/value pairs related to additional information on item level.", example = "{\"key1\":\"value1\",\"key2\":\"value2\"}")
    @JsonProperty(JSONParameter.ADDITIONAL_INFORMATION)
    private Map<String, String> additionalInformation;

    private Item(Builder builder) {
        this.id = builder.id;
        this.assignedProductId = builder.assignedProductId;
        this.quantity = builder.quantity;
        this.unitPrice = builder.unitPrice;
        this.costs = builder.costs;
        this.additionalInformation = builder.additionalInformation;
        this.assignedParties = builder.assignedParties;
    }

    @Override
    public String getIndentifierValue() {
        return this.id;
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingAttributes = new ArrayList<>();

        validateMandatoryProperty(this.id, JSONParameter.ID, missingAttributes);
        validateMandatoryProperty(this.assignedProductId, JSONParameter.ASSIGNED_PRODUCT_ID, missingAttributes);
        validateMandatoryProperty(this.quantity, JSONParameter.QUANTITY, missingAttributes);
        validateMandatoryProperty(this.unitPrice, JSONParameter.UNIT_PRICE, missingAttributes);
        validateListItems(this.costs, JSONParameter.COSTS, missingAttributes);
        mandatoryListProperty(this.assignedParties, JSONParameter.ASSIGNED_PARTIES, missingAttributes);
        validateListItems(this.assignedParties, JSONParameter.ASSIGNED_PARTIES, missingAttributes);

        InvalidModelException.checkExceptions(missingAttributes);
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String id;
        private String assignedProductId;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private List<CostInformation> costs;
        private Map<String, String> additionalInformation;
        private List<AssignedParty> assignedParties;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withAssignedProductId(String assignedProductId) {
            this.assignedProductId = assignedProductId;
            return this;
        }

        public Builder withQuantity(BigDecimal quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder withUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder withCosts(CostInformation... costs) {
            this.costs = Arrays.asList(costs);
            return this;
        }

        @JsonProperty(JSONParameter.COSTS)
        public Builder withCosts(List<CostInformation> costs) {
            this.costs = costs;
            return this;
        }

        public Builder withAdditionalInformation(Map<String, String> additionalInformation) {
            this.additionalInformation = additionalInformation;
            return this;
        }

        public Builder withAssignedParties(AssignedParty... assignedParties) {
            this.assignedParties = Arrays.asList(assignedParties);
            return this;
        }

        @JsonProperty(JSONParameter.ASSIGNED_PARTIES)
        public Builder withAssignedParties(List<AssignedParty> assignedParties) {
            this.assignedParties = assignedParties;
            return this;
        }

        public Item build() {
            return new Item(this);
        }
    }

    public String getId() {
        return id;
    }

    public String getAssignedProductId() {
        return assignedProductId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public List<CostInformation> getCosts() {
        return costs;
    }

    public Map<String, String> getAdditionalInformation() {
        return additionalInformation;
    }

    public List<AssignedParty> getAssignedParties() {
        return assignedParties;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Item [id=");
        sb.append(id);
        sb.append(", assignedProductId=");
        sb.append(assignedProductId);
        sb.append(", quantity=");
        sb.append(quantity);
        sb.append(", unitPrice=");
        sb.append(unitPrice);
        sb.append(", costs=");
        sb.append(costs);
        sb.append(", assignedParties=");
        sb.append(assignedParties);
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
        if (!(o instanceof Item)) {
            return false;
        }
        Item item = (Item)o;
        return Objects.equals(getId(), item.getId()) &&
            Objects.equals(getAssignedProductId(), item.getAssignedProductId()) &&
            Objects.equals(getQuantity(), item.getQuantity()) &&
            Objects.equals(getUnitPrice(), item.getUnitPrice()) &&
            Objects.equals(getAssignedParties(), item.getAssignedParties()) &&
            Objects.equals(getCosts(), item.getCosts()) &&
            Objects.equals(getAdditionalInformation(), item.getAdditionalInformation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAssignedProductId(), getQuantity(), getUnitPrice(), getAssignedParties(),
            getCosts(), getAdditionalInformation());
    }

    public static class JSONParameter {
        private JSONParameter() {
        }

        public static final String ID = "id";
        public static final String ASSIGNED_PRODUCT_ID = "assignedProductId";
        public static final String QUANTITY = "quantity";
        public static final String UNIT_PRICE = "unitPrice";
        public static final String ASSIGNED_PARTIES = "assignedParties";
        public static final String COSTS = "costs";
        public static final String ADDITIONAL_INFORMATION = "additionalInformation";
    }

}
