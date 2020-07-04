package com.sap.slh.tax.maestro.api.v0.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.slh.tax.maestro.api.common.BaseModel;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.domain.CurrencyCode;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMandatoryValueMissingDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMultipleValues;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorReferenceDetail;
import com.sap.slh.tax.maestro.api.v0.domain.BooleanValue;
import com.sap.slh.tax.maestro.api.v0.domain.GrossOrNet;
import com.sap.slh.tax.maestro.api.v0.domain.LocationType;
import com.sap.slh.tax.maestro.api.v0.domain.SaleOrPurchase;

@ApiModel(description = "The request payload as per the model schema provided.")
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = TaxRequest.Builder.class)
public class TaxRequest extends BaseModel {
    @ApiModelProperty(required = true, position = 0, value = "A document ID that uniquely identifies the transaction, which is the request that is sent for tax calculation.")
    private String id;

    @ApiModelProperty(position = 1, value = "The company ID, as maintained in the Tax Configuration application. If the company master data is maintained in the tax configuration application, include a value for this key in the request for the service to use the relevant tax classification available in the application to determine the tax amount.")
    private String companyId;

    @ApiModelProperty(position = 2, value = "The ID of a supplier or customer, as maintained in the Tax Configuration application. Include a value for this parameter in the request only if the tax configuration data for suppliers or customers is maintained in the tax configuration application. In this case, the service considers the relevant tax classification available in the application to determine the tax amount.")
    private String businessPartnerId;

    @ApiModelProperty(required = true, position = 3, value = "The date of a transaction, such as the order date, invoice date, or return date. To determine the tax due, the service applies the relevant tax rules that apply on this date.") //how to put timezone here?
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Date date;

    @ApiModelProperty(required = true, position = 4, value = "Specifies the currency code according to ISO 4217 standards. All amounts in the response have the number digits after the decimal place in accordance with the currency in question.")
    private CurrencyCode currency;

    @ApiModelProperty(position = 5, value = "Indicates if the parties involved in the transaction belong to the same tax reporting group, as defined by law.")
    private BooleanValue isTransactionWithinTaxReportingGroup;

    @ApiModelProperty(position = 6, value = "The percentage discount that applies to the business transaction request sent. In accordance with country/region tax determination guidelines, the service determines whether a cash discount affects the base amount used for tax calculation.")
    private String cashDiscountPercent;

    @ApiModelProperty(position = 7, value = "If the cash discount is planned for the transaction, enter 'y' or 'Y' as the value of this parameter. If the cash discount is already considered in the transaction, enter 'n' or 'N' as the value of this parameter.")
    private BooleanValue isCashDiscountPlanned;

    @ApiModelProperty(position = 8, value = "Shows whether the transaction is gross or net. Use g for gross and n for net.")
    private GrossOrNet grossOrNet;

    @ApiModelProperty(position = 9, value = "Shows whether the transaction is a sale or purchase. Use s or S for a sale and p or P for a purchase.")
    @JsonProperty("saleorPurchase")
    private SaleOrPurchase saleOrPurchase;

    @ApiModelProperty(position = 10, value = "Identifies the nature of the operation.")
    private String operationNatureCode;

    @ApiModelProperty(position = 11, value = "Indicates if the company can defer the tax due until the date of the invoice payment. This applies only if the company has the position = 12, required authorizations for tax deferral. If you enter 'y' as the value in this parameter, the service applies deferred tax rules and indicates tax deferral in the response.")
    private BooleanValue isCompanyDeferredTaxEnabled;

    @ApiModelProperty(required = true, position = 13, value = "If you include too many line items in the request, it can impact the performance. For more information, see SAP Note 2698505 on the SAP ONE Support Launchpad.")
    @JsonProperty("Items")
    private List<Item> items;

    @ApiModelProperty(position = 14, value = "")
    @JsonProperty("Locations")
    private List<Location> locations;

    @ApiModelProperty(position = 15, value = "")
    @JsonProperty("BusinessPartnerExemptionDetails")
    private List<BusinessPartnerExemptionDetail> businessPartnerExemptionDetails;

