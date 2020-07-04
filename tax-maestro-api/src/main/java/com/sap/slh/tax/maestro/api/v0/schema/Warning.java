package com.sap.slh.tax.maestro.api.v0.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@ApiModel(description = "Return messages for scenarios where one or more input values are not taken into consideration for tax calculation. It is also relevant in cases in which the API overrides input values.")
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = Warning.Builder.class)
public class Warning extends BaseModel {
    @ApiModelProperty(position = 0, value = "The code of the warning.")
    private String code;

    @ApiModelProperty(position = 1, value = "The description of the warning.")
    private String description;

    private Warning(Builder builder) {
        code = builder.code;
        description = builder.description;
    }

    /**
     * Builder for {@link Warning}.
     * 
     * @return Builder for {@link Warning}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String code = null;
        private String description = null;

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Warning build() {
            return new Warning(this);
        }
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Warning{");
        sb.append("code='").append(code).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Warning)) {
            return false;
        }
        Warning warning = (Warning)o;
        return Objects.equals(getCode(), warning.getCode()) &&
            Objects.equals(getDescription(), warning.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getDescription());
    }
}
