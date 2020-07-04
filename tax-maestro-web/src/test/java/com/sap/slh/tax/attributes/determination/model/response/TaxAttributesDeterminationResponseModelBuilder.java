package com.sap.slh.tax.attributes.determination.model.response;

public class TaxAttributesDeterminationResponseModelBuilder {
    private TaxAttributesDeterminationResponseModel instance;

    private TaxAttributesDeterminationResponseModelBuilder() {
        this.instance = new TaxAttributesDeterminationResponseModel();
    }

    public static TaxAttributesDeterminationResponseModelBuilder builder() {
        return new TaxAttributesDeterminationResponseModelBuilder();
    }

    public TaxAttributesDeterminationResponseModelBuilder withResult(TaxAttributesDeterminationResponse result) {
        this.instance.setResult(result);
        return this;
    }

    public TaxAttributesDeterminationResponseModel build() {
        return this.instance;
    }
}
