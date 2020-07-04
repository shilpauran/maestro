package com.sap.slh.tax.maestro.tax.mapper;

import com.sap.slh.tax.maestro.api.v0.domain.GrossOrNet;
import com.sap.slh.tax.maestro.api.v0.schema.TaxLine;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse;
import org.springframework.util.CollectionUtils;

import java.util.function.Function;
import java.util.stream.Collectors;

public final class TaxRequestToTaxResponseMapper implements Function<TaxRequest, TaxResponse> {

    private static String TAX_SERVICE_ENGINE = "Tax Service";
    private static TaxRequestToTaxResponseMapper instance;

    private TaxRequestToTaxResponseMapper() {

    }

    public static TaxRequestToTaxResponseMapper getInstance() {
        if (instance == null) {
            instance = new TaxRequestToTaxResponseMapper();
        }
        return instance;
    }

    @Override
    public TaxResponse apply(TaxRequest request) {
        return TaxResponse.builder()
                .withDate(request.getDate())
                .withInclusive(mapGrossOrNetToInclusive(request.getGrossOrNet()))
                .withTaxLines(!CollectionUtils.isEmpty(request.getItems()) ? request.getItems()
                        .stream()
                        .map(item -> TaxLine.builder().withId(item.getId()).build())
                        .collect(Collectors.toList()) : null)
                        .withPartnerName(TAX_SERVICE_ENGINE)
                .build();
    }

    private String mapGrossOrNetToInclusive(GrossOrNet grossOrNet) {
        if (grossOrNet == null) {
            return null;
        } else if (grossOrNet.isGross()) {
            return "true";
        } else {
            return "false";
        }
    }
}
