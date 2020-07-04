package com.sap.slh.tax.maestro.api.v0.schema;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@JsonPropertyOrder({ "id", "country", "totalTax", "taxcode", "taxCodeDescription", "taxCodeLegalPhrase", "totalRate",
        "totalWithholdingTax", "totalWithholdingTaxRate", "withholdingTaxCode", "taxValues" })
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = TaxLine.Builder.class)
@ApiModel
public class TaxLine extends BaseModel {
    @ApiModelProperty(position = 0, value = "The unique identifier for each item in the transaction.")
    private String id;

    @ApiModelProperty(position = 1, value = "The taxable country/region. If the tax country/region for an item differs from the other items, then the tax service sends back the tax country/region for each item.")
    private CountryCode country;

    @ApiModelProperty(position = 2, value = "The total amount of tax for an item.")
    private String totalTax;

    @JsonProperty("taxcode")
    @ApiModelProperty(position = 3, value = "The tax code for an item in the operation.")
    private String taxCode;

    @ApiModelProperty(position = 4, value = "The description of the tax code that is determined for the transaction.")
    private String taxCodeDescription;

    @ApiModelProperty(position = 5, value = "The legal phrase associated with the tax code. Where applicable, this item relates to the tax act or article numbers.")
    private String taxCodeLegalPhrase;

    @ApiModelProperty(position = 6, value = "The total tax rate.")
    private String totalRate;

    @ApiModelProperty(position = 7, value = "The total amount of withholding tax.")
    private String totalWithholdingTax;

    @ApiModelProperty(position = 8, value = "The total withholding tax rate.")
    private String totalWithholdingTaxRate;

    @ApiModelProperty(position = 9, value = "The withholding tax code.")
    private String withholdingTaxCode;

    @ApiModelProperty(position = 10)
    private List<TaxValue> taxValues;

    private TaxLine(Builder builder) {
        id = builder.id;
        country = builder.country;
        totalTax = builder.totalTax;
        taxCode = builder.taxCode;
        taxCodeDescription = builder.taxCodeDescription;
        taxCodeLegalPhrase = builder.taxCodeLegalPhrase;
        totalRate = builder.totalRate;
        totalWithholdingTax = builder.totalWithholdingTax;
        totalWithholdingTaxRate = builder.totalWithholdingTaxRate;
        withholdingTaxCode = builder.withholdingTaxCode;
        taxValues = builder.taxValues;
    }

    /**
     * Builder for {@link TaxLine}.
     * 
     * @return Builder for {@link TaxLine}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id = null;
        private CountryCode country = null;
        private String totalTax = null;
        private String taxCode = null;
        private String taxCodeDescription = null;
        private String taxCodeLegalPhrase = null;
        private String totalRate = null;
        private String totalWithholdingTax = null;
        private String totalWithholdingTaxRate = null;
        private String withholdingTaxCode = null;
        private List<TaxValue> taxValues = null;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withCountry(CountryCode country) {
            this.country = country;
            return this;
        }

        public Builder withTotalTax(String totalTax) {
            this.totalTax = totalTax;
            return this;
        }

        @JsonProperty("taxcode")
        public Builder withTaxCode(String taxCode) {
            this.taxCode = taxCode;
            return this;
        }

        public Builder withTaxCodeDescription(String taxCodeDescription) {
            this.taxCodeDescription = taxCodeDescription;
            return this;
        }

        public Builder withTaxCodeLegalPhrase(String taxCodeLegalPhrase) {
            this.taxCodeLegalPhrase = taxCodeLegalPhrase;
            return this;
        }

        public Builder withTotalRate(String totalRate) {
            this.totalRate = totalRate;
            return this;
        }

        public Builder withTotalWithholdingTax(String totalWithholdingTax) {
            this.totalWithholdingTax = totalWithholdingTax;
            return this;
        }

        public Builder withTotalWithholdingTaxRate(String totalWithholdingTaxRate) {
            this.totalWithholdingTaxRate = totalWithholdingTaxRate;
            return this;
        }

        public Builder withWithholdingTaxCode(String withholdingTaxCode) {
            this.withholdingTaxCode = withholdingTaxCode;
            return this;
        }

        public Builder withTaxValues(List<TaxValue> taxValues) {
            this.taxValues = taxValues;
            return this;
        }

        public TaxLine build() {
            return new TaxLine(this);
        }
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingProperties = new ArrayList<>();
        validateListItems(taxValues, "taxValues", missingProperties);
        InvalidModelException.checkExceptions(missingProperties);
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the country
     */
    public CountryCode getCountry() {
        return country;
    }