    @ApiModelProperty(position = 16, value = "")
    @JsonProperty("Party")
    private List<Party> party;

    @ApiModelProperty(position = 17, value = "Shows whether tracing of the tax determination process is enabled. To enable the trace, enter 'y' or 'Y' as the value of the parameter. To disable the trace, enter 'n' or 'N'. When enabled, the trace content is sent as part of the response payload.")
    private BooleanValue isTraceRequired;

    @ApiModelProperty(hidden = true)
    private List<Party> removedParties;

    @ApiModelProperty(hidden = true)
    private String tteDocumentId;

    private TaxRequest(Builder builder) {
        id = builder.id;
        companyId = builder.companyId;
        businessPartnerId = builder.businessPartnerId;
        date = builder.date;
        currency = builder.currency;
        isTransactionWithinTaxReportingGroup = builder.isTransactionWithinTaxReportingGroup;
        cashDiscountPercent = builder.cashDiscountPercent;
        isCashDiscountPlanned = builder.isCashDiscountPlanned;
        grossOrNet = builder.grossOrNet;
        saleOrPurchase = builder.saleOrPurchase;
        operationNatureCode = builder.operationNatureCode;
        isCompanyDeferredTaxEnabled = builder.isCompanyDeferredTaxEnabled;
        items = builder.items;
        locations = builder.locations;
        businessPartnerExemptionDetails = builder.businessPartnerExemptionDetails;
        party = builder.party;
        removedParties = builder.removedParties;
        isTraceRequired = builder.isTraceRequired;
        tteDocumentId = builder.tteDocumentId;
    }

