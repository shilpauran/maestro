package com.sap.slh.tax.maestro.api.v0.schema;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.common.deserializer.CustomBigDecimalDeserializer;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;
import com.sap.slh.tax.maestro.api.v0.domain.ItemType;
import com.sap.slh.tax.maestro.api.v0.domain.TaxCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = Item.Builder.class)
@ApiModel
public class Item extends BaseModel {
    @ApiModelProperty(required = true, position = 0, value = "A unique identifier for each item in the transaction.")
    private String id;

    @ApiModelProperty(position = 1, value = "Pass the item code, for example, SKU of a product. Else pass the productid as maintained in the Tax configuration application. In the second case, the service considers the relevant tax classification as maintained in the application.")
    private String itemCode;

    @ApiModelProperty(required = true, position = 2, value = "The quantity of the line item. This can include fractions expressed using decimal places.")
    @JsonFormat(shape = Shape.STRING)
    private BigDecimal quantity;

    @ApiModelProperty(required = true, position = 3, value = "The unit price of the line item.")
    @JsonFormat(shape = Shape.STRING)
    private BigDecimal unitPrice;

    @ApiModelProperty(position = 4, value = "To determine tax on shipping costs, specify the tax as a separate item. To indicate that an item relates to shipping costs, you must enter 'Y' as the value of this parameter. For the United States of America, tax is calculated if shipping is taxable in the particular jurisdiction. If shipping is not taxable, the API returns zero tax for the item.")
    private String shippingCost;

    @ApiModelProperty(position = 5, value = "The type of product - service or material. Enter 's' for service and 'm' for material.")
    private ItemType itemType;

    @ApiModelProperty(position = 6, value = "")
    private List<ExemptionDetail> exemptionDetails;

    @ApiModelProperty(position = 7, value = "The ID of the exemption certificate, as maintained in the tax configuration application. The API considers the exemption only when the following conditions are met: 1. The certificate ID is valid on the date in the request payload. 2. The exemption details are maintained in the tax configuration application.")
    private String certificateId;

    @ApiModelProperty(position = 8, value = "")
    private List<ItemClassification> itemClassifications;

    @ApiModelProperty(position = 9, value = "")
    private List<AdditionalItemInformation> additionalItemInformation;

    @ApiModelProperty(position = 10, value = "")
    private List<CostInformation> costInformation;

    @ApiModelProperty(position = 11, value = "Determines if the taxes to be calculated are product taxes or withholding taxes. This parameter is valid only for direct calculation scenarios.")
    private TaxCategory taxCategory;

    @ApiModelProperty(position = 12, value = "Determines the country/region for which the system calculates taxes. The system uses the 2-character country/region code described on ISO 3166-1 alpha-2. This parameter is valid only for direct calculation scenarios.")
    private CountryCode taxCodeCountry;

    @ApiModelProperty(position = 13, value = "Determines which rule the system uses to calculate taxes. This parameter is valid only for direct calculation scenarios.")
    private String taxCode;

    @ApiModelProperty(position = 14, value = "Determines the region for which the system calculates taxes. This parameter is optional and valid only for direct calculation scenarios.")
    private String taxCodeRegion;

    private Item(Builder builder) {
        id = builder.id;
        itemCode = builder.itemCode;
        quantity = builder.quantity;
        unitPrice = builder.unitPrice;
        shippingCost = builder.shippingCost;
        itemType = builder.itemType;
        exemptionDetails = builder.exemptionDetails;
        certificateId = builder.certificateId;
        itemClassifications = builder.itemClassifications;
        additionalItemInformation = builder.additionalItemInformation;
        costInformation = builder.costInformation;
        taxCategory = builder.taxCategory;
        taxCodeCountry = builder.taxCodeCountry;
        taxCode = builder.taxCode;
        taxCodeRegion = builder.taxCodeRegion;
    }