    /**
     * @param country
     *            the country to set
     */
    public void setCountry(CountryCode country) {
        this.country = country;
    }

    /**
     * @return the totalTax
     */
    public String getTotalTax() {
        return totalTax;
    }

    /**
     * @return the taxCode
     */
    public String getTaxCode() {
        return taxCode;
    }

    /**
     * @param taxCode
     *            the taxCode to set
     */
    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    /**
     * @return the taxCodeDescription
     */
    public String getTaxCodeDescription() {
        return taxCodeDescription;
    }

    /**
     * @return the taxCodeLegalPhrase
     */
    public String getTaxCodeLegalPhrase() {
        return taxCodeLegalPhrase;
    }

    /**
     * @param taxCodeLegalPhrase
     *            the taxCodeLegalPhrase to set
     */
    public void setTaxCodeLegalPhrase(String taxCodeLegalPhrase) {
        this.taxCodeLegalPhrase = taxCodeLegalPhrase;
    }

    /**
     * @return the totalRate
     */
    public String getTotalRate() {
        return totalRate;
    }

    /**
     * @return the totalWithholdingTax
     */
    public String getTotalWithholdingTax() {
        return totalWithholdingTax;
    }

    /**
     * @return the totalWithholdingTaxRate
     */
    public String getTotalWithholdingTaxRate() {
        return totalWithholdingTaxRate;
    }

    /**
     * @return the withholdingTaxCode
     */
    public String getWithholdingTaxCode() {
        return withholdingTaxCode;
    }

    /**
     * @return the taxValues
     */
    public List<TaxValue> getTaxValues() {
        return taxValues;
    }

    /**
     * @param taxValues
     *            the taxValues to set
     */
    public void setTaxValues(List<TaxValue> taxValues) {
        this.taxValues = taxValues;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaxLine{");
        sb.append("id='").append(id).append('\'');
        sb.append(", country=").append(country);
        sb.append(", totalTax='").append(totalTax).append('\'');
        sb.append(", taxCode='").append(taxCode).append('\'');
        sb.append(", taxCodeDescription='").append(taxCodeDescription).append('\'');
        sb.append(", taxCodeLegalPhrase='").append(taxCodeLegalPhrase).append('\'');
        sb.append(", totalRate='").append(totalRate).append('\'');
        sb.append(", totalWithholdingTax='").append(totalWithholdingTax).append('\'');
        sb.append(", totalWithholdingTaxRate='").append(totalWithholdingTaxRate).append('\'');
        sb.append(", withholdingTaxCode='").append(withholdingTaxCode).append('\'');
        sb.append(", taxValues=").append(taxValues);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaxLine)) {
            return false;
        }
        TaxLine taxLine = (TaxLine)o;
        return Objects.equals(getId(), taxLine.getId()) &&
            getCountry() == taxLine.getCountry() &&
            Objects.equals(getTotalTax(), taxLine.getTotalTax()) &&
            Objects.equals(getTaxCode(), taxLine.getTaxCode()) &&
            Objects.equals(getTaxCodeDescription(), taxLine.getTaxCodeDescription()) &&
            Objects.equals(getTaxCodeLegalPhrase(), taxLine.getTaxCodeLegalPhrase()) &&
            Objects.equals(getTotalRate(), taxLine.getTotalRate()) &&
            Objects.equals(getTotalWithholdingTax(), taxLine.getTotalWithholdingTax()) &&
            Objects.equals(getTotalWithholdingTaxRate(), taxLine.getTotalWithholdingTaxRate()) &&
            Objects.equals(getWithholdingTaxCode(), taxLine.getWithholdingTaxCode()) &&
            Objects.equals(getTaxValues(), taxLine.getTaxValues());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCountry(), getTotalTax(), getTaxCode(), getTaxCodeDescription(),
            getTaxCodeLegalPhrase(), getTotalRate(), getTotalWithholdingTax(), getTotalWithholdingTaxRate(),
            getWithholdingTaxCode(), getTaxValues());
    }
}
