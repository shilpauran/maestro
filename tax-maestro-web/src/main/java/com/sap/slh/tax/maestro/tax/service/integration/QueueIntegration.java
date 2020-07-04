package com.sap.slh.tax.maestro.tax.service.integration;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.http.HttpHeaders;

import com.sap.slh.tax.maestro.context.RequestContextService;
import com.sap.slh.tax.maestro.tax.exceptions.NoAmqpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.UnknownAmqpResponseTypeException;
import com.sap.slh.tax.maestro.tax.jmx.IntegrationMBean;

import reactor.core.publisher.Mono;

public abstract class QueueIntegration<I, O> extends Integration<I, O> {
    private static final Logger logger = LoggerFactory.getLogger(QueueIntegration.class);

    protected static final String HEADER_CORRELATION_ID = "X-CorrelationID";

    protected RabbitTemplate rabbitTemplate;
    protected Jackson2JsonMessageConverter msgConverter;
    protected String exchange;
    protected String routingKey;
    protected RequestContextService requestContextService;

    protected QueueIntegration(String exchange, String routingKey, final RabbitTemplate rabbitTemplate,
            final Jackson2JsonMessageConverter msgConverter, RequestContextService requestContextService) {
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.rabbitTemplate = rabbitTemplate;
        this.msgConverter = msgConverter;
        this.requestContextService = requestContextService;
    }

    public <J, K> Mono<K> call(Mono<J> request, Function<J, Mono<I>> mapRequest, Function<O, Mono<K>> mapResponse) {
        return request.flatMap(mapRequest).flatMap(input -> this.call(Mono.just(input))).flatMap(mapResponse);
    }

    public <J, K> Mono<K> call(Mono<J> request, Function<J, Mono<I>> mapRequest, BiFunction<O, J, K> mapResponse) {
        return request.flatMap(mapRequest).flatMap(input -> this.call(Mono.just(input))).zipWith(request, mapResponse);
    }

    public <J, G, K> Mono<O> call(Mono<J> request, Mono<G> requestAggregate, BiFunction<J, G, I> mapAggregator) {
        return request.zipWith(requestAggregate, mapAggregator).flatMap(input -> this.call(Mono.just(input)));
    }

    public Mono<O> call(Mono<I> input) {
        return input.flatMap(i -> callQueue(i));
    }

    protected Mono<O> callQueue(I input) {
        return toMessage(input).flatMap(requestMessage -> requestContextService.getTenantId()
                .flatMap(tenantId -> this.queueCall(tenantId, requestMessage))
                .map(responseMessage -> handleAmqpResponse(requestMessage, responseMessage)));
    }

    protected abstract IntegrationMBean getQueueIntegrationErrorMBean();

    protected abstract O handleAmqpResponse(Message requestMessage, Message responseMessage);

    protected abstract String callingService();

    private Mono<Message> toMessage(I input) {
        MessageProperties mp = new MessageProperties();

        return requestContextService.getCorrelationId().map(correlationId -> {
            mp.setHeader(HEADER_CORRELATION_ID, correlationId);
            return mp;
        }).then(requestContextService.getJwt().map(jwt -> {
            mp.setHeader(HttpHeaders.AUTHORIZATION, jwt);
            return mp;
        })).then(requestContextService.getLocale().map(locale -> {
            mp.setHeader(HttpHeaders.ACCEPT_LANGUAGE, locale.toLanguageTag());
            return mp;
        })).map(messageProperties -> msgConverter.toMessage(input, messageProperties));
    }

    protected Object fromMessage(Message message) {
        Object msg = null;
        try {
            msg = msgConverter.fromMessage(message);
        } catch (MessageConversionException e) {
            logger.error("Unkown AMQP response: {}", e.toString());
            throw new UnknownAmqpResponseTypeException(e);
        }
        return msg;
    }

    protected Object fromExpectedMessage(Message message, Class<?>... expectedMessages) {
        Object msg = fromMessage(message);
        Boolean msgFromExpectedClasses = Boolean.FALSE;
        for (Class<?> clazz : expectedMessages) {
            if (clazz.isInstance(msg)) {
                msgFromExpectedClasses = Boolean.TRUE;
            }
        }
        if (msgFromExpectedClasses == Boolean.FALSE) {
            logger.error("Unkown AMQP response: {}", msg);
            throw new UnknownAmqpResponseTypeException();
        }
        return msg;
    }

    private Mono<Message> queueCall(String tenantId, Message requestMessage) {
        return Mono.fromCallable(() -> {
            return this.sendAndReceiveMessage(getRoutingKeyForTenant(tenantId), this.exchange, requestMessage);
        });
    }

    private String getRoutingKeyForTenant(String tenant) {
        return String.join(".", tenant, this.routingKey);
    }

    private Message sendAndReceiveMessage(String routingKey, String exchange, Message message) {
        logger.info("Sending message to {}, exchange {}, and routing key {}", callingService(), exchange, routingKey);

        logger.debug("Sending message: {}", message);
        Message responseMessage = rabbitTemplate.sendAndReceive(exchange, routingKey, message);
        logger.debug("Received message: {}", responseMessage);

        if (responseMessage == null) {
            getQueueIntegrationErrorMBean().incrementCount();
            throw new NoAmqpResponseException(exchange, routingKey);
        }

        return responseMessage;
    }

}
