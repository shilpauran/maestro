package com.sap.slh.tax.maestro.tax.service.integration;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import com.google.common.net.HttpHeaders;
import com.sap.slh.tax.attributes.determination.model.api.ApplicationError;
import com.sap.slh.tax.attributes.determination.model.api.Detail;
import com.sap.slh.tax.attributes.determination.model.api.DetailBuilder;
import com.sap.slh.tax.attributes.determination.model.api.ProcessingStatusCode;
import com.sap.slh.tax.attributes.determination.model.api.Status;
import com.sap.slh.tax.attributes.determination.model.api.ValidationErrorCode;
import com.sap.slh.tax.attributes.determination.model.request.TaxAttributesDeterminationRequest;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponse;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.attributes.determination.model.response.TaxLine;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.v1.domain.PartyRole;
import com.sap.slh.tax.maestro.api.v1.domain.ProductType;
import com.sap.slh.tax.maestro.api.v1.domain.TransactionType;
import com.sap.slh.tax.maestro.api.v1.schema.AssignedParty;
import com.sap.slh.tax.maestro.api.v1.schema.Item;
import com.sap.slh.tax.maestro.api.v1.schema.Party;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;
import com.sap.slh.tax.maestro.context.RequestContextService;
import com.sap.slh.tax.maestro.tax.exceptions.NoAmqpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.QueueCommunicationException;
import com.sap.slh.tax.maestro.tax.exceptions.UnknownAmqpResponseTypeException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DetermineInvalidModelException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DetermineNoContentException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DeterminePartialContentException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail;
import com.sap.slh.tax.maestro.tax.jmx.DetermineQueueIntegrationErrorMBean;
import com.sap.slh.tax.maestro.tax.mapper.QuoteDocumentToDetermineRequestMapper;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class DetermineQueueIntegrationTest {

    private RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
    private Jackson2JsonMessageConverter msgConverter = new Jackson2JsonMessageConverter();
    private RequestContextService requestContextService = Mockito.mock(RequestContextService.class);
    private String defaultTenant = "tenant1";
    private Locale defaultLocale = Locale.ENGLISH;
    private String defaultJwt = "jwt";
    private String defaultCorrelationId = "b03d17e7-480d-4f3b-9f3c-5307a0d27321";

    private Function<TaxAttributesDeterminationResponseModel, Mono<TaxAttributesDeterminationResponseModel>> mapDeterminationResponseToDeterminationResponse = Mono::just;

    private DetermineQueueIntegration integration;

    @Before
    public void setup() {
        integration = new DetermineQueueIntegration(rabbitTemplate, msgConverter, requestContextService);
        integration.determineErrorMBean = new DetermineQueueIntegrationErrorMBean();

        Mockito.when(requestContextService.getCorrelationId()).thenReturn(Mono.just(defaultCorrelationId));
        Mockito.when(requestContextService.getTenantId()).thenReturn(Mono.just(defaultTenant));
        Mockito.when(requestContextService.getJwt()).thenReturn(Mono.just(defaultJwt));
        Mockito.when(requestContextService.getLocale()).thenReturn(Mono.just(defaultLocale));
    }

    @Test
    public void testCallDetermineQueueIntegration() {
        TaxAttributesDeterminationResponseModel determineResponse = buildDetermineResponse();
        Message determineResponsedMsg = msgConverter.toMessage(determineResponse, new MessageProperties(),
                TaxAttributesDeterminationResponseModel.class);

        mockSendAndReceive(determineResponsedMsg);

        StepVerifier.create(integration.call(Mono.just(buildQuoteDocument()),
                QuoteDocumentToDetermineRequestMapper.getInstance(), mapDeterminationResponseToDeterminationResponse))
                .consumeNextWith((output -> {
                    assertEquals(Status.SUCCESS, output.getStatus());
                    assertEquals(0, integration.determineErrorMBean.getCount());
                    this.verifyMessageHeaderAttributes(
                            "com.sap.slh.tax.attributes.determination.model.request.TaxAttributesDeterminationRequest",
                            defaultLocale.toLanguageTag(), defaultJwt, defaultCorrelationId);
                })).verifyComplete();
    }

    @Test
    public void testCallDetermineQueueIntegrationWithNoResponse() {
        StepVerifier.create(integration.call(Mono.just(new TaxAttributesDeterminationRequest())))
                .expectError(NoAmqpResponseException.class).verify();
        assertEquals(1, integration.determineErrorMBean.getCount());
    }

    @Test
    public void testCallDetermineQueueIntegrationWithError() {
        TaxAttributesDeterminationResponseModel determineResponse = buildErrorDetermineResponse(Status.FAILURE,
                ProcessingStatusCode.INTERNAL_SERVER_ERROR);
        Message determineResponsedMsg = msgConverter.toMessage(determineResponse, new MessageProperties());

        mockSendAndReceive(determineResponsedMsg);

        StepVerifier.create(integration.call(Mono.just(buildQuoteDocument()),
                QuoteDocumentToDetermineRequestMapper.getInstance(), mapDeterminationResponseToDeterminationResponse))
                .expectError(QueueCommunicationException.class).verify();
        assertEquals(0, integration.determineErrorMBean.getCount());
    }

    @Test
    public void testCallDetermineQueueIntegrationWithNoContentError() {
        TaxAttributesDeterminationResponseModel determineResponse = buildErrorDetermineResponse(Status.NO_CONTENT,
                ProcessingStatusCode.CONTENT_NOT_FOUND);
        Message determineResponsedMsg = msgConverter.toMessage(determineResponse, new MessageProperties());

        mockSendAndReceive(determineResponsedMsg);

        StepVerifier.create(integration.call(Mono.just(buildQuoteDocument()),
                QuoteDocumentToDetermineRequestMapper.getInstance(), mapDeterminationResponseToDeterminationResponse))
                .expectError(DetermineNoContentException.class).verify();
        assertEquals(0, integration.determineErrorMBean.getCount());
    }

    @Test
    public void testCallDetermineQueueIntegrationWithPartialContentError() {
        TaxAttributesDeterminationResponseModel determineResponse = buildErrorDetermineResponse(Status.PARTIAL_CONTENT,
                ProcessingStatusCode.TAX_ATTRIBUTES_PARTIALLY_FETCHED);
        Message determineResponsedMsg = msgConverter.toMessage(determineResponse, new MessageProperties());

        mockSendAndReceive(determineResponsedMsg);

        StepVerifier.create(integration.call(Mono.just(buildQuoteDocument()),
                QuoteDocumentToDetermineRequestMapper.getInstance(), mapDeterminationResponseToDeterminationResponse))
                .expectError(DeterminePartialContentException.class).verify();
        assertEquals(0, integration.determineErrorMBean.getCount());
    }

    @Test
    public void testCallDetermineQueueIntegrationWithInvalidRequestError() {
        TaxAttributesDeterminationResponseModel determineResponse = buildErrorDetermineResponseWithDetails(
                Status.INVALID_REQUEST, ProcessingStatusCode.REQUEST_NOT_VALID);

        Message determineResponsedMsg = msgConverter.toMessage(determineResponse, new MessageProperties());

        mockSendAndReceive(determineResponsedMsg);

        List<ErrorDetail> errorDetails = Arrays.asList(
                new ErrorDetail(ValidationErrorCode.DATE_DOES_NOT_EXIST, "message1"),
                new ErrorDetail(ValidationErrorCode.ERROR_DOCUMENT_ITEM_REQUIRED, "message2"));

        DetermineInvalidModelException expectedException = new DetermineInvalidModelException(
                "We were not able to fetch the tax attributes for this request; please check the errors for details.",
                errorDetails);

        StepVerifier.create(integration.call(Mono.just(buildQuoteDocument()),
                QuoteDocumentToDetermineRequestMapper.getInstance(), mapDeterminationResponseToDeterminationResponse))
                .consumeErrorWith(exception -> {
                    assertEquals(expectedException, exception);
                    assertEquals(0, integration.determineErrorMBean.getCount());
                }).verify();
    }

    @Test
    public void testCallDetermineQueueIntegrationWithUnknownResponse() {
        Message determineResponsedMsg = msgConverter.toMessage("unexpected", new MessageProperties(), String.class);

        mockSendAndReceive(determineResponsedMsg);

        StepVerifier.create(integration.call(Mono.just(buildQuoteDocument()),
                QuoteDocumentToDetermineRequestMapper.getInstance(), mapDeterminationResponseToDeterminationResponse))
                .expectError(UnknownAmqpResponseTypeException.class).verify();
        assertEquals(0, integration.determineErrorMBean.getCount());
    }

    private void mockSendAndReceive(Message returnMessage) {
        Mockito.when(rabbitTemplate.sendAndReceive(Mockito.anyString(), Mockito.anyString(), any(Message.class)))
                .thenReturn(returnMessage);
    }

    private QuoteDocument buildQuoteDocument() {
        return QuoteDocument.builder().withDate(new Date())
                .withProducts(
                        com.sap.slh.tax.maestro.api.v1.schema.Product.builder().withMasterDataProductId("001").build())
                .withTransactionTypeCode(TransactionType.PURCHASE)
                .withItems(Item.builder()
                        .withAssignedParties(AssignedParty.builder().withId("id").withRole(PartyRole.BILL_FROM).build())
                        .build())
                .withProducts(com.sap.slh.tax.maestro.api.v1.schema.Product.builder().withTypeCode(ProductType.MATERIAL)
                        .build())
                .withParties(Party.builder().withCountryRegionCode(CountryCode.CU).build()).build();
    }

    private TaxAttributesDeterminationResponseModel buildDetermineResponse() {
        TaxAttributesDeterminationResponseModel responseModel = new TaxAttributesDeterminationResponseModel();

        TaxAttributesDeterminationResponse detResponse = new TaxAttributesDeterminationResponse();
        detResponse.setId("parentID");

        TaxLine taxLine = new TaxLine();
        taxLine.setId("taxLineId");
        taxLine.setIsTaxDeferred(Boolean.FALSE);
        taxLine.setNonDeductibleTaxRate(BigDecimal.TEN);
        taxLine.setDueCategoryCode("dueCategory");
        taxLine.setTaxTypeCode("taxTypeCode");

        com.sap.slh.tax.attributes.determination.model.response.ResponseItem item = new com.sap.slh.tax.attributes.determination.model.response.ResponseItem();
        item.setId("id");
        item.setCountryRegionCode("BR");
        item.setSubdivisionCode("state");
        item.setTaxCode("taxCode");
        item.setTaxEventLegalPhrase("legalPhrase");
        item.setTaxDeterminationDetails("details");
        item.setTaxEventCode("eventCode");
        item.setIsTaxEventNonTaxable(Boolean.FALSE);
        item.setTaxes(Arrays.asList(taxLine));

        detResponse.setItems(Arrays.asList(item));

        responseModel.setResult(detResponse);

        responseModel.setStatus(Status.SUCCESS);
        responseModel.setProcessingStatusCode(ProcessingStatusCode.TAX_ATTRIBUTES_FETCHED_SUCCESSFULLY);
        responseModel.setStatusMessage("Tax attributes fetched successfully");

        return responseModel;
    }

    private TaxAttributesDeterminationResponseModel buildErrorDetermineResponse(Status status,
            ProcessingStatusCode processingStatusCode) {
        TaxAttributesDeterminationResponseModel responseModel = new TaxAttributesDeterminationResponseModel();

        responseModel.setStatus(status);
        responseModel.setProcessingStatusCode(processingStatusCode);
        responseModel.setStatusMessage("Error");

        return responseModel;
    }

    private TaxAttributesDeterminationResponseModel buildErrorDetermineResponseWithDetails(Status status,
            ProcessingStatusCode processingStatusCode) {
        TaxAttributesDeterminationResponseModel responseModel = this.buildErrorDetermineResponse(status,
                processingStatusCode);

        ApplicationError determinationError = new ApplicationError();

        Detail detail1 = DetailBuilder.builder().withErrorCode(ValidationErrorCode.DATE_DOES_NOT_EXIST)
                .withMessage("message1").build();

        Detail detail2 = DetailBuilder.builder().withErrorCode(ValidationErrorCode.ERROR_DOCUMENT_ITEM_REQUIRED)
                .withMessage("message2").build();

        List<Detail> details = Arrays.asList(detail1, detail2);
        determinationError.setDetails(details);

        responseModel.setError(determinationError);

        return responseModel;
    }

    private void verifyMessageHeaderAttributes(String objectType, String locale, String jwt, String correlationId) {
        Mockito.verify(rabbitTemplate).sendAndReceive(any(String.class), any(String.class),
                argThat((Message msg) -> msg.getMessageProperties().getHeaders().get("__TypeId__") == objectType
                        && msg.getMessageProperties().getHeaders().get(HttpHeaders.ACCEPT_LANGUAGE) == locale
                        && msg.getMessageProperties().getHeaders().get(HttpHeaders.AUTHORIZATION) == jwt
                        && msg.getMessageProperties().getHeaders()
                                .get(QueueIntegration.HEADER_CORRELATION_ID) == correlationId));
    }
}
