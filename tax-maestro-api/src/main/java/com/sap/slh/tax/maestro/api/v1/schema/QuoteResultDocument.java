package com.sap.slh.tax.maestro.api.v1.schema;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.common.domain.CurrencyCode;
import com.sap.slh.tax.maestro.api.v1.domain.AmountType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "The tax results details for a successfully processed request for a business transaction.")
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = QuoteResultDocument.Builder.class)
public class QuoteResultDocument extends BaseModel {

    @ApiModelProperty(position = 0, value = "The identifier of the business transaction for which the tax service calculates taxes.", example = "SO_1")
    private String id;

    @ApiModelProperty(position = 1, value = "The date that the tax service used to calculate the taxes for a business transaction. This date is the same as informed in the Quote request. Pattern: yyyy-MM-dd'T'HH:mm:ss.SSS'Z'.", example = "2018-09-04T10:58:14.000Z", dataType = "java.util.Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Date date;

    @ApiModelProperty(position = 2, value = "The currency that the tax service used when calculating the taxes for a business transaction. This currency is the same as informed in the Quote request. This code follows the standards of ISO 4217.", example = "CAD")
    private CurrencyCode currencyCode;

    @ApiModelProperty(position = 3, value = "The type of the amount that the tax service used to calculate the tax amount for each item in a business transaction. This code is the same as informed in the Quote request.", example = "NET")
    private AmountType amountTypeCode;

    @ApiModelProperty(position = 4, value = "A list with the items of a business transaction and their tax results.")
    private List<ResultDocumentItem> items;

    @ApiModelProperty(position = 5, value = "The parameter that contains dynamic key/value pairs related to additional information on document level.", example = "{\"key1\":\"value1\",\"key2\":\"value2\"}")
    private Map<String, String> additionalInformation;

    private QuoteResultDocument(Builder builder) {
        id = builder.id;
        date = builder.date;
        currencyCode = builder.currencyCode;
        amountTypeCode = builder.amountTypeCode;
        items = builder.items;
        additionalInformation = builder.additionalInformation;
    }

    /**
     * Builder for {@link QuoteResultDocument}.
     * 
     * @return Builder for {@link QuoteResultDocument}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Date date;
        private CurrencyCode currencyCode;
        private AmountType amountTypeCode;
        private List<ResultDocumentItem> items;
        private Map<String, String> additionalInformation;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC", lenient = OptBoolean.FALSE)
        public Builder withDate(Date date) {
            this.date = date;
            return this;
        }

        public Builder withCurrencyCode(CurrencyCode currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder withAmountTypeCode(AmountType amountTypeCode) {
            this.amountTypeCode = amountTypeCode;
            return this;
        }

        public Builder withItems(ResultDocumentItem... items) {
            this.items = Arrays.asList(items);
            return this;
        }

        @JsonProperty("items")
        public Builder withItems(List<ResultDocumentItem> items) {
            this.items = items;
            return this;
        }

        @JsonProperty("additionalInformation")
        public Builder withAdditionalInformation(Map<String, String> additionalInformation) {
            this.additionalInformation = additionalInformation;
            return this;
        }

        public Builder withAdditionalInformation(String key, String value) {
            if (this.additionalInformation == null) {
                this.additionalInformation = new HashMap<>();
            }
            this.additionalInformation.put(key, value);
            return this;
        }

        public QuoteResultDocument build() {
            return new QuoteResultDocument(this);
        }
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return the currencyCode
     */
    public CurrencyCode getCurrencyCode() {
        return currencyCode;
    }

    /**
     * @return the amountTypeCode
     */
    public AmountType getAmountTypeCode() {
        return amountTypeCode;
    }

    /**
     * @return the items
     */
    public List<ResultDocumentItem> getItems() {
        return items;
    }

    /**
     * @return the additionalInformation
     */
    public Map<String, String> getAdditionalInformation() {
        return additionalInformation;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("QuoteResultDocument [id=");
        sb.append(id);
        sb.append(", date=");
        sb.append(date);
        sb.append(", currencyCode=");
        sb.append(currencyCode);
        sb.append(", amountTypeCode=");
        sb.append(amountTypeCode);
        sb.append(", items=");
        sb.append(items);
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
        if (!(o instanceof QuoteResultDocument)) {
            return false;
        }
        QuoteResultDocument that = (QuoteResultDocument)o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getDate(), that.getDate())
                && getCurrencyCode() == that.getCurrencyCode() && getAmountTypeCode() == that.getAmountTypeCode()
                && Objects.equals(getItems(), that.getItems())
                && Objects.equals(getAdditionalInformation(), that.getAdditionalInformation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDate(), getCurrencyCode(), getAmountTypeCode(), getItems(),
                getAdditionalInformation());
    }
}
