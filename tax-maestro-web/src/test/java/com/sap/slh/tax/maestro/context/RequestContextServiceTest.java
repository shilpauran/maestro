package com.sap.slh.tax.maestro.context;

import static com.sap.slh.tax.maestro.context.RequestContextService.REQUEST_CONTEXT;
import static org.junit.Assert.assertEquals;

import java.util.Locale;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.sap.slh.tax.maestro.tax.models.RequestContext;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

public class RequestContextServiceTest {

    private static final String DUMMY_CORRELATION_ID = "38fc2962-ceaa-4e32-b4d3-5e66cbd25790";
    private static final String DUMMY_TENANT_ID = "23a7192a-fa3a-4a22-8694-fa2d5c4e5791";
    private static final Locale DUMMY_LOCALE = Locale.CANADA;
    private static final String DUMMY_JWT = "dummy_jwt";
    private static final String DUMMY_CACHE_CONTROL = "dummy_cache_control";

    private RequestContextService requestContextService;

    @Before
    public void init() {
        requestContextService = new RequestContextService();
    }
    
    @Test
    public void shouldPutCorrelationIdInProvidedContext() {
        Context context = Context.empty();
        RequestContext requestContext = RequestContext.builder().withCorrelationId(DUMMY_CORRELATION_ID).build();

        context = requestContextService.putCorrelationId(DUMMY_CORRELATION_ID, context);

        assertEquals(requestContext, context.get(REQUEST_CONTEXT));
    }
    
    @Test
    public void shouldPutTenantIdInProvidedContext() {
        Context context = Context.empty();
        RequestContext requestContext = RequestContext.builder().withTenantId(DUMMY_TENANT_ID).build();

        context = requestContextService.putTenantId(DUMMY_TENANT_ID, context);
        
        assertEquals(requestContext, context.get(REQUEST_CONTEXT));
    }
    
    @Test
    public void shouldPutCacheControlInProvidedContext() {
        Context context = Context.empty();
        RequestContext requestContext = RequestContext.builder().withCacheControl(DUMMY_CACHE_CONTROL).build();

        context = requestContextService.putCacheControl(DUMMY_CACHE_CONTROL, context);

        assertEquals(requestContext, context.get(REQUEST_CONTEXT));
    }

    @Test
    public void shouldReturnCorrelationIdFoundInProvidedContext() {
        RequestContext requestContext = new RequestContext();
        requestContext.setCorrelationId(DUMMY_CORRELATION_ID);

        Context context = Context.of(REQUEST_CONTEXT, requestContext);

        Optional<String> maybeCorrelationId = requestContextService.getCorrelationId(context);

        assertEquals(Optional.of(DUMMY_CORRELATION_ID), maybeCorrelationId);
    }

    @Test
    public void shouldReturnTenantIdFoundInProvidedContext() {
        RequestContext requestContext = new RequestContext();
        requestContext.setTenantId(DUMMY_TENANT_ID);

        Context context = Context.of(REQUEST_CONTEXT, requestContext);

        Optional<String> maybeTenantId = requestContextService.getTenantId(context);

        assertEquals(Optional.of(DUMMY_TENANT_ID), maybeTenantId);
    }

    @Test
    public void shouldReturnEmptyWhenCorrelationIdIsNotFoundInProvidedContext() {
        Context context = Context.empty();

        Optional<String> maybeCorrelationId = requestContextService.getCorrelationId(context);

        assertEquals(Optional.empty(), maybeCorrelationId);
    }

    @Test
    public void shouldReturnEmptyWhenTenantIdIsNotFoundInContext() {
        Context context = Context.empty();

        Optional<String> maybeTenantId = requestContextService.getTenantId(context);

        assertEquals(Optional.empty(), maybeTenantId);
    }

    @Test
    public void shouldReturnCorrelationIdFoundInContext() {
        Mono<String> correlationId = requestContextService.getCorrelationId()
                .subscriberContext(context -> context.put(RequestContextService.REQUEST_CONTEXT,
                        RequestContext.builder().withCorrelationId(DUMMY_CORRELATION_ID).build()));

        StepVerifier.create(correlationId).expectNext(DUMMY_CORRELATION_ID).verifyComplete();
    }
    
    @Test
    public void shouldReturnTenantIdFoundInContext() {
        Mono<String> tenantId = requestContextService.getTenantId()
                .subscriberContext(context -> context.put(RequestContextService.REQUEST_CONTEXT,
                        RequestContext.builder().withTenantId(DUMMY_TENANT_ID).build()));

        StepVerifier.create(tenantId).expectNext(DUMMY_TENANT_ID).verifyComplete();
    }
    
    @Test
    public void shouldReturnCacheControlFoundInContext() {
        Mono<String> cacheControl = requestContextService.getCacheControl()
                .subscriberContext(context -> context.put(RequestContextService.REQUEST_CONTEXT,
                        RequestContext.builder().withCacheControl(DUMMY_CACHE_CONTROL).build()));

        StepVerifier.create(cacheControl).expectNext(DUMMY_CACHE_CONTROL).verifyComplete();
    }
    
    @Test
    public void shouldReturnShouldUseCacheFoundInContext() {
        Mono<Boolean> shouldUseCache = requestContextService.shouldUseCache()
                .subscriberContext(context -> context.put(RequestContextService.REQUEST_CONTEXT,
                        RequestContext.builder().withCacheControl(DUMMY_CACHE_CONTROL).build()));

        StepVerifier.create(shouldUseCache).expectNext(true).verifyComplete();
    }
    
    @Test
    public void shouldReturnJwtFoundInContext() {
        Mono<String> jwt = requestContextService.getJwt()
                .subscriberContext(context -> context.put(RequestContextService.REQUEST_CONTEXT,
                        RequestContext.builder().withJwt(DUMMY_JWT).build()));

        StepVerifier.create(jwt).expectNext(DUMMY_JWT).verifyComplete();
    }
    
    @Test
    public void shouldReturnLocaleFoundInContext() {
        Mono<Locale> locale = requestContextService.getLocale()
                .subscriberContext(context -> context.put(RequestContextService.REQUEST_CONTEXT,
                        RequestContext.builder().withLocale(DUMMY_LOCALE).build()));

        StepVerifier.create(locale).expectNext(DUMMY_LOCALE).verifyComplete();
    }

}
