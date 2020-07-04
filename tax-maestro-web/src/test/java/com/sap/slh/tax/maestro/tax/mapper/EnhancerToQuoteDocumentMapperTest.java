package com.sap.slh.tax.maestro.tax.mapper;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.sap.slh.tax.product.tax.classification.models.ProductClassification;
import com.sap.slh.tax.product.tax.classification.models.ProductClassifications;
import com.sap.slh.tax.product.tax.classification.models.ProductTaxClassification;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.domain.CurrencyCode;
import com.sap.slh.tax.maestro.api.v1.domain.AmountType;
import com.sap.slh.tax.maestro.api.v1.domain.PartyRole;
import com.sap.slh.tax.maestro.api.v1.domain.ProductType;
import com.sap.slh.tax.maestro.api.v1.domain.TransactionType;
import com.sap.slh.tax.maestro.api.v1.schema.AssignedParty;
import com.sap.slh.tax.maestro.api.v1.schema.CompanyInformation;
import com.sap.slh.tax.maestro.api.v1.schema.Item;
import com.sap.slh.tax.maestro.api.v1.schema.Party;
import com.sap.slh.tax.maestro.api.v1.schema.Product;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;
import com.sap.slh.tax.maestro.tax.exceptions.InternalInvalidModelException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class EnhancerToQuoteDocumentMapperTest {

    @Test
    public void testEnhancerValidationAfterMapping() {
        ProductClassification productOne = getEnhanceProductWithTaxClassification(2);
        ProductClassification productTwo = getEnhanceProductWithTaxClassification(1);
        Mono<ProductClassifications> enhancer = Mono
                .just(new ProductClassifications(Arrays.asList(productOne, productTwo)));

        Item item = Item.builder()
                .withId("10")
                .withAssignedProductId("10")
                .withUnitPrice(BigDecimal.ZERO)
                .withUnitPrice(BigDecimal.ZERO)
                .withAssignedParties(AssignedParty.builder().withId("10").withRole(PartyRole.SHIP_FROM).build())
                .withQuantity(BigDecimal.ZERO)
                .build();

        Party party = Party.builder().withId("10").withCountryRegionCode(CountryCode.US).build();
        CompanyInformation companyInformation = CompanyInformation.builder().withAssignedPartyId("10").build();

        Date date = Date.from(Instant.now());
        Mono<QuoteDocument> monoDocument = Mono.just(QuoteDocument.builder()
                .withId("ID")
                .withDate(date)
                .withTransactionTypeCode(TransactionType.PURCHASE)
                .withAmountTypeCode(AmountType.NET)
                .withCurrencyCode(CurrencyCode.USD)
                .withCompanyInformation(companyInformation)
                .withProducts(getQuoteDocumentProductsWithError())
                .withItems(item)
                .withParties(party)
                .build());

        StepVerifier.create(enhancer.zipWith(monoDocument, EnhancerToQuoteDocumentMapper.getInstance()))
                .expectError(InternalInvalidModelException.class)
                .verify();
    }

    @Test
    public void testWithMoreQuoteProductsThanEnhanceProducts() {
        ProductClassification productOne = getEnhanceProductWithTaxClassification(2);
        ProductClassification productTwo = getEnhanceProductWithTaxClassification(1);
        Mono<ProductClassifications> enhancer = Mono
                .just(new ProductClassifications(Arrays.asList(productOne, productTwo)));

        Item item = Item.builder()
                .withId("10")
                .withAssignedProductId("10")
                .withUnitPrice(BigDecimal.ZERO)
                .withUnitPrice(BigDecimal.ZERO)
                .withAssignedParties(AssignedParty.builder().withId("10").withRole(PartyRole.SHIP_FROM).build())
                .withQuantity(BigDecimal.ZERO)
                .build();

        Party party = Party.builder().withId("10").withCountryRegionCode(CountryCode.US).build();
        CompanyInformation companyInformation = CompanyInformation.builder().withAssignedPartyId("10").build();

        Date date = Date.from(Instant.now());
        Mono<QuoteDocument> monoDocument = Mono.just(QuoteDocument.builder()
                .withId("ID")
                .withDate(date)
                .withTransactionTypeCode(TransactionType.PURCHASE)
                .withAmountTypeCode(AmountType.NET)
                .withCurrencyCode(CurrencyCode.USD)
                .withCompanyInformation(companyInformation)
                .withProducts(getQuoteDocumentProducts())
                .withItems(item)
                .withParties(party)
                .build());

        QuoteDocument expectedQuoteDocument = QuoteDocument.builder()
                .withId("ID")
                .withDate(date)
                .withTransactionTypeCode(TransactionType.PURCHASE)
                .withAmountTypeCode(AmountType.NET)
                .withCurrencyCode(CurrencyCode.USD)
                .withCompanyInformation(companyInformation)
                .withProducts(getExpectedProductsWithProductTaxClassifications())
                .withItems(item)
                .withParties(party)
                .build();

        StepVerifier.create(enhancer.zipWith(monoDocument, EnhancerToQuoteDocumentMapper.getInstance()))
                .assertNext(document -> {
                    assertEquals(expectedQuoteDocument, document);
                })
                .verifyComplete();
    }

    @Test
    public void testWithLessQuoteProductsThanEnhanceProducts() {
        ProductClassification productOne = getEnhanceProductWithTaxClassification(2);
        ProductClassification productTwo = getEnhanceProductWithTaxClassification(1);
        Mono<ProductClassifications> enhancer = Mono
                .just(new ProductClassifications(Arrays.asList(productOne, productTwo)));

        Date date = Date.from(Instant.now());
        Item item = Item.builder()
                .withId("10")
                .withAssignedProductId("10")
                .withUnitPrice(BigDecimal.ZERO)
                .withUnitPrice(BigDecimal.ZERO)
                .withAssignedParties(AssignedParty.builder().withId("10").withRole(PartyRole.SHIP_FROM).build())
                .withQuantity(BigDecimal.ZERO)
                .build();

        List<Product> products = Arrays.asList(Product.builder().withId("10").withMasterDataProductId("10").build());

        Party party = Party.builder().withId("10").withCountryRegionCode(CountryCode.US).build();
        CompanyInformation companyInformation = CompanyInformation.builder().withAssignedPartyId("10").build();

        Mono<QuoteDocument> monoDocument = Mono.just(QuoteDocument.builder()
                .withId("ID")
                .withDate(date)
                .withTransactionTypeCode(TransactionType.PURCHASE)
                .withAmountTypeCode(AmountType.NET)
                .withCurrencyCode(CurrencyCode.USD)
                .withCompanyInformation(companyInformation)
                .withProducts(products)
                .withItems(item)
                .withParties(party)
                .build());
        
        QuoteDocument expectedQuoteDocument = QuoteDocument.builder()
                .withId("ID")
                .withDate(date)
                .withTransactionTypeCode(TransactionType.PURCHASE)
                .withAmountTypeCode(AmountType.NET)
                .withCurrencyCode(CurrencyCode.USD)
                .withCompanyInformation(companyInformation)
                .withProducts(getExpectedProductWithProductTaxClassifications())
                .withItems(item)
                .withParties(party)
                .build();

        StepVerifier.create(enhancer.zipWith(monoDocument, EnhancerToQuoteDocumentMapper.getInstance()))
                .assertNext(document -> {
                    assertEquals(expectedQuoteDocument, document);
                })
                .verifyComplete();
    }

    @Test
    public void testProductsWithSameMasterDataProductId() {
        ProductClassification productOne = getEnhanceProductWithTaxClassification(1);
        Mono<ProductClassifications> enhancer = Mono.just(new ProductClassifications(Arrays.asList(productOne)));

        Item item = Item.builder()
                .withId("10")
                .withAssignedProductId("10")
                .withUnitPrice(BigDecimal.ZERO)
                .withUnitPrice(BigDecimal.ZERO)
                .withAssignedParties(AssignedParty.builder().withId("10").withRole(PartyRole.SHIP_FROM).build())
                .withQuantity(BigDecimal.ZERO)
                .build();

        List<Product> products = Arrays.asList(Product.builder().withId("10").withMasterDataProductId("20").build(),
                Product.builder().withId("20").withMasterDataProductId("20").build());

        Party party = Party.builder().withId("10").withCountryRegionCode(CountryCode.US).build();

        CompanyInformation companyInformation = CompanyInformation.builder().withAssignedPartyId("10").build();

        Date date = Date.from(Instant.now());
        Mono<QuoteDocument> monoDocument = Mono.just(QuoteDocument.builder()
                .withId("ID")
                .withDate(date)
                .withTransactionTypeCode(TransactionType.PURCHASE)
                .withAmountTypeCode(AmountType.NET)
                .withCurrencyCode(CurrencyCode.USD)
                .withCompanyInformation(companyInformation)
                .withProducts(products)
                .withItems(item)
                .withParties(party)
                .build());

        QuoteDocument expectedQuoteDocument = QuoteDocument.builder()
                .withId("ID")
                .withDate(date)
                .withTransactionTypeCode(TransactionType.PURCHASE)
                .withAmountTypeCode(AmountType.NET)
                .withCurrencyCode(CurrencyCode.USD)
                .withCompanyInformation(companyInformation)
                .withProducts(getExpectedProductsWithProductTaxClassification())
                .withItems(item)
                .withParties(party)
                .build();

        StepVerifier.create(enhancer.zipWith(monoDocument, EnhancerToQuoteDocumentMapper.getInstance()))
                .assertNext(document -> {
                    assertEquals(expectedQuoteDocument, document);
                })
                .verifyComplete();
    }

    @Test
    public void testProductWithSameProductClassifications() {
        ProductClassification productClassification = getEnhanceProductWithTaxClassification(1);
        Mono<ProductClassifications> enhancer = Mono
                .just(new ProductClassifications(Arrays.asList(productClassification, productClassification)));

        Item item = Item.builder()
                .withId("10")
                .withAssignedProductId("10")
                .withUnitPrice(BigDecimal.ZERO)
                .withUnitPrice(BigDecimal.ZERO)
                .withAssignedParties(AssignedParty.builder().withId("10").withRole(PartyRole.SHIP_FROM).build())
                .withQuantity(BigDecimal.ZERO)
                .build();

        List<Product> products = Arrays.asList(Product.builder().withId("10").withMasterDataProductId("20").build());

        Party party = Party.builder().withId("10").withCountryRegionCode(CountryCode.US).build();

        CompanyInformation companyInformation = CompanyInformation.builder().withAssignedPartyId("10").build();

        Date date = Date.from(Instant.now());
        Mono<QuoteDocument> monoDocument = Mono.just(QuoteDocument.builder()
                .withId("ID")
                .withDate(date)
                .withTransactionTypeCode(TransactionType.PURCHASE)
                .withAmountTypeCode(AmountType.NET)
                .withCurrencyCode(CurrencyCode.USD)
                .withCompanyInformation(companyInformation)
                .withProducts(products)
                .withItems(item)
                .withParties(party)
                .build());

        QuoteDocument expectedQuoteDocument = QuoteDocument.builder()
                .withId("ID")
                .withDate(date)
                .withTransactionTypeCode(TransactionType.PURCHASE)
                .withAmountTypeCode(AmountType.NET)
                .withCurrencyCode(CurrencyCode.USD)
                .withCompanyInformation(companyInformation)
                .withProducts(getExpectedProductWithProductTaxClassification())
                .withItems(item)
                .withParties(party)
                .build();

        StepVerifier.create(enhancer.zipWith(monoDocument, EnhancerToQuoteDocumentMapper.getInstance()))
                .assertNext(document -> {
                    assertEquals(expectedQuoteDocument, document);
                })
                .verifyComplete();
    }

    private List<Product> getExpectedProductsWithProductTaxClassification() {
        com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification productTaxClassification = com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification
                .builder()
                .withCountryRegionCode(CountryCode.AR)
                .withSubdivisionCode("BA")
                .withTaxTypeCode("AA")
                .withTaxRateTypeCode("AA")
                .withExemptionReasonCode("AA")
                .withIsSoldElectronically(false)
                .withIsServicePointTaxable(false)
                .build();

        return Arrays.asList(
                Product.builder()
                        .withId("10")
                        .withTypeCode(ProductType.SERVICE)
                        .withTaxClassifications(productTaxClassification)
                        .build(),
                Product.builder()
                        .withId("20")
                        .withTypeCode(ProductType.SERVICE)
                        .withTaxClassifications(productTaxClassification)
                        .build());

    }

    private List<Product> getExpectedProductWithProductTaxClassification() {
        com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification productTaxClassification = com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification
                .builder()
                .withCountryRegionCode(CountryCode.AR)
                .withSubdivisionCode("BA")
                .withTaxTypeCode("AA")
                .withTaxRateTypeCode("AA")
                .withExemptionReasonCode("AA")
                .withIsSoldElectronically(false)
                .withIsServicePointTaxable(false)
                .build();

        return Arrays.asList(Product.builder()
                .withId("10")
                .withTypeCode(ProductType.SERVICE)
                .withTaxClassifications(productTaxClassification)
                .build());

    }

    private List<Product> getExpectedProductWithProductTaxClassifications() {

        List<com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification> productTaxClassifications = Arrays.asList(
                com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification.builder()
                        .withCountryRegionCode(CountryCode.CA)
                        .withSubdivisionCode("AL")
                        .withTaxTypeCode("AA")
                        .withTaxRateTypeCode("AA")
                        .withExemptionReasonCode("AA")
                        .withIsSoldElectronically(false)
                        .withIsServicePointTaxable(false)
                        .build(),
                com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification.builder()
                        .withCountryRegionCode(CountryCode.BR)
                        .withSubdivisionCode("RS")
                        .withTaxTypeCode("AA")
                        .withTaxRateTypeCode("AA")
                        .withExemptionReasonCode("AA")
                        .withIsSoldElectronically(false)
                        .withIsServicePointTaxable(false)
                        .build());

        return Arrays.asList(Product.builder()
                .withId("10")
                .withTypeCode(ProductType.SERVICE)
                .withTaxClassifications(productTaxClassifications)
                .build());

    }

    private List<Product> getExpectedProductsWithProductTaxClassifications() {

        List<com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification> productTaxClassifications;

        productTaxClassifications = Arrays.asList(
                com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification.builder()
                        .withCountryRegionCode(CountryCode.CA)
                        .withSubdivisionCode("AL")
                        .withTaxTypeCode("AA")
                        .withTaxRateTypeCode("AA")
                        .withExemptionReasonCode("AA")
                        .withIsSoldElectronically(false)
                        .withIsServicePointTaxable(false)
                        .build(),
                com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification.builder()
                        .withCountryRegionCode(CountryCode.BR)
                        .withSubdivisionCode("RS")
                        .withTaxTypeCode("AA")
                        .withTaxRateTypeCode("AA")
                        .withExemptionReasonCode("AA")
                        .withIsSoldElectronically(false)
                        .withIsServicePointTaxable(false)
                        .build());

        Product product1 = Product.builder()
                .withId("10")
                .withTypeCode(ProductType.SERVICE)
                .withTaxClassifications(productTaxClassifications)
                .build();

        productTaxClassifications = Arrays
                .asList(com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification.builder()
                        .withCountryRegionCode(CountryCode.AR)
                        .withSubdivisionCode("BA")
                        .withTaxTypeCode("AA")
                        .withTaxRateTypeCode("AA")
                        .withExemptionReasonCode("AA")
                        .withIsSoldElectronically(false)
                        .withIsServicePointTaxable(false)
                        .build());

        Product product2 = Product.builder()
                .withId("20")
                .withTypeCode(ProductType.SERVICE)
                .withTaxClassifications(productTaxClassifications)
                .build();

        Product product3 = Product.builder().withTypeCode(ProductType.SERVICE).withId("ID").build();

        return Arrays.asList(product1, product2, product3);

    }

    private List<Product> getQuoteDocumentProducts() {
        List<Product> products = new ArrayList<>();
        products.add(Product.builder().withId("10").withMasterDataProductId("10").build());
        products.add(Product.builder().withId("20").withMasterDataProductId("20").build());
        products.add(Product.builder().withTypeCode(ProductType.SERVICE).withId("ID").build());

        return products;
    }

    private List<Product> getQuoteDocumentProductsWithError() {
        List<Product> products = new ArrayList<>();
        products.add(Product.builder().withId("10").withMasterDataProductId("10").build());
        products.add(Product.builder().withId("20").withMasterDataProductId("20").build());
        products.add(Product.builder().withId("ID").build());

        return products;
    }

    private ProductClassification getEnhanceProductWithTaxClassification(int numberOfClassification) {
        ProductTaxClassification CA = new ProductTaxClassification("CA", "AL", "AA", "AA", "AA", false, false);
        ProductTaxClassification BR = new ProductTaxClassification("BR", "RS", "AA", "AA", "AA", false, false);
        ProductTaxClassification AR = new ProductTaxClassification("AR", "BA", "AA", "AA", "AA", false, false);

        if (numberOfClassification > 1) {
            return new ProductClassification("10", "SERVICE", Arrays.asList(CA, BR));
        }
        return new ProductClassification("20", "SERVICE", Arrays.asList(AR));
    }

}
