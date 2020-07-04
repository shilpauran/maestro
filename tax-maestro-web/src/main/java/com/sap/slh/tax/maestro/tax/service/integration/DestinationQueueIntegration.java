package com.sap.slh.tax.maestro.tax.service.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationRequest;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationResponse;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.ErrorResponse;
import com.sap.slh.tax.maestro.context.RequestContextService;
import com.sap.slh.tax.maestro.tax.exceptions.DestinationBadRequestException;
import com.sap.slh.tax.maestro.tax.exceptions.QueueCommunicationException;
import com.sap.slh.tax.maestro.tax.jmx.DestinationQueueIntegrationErrorMBean;
import com.sap.slh.tax.maestro.tax.jmx.IntegrationMBean;
import com.sap.slh.tax.maestro.tax.models.DestinationCacheKey;

import reactor.core.publisher.Mono;

@Service
public class DestinationQueueIntegration extends QueueIntegration<DestinationRequest, DestinationResponse> {
    private static final Logger logger = LoggerFactory.getLogger(DestinationQueueIntegration.class);

    private static final String EXCHANGE_NAME_DESTINATION = "txs.tax-destination-service";
    private static final String ROUTING_KEY_DESTINATION = "txs.tax-destination-service.get";
    private static final String DEFAULT_TAX_SERVICE_ENGINE = "tax-service";
    private static final Integer DEFAULT_VALIDITY_IN_SECONDS = 86400;

    @Autowired
    protected DestinationQueueIntegrationErrorMBean destinationErrorMBean;

    private Cache<DestinationCacheKey, DestinationResponse> cacheDestination;

    @Autowired
    public DestinationQueueIntegration(RabbitTemplate rabbitTemplate, Jackson2JsonMessageConverter msgConverter,
            Cache<DestinationCacheKey, DestinationResponse> cacheDestination,
            RequestContextService requestContextService) {
        super(EXCHANGE_NAME_DESTINATION, ROUTING_KEY_DESTINATION, rabbitTemplate, msgConverter, requestContextService);
        this.cacheDestination = cacheDestination;
    }

    @Override
    protected String callingService() {
        return "Tax Destination Configuration";
    }

    @Override
    public Mono<DestinationResponse> call(Mono<DestinationRequest> input) {
        return requestContextService.shouldUseCache().flatMap(shouldUseCache -> input.flatMap(destinationRequest -> {
            if (shouldUseCache) {
                return callQueueUsingCache(destinationRequest);
            } else {
                logger.info("Destination cache will not be used because of HTTP header has no-cache property.");
                return getDestinationCacheKey(destinationRequest)
                        .flatMap(cacheKey -> callQueueAndPutInCache(destinationRequest, cacheKey));
            }
        }));
    }

    private Mono<DestinationResponse> callQueueUsingCache(DestinationRequest destinationRequest) {
        return getDestinationCacheKey(destinationRequest).flatMap(cacheKey -> {
            DestinationResponse cacheValue = this.cacheDestination.getIfPresent(cacheKey);
            if (cacheValue != null) {
                logger.info("Destination key {} found in the cache with value {}", cacheKey, cacheValue);
                return Mono.just(cacheValue);
            } else {
                logger.info("Destination key {} not found in the cache.", cacheKey);
                return callQueueAndPutInCache(destinationRequest, cacheKey);
            }
        });
    }

    private Mono<DestinationResponse> callQueueAndPutInCache(DestinationRequest destinationRequest,
            DestinationCacheKey cacheKey) {
        return this.callQueue(destinationRequest).doOnNext(destinationResponse -> {
            logger.info("Destination key {} put in the cache with value {}", cacheKey, destinationResponse);
            cacheDestination.put(cacheKey, destinationResponse);
        });
    }

    private Mono<DestinationCacheKey> getDestinationCacheKey(DestinationRequest destinationRequest) {
        return requestContextService.getTenantId().map(tenantId -> DestinationCacheKey.builder().withTenantId(tenantId)
                .withDestinationRequest(destinationRequest).build());
    }

    @Override
    protected DestinationResponse handleAmqpResponse(Message requestMessage, Message responseMessage) {
        Object response = fromExpectedMessage(responseMessage, DestinationResponse.class, ErrorResponse.class);

        if (response instanceof ErrorResponse) {
            ErrorResponse error = (ErrorResponse) response;

            switch (HttpStatus.valueOf(error.getStatus())) {
            case NO_CONTENT:
                logger.info("No content found for this destination request. Tax Service engine used as default");
                return DestinationResponse.builder().withName(DEFAULT_TAX_SERVICE_ENGINE)
                        .withValidity(DEFAULT_VALIDITY_IN_SECONDS).build();
            case BAD_REQUEST:
                logger.error("Tax destination error: {}\n from message: {}", responseMessage, requestMessage);
                throw new DestinationBadRequestException(error.getMessage());
            default:
                logger.error("Tax destination error: {}\n from message: {}", responseMessage, requestMessage);
                throw new QueueCommunicationException(error.getMessage());
            }

        }

        DestinationResponse destinationResponse = (DestinationResponse) response;
        logger.info("Content found for this destination request, response body object: {}", destinationResponse);
        return (DestinationResponse) response;
    }

    @Override
    protected IntegrationMBean getQueueIntegrationErrorMBean() {
        return destinationErrorMBean;
    }

}
