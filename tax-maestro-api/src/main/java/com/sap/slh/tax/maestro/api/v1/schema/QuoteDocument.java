package com.sap.slh.tax.maestro.api.v1.schema;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.common.domain.CurrencyCode;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMissingReferenceDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorReferenceDetail;
import com.sap.slh.tax.maestro.api.v1.domain.AmountType;
import com.sap.slh.tax.maestro.api.v1.domain.TransactionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

@ApiModel(description = "The information that is relevant to determine and calculate taxes for a business transaction.")
@JsonDeserialize(builder = QuoteDocument.Builder.class)
@JsonInclude(Include.NON_NULL)
public class QuoteDocument extends BaseModel {

    @ApiModelProperty(required = true, position = 0, value = "The identifier of the business transaction for which the consuming application needs taxes to be calculated.", example = "SO_1")
    @JsonProperty(JSONParameter.ID)
    private String id;

    @ApiModelProperty(position = 1, required = true, value = "The date of the business transaction, for example, the order date, invoice date, or return date. To determine the tax due for this business transaction, the service applies the tax rules that are applicable on this date. Pattern: YYYY-MM-DD:THH:MM:SSZ.", example = "2018-09-04T10:58:14.000Z")
    @JsonProperty(JSONParameter.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Date date;

    @ApiModelProperty(position = 2, required = true, value = "The currency of the business transaction. This code follows the standards of ISO 4217. The currency provided here influences the number of digits after the decimal place in the ''amount'' parameters. Pattern: [A-Z]{3}.", example = "CAD")
    @JsonProperty(JSONParameter.CURRENCY_CODE)
    private CurrencyCode currencyCode;

    @ApiModelProperty(position = 3, required = true, value = "The type of business transaction.", example = "SALE")
    @JsonProperty(JSONParameter.TRANSACTION_TYPE_CODE)
    private TransactionType transactionTypeCode;

    @ApiModelProperty(position = 4, required = true, value = "The type of the amount that can be net or gross indicating if the consuming application wants the tax service to include the tax amount in the item value.", example = "NET")
    @JsonProperty(JSONParameter.AMOUNT_TYPE_CODE)
    private AmountType amountTypeCode;

    @ApiModelProperty(position = 5, allowEmptyValue = true, value = "Determines if the parties involved in the transaction belong to the same tax reporting group, as defined by law.", example = "false")
    @JsonProperty(JSONParameter.IS_TRANSACTION_WITHIN_TAX_REPORTING_GROUP)
    private Boolean isTransactionWithinTaxReportingGroup;

    @ApiModelProperty(position = 6, required = true, value = "The parameter that comprises the detailed information on the company that triggers the business transaction")
    @JsonProperty(JSONParameter.COMPANY_INFORMATION)
    private CompanyInformation companyInformation;

    @ApiModelProperty(position = 7, required = true, value = "A list with the item details. If you include too many line items in the request payload, it can impact the performance.")
    @JsonProperty(JSONParameter.ITEMS)
    private List<Item> items;

    @ApiModelProperty(position = 8, required = true, value = "A list with product details.")
    @JsonProperty(JSONParameter.PRODUCTS)
    private List<Product> products;

    @ApiModelProperty(position = 9, required = true, value = "A list with party details.")
    @JsonProperty(JSONParameter.PARTIES)
    private List<Party> parties;

    @ApiModelProperty(position = 10, value = "The parameter that contains dynamic key/value pairs related to additional information on document level.", example = "{\"key1\":\"value1\",\"key2\":\"value2\"}")
    @JsonProperty(JSONParameter.ADDITIONAL_INFORMATION)
    private Map<String, String> additionalInformation;

    private QuoteDocument(Builder builder) {
        this.id = builder.id;
        this.date = builder.date;
        this.transactionTypeCode = builder.transactionTypeCode;
        this.amountTypeCode = builder.amountTypeCode;
        this.currencyCode = builder.currencyCode;
        this.isTransactionWithinTaxReportingGroup = builder.isTransactionWithinTaxReportingGroup;
        this.companyInformation = builder.companyInformation;
        this.items = builder.items;
        this.additionalInformation = builder.additionalInformation;
        this.products = builder.products;
        this.parties = builder.parties;

    }

    @Override
    public String getIndentifierValue() {
        return this.id;
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> invalidProperties = new ArrayList<>();

        validateMandatoryProperty(this.id, JSONParameter.ID, invalidProperties);
        validateMandatoryProperty(this.date, JSONParameter.DATE, invalidProperties);
        validateMandatoryProperty(this.transactionTypeCode, JSONParameter.TRANSACTION_TYPE_CODE, invalidProperties);
        validateMandatoryProperty(this.amountTypeCode, JSONParameter.AMOUNT_TYPE_CODE, invalidProperties);
        validateMandatoryProperty(this.currencyCode, JSONParameter.CURRENCY_CODE, invalidProperties);
        validateMandatoryProperty(this.companyInformation, JSONParameter.COMPANY_INFORMATION, invalidProperties);
        mandatoryListProperty(this.items, JSONParameter.ITEMS, invalidProperties);
        validateListItems(this.items, JSONParameter.ITEMS, invalidProperties);
        mandatoryListProperty(this.products, JSONParameter.PRODUCTS, invalidProperties);
        validateListItems(this.products, JSONParameter.PRODUCTS, invalidProperties);
        mandatoryListProperty(this.parties, JSONParameter.PARTIES, invalidProperties);
        validateListItems(this.parties, JSONParameter.PARTIES, invalidProperties);

        validateItemsReferences(invalidProperties);
        validateCompanyInformationReference(invalidProperties);

        InvalidModelException.checkExceptions(invalidProperties);
    }

    private void validateItemsReferences(List<PropertyErrorDetail> invalidProperties) {
        if (this.items == null) {
            return;
        }

        for (Item item : this.items) {
            validateAssignedProductIdIsWithinProduct(item, invalidProperties);
            validateAssignedPartyIdIsWithinParty(item, invalidProperties);
        }
    }

    private void validateCompanyInformationReference(List<PropertyErrorDetail> invalidProperties) {
        if (companyInformation == null || companyInformation.getAssignedPartyId() == null) {
            return;
        }

        if (this.parties != null && this.parties.stream()
                .noneMatch(party -> companyInformation.getAssignedPartyId().equals(party.getId()))) {
            PropertyErrorMissingReferenceDetail refError = new PropertyErrorMissingReferenceDetail(
                    CompanyInformation.JSONParameter.ASSIGNED_PARTY_ID, companyInformation.getIndentifierValue());

            refError.addReferencePropertyErrorDetail(
                    new PropertyErrorReferenceDetail(JSONParameter.COMPANY_INFORMATION));

            invalidProperties.add(refError);
        }

    }

    private void validateAssignedProductIdIsWithinProduct(Item item, List<PropertyErrorDetail> invalidProperties) {
        if (item == null || this.products == null || StringUtils.isBlank(item.getAssignedProductId())) {
            return;
        }

        for (Product product : this.products) {
            if (product == null) {
                continue;
            }

            if (item.getAssignedProductId().equals(product.getId())) {
                return;
            }
        }

        PropertyErrorMissingReferenceDetail refError = new PropertyErrorMissingReferenceDetail(
                Item.JSONParameter.ASSIGNED_PRODUCT_ID, item.getAssignedProductId());
        refError.addReferencePropertyErrorDetail(
                new PropertyErrorReferenceDetail(JSONParameter.ITEMS, item.getIndentifierValue()));
        invalidProperties.add(refError);
    }

    private void validateAssignedPartyIdIsWithinParty(Item item, List<PropertyErrorDetail> invalidProperties) {
        if (item == null || item.getAssignedParties() == null) {
            return;
        }

        for (AssignedParty assignedParty : item.getAssignedParties()) {
            if (assignedParty == null || StringUtils.isBlank(assignedParty.getId())) {
                continue;
            }

            if (this.parties != null
                    && this.parties.stream().noneMatch(party -> assignedParty.getId().equals(party.getId()))) {
                PropertyErrorMissingReferenceDetail refError = new PropertyErrorMissingReferenceDetail(
                        Item.JSONParameter.ASSIGNED_PARTIES, assignedParty.getIndentifierValue());

                refError.addReferencePropertyErrorDetail(
                        new PropertyErrorReferenceDetail(JSONParameter.ITEMS, item.getIndentifierValue()));

                invalidProperties.add(refError);
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String id;
        private Date date;
        private TransactionType transactionTypeCode;
        private AmountType amountTypeCode;
        private CurrencyCode currencyCode;
        private Boolean isTransactionWithinTaxReportingGroup;
        private CompanyInformation companyInformation;
        private List<Item> items;
        private Map<String, String> additionalInformation;
        private List<Product> products;
        private List<Party> parties;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC", lenient = OptBoolean.FALSE)
        public Builder withDate(Date date) {
            this.date = date;
            return this;
        }

        public Builder withTransactionTypeCode(TransactionType transactionTypeCode) {
            this.transactionTypeCode = transactionTypeCode;
            return this;
        }

        public Builder withAmountTypeCode(AmountType amountTypeCode) {
            this.amountTypeCode = amountTypeCode;
            return this;
        }

        public Builder withCurrencyCode(CurrencyCode currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder withIsTransactionWithinTaxReportingGroup(Boolean isTransactionWithinTaxReportingGroup) {
            this.isTransactionWithinTaxReportingGroup = isTransactionWithinTaxReportingGroup;
            return this;
        }

        public Builder withCompanyInformation(CompanyInformation companyInformation) {
            this.companyInformation = companyInformation;
            return this;
        }

        public Builder withItems(Item... items) {
            this.items = Arrays.asList(items);
            return this;
        }

        @JsonProperty("items")
        public Builder withItems(List<Item> items) {
            this.items = items;
            return this;
        }

        public Builder withAdditionalInformation(Map<String, String> additionalInformation) {
            this.additionalInformation = additionalInformation;
            return this;
        }

        public Builder withProducts(Product... products) {
            this.products = Arrays.asList(products);
            return this;
        }

        @JsonProperty("products")
        public Builder withProducts(List<Product> products) {
            this.products = products;
            return this;
        }

        public Builder withParties(Party... parties) {
            this.parties = Arrays.asList(parties);
            return this;
        }

        @JsonProperty("parties")
        public Builder withParties(List<Party> parties) {
            this.parties = parties;
            return this;
        }

        public QuoteDocument build() {
            return new QuoteDocument(this);
        }

    }

    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public TransactionType getTransactionTypeCode() {
        return transactionTypeCode;
    }

    public AmountType getAmountTypeCode() {
        return amountTypeCode;
    }

    public CurrencyCode getCurrencyCode() {
        return currencyCode;
    }

    public Boolean getIsTransactionWithinTaxReportingGroup() {
        return isTransactionWithinTaxReportingGroup;
    }

    public CompanyInformation getCompanyInformation() {
        return companyInformation;
    }
    
    public void setCompanyInformation(CompanyInformation companyInformation) {
        this.companyInformation = companyInformation;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Map<String, String> getAdditionalInformation() {
        return additionalInformation;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Party> getParties() {
        return parties;
    }

    public void setParties(List<Party> parties) {
        this.parties = parties;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("QuoteDocument [id=");
        sb.append(id);
        sb.append(", date=");
        sb.append(date);
        sb.append(", transactionTypeCode=");
        sb.append(transactionTypeCode);
        sb.append(", amountTypeCode=");
        sb.append(amountTypeCode);
        sb.append(", currencyCode=");
        sb.append(currencyCode);
        sb.append(", isTransactionWithinTaxReportingGroup=");
        sb.append(isTransactionWithinTaxReportingGroup);
        sb.append(", companyInformation=");
        sb.append(companyInformation);
        sb.append(", items=");
        sb.append(items);
        sb.append(", products=");
        sb.append(products);
        sb.append(", parties=");
        sb.append(parties);
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
        if (!(o instanceof QuoteDocument)) {
            return false;
        }
        QuoteDocument that = (QuoteDocument)o;
        return Objects.equals(getId(), that.getId()) &&
            Objects.equals(getDate(), that.getDate()) &&
            getCurrencyCode() == that.getCurrencyCode() &&
            getTransactionTypeCode() == that.getTransactionTypeCode() &&
            getAmountTypeCode() == that.getAmountTypeCode() &&
            Objects.equals(getIsTransactionWithinTaxReportingGroup(), that.getIsTransactionWithinTaxReportingGroup()) &&
            Objects.equals(getCompanyInformation(), that.getCompanyInformation()) &&
            Objects.equals(getItems(), that.getItems()) &&
            Objects.equals(getProducts(), that.getProducts()) &&
            Objects.equals(getParties(), that.getParties()) &&
            Objects.equals(getAdditionalInformation(), that.getAdditionalInformation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDate(), getCurrencyCode(), getTransactionTypeCode(), getAmountTypeCode(),
            getIsTransactionWithinTaxReportingGroup(), getCompanyInformation(), getItems(), getProducts(),
            getParties(), getAdditionalInformation());
    }

    public static class JSONParameter {
        private JSONParameter() {
        }

        public static final String ID = "id";
        public static final String DATE = "date";
        public static final String TRANSACTION_TYPE_CODE = "transactionTypeCode";
        public static final String AMOUNT_TYPE_CODE = "amountTypeCode";
        public static final String CURRENCY_CODE = "currencyCode";
        public static final String IS_TRANSACTION_WITHIN_TAX_REPORTING_GROUP = "isTransactionWithinTaxReportingGroup";
        public static final String COMPANY_INFORMATION = "companyInformation";
        public static final String ITEMS = "items";
        public static final String ADDITIONAL_INFORMATION = "additionalInformation";
        public static final String PRODUCTS = "products";
        public static final String PARTIES = "parties";
    }

}
