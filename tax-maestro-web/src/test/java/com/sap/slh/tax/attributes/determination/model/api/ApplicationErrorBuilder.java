package com.sap.slh.tax.attributes.determination.model.api;

import java.util.Arrays;
import java.util.List;

public class ApplicationErrorBuilder {
    private ApplicationError instance;

    private ApplicationErrorBuilder() {
        this.instance = new ApplicationError();
    }

    public static ApplicationErrorBuilder builder() {
        return new ApplicationErrorBuilder();
    }

    public ApplicationErrorBuilder withDetails(List<Detail> details) {
        this.instance.setDetails(details);
        return this;
    }

    public ApplicationErrorBuilder withDetails(Detail... details) {
        this.instance.setDetails(Arrays.asList(details));
        return this;
    }

    public ApplicationErrorBuilder withErrorId(String errorId) {
        this.instance.setErrorId(errorId);
        return this;
    }

    public ApplicationError build() {
        return this.instance;
    }
}
