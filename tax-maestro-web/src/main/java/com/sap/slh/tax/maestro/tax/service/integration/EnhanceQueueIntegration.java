package com.sap.slh.tax.maestro.tax.service.integration;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sap.slh.tax.maestro.context.RequestContextService;
import com.sap.slh.tax.maestro.tax.exceptions.QueueCommunicationException;
import com.sap.slh.tax.maestro.tax.exceptions.enhance.EnhanceBadRequestException;
import com.sap.slh.tax.maestro.tax.jmx.EnhanceQueueIntegrationErrorMBean;
import com.sap.slh.tax.maestro.tax.jmx.IntegrationMBean;
import com.sap.slh.tax.product.tax.classification.models.ErrorDetail;
import com.sap.slh.tax.product.tax.classification.models.ErrorResponse;
import com.sap.slh.tax.product.tax.classification.models.ProductClassifications;
import com.sap.slh.tax.product.tax.classification.models.ProductIdsForProductClassification;

@Service
public class EnhanceQueueIntegration
        extends QueueIntegration<ProductIdsForProductClassification, ProductClassifications> {
    private static final Logger logger = LoggerFactory.getLogger(EnhanceQueueIntegration.class);

    private static final String EXCHANGE_NAME_ENHANCER = "txs.tax-classifier";
    private static final String ROUTING_KEY_ENHANCER = "txs.tax-classifier.product-classification";
    private static final Integer STATUS_NOT_FOUND = Integer.valueOf(404);

    @Autowired
    protected EnhanceQueueIntegrationErrorMBean enhanceErrorMBean;

    @Autowired
    public EnhanceQueueIntegration(RabbitTemplate rabbitTemplate, Jackson2JsonMessageConverter msgConverter,
            RequestContextService requestContextService) {
        super(EXCHANGE_NAME_ENHANCER, ROUTING_KEY_ENHANCER, rabbitTemplate, msgConverter, requestContextService);
    }

    @Override
    protected String callingService() {
        return "Tax Classifier";
    }

    @Override
    protected ProductClassifications handleAmqpResponse(Message requestMessage, Message responseMessage) {
        Object response = fromExpectedMessage(responseMessage, ProductClassifications.class, ErrorResponse.class);

        if (response instanceof ErrorResponse) {
            ErrorResponse error = (ErrorResponse) response;
            logger.error("Tax classification error: {}\n from message: {}", responseMessage, requestMessage);
            if (error.getStatus().equals(STATUS_NOT_FOUND)) {
                handleNotFound(error);
            } else {
                throw new QueueCommunicationException(error.getMessage());
            }
        }

        ProductClassifications productClassifications = (ProductClassifications) response;
        logger.debug("Response body object: {}", productClassifications);
        return productClassifications;
    }

    private void handleNotFound(ErrorResponse error) {
        List<ErrorDetail> enhanceErrorDetails = error.getErrorDetails();

        List<String> errorDetails = enhanceErrorDetails.stream().map(detail -> detail.getMessage())
                .collect(Collectors.toList());

        throw new EnhanceBadRequestException(error.getMessage(), errorDetails);

    }

    @Override
    protected IntegrationMBean getQueueIntegrationErrorMBean() {
        return enhanceErrorMBean;
    }

}
