package com.sap.slh.tax.maestro.api.v1.schema;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

@ApiModel(description = "The details of the error.", parent = ErrorResponse.class)
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ErrorDetail.Builder.class)
public class ErrorDetail extends BaseModel {
    @ApiModelProperty(value = "A detailed error message.", example = "The ''date'' parameter is mandatory in a request.", required = true)
    private String message;

    private ErrorDetail(Builder builder) {
        this.message = builder.message;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String message;

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public ErrorDetail build() {
            return new ErrorDetail(this);
        }
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingProperties = new ArrayList<>();
        validateMandatoryProperty(message, "message", missingProperties);

        InvalidModelException.checkExceptions(missingProperties);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ErrorDetail [message=");
        sb.append(message);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ErrorDetail)) {
            return false;
        }
        ErrorDetail that = (ErrorDetail)o;
        return Objects.equals(getMessage(), that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessage());
    }
}