    /**
     * Builder for {@link TaxRequest}.
     * 
     * @return Builder for {@link TaxRequest}
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id = null;
        private String companyId = null;
        private String businessPartnerId = null;
        private Date date = null;
        private CurrencyCode currency = null;
        private BooleanValue isTransactionWithinTaxReportingGroup = null;
        private String cashDiscountPercent = null;
        private BooleanValue isCashDiscountPlanned = null;
        private GrossOrNet grossOrNet = GrossOrNet.N;
        private SaleOrPurchase saleOrPurchase = SaleOrPurchase.S;
        private String operationNatureCode = null;
        private BooleanValue isCompanyDeferredTaxEnabled = BooleanValue.N;
        private List<Item> items = null;
        private List<Location> locations = null;
        private List<BusinessPartnerExemptionDetail> businessPartnerExemptionDetails = null;
        private List<Party> party = null;
        private List<Party> removedParties = null;
        private BooleanValue isTraceRequired = null;
        private String tteDocumentId = null;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withCompanyId(String companyId) {
            this.companyId = companyId;
            return this;
        }

        public Builder withBusinessPartnerId(String businessPartnerId) {
            this.businessPartnerId = businessPartnerId;
            return this;
        }

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC", lenient = OptBoolean.FALSE)
        public Builder withDate(Date date) {
            this.date = date;
            return this;
        }

        public Builder withCurrency(CurrencyCode currency) {
            this.currency = currency;
            return this;
        }

        public Builder withIsTransactionWithinTaxReportingGroup(BooleanValue isTransactionWithinTaxReportingGroup) {
            this.isTransactionWithinTaxReportingGroup = isTransactionWithinTaxReportingGroup;
            return this;
        }

        public Builder withCashDiscountPercent(String cashDiscountPercent) {
            this.cashDiscountPercent = cashDiscountPercent;
            return this;
        }

        public Builder withIsCashDiscountPlanned(BooleanValue isCashDiscountPlanned) {
            this.isCashDiscountPlanned = isCashDiscountPlanned;
            return this;
        }

        public Builder withGrossOrNet(GrossOrNet grossOrNet) {
            if (grossOrNet != null) {
                this.grossOrNet = grossOrNet;
            }
            return this;
        }

        @JsonProperty("saleorPurchase")
        public Builder withSaleOrPurchase(SaleOrPurchase saleOrPurchase) {
            if (saleOrPurchase != null) {
                this.saleOrPurchase = saleOrPurchase;
            }
            return this;
        }

        public Builder withOperationNatureCode(String operationNatureCode) {
            this.operationNatureCode = operationNatureCode;
            return this;
        }

        public Builder withIsCompanyDeferredTaxEnabled(BooleanValue isCompanyDeferredTaxEnabled) {
            if (isCompanyDeferredTaxEnabled != null) {
                this.isCompanyDeferredTaxEnabled = isCompanyDeferredTaxEnabled;
            }
            return this;
        }

        @JsonProperty("Items")
        public Builder withItems(List<Item> items) {
            this.items = items;
            return this;
        }

        public Builder withItems(Item... items) {
            this.items = Arrays.asList(items);
            return this;
        }

        @JsonProperty("Locations")
        public Builder withLocations(List<Location> locations) {
            this.locations = locations;
            return this;
        }

        @JsonProperty("BusinessPartnerExemptionDetails")
        public Builder withBusinessPartnerExemptionDetails(
                List<BusinessPartnerExemptionDetail> businessPartnerExemptionDetails) {
            this.businessPartnerExemptionDetails = businessPartnerExemptionDetails;
            return this;
        }

        @JsonProperty("Party")
        public Builder withParty(List<Party> party) {
            this.party = party;
            return this;
        }

        public Builder withRemovedParties(List<Party> removedParties) {
            this.removedParties = removedParties;
            return this;
        }

        public Builder withIsTraceRequired(BooleanValue isTraceRequired) {
            this.isTraceRequired = isTraceRequired;
            return this;
        }

        public Builder withTteDocumentId(String tteDocumentId) {
            this.tteDocumentId = tteDocumentId;
            return this;
        }

        public TaxRequest build() {
            return new TaxRequest(this);
        }
    }

    @Override
    public void validate() {
        List<PropertyErrorDetail> missingAttributes = new ArrayList<>();
        validateMandatoryProperty(id, "id", missingAttributes);
        validateMandatoryProperty(date, "date", missingAttributes);
        validateMandatoryProperty(currency, "currency", missingAttributes);
        mandatoryListProperty(items, "Items", missingAttributes);
        validateListItems(items, "Items", missingAttributes);
        if (isDirectPayload()) {
            validateDirectPayload(missingAttributes);
        } else {
            validateFullPayload(missingAttributes);
        }
    }

    /**
     * Checks if the Tax Request is a direct payload 
     * 
     * @return boolean identifying if the Tax Request is a direct payload
     * 
     * Needs to be anotated as JsonIgnorable so that serialization/deserialization is not affected
     */
    @JsonIgnore
    public boolean isDirectPayload() {
        if (items != null) {
            return items.stream().anyMatch(i -> i.getTaxCode() != null || i.getTaxCodeCountry() != null);
        }
        return false;
    }

    private void validateFullPayload(List<PropertyErrorDetail> missingAttributes) {
        mandatoryListProperty(locations, "Locations", missingAttributes);
        validateListItems(locations, "Locations", missingAttributes);
        mandatoryLocationType(saleOrPurchase, locations, missingAttributes);
        validateListItems(businessPartnerExemptionDetails, "businessPartnerExemptionDetails", missingAttributes);
        validateListItems(party, "party", missingAttributes);
        validateListItems(removedParties, "removedParties", missingAttributes);
        InvalidModelException.checkExceptions(missingAttributes);
    }

    private void validateDirectPayload(List<PropertyErrorDetail> missingAttributes) {
        mandatoryTaxCodes(missingAttributes);
        mandatoryTaxCodeCountry(missingAttributes);
        mandatoryTaxCategory(missingAttributes);
        uniqueTaxCodeCountry(missingAttributes);
        InvalidModelException.checkExceptions(missingAttributes);
    }

    private void mandatoryTaxCodes(List<PropertyErrorDetail> missingProperties) {
        for (Item item : items) {
            validateMandatoryProperty(item.getTaxCode(), "taxCode", missingProperties, "Items",
                    item.getIndentifierValue());
        }
    }

    private void mandatoryTaxCodeCountry(List<PropertyErrorDetail> missingProperties) {
        for (Item item : items) {
            validateMandatoryProperty(item.getTaxCodeCountry(), "taxCodeCountry", missingProperties, "Items",
                    item.getIndentifierValue());
        }
    }

