package com.sap.slh.tax.maestro.tax.mapper;

import com.sap.slh.tax.product.tax.classification.models.ProductIdsForProductClassification;
import com.sap.slh.tax.maestro.api.v1.schema.Product;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class QuoteDocumentToEnhancerMapper
        implements Function<QuoteDocument, Mono<ProductIdsForProductClassification>> {

    private static final String DEFAULT_TIME_ZONE = "UTC";

    private static QuoteDocumentToEnhancerMapper instance;

    private QuoteDocumentToEnhancerMapper() {

    }

    public static QuoteDocumentToEnhancerMapper getInstance() {
        if (instance == null) {
            instance = new QuoteDocumentToEnhancerMapper();
        }
        return instance;
    }

    @Override
    public Mono<ProductIdsForProductClassification> apply(QuoteDocument document) {
        return Flux.fromIterable(document.getProducts())
                .filter(this::hasMasterDataProductCode)
                .map(product -> new String(product.getMasterDataProductId()))
                .distinct()
                .collect(Collectors.toList())
                .map(products -> new ProductIdsForProductClassification(products, getInstantDate(document.getDate())));
    }

    private boolean hasMasterDataProductCode(Product product) {
        return StringUtils.isNotBlank(product.getMasterDataProductId());
    }

    private Instant getInstantDate(Date date) {
        return date.toInstant().atZone(ZoneId.of(DEFAULT_TIME_ZONE)).toInstant();
    }

}
