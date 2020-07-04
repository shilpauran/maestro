package com.sap.slh.tax.maestro.api.v1.schema;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

@ApiModel(description = "The costs that are assigned to this item.")
@JsonDeserialize(builder = CostInformation.Builder.class)
@JsonInclude(Include.NON_NULL)
public class CostInformation extends BaseModel {

    @ApiModelProperty(position = 0, required = true, value = "The type of cost, such as freight, discount, shipping, or others.")
    @JsonProperty(JSONParameter.TYPE)
    private String type;

    @ApiModelProperty(position = 1, required = true, value = "The amount for the cost type.")
    @JsonProperty(JSONParameter.AMOUNT)
    private BigDecimal amount;
    
    private CostInformation(Builder builder) {
    	this.type = builder.type;
    	this.amount = builder.amount;
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingAttributes = new ArrayList<>();
        validateMandatoryProperty(this.type, JSONParameter.TYPE, missingAttributes);
        validateMandatoryProperty(this.amount, JSONParameter.AMOUNT, missingAttributes);
        InvalidModelException.checkExceptions(missingAttributes);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
    	private String type;
    	private BigDecimal amount;
    	
    	public Builder withType(String type) {
            this.type = type;
            return this;
        }
    	
    	public Builder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }
    	
    	public CostInformation build() {
    		return new CostInformation(this);
    	}
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CostInformation{");
        sb.append("type='").append(type).append('\'');
        sb.append(", amount=").append(amount);
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
        return Objects.equals(getType(), that.getType()) &&
            Objects.equals(getAmount(), that.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getAmount());
    }

    public static class JSONParameter {
        private JSONParameter() {
        }

        public static final String TYPE = "type";
        public static final String AMOUNT = "amount";
    }

}
