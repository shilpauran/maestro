package com.sap.slh.tax.maestro.api.v1.schema;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "The tax results for each item in a business transaction.", parent = QuoteResultDocument.class)
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ResultDocumentItem.Builder.class)
public class ResultDocumentItem extends BaseModel {

    @ApiModelProperty(position = 0, value = "The identifier of each item in the business transaction.", example = "0010")
    private String id;

    @ApiModelProperty(position = 1, value = "The code that identifies the taxable country or region. This code follows the standards of ISO 3166-1 alpha-2.", example = "CA")
    private CountryCode countryRegionCode;

    @ApiModelProperty(position = 2, value = "The code that identifies the taxable subdivision. The subdivision can be, for example, a state or province. This code follows the standard of the latter part of the complete ISO 3166-2 code element (after the hyphen). Pattern: [a-zA-Z]{1,3}.", example = "BC")
    private String subdivisionCode;

    @ApiModelProperty(position = 3, value = "The tax-related circumstances of an item in a business transaction under tax law.", example = "A314")
    private String taxEventCode;

    @ApiModelProperty(position = 4, value = "The legal phrase associated with the tax event. When applicable, this item relates to the tax act or article numbers.", example = "Regular HST Sales")
    private String taxEventLegalPhrase;

    @ApiModelProperty(position = 5, value = "A list with detailed information about the taxes that the tax service calculates for an item.")
    private List<TaxResultItem> taxes;

    @ApiModelProperty(position = 6, value = "The parameter that contains dynamic key/value pairs related to additional information on item level. For example, ''taxCode'' with its respective value.", example = "{\"key1\":\"value1\",\"key2\":\"value2\"}")
    private Map<String, String> additionalInformation;

    private ResultDocumentItem(Builder builder) {
        id = builder.id;
        countryRegionCode = builder.countryRegionCode;
        subdivisionCode = builder.subdivisionCode;
        taxEventCode = builder.taxEventCode;
        taxEventLegalPhrase = builder.taxEventLegalPhrase;
        taxes = builder.taxes;
        additionalInformation = builder.additionalInformation;
    }

    /**
     * Builder for {@link ResultDocumentItem}.
     * 
     * @return Builder for {@link ResultDocumentItem}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private CountryCode countryRegionCode;
        private String subdivisionCode;
        private String taxEventCode;
        private String taxEventLegalPhrase;
        private List<TaxResultItem> taxes;
        private Map<String, String> additionalInformation;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withCountryRegionCode(CountryCode countryRegionCode) {
            this.countryRegionCode = countryRegionCode;
            return this;
        }

        public Builder withSubdivisionCode(String subdivisionCode) {
            this.subdivisionCode = subdivisionCode;
            return this;
        }

        public Builder withTaxEventCode(String taxEventCode) {
            this.taxEventCode = taxEventCode;
            return this;
        }

        public Builder withTaxEventLegalPhrase(String taxEventLegalPhrase) {
            this.taxEventLegalPhrase = taxEventLegalPhrase;
            return this;
        }

        @JsonProperty("taxes")
        public Builder withTaxes(List<TaxResultItem> taxes) {
            this.taxes = taxes;
            return this;
        }

        public Builder withTaxes(TaxResultItem... taxes) {
            this.taxes = Arrays.asList(taxes);
            return this;
        }

        public Builder withAdditionalInformation(Map<String, String> additionalInformation) {
            this.additionalInformation = additionalInformation;
            return this;
        }

        public Builder addAdditionalInformation(String key, String value) {
            if (this.additionalInformation == null) {
                this.additionalInformation = new HashMap<String, String>();
            }
            this.additionalInformation.put(key, value);
            return this;
        }

        public ResultDocumentItem build() {
            return new ResultDocumentItem(this);
        }
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the countryRegionCode
     */
    public CountryCode getCountryRegionCode() {
        return countryRegionCode;
    }

    /**
     * @param countryRegionCode
     *            the countryRegionCode to set
     */
    public void setCountryRegionCode(CountryCode countryRegionCode) {
        this.countryRegionCode = countryRegionCode;
    }

    /**
     * @return the subdivisionCode
     */
    public String getSubdivisionCode() {
        return subdivisionCode;
    }

    /**
     * @param subdivisionCode
     *            the subdivisionCode to set
     */
    public void setSubdivisionCode(String subdivisionCode) {
        this.subdivisionCode = subdivisionCode;
    }

    /**
     * @return the taxEventCode
     */
    public String getTaxEventCode() {
        return taxEventCode;
    }

    /**
     * @param taxEventCode
     *            the taxEvent to set
     */
    public void setTaxEventCode(String taxEventCode) {
        this.taxEventCode = taxEventCode;
    }

    /**
     * @return the taxEventLegalPhrase
     */
    public String getTaxEventLegalPhrase() {
        return taxEventLegalPhrase;
    }

    /**
     * @param taxEventLegalPhrase
     *            the taxEventLegalPhrase to set
     */
    public void setTaxEventLegalPhrase(String taxEventLegalPhrase) {
        this.taxEventLegalPhrase = taxEventLegalPhrase;
    }

    /**
     * @return the taxes
     */
    public List<TaxResultItem> getTaxes() {
        return taxes;
    }

    /**
     * @param taxes
     *            the taxes to set
     */
    public void setTaxes(List<TaxResultItem> taxes) {
        this.taxes = taxes;
    }

    /**
     * @return the additionalInformation
     */
    public Map<String, String> getAdditionalInformation() {
        return additionalInformation;
    }

    /**
     * @param additionalInformation
     *            the additionalInformation to set
     */
    public void setAdditionalInformation(Map<String, String> additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ResultDocumentItem [id=");
        sb.append(id);
        sb.append(", countryRegionCode=");
        sb.append(countryRegionCode);
        sb.append(", subdivisionCode=");
        sb.append(subdivisionCode);
        sb.append(", taxEventCode=");
        sb.append(taxEventCode);
        sb.append(", taxEventLegalPhrase=");
        sb.append(taxEventLegalPhrase);
        sb.append(", taxes=");
        sb.append(taxes);
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
        if (!(o instanceof ResultDocumentItem)) {
            return false;
        }
        ResultDocumentItem that = (ResultDocumentItem)o;
        return Objects.equals(getId(), that.getId()) &&
            getCountryRegionCode() == that.getCountryRegionCode() &&
            Objects.equals(getSubdivisionCode(), that.getSubdivisionCode()) &&
            Objects.equals(getTaxEventCode(), that.getTaxEventCode()) &&
            Objects.equals(getTaxEventLegalPhrase(), that.getTaxEventLegalPhrase()) &&
            Objects.equals(getTaxes(), that.getTaxes()) &&
            Objects.equals(getAdditionalInformation(), that.getAdditionalInformation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCountryRegionCode(), getSubdivisionCode(), getTaxEventCode(),
                getTaxEventLegalPhrase(), getTaxes(), getAdditionalInformation());
    }

}
