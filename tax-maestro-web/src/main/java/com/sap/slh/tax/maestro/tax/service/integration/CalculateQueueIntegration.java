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

import com.sap.slh.tax.calculation.model.common.TaxCalculationErrorDetail;
import com.sap.slh.tax.calculation.model.common.TaxCalculationRequest;
import com.sap.slh.tax.calculation.model.common.TaxCalculationResponse;
import com.sap.slh.tax.calculation.model.common.TaxCalculationStatus;
import com.sap.slh.tax.maestro.context.RequestContextService;
import com.sap.slh.tax.maestro.tax.exceptions.QueueCommunicationException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculateInvalidModelException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculateNoContentException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculatePartialContentException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail;
import com.sap.slh.tax.maestro.tax.jmx.CalculateQueueIntegrationErrorMBean;
import com.sap.slh.tax.maestro.tax.jmx.IntegrationMBean;

@Service
public class CalculateQueueIntegration extends QueueIntegration<TaxCalculationRequest, TaxCalculationResponse> {
    private static final Logger logger = LoggerFactory.getLogger(CalculateQueueIntegration.class);

    private static final String EXCHANGE_NAME_CALCULATE = "txs.engine.TAXSERVICE";
    private static final String ROUTING_KEY_CALCULATE = "txs.tax.v1.calculate";

    @Autowired
    protected CalculateQueueIntegrationErrorMBean calculateErrorMBean;

    @Autowired
    protected CalculateQueueIntegration(RabbitTemplate rabbitTemplate, Jackson2JsonMessageConverter msgConverter,
            RequestContextService requestContextService) {
        super(EXCHANGE_NAME_CALCULATE, ROUTING_KEY_CALCULATE, rabbitTemplate, msgConverter, requestContextService);
    }

    @Override
    protected String callingService() {
        return "Tax Calculation";
    }

    @Override
    protected TaxCalculationResponse handleAmqpResponse(Message requestMessage, Message responseMessage) {
        TaxCalculationResponse response = (TaxCalculationResponse) fromExpectedMessage(responseMessage,
                TaxCalculationResponse.class);

        if (!response.getStatus().equals(TaxCalculationStatus.SUCCESS)) {
            logger.error("Tax calculation error: {}\n from message: {}", responseMessage, requestMessage);
            handleErrorResponse(response);
        }

        return response;
    }

    private void handleErrorResponse(TaxCalculationResponse response) {
        switch (response.getStatus()) {
        case INVALID_REQUEST:
            throw new CalculateInvalidModelException(response.getStatusMessage(), getErrorDetails(response));
        case NO_CONTENT:
            throw new CalculateNoContentException(response.getStatusMessage(), getErrorDetails(response));
        case PARTIAL_CONTENT:
            throw new CalculatePartialContentException(response.getStatusMessage(), getErrorDetails(response));
        default:
            throw new QueueCommunicationException(response.getStatusMessage());
        }
    }

    private List<ErrorDetail> getErrorDetails(TaxCalculationResponse response) {
        List<ErrorDetail> errorDetails = Collections.emptyList();
        if (response.getError() != null && !CollectionUtils.isEmpty(response.getError().getDetails())) {
            List<TaxCalculationErrorDetail> calculationErrorDetails = response.getError().getDetails();

            errorDetails = calculationErrorDetails.stream()
                    .map(detail -> new ErrorDetail(detail.getErrorCode(), detail.getMessage()))
                    .collect(Collectors.toList());
        }
        return errorDetails;
    }

    @Override
    protected IntegrationMBean getQueueIntegrationErrorMBean() {
        return calculateErrorMBean;
    }

}
