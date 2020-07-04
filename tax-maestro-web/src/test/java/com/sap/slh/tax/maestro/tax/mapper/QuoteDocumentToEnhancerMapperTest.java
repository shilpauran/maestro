package com.sap.slh.tax.maestro.tax.mapper;

import com.sap.slh.tax.maestro.api.v1.schema.Product;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;
import com.sap.slh.tax.product.tax.classification.models.ProductIdsForProductClassification;

import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class QuoteDocumentToEnhancerMapperTest {

    @Test
    public void documentWithProductsAndDate() {

        Date date = Date.from(Instant.now());
        Instant expectedDate = date.toInstant().atZone(ZoneId.of("UTC")).toInstant();

        String productCodeOne = "1";
        String productCodeTwo = "2";
        Product productOne = Product.builder().withMasterDataProductId(productCodeOne).build();
        Product productTwo = Product.builder().withMasterDataProductId(productCodeTwo).build();
        Product productThree = Product.builder().build();
        List<Product> products = Arrays.asList(productOne, productTwo, productThree);

        QuoteDocument document = QuoteDocument.builder().withDate(date).withProducts(products).build();

        ProductIdsForProductClassification expectedOutput = new ProductIdsForProductClassification(
                Arrays.asList(productCodeOne, productCodeTwo), expectedDate);

        StepVerifier.create(Mono.just(document).flatMap(QuoteDocumentToEnhancerMapper.getInstance()))
                .expectNextMatches(request -> {
                    assertThat(request).usingRecursiveComparison().isEqualTo(expectedOutput);
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void documentWithProductsWithSameMasterDataProductId() {

        Date date = Date.from(Instant.now());
        Instant expectedDate = date.toInstant().atZone(ZoneId.of("UTC")).toInstant();

        String productCode = "04e158dd-b857-4dc9-a342-1df766a3d5c8";
        Product productOne = Product.builder().withMasterDataProductId(productCode).build();
        Product productTwo = Product.builder().withMasterDataProductId(productCode).build();
        List<Product> products = Arrays.asList(productOne, productTwo);

        QuoteDocument document = QuoteDocument.builder().withDate(date).withProducts(products).build();

        ProductIdsForProductClassification expectedOutput = new ProductIdsForProductClassification(
                Arrays.asList(productCode), expectedDate);

        StepVerifier.create(Mono.just(document).flatMap(QuoteDocumentToEnhancerMapper.getInstance()))
                .expectNextMatches(request -> {
                    assertThat(request).usingRecursiveComparison().isEqualTo(expectedOutput);
                    return true;
                })
                .expectComplete()
                .verify();

    }

}
