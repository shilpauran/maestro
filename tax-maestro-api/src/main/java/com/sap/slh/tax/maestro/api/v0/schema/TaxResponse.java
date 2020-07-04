package com.sap.slh.tax.maestro.api.v0.schema;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@JsonPropertyOrder({ "date", "total", "inclusive", "subTotal", "totalTax", "totalWithholdingTax", "country", "taxLines",
        "warning", "traceLog", "partnerName" })
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = TaxResponse.Builder.class)
@ApiModel
public class TaxResponse extends BaseModel {

    @ApiModelProperty(position = 0, value = "The total amount to be paid or collected.")
    private String total;

    @ApiModelProperty(position = 1, value = "Shows whether tax was included in the gross amount: included (true) or not included (false).")
    private String inclusive;

    @ApiModelProperty(position = 2, value = "The amount on which tax is applicable.")
    private String subTotal;

    @ApiModelProperty(position = 3, value = "The total amount of tax for an item.")
    private String totalTax;

    @ApiModelProperty(position = 4, value = "The total amount of withholding tax.")
    private String totalWithholdingTax;

    @ApiModelProperty(position = 5, value = "The taxable country/region. Note: the API returns the country/region only if all items have the same tax country/region.")
    private CountryCode country;

    @ApiModelProperty(position = 6, value = "")
    private List<TaxLine> taxLines;

    @ApiModelProperty(position = 7, value = "")
    private List<Warning> warning;

    @ApiModelProperty(position = 8, value = "")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Date date;

    @ApiModelProperty(position = 9, value = "Includes the trace content for tax determination. Applies only when the value of the 'isTraceRequired' parameter is 'y' in the request payload.")
    private List<String> traceLog;

    @ApiModelProperty(hidden = true)
    private String partnerName;

    private TaxResponse(Builder builder) {
        total = builder.total;
        inclusive = builder.inclusive;
        subTotal = builder.subTotal;
        totalTax = builder.totalTax;
        totalWithholdingTax = builder.totalWithholdingTax;
        country = builder.country;
        taxLines = builder.taxLines;
        warning = builder.warning;
        date = builder.date;
        traceLog = builder.traceLog;
        partnerName = builder.partnerName;
    }

    /**
     * Builder for {@link TaxResponse}.
     *
     * @return Builder for {@link TaxResponse}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String total = null;
        private String inclusive = null;
        private String subTotal = null;
        private String totalTax = null;
        private String totalWithholdingTax = null;
        private CountryCode country = null;
        private List<TaxLine> taxLines = null;
        private List<Warning> warning = null;
        private Date date = null;
        private List<String> traceLog = null;
        private String partnerName = null;

        public Builder withTotal(String total) {
            this.total = total;
            return this;
        }

        public Builder withInclusive(String inclusive) {
            this.inclusive = inclusive;
            return this;
        }

        public Builder withSubTotal(String subTotal) {
            this.subTotal = subTotal;
            return this;
        }

        public Builder withTotalTax(String totalTax) {
            this.totalTax = totalTax;
            return this;
        }

        public Builder withTotalWithholdingTax(String totalWithholdingTax) {
            this.totalWithholdingTax = totalWithholdingTax;
            return this;
        }

        public Builder withCountry(CountryCode country) {
            this.country = country;
            return this;
        }

        public Builder withTaxLines(List<TaxLine> taxLines) {
            this.taxLines = taxLines;
            return this;
        }

        public Builder withWarning(List<Warning> warning) {
            this.warning = warning;
            return this;
        }

        public Builder withDate(Date date) {
            this.date = date;
            return this;
        }

        public Builder withTraceLog(List<String> traceLog) {
            this.traceLog = traceLog;
            return this;
        }

        public Builder withPartnerName(String partnerName) {
            this.partnerName = partnerName;
            return this;
        }

        public TaxResponse build() {
            return new TaxResponse(this);
        }
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingProperties = new ArrayList<>();
        validateListItems(taxLines, "taxLines", missingProperties);
        validateListItems(warning, "warning", missingProperties);
        InvalidModelException.checkExceptions(missingProperties);
    }

    /**
     * @return the total
     */
    public String getTotal() {
        return total;
    }

    /**
     * @return the inclusive
     */
    public String getInclusive() {
        return inclusive;
    }

    /**
     * @return the subTotal
     */
    public String getSubTotal() {
        return subTotal;
    }

    /**
     * @return the totalTax
     */
    public String getTotalTax() {
        return totalTax;
    }

    /**
     * @return the totalWithholdingTax
     */
    public String getTotalWithholdingTax() {
        return totalWithholdingTax;
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
     * @return the taxLines
     */
    public List<TaxLine> getTaxLines() {
        return taxLines;
    }

    /**
     * @return the warning
     */
    public List<Warning> getWarning() {
        return warning;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return the traceLog
     */
    public List<String> getTraceLog() {
        return traceLog;
    }

    /**
     * @return the partnerName
     */
    public String getPartnerName() {
        return partnerName;
    }
    
    /**
     * @param partnerName
     *            the partnerName to set
     */
    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaxResponse{");
        sb.append("total='").append(total).append('\'');
        sb.append(", inclusive='").append(inclusive).append('\'');
        sb.append(", subTotal='").append(subTotal).append('\'');
        sb.append(", totalTax='").append(totalTax).append('\'');
        sb.append(", totalWithholdingTax='").append(totalWithholdingTax).append('\'');
        sb.append(", country=").append(country);
        sb.append(", taxLines=").append(taxLines);
        sb.append(", warning=").append(warning);
        sb.append(", date=").append(date);
        sb.append(", traceLog=").append(traceLog);
        sb.append(", partnerName='").append(partnerName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaxResponse)) {
            return false;
        }
        TaxResponse that = (TaxResponse) o;
        return Objects.equals(getTotal(), that.getTotal()) &&
            Objects.equals(getInclusive(), that.getInclusive()) &&
            Objects.equals(getSubTotal(), that.getSubTotal()) &&
            Objects.equals(getTotalTax(), that.getTotalTax()) &&
            Objects.equals(getTotalWithholdingTax(), that.getTotalWithholdingTax()) &&
            getCountry() == that.getCountry() &&
            Objects.equals(getTaxLines(), that.getTaxLines()) &&
            Objects.equals(getWarning(), that.getWarning()) &&
            Objects.equals(getDate(), that.getDate()) &&
            Objects.equals(getTraceLog(), that.getTraceLog()) &&
            Objects.equals(getPartnerName(), that.getPartnerName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTotal(), getInclusive(), getSubTotal(), getTotalTax(), getTotalWithholdingTax(),
            getCountry(), getTaxLines(), getWarning(), getDate(), getTraceLog(), getPartnerName());
    }
}
