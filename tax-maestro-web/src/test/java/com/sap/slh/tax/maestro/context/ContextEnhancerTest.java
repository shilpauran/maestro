package com.sap.slh.tax.maestro.context;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import com.sap.slh.tax.maestro.context.correlation.CorrelationIdGenerator;
import com.sap.slh.tax.maestro.context.correlation.CorrelationIdHeaderService;
import com.sap.slh.tax.maestro.context.jwt.JwtHeaderService;
import com.sap.slh.tax.maestro.context.tenant.TenantContextService;
import com.sap.slh.tax.maestro.i18n.LocaleResolver;
import com.sap.slh.tax.maestro.tax.models.RequestContext;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@RunWith(MockitoJUnitRunner.class)
public class ContextEnhancerTest {

    private static final String DUMMY_CORRELATION_ID = "38fc2962-ceaa-4e32-b4d3-5e66cbd25790";
    private static final String DUMMY_TENANT_ID = "23a7192a-fa3a-4a22-8694-fa2d5c4e5791";
    private static final Locale DUMMY_LOCALE = Locale.CANADA;
    private static final String DUMMY_JWT = "dummy_jwt";
    private static final String DUMMY_CACHE_CONTROL = "dummy_cache_control";
    private static final RequestContext REQUEST_CONTEXT_WITH_ALL_FIELDS = RequestContext.builder()
            .withCorrelationId(DUMMY_CORRELATION_ID).withTenantId(DUMMY_TENANT_ID).withCacheControl(DUMMY_CACHE_CONTROL)
            .withJwt(DUMMY_JWT).withLocale(DUMMY_LOCALE).build();
    private static final Context CONTEXT_WITH_ALL_FIELDS = Context.of(RequestContextService.REQUEST_CONTEXT,
            REQUEST_CONTEXT_WITH_ALL_FIELDS);

    @InjectMocks
    private ContextEnhancer contextEnhancer;
    @Mock
    private RequestContextService requestContextService;
    @Mock
    private CorrelationIdGenerator correlationIdGenerator;
    @Mock
    private CorrelationIdHeaderService correlationIdHeaderService;
    @Mock
    private TenantContextService tenantContextService;
    @Mock
    private HttpHeaders headers;
    @Mock
    private LocaleResolver localeResolver;
    @Mock
    private JwtHeaderService jwtHeaderService;

    private Context context;

    @Before
    public void init() {
        context = Context.empty();
        mockRequestContextServiceInsertion();
        mockRequestHeaderReading();
    }

    @Test
    public void shouldPutAllInContextIncludingCorrelationIdFromHeader() {
        when(correlationIdHeaderService.getCorrelationIdFromRequestHeader(headers))
                .thenReturn(Optional.of(DUMMY_CORRELATION_ID));

        Context enhancedContext = contextEnhancer.enhance(headers, context);

        assertEquals(CONTEXT_WITH_ALL_FIELDS, enhancedContext);
    }

    @Test
    public void shouldPutAllInContextIncludingGeneratedCorrelationId() {
        when(correlationIdHeaderService.getCorrelationIdFromRequestHeader(headers)).thenReturn(Optional.empty());
        when(correlationIdGenerator.generateCorrelationId()).thenReturn(DUMMY_CORRELATION_ID);

        Context enhancedContext = contextEnhancer.enhance(headers, context);

        assertEquals(CONTEXT_WITH_ALL_FIELDS, enhancedContext);
    }

    private void mockRequestContextServiceInsertion() {
        Context contextWithCorrelationId = mock(Context.class);
        when(requestContextService.putCorrelationId(DUMMY_CORRELATION_ID, context))
                .thenReturn(contextWithCorrelationId);
        Context contextWithTenantId = mock(Context.class);
        when(requestContextService.putTenantId(DUMMY_TENANT_ID, contextWithCorrelationId))
                .thenReturn(contextWithTenantId);
        Context contextWithJwt = mock(Context.class);
        when(requestContextService.putJwt(DUMMY_JWT, contextWithTenantId)).thenReturn(contextWithJwt);
        Context contextWithLocale = mock(Context.class);
        when(requestContextService.putLocale(DUMMY_LOCALE, contextWithJwt)).thenReturn(contextWithLocale);
        when(requestContextService.putCacheControl(DUMMY_CACHE_CONTROL, contextWithLocale))
                .thenReturn(CONTEXT_WITH_ALL_FIELDS);
    }

    private void mockRequestHeaderReading() {
        when(headers.getCacheControl()).thenReturn(DUMMY_CACHE_CONTROL);
        when(tenantContextService.getCurrentTenant()).thenReturn(Mono.just(DUMMY_TENANT_ID));
        when(localeResolver.getLocale(headers)).thenReturn(DUMMY_LOCALE);
        when(jwtHeaderService.getJwt(headers)).thenReturn(DUMMY_JWT);
    }

}
