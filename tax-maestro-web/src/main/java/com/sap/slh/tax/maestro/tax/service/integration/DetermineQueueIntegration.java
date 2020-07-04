package com.sap.slh.tax.maestro.tax.service.integration;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.sap.slh.tax.attributes.determination.model.api.Detail;
import com.sap.slh.tax.attributes.determination.model.api.Status;
import com.sap.slh.tax.attributes.determination.model.request.TaxAttributesDeterminationRequest;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.maestro.context.RequestContextService;
import com.sap.slh.tax.maestro.tax.exceptions.QueueCommunicationException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DetermineInvalidModelException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DetermineNoContentException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DeterminePartialContentException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail;
import com.sap.slh.tax.maestro.tax.jmx.DetermineQueueIntegrationErrorMBean;
import com.sap.slh.tax.maestro.tax.jmx.IntegrationMBean;

@Service
public class DetermineQueueIntegration
        extends QueueIntegration<TaxAttributesDeterminationRequest, TaxAttributesDeterminationResponseModel> {
    private static final Logger logger = LoggerFactory.getLogger(DetermineQueueIntegration.class);

    private static final String EXCHANGE_NAME_DETERMINE = "txs.engine.TAXSERVICE";
    private static final String ROUTING_KEY_DETERMINE = "txs.tax.v1.determine";

    @Autowired
    protected DetermineQueueIntegrationErrorMBean determineErrorMBean;

    @Autowired
    public DetermineQueueIntegration(RabbitTemplate rabbitTemplate, Jackson2JsonMessageConverter msgConverter,
            RequestContextService requestContextService) {
        super(EXCHANGE_NAME_DETERMINE, ROUTING_KEY_DETERMINE, rabbitTemplate, msgConverter, requestContextService);
    }

    @Override
    protected String callingService() {
        return "Tax Determination";
    }

    @Override
    protected TaxAttributesDeterminationResponseModel handleAmqpResponse(Message requestMessage,
            Message responseMessage) {
        TaxAttributesDeterminationResponseModel response = (TaxAttributesDeterminationResponseModel) fromExpectedMessage(
                responseMessage, TaxAttributesDeterminationResponseModel.class);

        if (!response.getStatus().equals(Status.SUCCESS)) {
            logger.error("Tax determination error: {}\n from message: {}", responseMessage, requestMessage);
            handleErrorResponse(response);
        }

        return response;
    }

    private void handleErrorResponse(TaxAttributesDeterminationResponseModel response) {
        switch (response.getStatus()) {
        case INVALID_REQUEST:
            throw new DetermineInvalidModelException(response.getStatusMessage(), getErrorDetails(response));
        case NO_CONTENT:
            throw new DetermineNoContentException(response.getStatusMessage(), getErrorDetails(response));
        case PARTIAL_CONTENT:
            throw new DeterminePartialContentException(response.getStatusMessage(), getErrorDetails(response));
        default:
            throw new QueueCommunicationException(response.getStatusMessage());
        }
    }

    private List<ErrorDetail> getErrorDetails(TaxAttributesDeterminationResponseModel response) {
        List<ErrorDetail> errorDetails = Collections.emptyList();
        if (response.getError() != null && !CollectionUtils.isEmpty(response.getError().getDetails())) {
            List<Detail> determineErrorDetails = response.getError().getDetails();

            errorDetails = determineErrorDetails.stream()
                    .map(detail -> new ErrorDetail(detail.getErrorCode(), detail.getMessage()))
                    .collect(Collectors.toList());
        }
        return errorDetails;
    }

    @Override
    protected IntegrationMBean getQueueIntegrationErrorMBean() {
        return determineErrorMBean;
    }

}