    private void uniqueTaxCodeCountry(List<PropertyErrorDetail> missingProperties) {
        Set<CountryCode> taxCountryCodes = new HashSet<>();
        for (Item item : items) {
            if (item.getTaxCodeCountry() != null)
                taxCountryCodes.add(item.getTaxCodeCountry());
        }

        if (taxCountryCodes.size() > 1) {
            PropertyErrorMultipleValues error = new PropertyErrorMultipleValues("taxCodeCountry");
            error.addReferencePropertyErrorDetail(new PropertyErrorReferenceDetail("Items"));
            
            missingProperties.add(error);
        }
    }

    private void mandatoryTaxCategory(List<PropertyErrorDetail> missingProperties) {
        for (Item item : items) {
            validateMandatoryProperty(item.getTaxCategory(), "taxCategory", missingProperties, "Items",
                    item.getIndentifierValue());
        }
    }

    private void mandatoryLocationType(SaleOrPurchase salesOrPurchase, List<Location> locations,
            List<PropertyErrorDetail> missingProperties) {
        if (salesOrPurchase != null && locations != null && !locations.isEmpty()) {
            if (saleOrPurchase.isPurchase()) {
                mandatoryToLocationForPurchase(locations, missingProperties);
            } else {
                mandatoryFromLocationForSale(locations, missingProperties);
            }
        }
    }

    private void mandatoryToLocationForPurchase(List<Location> locations, List<PropertyErrorDetail> missingProperties) {
        this.checkMandatoryLocation(locations, missingProperties, LocationType.CONTRACT_TO, LocationType.SHIP_TO);
    }

    private void mandatoryFromLocationForSale(List<Location> locations, List<PropertyErrorDetail> missingProperties) {
        this.checkMandatoryLocation(locations, missingProperties, LocationType.CONTRACT_FROM, LocationType.SHIP_FROM);
    }

