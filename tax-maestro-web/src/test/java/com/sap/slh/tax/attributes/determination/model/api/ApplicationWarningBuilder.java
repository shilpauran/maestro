package com.sap.slh.tax.attributes.determination.model.api;

import java.util.Arrays;
import java.util.List;

public class ApplicationWarningBuilder {
    private ApplicationWarning instance;

    private ApplicationWarningBuilder() {
        this.instance = new ApplicationWarning();
    }

    public static ApplicationWarningBuilder builder() {
        return new ApplicationWarningBuilder();
    }

    public ApplicationWarningBuilder withDetails(List<Detail> details) {
        this.instance.setDetails(details);
        return this;
    }

    public ApplicationWarningBuilder withDetails(Detail... details) {
        this.instance.setDetails(Arrays.asList(details));
        return this;
    }

    public ApplicationWarning build() {
        return this.instance;
    }
}
