package com.sap.slh.tax.maestro.tax.models;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.util.Locale;
import java.util.function.Predicate;

import org.junit.Test;

import com.jparams.verifier.tostring.ToStringVerifier;

public class ToStringTest {
    @Test
    public void testToString() {
        Predicate<Class<?>> predicate = (clazz) -> {
            boolean shouldTestClass;
            shouldTestClass = !clazz.getName().endsWith("Test");
            shouldTestClass = shouldTestClass && !clazz.getName().endsWith("$Builder");
            shouldTestClass = shouldTestClass && !clazz.getName().endsWith("$1");
            shouldTestClass = shouldTestClass
                    && !clazz.getName().equals("com.sap.slh.tax.maestro.tax.models.RequestContext");
            return shouldTestClass;
        };

        ToStringVerifier.forPackage("com.sap.slh.tax.maestro.tax.models", false, predicate).verify();
    }

    @Test
    public void testToStringRequestContext() throws MalformedURLException {
        // RequestContext contains sensitive data that is not logged. Necessary manual
        // test
        RequestContext requestContext = new RequestContext();

        requestContext.setLocale(Locale.CANADA);
        requestContext.setTenantId("ID");
        requestContext.setJwt("sensitive-jwt");
        requestContext.setCacheControl("no-cache");
        requestContext.setCorrelationId("b03d17e7-480d-4f3b-9f3c-5307a0d27321");

        assertEquals(
                "RequestContext [tenantId=ID, locale=en_CA, jwt=***, cacheControl=no-cache, correlationId=b03d17e7-480d-4f3b-9f3c-5307a0d27321]",
                requestContext.toString());
    }

}
