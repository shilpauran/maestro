package com.sap.slh.tax.attributes.determination.model.response;

import java.util.Arrays;
import java.util.List;

public class TaxAttributesDeterminationResponseBuilder {
    private TaxAttributesDeterminationResponse instance;

    private TaxAttributesDeterminationResponseBuilder() {
        this.instance = new TaxAttributesDeterminationResponse();
    }

    public static TaxAttributesDeterminationResponseBuilder builder() {
        return new TaxAttributesDeterminationResponseBuilder();
    }

    public TaxAttributesDeterminationResponseBuilder withId(String id) {
        this.instance.setId(id);
        return this;
    }

    public TaxAttributesDeterminationResponseBuilder withItems(List<ResponseItem> items) {
        this.instance.setItems(items);
        return this;
    }

    public TaxAttributesDeterminationResponseBuilder withItems(ResponseItem... items) {
        this.instance.setItems(Arrays.asList(items));
        return this;
    }

    public TaxAttributesDeterminationResponse build() {
        return this.instance;
    }
}
