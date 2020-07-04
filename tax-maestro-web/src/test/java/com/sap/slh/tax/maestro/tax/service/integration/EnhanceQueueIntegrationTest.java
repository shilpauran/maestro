package com.sap.slh.tax.maestro.tax.service.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.core.ParameterizedTypeReference;

import com.google.common.net.HttpHeaders;
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
import com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;
import com.sap.slh.tax.maestro.context.RequestContextService;
import com.sap.slh.tax.maestro.tax.exceptions.NoAmqpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.QueueCommunicationException;
import com.sap.slh.tax.maestro.tax.exceptions.UnknownAmqpResponseTypeException;
import com.sap.slh.tax.maestro.tax.exceptions.enhance.EnhanceBadRequestException;
import com.sap.slh.tax.maestro.tax.jmx.EnhanceQueueIntegrationErrorMBean;
import com.sap.slh.tax.maestro.tax.mapper.EnhancerToQuoteDocumentMapper;
import com.sap.slh.tax.maestro.tax.mapper.QuoteDocumentToEnhancerMapper;
import com.sap.slh.tax.product.tax.classification.models.ErrorDetail;
import com.sap.slh.tax.product.tax.classification.models.ErrorResponse;
import com.sap.slh.tax.product.tax.classification.models.ProductClassification;
import com.sap.slh.tax.product.tax.classification.models.ProductClassifications;
import com.sap.slh.tax.product.tax.classification.models.ProductIdsForProductClassification;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class EnhanceQueueIntegrationTest {

    private RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
    private Jackson2JsonMessageConverter msgConverter = new Jackson2JsonMessageConverter();
    private RequestContextService requestContextService = Mockito.mock(RequestContextService.class);
    private String defaultTenant = "tenant1";
    private Locale defaultLocale = Locale.ENGLISH;
    private String defaultJwt = "jwt";
    private String defaultCorrelationId = "b03d17e7-480d-4f3b-9f3c-5307a0d27321";

    private EnhanceQueueIntegration integration;

    @Before
    public void setup() {
        integration = new EnhanceQueueIntegration(rabbitTemplate, msgConverter, requestContextService);
        integration.enhanceErrorMBean = new EnhanceQueueIntegrationErrorMBean();

        Mockito.when(requestContextService.getCorrelationId()).thenReturn(Mono.just(defaultCorrelationId));
        Mockito.when(requestContextService.getTenantId()).thenReturn(Mono.just(defaultTenant));
        Mockito.when(requestContextService.getJwt()).thenReturn(Mono.just(defaultJwt));
        Mockito.when(requestContextService.getLocale()).thenReturn(Mono.just(defaultLocale));
    }

    @Test
    public void testCallEnhanceQueueIntegration() {
        ProductClassifications enhancerResponse = buildEnhancerResponse(buildProduct());
        Message taxRequestEnhancedMsg = msgConverter.toMessage(enhancerResponse, new MessageProperties(),
                new ParameterizedTypeReference<ProductClassifications>() {
                }.getType());

        mockSendAndReceive(taxRequestEnhancedMsg);

        StepVerifier.create(integration.call(Mono.just(buildQuoteDocument()),
                QuoteDocumentToEnhancerMapper.getInstance(), EnhancerToQuoteDocumentMapper.getInstance()))
                .consumeNextWith((output -> {
                    this.verifyMappingResponse(enhancerResponse, output);
                    assertEquals(0, integration.enhanceErrorMBean.getCount());
                    this.verifyMessageHeaderAttributes(
                            "com.sap.slh.tax.product.tax.classification.models.ProductIdsForProductClassification",
                            defaultLocale.toLanguageTag(), defaultJwt, defaultCorrelationId);
                })).verifyComplete();
    }

    @Test
    public void testCallEnhanceQueueIntegrationWithNoResponse() {
        StepVerifier.create(integration.call(Mono.just(new ProductIdsForProductClassification())))
                .expectError(NoAmqpResponseException.class).verify();
        assertEquals(1, integration.enhanceErrorMBean.getCount());
    }

    @Test
    public void testCallEnhanceQueueIntegrationWithNotFoundError() {
        String message = "Could not enhance the request";

        ErrorResponse error = buildErrorResponse(404, message,
                ErrorDetail.builder().withMessage("the id you sent is not correct").build());

        Message errorMessage = msgConverter.toMessage(error, new MessageProperties(),
                new ParameterizedTypeReference<ErrorResponse>() {
                }.getType());

        mockSendAndReceive(errorMessage);

        StepVerifier.create(integration.call(Mono.just(buildQuoteDocument()),
                QuoteDocumentToEnhancerMapper.getInstance(), EnhancerToQuoteDocumentMapper.getInstance()))
                .consumeErrorWith(exception -> {
                    EnhanceBadRequestException e = (EnhanceBadRequestException) exception;
                    assertEquals(message, e.getErrorMessage());
                    assertEquals(0, integration.enhanceErrorMBean.getCount());
                }).verify();
    }

    @Test
    public void testCallEnhanceQueueIntegrationWithError() {
        String message = "Could not enhance the request";
        ErrorResponse error = buildErrorResponse(500, message,
                ErrorDetail.builder().withMessage("unable to process the request").build());

        Message errorMessage = msgConverter.toMessage(error, new MessageProperties(),
                new ParameterizedTypeReference<ErrorResponse>() {
                }.getType());

        mockSendAndReceive(errorMessage);

        StepVerifier.create(integration.call(Mono.just(buildQuoteDocument()),
                QuoteDocumentToEnhancerMapper.getInstance(), EnhancerToQuoteDocumentMapper.getInstance()))
                .consumeErrorWith(exception -> {
                    QueueCommunicationException e = (QueueCommunicationException) exception;
                    assertEquals(message, e.getErrorMessage());
                    assertEquals(0, integration.enhanceErrorMBean.getCount());
                }).verify();
    }

    @Test
    public void testCallEnhanceQueueIntegrationWithUnknownResponse() {
        Message errorMessage = msgConverter.toMessage("unexpected", new MessageProperties(), String.class);

        mockSendAndReceive(errorMessage);

        StepVerifier
                .create(integration.call(Mono.just(buildQuoteDocument()), QuoteDocumentToEnhancerMapper.getInstance(),
                        EnhancerToQuoteDocumentMapper.getInstance()))
                .expectError(UnknownAmqpResponseTypeException.class).verify();
        assertEquals(0, integration.enhanceErrorMBean.getCount());

    }

    private void mockSendAndReceive(Message returnMessage) {
        Mockito.when(rabbitTemplate.sendAndReceive(Mockito.anyString(), Mockito.anyString(), any(Message.class)))
                .thenReturn(returnMessage);
    }

    private QuoteDocument buildQuoteDocument() {
        Item item = Item.builder().withId("10").withAssignedProductId("10").withUnitPrice(BigDecimal.ZERO)
                .withUnitPrice(BigDecimal.ZERO)
                .withAssignedParties(AssignedParty.builder().withId("10").withRole(PartyRole.SHIP_FROM).build())
                .withQuantity(BigDecimal.ZERO).build();

        Party party = Party.builder().withId("10").withCountryRegionCode(CountryCode.US).build();

        CompanyInformation companyInformation = CompanyInformation.builder().withAssignedPartyId("10").build();

        return QuoteDocument.builder().withId("ID").withTransactionTypeCode(TransactionType.PURCHASE)
                .withAmountTypeCode(AmountType.NET).withCurrencyCode(CurrencyCode.USD)
                .withCompanyInformation(companyInformation).withDate(new Date())
                .withProducts(Product.builder().withId("10").withMasterDataProductId("001").build()).withItems(item)
                .withParties(party).build();
    }

    private ProductClassification buildProduct() {
        return new ProductClassification("001", ProductType.SERVICE.toString(),
                Arrays.asList(new com.sap.slh.tax.product.tax.classification.models.ProductTaxClassification("CA",
                        "subdivisionCode", "taxTypeCode", "taxExemptionReasonCode", "taxRateTypeCode", false, false)));
    }

    private ProductClassifications buildEnhancerResponse(ProductClassification... products) {
        ProductClassifications enhancerResponse = new ProductClassifications();
        enhancerResponse.setProductClassifications(Arrays.asList(products));

        return enhancerResponse;
    }

    private ErrorResponse buildErrorResponse(Integer status, String message, ErrorDetail detail) {
        return ErrorResponse.builder().withStatus(status).withMessage(message).withErrorDetail(detail).build();
    }

    private void verifyMappingResponse(ProductClassifications enhancerResponse, QuoteDocument output) {
        List<ProductClassification> inProducts = enhancerResponse.getProductClassifications();
        List<Product> outProducts = output.getProducts();

        assertEquals(inProducts.size(), outProducts.size());

        for (int i = 0; i < inProducts.size(); i++) {
            assertNull(outProducts.get(i).getMasterDataProductId());
            assertEquals(inProducts.get(i).getTypeCode(), outProducts.get(i).getTypeCode().toString());

            List<com.sap.slh.tax.product.tax.classification.models.ProductTaxClassification> inClassifications = inProducts
                    .get(i).getProductTaxClassifications();
            List<ProductTaxClassification> outClassifications = outProducts.get(i).getTaxClassifications();

            assertEquals(inClassifications.size(), outClassifications.size());

            for (int j = 0; j < inClassifications.size(); j++) {
                assertEquals(inClassifications.get(j).getCountryRegionCode(),
                        outClassifications.get(j).getCountryRegionCode().toString());
                assertEquals(inClassifications.get(j).getIsServicePointTaxable(),
                        outClassifications.get(j).getIsServicePointTaxable());
                assertEquals(inClassifications.get(j).getIsSoldElectronically(),
                        outClassifications.get(j).getIsSoldElectronically());
                assertEquals(inClassifications.get(j).getSubdivisionCode(),
                        outClassifications.get(j).getSubdivisionCode());
                assertEquals(inClassifications.get(j).getExemptionReasonCode(),
                        outClassifications.get(j).getExemptionReasonCode());
                assertEquals(inClassifications.get(j).getTaxRateTypeCode(),
                        outClassifications.get(j).getTaxRateTypeCode());
                assertEquals(inClassifications.get(j).getTaxTypeCode(), outClassifications.get(j).getTaxTypeCode());
            }
        }
    }

    private void verifyMessageHeaderAttributes(String objectType, String locale, String jwt, String correlationId) {
        Mockito.verify(rabbitTemplate).sendAndReceive(any(String.class), any(String.class), argThat((Message msg) -> {
            return msg.getMessageProperties().getHeaders().get("__TypeId__") == objectType
                    && msg.getMessageProperties().getHeaders().get(HttpHeaders.ACCEPT_LANGUAGE) == locale
                    && msg.getMessageProperties().getHeaders().get(HttpHeaders.AUTHORIZATION) == jwt
                    && msg.getMessageProperties().getHeaders()
                            .get(QueueIntegration.HEADER_CORRELATION_ID) == correlationId;
        }));
    }
}
