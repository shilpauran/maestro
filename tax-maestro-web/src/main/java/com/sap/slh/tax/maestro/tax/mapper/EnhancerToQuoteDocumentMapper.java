package com.sap.slh.tax.maestro.tax.mapper;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sap.slh.tax.product.tax.classification.models.ProductClassification;
import com.sap.slh.tax.product.tax.classification.models.ProductClassifications;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.v1.domain.ProductType;
import com.sap.slh.tax.maestro.api.v1.schema.Product;
import com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;
import com.sap.slh.tax.maestro.tax.exceptions.InternalInvalidModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EnhancerToQuoteDocumentMapper implements BiFunction<ProductClassifications, QuoteDocument, QuoteDocument> {

    private static EnhancerToQuoteDocumentMapper instance;
    private static final Logger logger = LoggerFactory.getLogger(EnhancerToQuoteDocumentMapper.class);

    private Function<com.sap.slh.tax.product.tax.classification.models.ProductTaxClassification, ProductTaxClassification> mapTaxClassificationFromProduct = taxClassification ->
            ProductTaxClassification.builder()
                    .withCountryRegionCode(CountryCode.valueOf(taxClassification.getCountryRegionCode()))
                    .withSubdivisionCode(taxClassification.getSubdivisionCode())
                    .withTaxTypeCode(taxClassification.getTaxTypeCode())
                    .withExemptionReasonCode(taxClassification.getExemptionReasonCode())
                    .withIsSoldElectronically(taxClassification.getIsSoldElectronically())
                    .withIsServicePointTaxable(taxClassification.getIsServicePointTaxable())
                    .withTaxRateTypeCode(taxClassification.getTaxRateTypeCode()).build();

    private BiFunction<ProductClassification, Product, Product>
            mapProductFromEnhance = (enhanceProduct, quoteProduct) ->

            Product.builder()
                    .withId(quoteProduct.getId())
                    .withTypeCode(ProductType.valueOf(enhanceProduct.getTypeCode()))
                    .withTaxClassifications(
                            enhanceProduct.getProductTaxClassifications().stream()
                                    .map(mapTaxClassificationFromProduct).collect(Collectors.toList())
                    ).build();

    private EnhancerToQuoteDocumentMapper() {

    }

    public static EnhancerToQuoteDocumentMapper getInstance() {
        if (instance == null) {
            instance = new EnhancerToQuoteDocumentMapper();
        }
        return instance;
    }


    @Override
    public QuoteDocument apply(ProductClassifications enhanceResponse, QuoteDocument quoteRequest) {
        Map<String, ProductClassification> enhanceProductsMap = convertEnhanceProductsToMap(enhanceResponse);

        List<Product> mappedProducts = quoteRequest.getProducts().stream()
                .map(quoteProduct -> {
                    if (enhanceProductsMap.containsKey(quoteProduct.getMasterDataProductId())) {
                        return mapProductFromEnhance.apply(
                                enhanceProductsMap.get(quoteProduct.getMasterDataProductId()),
                                quoteProduct);
                    }
                    return quoteProduct;
                }).collect(Collectors.toList());

        quoteRequest.setProducts(mappedProducts);

        validateEnhancedRequest(quoteRequest);
        return quoteRequest;
    }

    private void validateEnhancedRequest(QuoteDocument quoteRequest) {
        try {
            quoteRequest.validate();
        } catch (InvalidModelException e) {
            logger.error(e.getMessage(), e);
            throw new InternalInvalidModelException(e.getMessage());
        }
    }

    private Map<String, ProductClassification> convertEnhanceProductsToMap(ProductClassifications enhanceResponse) {
        return enhanceResponse.getProductClassifications().stream()
                .collect(Collectors.toMap(
                        ProductClassification::getProductId,
                        Function.identity(), (oldValue, newValue) -> newValue));
    }

}
