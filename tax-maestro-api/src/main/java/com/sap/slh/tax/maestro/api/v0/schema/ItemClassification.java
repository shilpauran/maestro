package com.sap.slh.tax.maestro.api.v0.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ItemClassification.Builder.class)
@ApiModel
public class ItemClassification extends BaseModel {
    @ApiModelProperty(position = 0, value = "The identifier of the item.")
    private String itemStandardClassificationSystemCode;

    @ApiModelProperty(position = 1, value = "The classification code for the system code.")
    private String itemStandardClassificationCode;

    private ItemClassification(Builder builder) {
        itemStandardClassificationSystemCode = builder.itemStandardClassificationSystemCode;
        itemStandardClassificationCode = builder.itemStandardClassificationCode;
    }

    /**
     * Builder for {@link ItemClassification}.
     * 
     * @return Builder for {@link ItemClassification}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String itemStandardClassificationSystemCode = null;
        private String itemStandardClassificationCode = null;

        public Builder withItemStandardClassificationSystemCode(String itemStandardClassificationSystemCode) {
            this.itemStandardClassificationSystemCode = itemStandardClassificationSystemCode;
            return this;
        }

        public Builder withItemStandardClassificationCode(String itemStandardClassificationCode) {
            this.itemStandardClassificationCode = itemStandardClassificationCode;
            return this;
        }

        public ItemClassification build() {
            return new ItemClassification(this);
        }
    }

    /**
     * @return the itemStandardClassificationSystemCode
     */
    public String getItemStandardClassificationSystemCode() {
        return itemStandardClassificationSystemCode;
    }

    /**
     * @return the itemStandardClassificationCode
     */
    public String getItemStandardClassificationCode() {
        return itemStandardClassificationCode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ItemClassification{");
        sb.append("itemStandardClassificationSystemCode='").append(itemStandardClassificationSystemCode).append('\'');
        sb.append(", itemStandardClassificationCode='").append(itemStandardClassificationCode).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemClassification)) {
            return false;
        }
        ItemClassification that = (ItemClassification)o;
        return Objects.equals(getItemStandardClassificationSystemCode(), that.getItemStandardClassificationSystemCode()) &&
            Objects.equals(getItemStandardClassificationCode(), that.getItemStandardClassificationCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItemStandardClassificationSystemCode(), getItemStandardClassificationCode());
    }
}