    /**
     * Builder for {@link Item}.
     * 
     * @return Builder for {@link Item}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id = null;
        private String itemCode = null;
        @JsonDeserialize(using = CustomBigDecimalDeserializer.class)
        private BigDecimal quantity = null;
        @JsonDeserialize(using = CustomBigDecimalDeserializer.class)
        private BigDecimal unitPrice = null;
        private String shippingCost = null;
        private ItemType itemType = null;
        private List<ExemptionDetail> exemptionDetails = null;
        private String certificateId = null;
        private List<ItemClassification> itemClassifications = null;
        private List<AdditionalItemInformation> additionalItemInformation = null;
        private List<CostInformation> costInformation = null;
        private TaxCategory taxCategory = null;
        private CountryCode taxCodeCountry = null;
        private String taxCode = null;
        private String taxCodeRegion = null;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withItemCode(String itemCode) {
            this.itemCode = itemCode;
            return this;
        }

        public Builder withQuantity(BigDecimal quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder withUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder withShippingCost(String shippingCost) {
            this.shippingCost = shippingCost;
            return this;
        }

        public Builder withItemType(ItemType itemType) {
            this.itemType = itemType;
            return this;
        }

        public Builder withExemptionDetails(List<ExemptionDetail> exemptionDetails) {
            this.exemptionDetails = exemptionDetails;
            return this;
        }

        public Builder withCertificateId(String certificateId) {
            this.certificateId = certificateId;
            return this;
        }

        public Builder withItemClassifications(List<ItemClassification> itemClassifications) {
            this.itemClassifications = itemClassifications;
            return this;
        }

        public Builder withAdditionalItemInformation(List<AdditionalItemInformation> additionalItemInformation) {
            this.additionalItemInformation = additionalItemInformation;
            return this;
        }

        public Builder withCostInformation(List<CostInformation> costInformation) {
            this.costInformation = costInformation;
            return this;
        }

        public Builder withTaxCategory(TaxCategory taxCategory) {
            this.taxCategory = taxCategory;
            return this;
        }

        public Builder withTaxCodeCountry(CountryCode taxCodeCountry) {
            this.taxCodeCountry = taxCodeCountry;
            return this;
        }

        public Builder withTaxCode(String taxCode) {
            this.taxCode = taxCode;
            return this;
        }

        public Builder withTaxCodeRegion(String taxCodeRegion) {
            this.taxCodeRegion = taxCodeRegion;
            return this;
        }

        public Item build() {
            return new Item(this);
        }
    }

    @Override
    public String getIndentifierValue() {
        return this.id;
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingProperties = new ArrayList<>();
        validateMandatoryProperty(id, "id", missingProperties);
        validateMandatoryProperty(quantity, "quantity", missingProperties);
        validateMandatoryProperty(unitPrice, "unitPrice", missingProperties);
        validateListItems(exemptionDetails, "exemptionDetails", missingProperties);
        validateListItems(itemClassifications, "itemClassifications", missingProperties);
        validateListItems(additionalItemInformation, "additionalItemInformation", missingProperties);
        validateListItems(costInformation, "costInformation", missingProperties);
        InvalidModelException.checkExceptions(missingProperties);
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the itemCode
     */
    public String getItemCode() {
        return itemCode;
    }

    /**
     * @return the quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * @return the unitPrice
     */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * @return the shippingCost
     */
    public String getShippingCost() {
        return shippingCost;
    }

    /**
     * @return the itemType
     */
    public ItemType getItemType() {
        return itemType;
    }

    /**
     * @return the exemptionDetails
     */
    public List<ExemptionDetail> getExemptionDetails() {
        return exemptionDetails;
    }

    /**
     * @return the certificateId
     */
    public String getCertificateId() {
        return certificateId;
    }

    /**
     * @return the itemClassifications
     */
    public List<ItemClassification> getItemClassifications() {
        return itemClassifications;
    }

    /**
     * @return the additionalItemInformation
     */
    public List<AdditionalItemInformation> getAdditionalItemInformation() {
        return additionalItemInformation;
    }

    /**
     * @return the costInformation
     */
    public List<CostInformation> getCostInformation() {
        return costInformation;
    }

    /**
     * @return the taxCategory
     */
    public TaxCategory getTaxCategory() {
        return taxCategory;
    }

    /**
     * @return the taxCodeCountry
     */
    public CountryCode getTaxCodeCountry() {
        return taxCodeCountry;
    }

    /**
     * @return the taxCode
     */
    public String getTaxCode() {
        return taxCode;
    }

    /**
     * @return the taxCodeRegion
     */
    public String getTaxCodeRegion() {
        return taxCodeRegion;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Item{");
        sb.append("id='").append(id).append('\'');
        sb.append(", itemCode='").append(itemCode).append('\'');
        sb.append(", quantity=").append(quantity);
        sb.append(", unitPrice=").append(unitPrice);
        sb.append(", shippingCost='").append(shippingCost).append('\'');
        sb.append(", itemType=").append(itemType);
        sb.append(", exemptionDetails=").append(exemptionDetails);
        sb.append(", certificateId='").append(certificateId).append('\'');
        sb.append(", itemClassifications=").append(itemClassifications);
        sb.append(", additionalItemInformation=").append(additionalItemInformation);
        sb.append(", costInformation=").append(costInformation);
        sb.append(", taxCategory=").append(taxCategory);
        sb.append(", taxCodeCountry=").append(taxCodeCountry);
        sb.append(", taxCode='").append(taxCode).append('\'');
        sb.append(", taxCodeRegion='").append(taxCodeRegion).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Item)) {
            return false;
        }
        Item item = (Item)o;
        return Objects.equals(getId(), item.getId()) &&
            Objects.equals(getItemCode(), item.getItemCode()) &&
            Objects.equals(getQuantity(), item.getQuantity()) &&
            Objects.equals(getUnitPrice(), item.getUnitPrice()) &&
            Objects.equals(getShippingCost(), item.getShippingCost()) &&
            getItemType() == item.getItemType() &&
            Objects.equals(getExemptionDetails(), item.getExemptionDetails()) &&
            Objects.equals(getCertificateId(), item.getCertificateId()) &&
            Objects.equals(getItemClassifications(), item.getItemClassifications()) &&
            Objects.equals(getAdditionalItemInformation(), item.getAdditionalItemInformation()) &&
            Objects.equals(getCostInformation(), item.getCostInformation()) &&
            getTaxCategory() == item.getTaxCategory() &&
            getTaxCodeCountry() == item.getTaxCodeCountry() &&
            Objects.equals(getTaxCode(), item.getTaxCode()) &&
            Objects.equals(getTaxCodeRegion(), item.getTaxCodeRegion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getItemCode(), getQuantity(), getUnitPrice(), getShippingCost(), getItemType(),
            getExemptionDetails(), getCertificateId(), getItemClassifications(), getAdditionalItemInformation(),
            getCostInformation(), getTaxCategory(), getTaxCodeCountry(), getTaxCode(), getTaxCodeRegion());
    }
}
