package com.sap.slh.tax.maestro.tax.models;

import static org.junit.Assert.*;
import java.util.Locale;
import org.junit.Test;

public class RequestContextTest {

    private static final String DEFAULT_TENANT_ID = "ID";
    private static final Locale DEFAULT_LOCALE = Locale.CANADA;
    private static final String DEFAULT_JWT = "jwt";
    private static final String DEFAULT_CACHE_CONTROL_WITH_NO_CACHE = "no-cache, no-store, max-age=0, must-revalidate";
    private static final String DEFAULT_CACHE_CONTROL_WITHOUT_NO_CACHE = "no-store, max-age=0";
    private static final String DEFAULT_CORRELATION_ID = "b03d17e7-480d-4f3b-9f3c-5307a0d27321";

    @Test
    public void requestContextAllAttributes() {
        RequestContext requestContext = new RequestContext();

        requestContext.setLocale(DEFAULT_LOCALE);
        requestContext.setTenantId(DEFAULT_TENANT_ID);
        requestContext.setJwt(DEFAULT_JWT);
        requestContext.setCacheControl(DEFAULT_CACHE_CONTROL_WITH_NO_CACHE);
        requestContext.setCorrelationId(DEFAULT_CORRELATION_ID);

        assertEquals(requestContext.getTenantId(), DEFAULT_TENANT_ID);
        assertEquals(requestContext.getLocale(), DEFAULT_LOCALE);
        assertEquals(requestContext.getJwt(), DEFAULT_JWT);
        assertEquals(requestContext.getCacheControl(), DEFAULT_CACHE_CONTROL_WITH_NO_CACHE);
        assertFalse(requestContext.shouldUseCache());
        assertEquals(requestContext.getCorrelationId(), DEFAULT_CORRELATION_ID);
    }

    @Test
    public void requestContextOnlyTenantId() {
        RequestContext requestContext = new RequestContext();

        requestContext.setTenantId(DEFAULT_TENANT_ID);

        assertEquals(requestContext.getTenantId(), DEFAULT_TENANT_ID);
        assertNull(requestContext.getLocale());
        assertNull(requestContext.getJwt());
        assertNull(requestContext.getCacheControl());
        assertTrue(requestContext.shouldUseCache());
        assertNull(requestContext.getCorrelationId());
    }

    @Test
    public void requestContextOnlyLocale() {
        RequestContext requestContext = new RequestContext();

        requestContext.setLocale(DEFAULT_LOCALE);

        assertEquals(requestContext.getLocale(), DEFAULT_LOCALE);
        assertNull(requestContext.getTenantId());
        assertNull(requestContext.getJwt());
        assertNull(requestContext.getCacheControl());
        assertTrue(requestContext.shouldUseCache());
        assertNull(requestContext.getCorrelationId());
    }

    @Test
    public void requestContextOnlyJwt() {
        RequestContext requestContext = new RequestContext();

        requestContext.setJwt(DEFAULT_JWT);

        assertEquals(requestContext.getJwt(), DEFAULT_JWT);
        assertNull(requestContext.getTenantId());
        assertNull(requestContext.getLocale());
        assertNull(requestContext.getCacheControl());
        assertTrue(requestContext.shouldUseCache());
        assertNull(requestContext.getCorrelationId());
    }

    @Test
    public void requestContextOnlyCacheControlWithNoCache() {
        RequestContext requestContext = new RequestContext();

        requestContext.setCacheControl(DEFAULT_CACHE_CONTROL_WITH_NO_CACHE);

        assertEquals(requestContext.getCacheControl(), DEFAULT_CACHE_CONTROL_WITH_NO_CACHE);
        assertFalse(requestContext.shouldUseCache());
        assertNull(requestContext.getTenantId());
        assertNull(requestContext.getLocale());
        assertNull(requestContext.getJwt());
        assertNull(requestContext.getCorrelationId());
    }

    @Test
    public void requestContextOnlyCacheControlWithoutNoCache() {
        RequestContext requestContext = new RequestContext();

        requestContext.setCacheControl(DEFAULT_CACHE_CONTROL_WITHOUT_NO_CACHE);

        assertEquals(requestContext.getCacheControl(), DEFAULT_CACHE_CONTROL_WITHOUT_NO_CACHE);
        assertTrue(requestContext.shouldUseCache());
        assertNull(requestContext.getTenantId());
        assertNull(requestContext.getLocale());
        assertNull(requestContext.getJwt());
        assertNull(requestContext.getCorrelationId());
    }

    @Test
    public void requestContextOnlyCorrelationId() {
        RequestContext requestContext = new RequestContext();
        requestContext.setCorrelationId(DEFAULT_CORRELATION_ID);

        assertEquals(requestContext.getCorrelationId(), DEFAULT_CORRELATION_ID);
        assertNull(requestContext.getTenantId());
        assertNull(requestContext.getLocale());
        assertNull(requestContext.getJwt());
        assertNull(requestContext.getCacheControl());
        assertTrue(requestContext.shouldUseCache());
    }

    @Test
    public void requestContextEmpty() {
        RequestContext requestContext = new RequestContext();
        assertNull(requestContext.getTenantId());
        assertNull(requestContext.getLocale());
        assertNull(requestContext.getJwt());
        assertNull(requestContext.getCacheControl());
        assertTrue(requestContext.shouldUseCache());
        assertNull(requestContext.getCorrelationId());
    }
}
