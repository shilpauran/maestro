package com.sap.slh.tax.maestro.api.v1.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

@ApiModel(description = "An error that indicates that the tax service cannot calculate the requested taxes.")
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ErrorResponse.Builder.class)
public class ErrorResponse extends BaseModel {
    @ApiModelProperty(position = 0, value = "The HTTP status code of the response as defined by [RFC 2616](https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html) of [Hypertext Transfer Protocol -- HTTP/1.1](https://www.w3.org/Protocols/rfc2616/rfc2616.html).", example = "400", required = true, allowableValues = "range[400, 599]")
    private Integer status;
    @ApiModelProperty(position = 1, value = "A general error message.", example = "The format of your request is invalid, review your request.", required = true)
    private String message;
    @ApiModelProperty(position = 2, value = "A list with more information on the errors.")
    private List<ErrorDetail> errorDetails;

    public static Builder builder() {
        return new Builder();
    }

    private ErrorResponse(Builder builder) {
        this.status = builder.status;
        this.message = builder.message;
        this.errorDetails = builder.errorDetails;
    }

    public static class Builder {
        private Integer status;
        private String message;
        private List<ErrorDetail> errorDetails;

        public Builder withStatus(Integer status) {
            this.status = status;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        @JsonProperty("errorDetails")
        public Builder withErrorDetails(List<ErrorDetail> errorDetails) {
            this.errorDetails = errorDetails;
            return this;
        }

        public Builder withErrorDetails(ErrorDetail... errorDetails) {
            this.errorDetails = Arrays.asList(errorDetails);
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(this);
        }
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<ErrorDetail> getErrorDetails() {
        return errorDetails;
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingProperties = new ArrayList<>();
        validateMandatoryProperty(status, "status", missingProperties);
        validateMandatoryProperty(message, "message", missingProperties);

        InvalidModelException.checkExceptions(missingProperties);
    }

    @Override
    public String toString() {
        StringBuilder builder2 = new StringBuilder();
        builder2.append("ErrorResponse [status=");
        builder2.append(status);
        builder2.append(", message=");
        builder2.append(message);
        builder2.append(", errorDetails=");
        builder2.append(errorDetails);
        builder2.append("]");
        return builder2.toString();
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
        return Objects.equals(getStatus(), that.getStatus()) &&
            Objects.equals(getMessage(), that.getMessage()) &&
            Objects.equals(getErrorDetails(), that.getErrorDetails());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStatus(), getMessage(), getErrorDetails());
    }
}