    private void checkMandatoryLocation(List<Location> locations, List<PropertyErrorDetail> missingProperties,
            LocationType... locationTypes) {
        Boolean thereIsLocationType = false;
        for (Location location : locations) {
            if (Arrays.stream(locationTypes).anyMatch(l -> l != null && l.equals(location.getType()))) {
                thereIsLocationType = true;
            }
        }

        if (!thereIsLocationType) {
            PropertyErrorMandatoryValueMissingDetail errorDetail = new PropertyErrorMandatoryValueMissingDetail("type",
                    StringUtils.join(locationTypes, ", "));
            errorDetail.addReferencePropertyErrorDetail(new PropertyErrorReferenceDetail("Locations"));
            missingProperties.add(errorDetail);
        }
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the companyId
     */
    public String getCompanyId() {
        return companyId;
    }

    /**
     * @return the businessPartnerId
     */
    public String getBusinessPartnerId() {
        return businessPartnerId;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return the currency
     */
    public CurrencyCode getCurrency() {
        return currency;
    }

    /**
     * @return the isTransactionWithinTaxReportingGroup
     */
    public BooleanValue getIsTransactionWithinTaxReportingGroup() {
        return isTransactionWithinTaxReportingGroup;
    }

    /**
     * @return the cashDiscountPercent
     */
    public String getCashDiscountPercent() {
        return cashDiscountPercent;
    }

    /**
     * @return the isCashDiscountPlanned
     */
    public BooleanValue getIsCashDiscountPlanned() {
        return isCashDiscountPlanned;
    }

    /**
     * @return the grossOrNet
     */
    public GrossOrNet getGrossOrNet() {
        return grossOrNet;
    }

    /**
     * @return the saleOrPurchase
     */
    public SaleOrPurchase getSaleOrPurchase() {
        return saleOrPurchase;
    }

    /**
     * @return the operationNatureCode
     */
    public String getOperationNatureCode() {
        return operationNatureCode;
    }

    /**
     * @return the isCompanyDeferredTaxEnabled
     */
    public BooleanValue getIsCompanyDeferredTaxEnabled() {
        return isCompanyDeferredTaxEnabled;
    }

    /**
     * @return the items
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * @return the locations
     */
    public List<Location> getLocations() {
        return locations;
    }

    /**
     * @return the businessPartnerExemptionDetails
     */
    public List<BusinessPartnerExemptionDetail> getBusinessPartnerExemptionDetails() {
        return businessPartnerExemptionDetails;
    }

    /**
     * @return the party
     */
    public List<Party> getParty() {
        return party;
    }

    /**
     * @return the removedParties
     */
    public List<Party> getRemovedParties() {
        return removedParties;
    }

    /**
     * @return the isTraceRequired
     */
    public BooleanValue getIsTraceRequired() {
        return isTraceRequired;
    }

    /**
     * @return the tteDocumentId
     */
    public String getTteDocumentId() {
        return tteDocumentId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaxRequest{");
        sb.append("id='").append(id).append('\'');
        sb.append(", companyId='").append(companyId).append('\'');
        sb.append(", businessPartnerId='").append(businessPartnerId).append('\'');
        sb.append(", date=").append(date);
        sb.append(", currency=").append(currency);
        sb.append(", isTransactionWithinTaxReportingGroup=").append(isTransactionWithinTaxReportingGroup);
        sb.append(", cashDiscountPercent='").append(cashDiscountPercent).append('\'');
        sb.append(", isCashDiscountPlanned=").append(isCashDiscountPlanned);
        sb.append(", grossOrNet=").append(grossOrNet);
        sb.append(", saleOrPurchase=").append(saleOrPurchase);
        sb.append(", operationNatureCode='").append(operationNatureCode).append('\'');
        sb.append(", isCompanyDeferredTaxEnabled=").append(isCompanyDeferredTaxEnabled);
        sb.append(", items=").append(items);
        sb.append(", locations=").append(locations);
        sb.append(", businessPartnerExemptionDetails=").append(businessPartnerExemptionDetails);
        sb.append(", party=").append(party);
        sb.append(", removedParties=").append(removedParties);
        sb.append(", isTraceRequired=").append(isTraceRequired);
        sb.append(", tteDocumentId='").append(tteDocumentId).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaxRequest)) {
            return false;
        }
        TaxRequest that = (TaxRequest)o;
        return Objects.equals(getId(), that.getId()) &&
            Objects.equals(getCompanyId(), that.getCompanyId()) &&
            Objects.equals(getBusinessPartnerId(), that.getBusinessPartnerId()) &&
            Objects.equals(getDate(), that.getDate()) &&
            getCurrency() == that.getCurrency() &&
            getIsTransactionWithinTaxReportingGroup() == that.getIsTransactionWithinTaxReportingGroup() &&
            Objects.equals(getCashDiscountPercent(), that.getCashDiscountPercent()) &&
            getIsCashDiscountPlanned() == that.getIsCashDiscountPlanned() &&
            getGrossOrNet() == that.getGrossOrNet() &&
            getSaleOrPurchase() == that.getSaleOrPurchase() &&
            Objects.equals(getOperationNatureCode(), that.getOperationNatureCode()) &&
            getIsCompanyDeferredTaxEnabled() == that.getIsCompanyDeferredTaxEnabled() &&
            Objects.equals(getItems(), that.getItems()) &&
            Objects.equals(getLocations(), that.getLocations()) &&
            Objects.equals(getBusinessPartnerExemptionDetails(), that.getBusinessPartnerExemptionDetails()) &&
            Objects.equals(getParty(), that.getParty()) &&
            Objects.equals(getRemovedParties(), that.getRemovedParties()) &&
            getIsTraceRequired() == that.getIsTraceRequired() &&
            Objects.equals(getTteDocumentId(), that.getTteDocumentId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCompanyId(), getBusinessPartnerId(), getDate(), getCurrency(),
            getIsTransactionWithinTaxReportingGroup(), getCashDiscountPercent(), getIsCashDiscountPlanned(),
            getGrossOrNet(), getSaleOrPurchase(), getOperationNatureCode(), getIsCompanyDeferredTaxEnabled(),
            getItems(), getLocations(), getBusinessPartnerExemptionDetails(), getParty(), getRemovedParties(),
            getIsTraceRequired(), getTteDocumentId());
    }
}
