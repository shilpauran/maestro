package com.sap.slh.tax.maestro.context;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.sap.slh.tax.maestro.context.correlation.CorrelationIdHeaderService;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
public class RequestContextWebFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestContextWebFilter.class);

    private RequestContextService requestContextService;
    private CorrelationIdHeaderService correlationIdHeaderService;
    private ContextEnhancer contextEnhancer;

    public RequestContextWebFilter(RequestContextService correlationIdService,
            CorrelationIdHeaderService correlationIdHeaderService, ContextEnhancer contextEnhancer) {
        this.requestContextService = correlationIdService;
        this.correlationIdHeaderService = correlationIdHeaderService;
        this.contextEnhancer = contextEnhancer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        addCorrelationIdFromContextToResponseHeader(exchange);
        return addCorrelationIdAndTenantIdToContext(exchange, chain);
    }

    private void addCorrelationIdFromContextToResponseHeader(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.beforeCommit(() -> Mono.subscriberContext()
                .doOnNext(context -> addCorrelationIdFromContextToResponseHeader(context, response)).then());
    }

    private void addCorrelationIdFromContextToResponseHeader(Context context, ServerHttpResponse response) {
        Optional<String> maybeCorrelationId = requestContextService.getCorrelationId(context);
        if (maybeCorrelationId.isPresent()) {
            String correlationId = maybeCorrelationId.get();
            correlationIdHeaderService.putCorrelationIdInResponseHeader(correlationId, response);
        } else {
            logger.warn("Correlation ID could not be added to response because it could not be found in context");
        }
    }

    private Mono<Void> addCorrelationIdAndTenantIdToContext(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        return chain.filter(exchange).subscriberContext(context -> contextEnhancer.enhance(headers, context));
    }

}
