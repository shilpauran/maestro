package com.sap.slh.tax.maestro.context;

import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sap.slh.tax.maestro.tax.models.RequestContext;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Service
public class RequestContextService {

    private static final Logger logger = LoggerFactory.getLogger(RequestContextService.class);
    protected static final String REQUEST_CONTEXT = "request_context";

    public Optional<String> getCorrelationId(Context context) {
        RequestContext requestContext = getRequestContext(context);
        return Optional.ofNullable(requestContext.getCorrelationId());
    }

    public Optional<String> getTenantId(Context context) {
        RequestContext requestContext = getRequestContext(context);
        return Optional.ofNullable(requestContext.getTenantId());
    }

    public Mono<String> getCorrelationId() {
        return this.getRequestContext().flatMap(requestContext -> Mono.justOrEmpty(requestContext.getCorrelationId()));
    }

    public Mono<String> getTenantId() {
        return this.getRequestContext().flatMap(requestContext -> Mono.justOrEmpty(requestContext.getTenantId()));
    }

    public Mono<Locale> getLocale() {
        return this.getRequestContext().flatMap(requestContext -> Mono.justOrEmpty(requestContext.getLocale()));
    }

    public Mono<String> getJwt() {
        return this.getRequestContext().flatMap(requestContext -> Mono.justOrEmpty(requestContext.getJwt()));
    }

    public Mono<String> getCacheControl() {
        return this.getRequestContext().flatMap(requestContext -> Mono.justOrEmpty(requestContext.getCacheControl()));
    }

    public Mono<Boolean> shouldUseCache() {
        return this.getRequestContext().flatMap(requestContext -> Mono.justOrEmpty(requestContext.shouldUseCache()));
    }

    public void logRequestContext(Context context) {
        logger.debug("Request Context: {}", getRequestContext(context));
    }

    protected Context putCorrelationId(String correlationId, Context context) {
        return updateRequestContext(context, RequestContext::setCorrelationId, correlationId);
    }

    protected Context putTenantId(String tenantId, Context context) {
        return updateRequestContext(context, RequestContext::setTenantId, tenantId);
    }

    protected Context putJwt(String jwt, Context context) {
        return updateRequestContext(context, RequestContext::setJwt, jwt);
    }

    protected Context putLocale(Locale locale, Context context) {
        return updateRequestContext(context, RequestContext::setLocale, locale);
    }

    protected Context putCacheControl(String cacheControl, Context context) {
        return updateRequestContext(context, RequestContext::setCacheControl, cacheControl);
    }

    private Mono<RequestContext> getRequestContext() {
        return Mono.subscriberContext().map(context -> getRequestContext(context));
    }

    private RequestContext getRequestContext(Context context) {
        return context.getOrDefault(REQUEST_CONTEXT, new RequestContext());
    }

    private <T> Context updateRequestContext(Context context, BiConsumer<RequestContext, T> update, T value) {
        RequestContext requestContext = getRequestContext(context);
        update.accept(requestContext, value);
        return context.put(REQUEST_CONTEXT, requestContext);
    }

}
