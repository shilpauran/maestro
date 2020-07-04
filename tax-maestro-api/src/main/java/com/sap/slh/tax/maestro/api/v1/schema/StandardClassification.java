package com.sap.slh.tax.maestro.api.v1.schema;

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

@ApiModel(description = "The standard classification information assigned to this product.")
@JsonDeserialize(builder = StandardClassification.Builder.class)
@JsonInclude(Include.NON_NULL)
public class StandardClassification extends BaseModel {

    @ApiModelProperty(position = 0, required = true, value = "The code that represents a standard tax classification system for a product. For example, NCM, UNSPSC, ISIC, etc.")
    @JsonProperty(JSONParameter.CLASSIFICATION_SYSTEM)
    private String classificationSystem = null;

    @ApiModelProperty(position = 1, required = true, value = "The classification code of the product.")
    @JsonProperty(JSONParameter.PRODUCT_CODE)
    private String productCode = null;
    
    private StandardClassification(Builder builder) {
    	this.classificationSystem = builder.classificationSystem;
    	this.productCode = builder.productCode;
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingAttributes = new ArrayList<>();

        validateMandatoryProperty(this.classificationSystem, JSONParameter.CLASSIFICATION_SYSTEM, missingAttributes);
        validateMandatoryProperty(this.productCode, JSONParameter.PRODUCT_CODE, missingAttributes);

        InvalidModelException.checkExceptions(missingAttributes);
    }
    
    public static Builder builder() {
    	return new Builder();
    }
    
    public static class Builder {
    	private String classificationSystem;
    	private String productCode;
    	
    	public Builder withClassificationSystem(String classificationSystem) {
    		this.classificationSystem = classificationSystem;
    		return this;
    	}
    	
    	public Builder withProductCode(String productCode) {
    		this.productCode = productCode;
    		return this;
    	}
    	
    	public StandardClassification build() {
    		return new StandardClassification(this);
    	}
    }

    public String getClassificationSystem() {
        return classificationSystem;
    }

    public String getProductCode() {
        return productCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StandardClassification [classificationSystem=");
        sb.append(classificationSystem);
        sb.append(", productCode=");
        sb.append(productCode);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StandardClassification)) {
            return false;
        }
        StandardClassification that = (StandardClassification)o;
        return Objects.equals(getClassificationSystem(), that.getClassificationSystem()) &&
            Objects.equals(getProductCode(), that.getProductCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClassificationSystem(), getProductCode());
    }

    public static class JSONParameter {
        private JSONParameter() {
        }

        public static final String CLASSIFICATION_SYSTEM = "classificationSystem";
        public static final String PRODUCT_CODE = "productCode";
    }
}
