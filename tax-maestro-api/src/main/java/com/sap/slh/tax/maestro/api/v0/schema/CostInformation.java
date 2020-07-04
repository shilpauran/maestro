package com.sap.slh.tax.maestro.api.v0.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = CostInformation.Builder.class)
@ApiModel
public class CostInformation extends BaseModel {
    @ApiModelProperty(position = 0, value = "The type of cost, such as freight, discount, shipping, or others.")
    private String costType;

    @ApiModelProperty(position = 1, value = "The corresponding amount for the cost type.")
    private String amount;

    private CostInformation(Builder builder) {
        costType = builder.costType;
        amount = builder.amount;
    }

    /**
     * Builder for {@link CostInformation}.
     * 
     * @return Builder for {@link CostInformation}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String costType = null;
        private String amount = null;

        public Builder withCostType(String costType) {
            this.costType = costType;
            return this;
        }

        public Builder withAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public CostInformation build() {
            return new CostInformation(this);
        }
    }

    /**
     * @return the costType
     */
    public String getCostType() {
        return costType;
    }

    /**
     * @return the amount
     */
    public String getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CostInformation{");
        sb.append("costType='").append(costType).append('\'');
        sb.append(", amount='").append(amount).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CostInformation)) {
            return false;
        }
        CostInformation that = (CostInformation)o;
        return Objects.equals(getCostType(), that.getCostType()) &&
            Objects.equals(getAmount(), that.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCostType(), getAmount());
    }
}
