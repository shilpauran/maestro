package com.sap.slh.tax.maestro.api.v0.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdditionalItemInformation.Builder.class)
@ApiModel
public class AdditionalItemInformation extends BaseModel {
    @ApiModelProperty(position = 0, value = "Information about the operation, such as the material origin, own production usage, and whether the service is sold electronically.")
    private String type;

    @ApiModelProperty(position = 1, value = "Information about the item type. The 'isServiceElectronicallySold' parameter can have the values 'Y' (yes) or 'N' (no). In the tax configuration application, you must maintain the product tax classification for all relevant tax countries where the product is sold electronically.")
    private String information;

    private AdditionalItemInformation(Builder builder) {
        type = builder.type;
        information = builder.information;
    }

    /**
     * Builder for {@link AdditionalItemInformation}.
     * 
     * @return Builder for {@link AdditionalItemInformation}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String type = null;
        private String information = null;

        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Builder withInformation(String information) {
            this.information = information;
            return this;
        }

        public AdditionalItemInformation build() {
            return new AdditionalItemInformation(this);
        }
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the information
     */
    public String getInformation() {
        return information;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AdditionalItemInformation{");
        sb.append("type='").append(type).append('\'');
        sb.append(", information='").append(information).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof  AdditionalItemInformation)) {
            return false;
        }
        AdditionalItemInformation that = (AdditionalItemInformation)o;
        return Objects.equals(type, that.type) &&
            Objects.equals(information, that.information);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, information);
    }
}
