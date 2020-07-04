package com.sap.slh.tax.maestro.api.v0.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = TaxAttribute.Builder.class)
@ApiModel(description = "Other tax attributes.")
public class TaxAttribute extends BaseModel {
    @ApiModelProperty(position = 0, value = "Can have values like CST, CENQ, surchargeRate (margem de valor agregado), legalProvision.")
    private String attributeType;

    @ApiModelProperty(position = 1, value = "Corresponding value for the attributes.")
    private String attributeValue;

    private TaxAttribute(Builder builder) {
        attributeType = builder.attributeType;
        attributeValue = builder.attributeValue;
    }

    /**
     * Builder for {@link TaxAttribute}.
     * 
     * @return Builder for {@link TaxAttribute}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String attributeType = null;
        private String attributeValue = null;

        public Builder withAttributeType(String attributeType) {
            this.attributeType = attributeType;
            return this;
        }

        public Builder withAttributeValue(String attributeValue) {
            this.attributeValue = attributeValue;
            return this;
        }

        public TaxAttribute build() {
            return new TaxAttribute(this);
        }
    }

    /**
     * @return the attributeType
     */
    public String getAttributeType() {
        return attributeType;
    }

    /**
     * @return the attributeValue
     */
    public String getAttributeValue() {
        return attributeValue;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaxAttribute{");
        sb.append("attributeType='").append(attributeType).append('\'');
        sb.append(", attributeValue='").append(attributeValue).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaxAttribute)) {
            return false;
        }
        TaxAttribute that = (TaxAttribute)o;
        return Objects.equals(getAttributeType(), that.getAttributeType()) &&
            Objects.equals(getAttributeValue(), that.getAttributeValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAttributeType(), getAttributeValue());
    }
}
