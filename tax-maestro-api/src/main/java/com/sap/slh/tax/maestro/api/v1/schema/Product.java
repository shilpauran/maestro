package com.sap.slh.tax.maestro.api.v1.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMutualExclusionDetail;
import com.sap.slh.tax.maestro.api.v1.domain.ProductType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "The product information that can be assigned to one or more items.")
@JsonDeserialize(builder = Product.Builder.class)
@JsonInclude(Include.NON_NULL)
public class Product extends BaseModel {

    @ApiModelProperty(position = 0, required = true, value = "The identifier of each product in the business transaction.")
    @JsonProperty(JSONParameter.ID)
    private String id;

    @ApiModelProperty(position = 1, value = "The id that identifies the product in the tax service master data. You can only enter a value for this parameter in the request payload if you have maintained the product master data in the tax service.\n" + 
    		"If the consuming application provides the \"masterDataProductId\" parameter, the tax service uses the product type and product tax classification that is available in the master data to determine the tax amount.\n" + 
    		"As an alternative to the \"masterDataProductId\" parameter, you can specify the \"typeCode\" and \"taxClassifications\" parameters in the request payload.")
    @JsonProperty(JSONParameter.MASTER_DATA_PRODUCT_ID)
    private String masterDataProductId;

    @ApiModelProperty(position = 2, value = "The code that identifies the type of product (service or material) in the business transaction. As an alternative to the \"typeCode\" parameter, you can specify the \"masterDataProductId\" parameter in the request payload.", example = "MATERIAL")
    @JsonProperty(JSONParameter.TYPE_CODE)
    private ProductType typeCode;

    @ApiModelProperty(position = 3, value = "A list with the tax classifications assigned to this product. As an alternative to the \"taxClassifications\" parameter, you can specify the \"masterDataProductId\" parameter in the request payload.")
    @JsonProperty(JSONParameter.TAX_CLASSIFICATIONS)
    private List<ProductTaxClassification> taxClassifications;

    @ApiModelProperty(position = 4, value = "A list with the standard classifications assigned to this product.")
    @JsonProperty(JSONParameter.STANDARD_CLASSIFICATIONS)
    private List<StandardClassification> standardClassifications;

    @ApiModelProperty(position = 5, value = "The parameter that contains dynamic key/value pairs related to additional information on product level.", example = "{\"key1\":\"value1\",\"key2\":\"value2\"}")
    @JsonProperty(JSONParameter.ADDITIONAL_INFORMATION)
    private Map<String, String> additionalInformation;

    private Product(Builder builder) {
        this.id = builder.id;
        this.typeCode = builder.typeCode;
        this.masterDataProductId = builder.masterDataProductId;
        this.taxClassifications = builder.taxClassifications;
        this.standardClassifications = builder.standardClassifications;
        this.additionalInformation = builder.additionalInformation;
    }

    @Override
    public String getIndentifierValue() {
        return this.id;
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingAttributes = new ArrayList<>();

        validateMandatoryProperty(this.id, JSONParameter.ID, missingAttributes);
        validateListItems(this.taxClassifications, JSONParameter.TAX_CLASSIFICATIONS, missingAttributes);
        validateListItems(this.standardClassifications, JSONParameter.STANDARD_CLASSIFICATIONS, missingAttributes);

        if (StringUtils.isBlank(masterDataProductId)) {
            validateMandatoryProperty(this.typeCode, JSONParameter.TYPE_CODE, missingAttributes);
        }

        validateMutualExclusion(missingAttributes);

        InvalidModelException.checkExceptions(missingAttributes);
    }

    private void validateMutualExclusion(List<PropertyErrorDetail> missingAttributes) {
        if (StringUtils.isNotBlank(masterDataProductId)) {
            if (this.typeCode != null) {
                missingAttributes.add(new PropertyErrorMutualExclusionDetail(JSONParameter.PRODUCTS,
                        Arrays.asList(JSONParameter.MASTER_DATA_PRODUCT_ID, JSONParameter.TYPE_CODE)));
            }
            if (this.taxClassifications != null && !this.taxClassifications.isEmpty()) {
                missingAttributes.add(new PropertyErrorMutualExclusionDetail(JSONParameter.PRODUCTS,
                        Arrays.asList(JSONParameter.MASTER_DATA_PRODUCT_ID, JSONParameter.TAX_CLASSIFICATIONS)));
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String masterDataProductId;
        private ProductType typeCode;
        private List<ProductTaxClassification> taxClassifications;
        private List<StandardClassification> standardClassifications;
        private Map<String, String> additionalInformation;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withMasterDataProductId(String masterDataProductId) {
            this.masterDataProductId = masterDataProductId;
            return this;
        }

        public Builder withTypeCode(ProductType typeCode) {
            this.typeCode = typeCode;
            return this;
        }

        public Builder withTaxClassifications(ProductTaxClassification... taxClassifications) {
            this.taxClassifications = Arrays.asList(taxClassifications);
            return this;
        }

        @JsonProperty(JSONParameter.TAX_CLASSIFICATIONS)
        public Builder withTaxClassifications(List<ProductTaxClassification> taxClassifications) {
            this.taxClassifications = taxClassifications;
            return this;
        }

        public Builder withStandardClassifications(StandardClassification... standardClassifications) {
            this.standardClassifications = Arrays.asList(standardClassifications);
            return this;
        }

        @JsonProperty(JSONParameter.STANDARD_CLASSIFICATIONS)
        public Builder withStandardClassifications(List<StandardClassification> standardClassifications) {
            this.standardClassifications = standardClassifications;
            return this;
        }

        public Builder withAdditionalInformation(Map<String, String> additionalInformation) {
            this.additionalInformation = additionalInformation;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }

    public String getId() {
        return id;
    }

    public String getMasterDataProductId() {
        return masterDataProductId;
    }

    public ProductType getTypeCode() {
        return typeCode;
    }

    public List<ProductTaxClassification> getTaxClassifications() {
        return taxClassifications;
    }

    public List<StandardClassification> getStandardClassifications() {
        return standardClassifications;
    }

    public Map<String, String> getAdditionalInformation() {
        return additionalInformation;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Product [id=");
        sb.append(id);
        sb.append(", masterDataProductId=");
        sb.append(masterDataProductId);
        sb.append(", typeCode=");
        sb.append(typeCode);
        sb.append(", taxClassifications=");
        sb.append(taxClassifications);
        sb.append(", standardClassifications=");
        sb.append(standardClassifications);
        sb.append(", additionalInformation=");
        sb.append(additionalInformation);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        Product product = (Product)o;
        return Objects.equals(getId(), product.getId()) &&
            Objects.equals(getMasterDataProductId(), product.getMasterDataProductId()) &&
            getTypeCode() == product.getTypeCode() &&
            Objects.equals(getTaxClassifications(), product.getTaxClassifications()) &&
            Objects.equals(getStandardClassifications(), product.getStandardClassifications()) &&
            Objects.equals(getAdditionalInformation(), product.getAdditionalInformation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getMasterDataProductId(), getTypeCode(), getTaxClassifications(),
            getStandardClassifications(), getAdditionalInformation());
    }

    public static class JSONParameter {
        private JSONParameter() {
        }

        public static final String ID = "id";
        public static final String MASTER_DATA_PRODUCT_ID = "masterDataProductId";
        public static final String TYPE_CODE = "typeCode";
        public static final String TAX_CLASSIFICATIONS = "taxClassifications";
        public static final String STANDARD_CLASSIFICATIONS = "standardClassifications";
        public static final String ADDITIONAL_INFORMATION = "additionalInformation";
        public static final String PRODUCTS = "products";
    }
}
