package com.sap.slh.tax.maestro.api.v0.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.v0.domain.ErrorType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@ApiModel(description = "Invalid date in the request / Invalid country/region code in the request / Invalid type in the request / Any of the mandatory parameters is missing / Invalid currency in the request / Invalid GrossOrNet in the request / Invalid SaleOrPurchase in the request / Max length given in the schema / Location Type is missing/invalid / ExemptionCode is missing/invalid / More than 1 Ship_From in the exemptionDetils / More than 1 Ship_To in the exemptionDetails / More than 1 Ship_From in the Locations / More than 1 Ship_To in the Locations/ Invalid Zipcode in SHIP_TO Location for US / More than 1 shippingCost in items / Invalid shippingCost in items \" / Invalid fromDate / Invalid toDate / Invalid Date Range / Missing value for toDate / Missing value for fromDate.")
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ErrorResponse.Builder.class)
public class ErrorResponse extends BaseModel {
    @ApiModelProperty(position = 0, value = "The status code.")
    private int status;

    @ApiModelProperty(position = 1, value = "The message type.")
    private ErrorType type;

    @ApiModelProperty(position = 2, value = "The message content.")
    private String message;

    private ErrorResponse(Builder builder) {
        this.status = builder.status;
        this.type = builder.type;
        this.message = builder.message;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int status;
        private ErrorType type;
        private String message;

        public Builder withStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder withType(ErrorType type) {
            this.type = type;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(this);
        }

    }

    public int getStatus() {
        return status;
    }

    public ErrorType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ErrorResponse{");
        sb.append("status=").append(status);
        sb.append(", type=").append(type);
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ErrorResponse)) {
            return false;
        }
        ErrorResponse that = (ErrorResponse)o;
        return getStatus() == that.getStatus() &&
            getType() == that.getType() &&
            Objects.equals(getMessage(), that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStatus(), getType(), getMessage());
    }
}
