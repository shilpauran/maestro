package com.sap.slh.tax.maestro.context;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.sap.slh.tax.maestro.context.correlation.CorrelationIdGenerator;
import com.sap.slh.tax.maestro.context.correlation.CorrelationIdHeaderService;
import com.sap.slh.tax.maestro.context.jwt.JwtHeaderService;
import com.sap.slh.tax.maestro.context.tenant.TenantContextService;
import com.sap.slh.tax.maestro.i18n.LocaleResolver;

import reactor.util.context.Context;

@Service
public class ContextEnhancer {

    private static final Logger logger = LoggerFactory.getLogger(ContextEnhancer.class);

    private RequestContextService requestContextService;
    private CorrelationIdGenerator correlationIdGenerator;
    private CorrelationIdHeaderService correlationIdHeaderService;
    private TenantContextService tenantContextService;
    private LocaleResolver localeResolver;
    private JwtHeaderService jwtHeaderService;

    public ContextEnhancer(RequestContextService requestContextService, CorrelationIdGenerator correlationIdGenerator,
            CorrelationIdHeaderService correlationIdHeaderService, TenantContextService tenantContextService,
            LocaleResolver localeResolver, JwtHeaderService jwtHeaderService) {
        this.requestContextService = requestContextService;
        this.correlationIdGenerator = correlationIdGenerator;
        this.correlationIdHeaderService = correlationIdHeaderService;
        this.tenantContextService = tenantContextService;
        this.localeResolver = localeResolver;
        this.jwtHeaderService = jwtHeaderService;
    }

    public Context enhance(HttpHeaders headers, Context context) {
        context = requestContextService.putCorrelationId(getCorrelationId(headers), context);
        context = requestContextService.putTenantId(getTenantId(context), context);
        context = requestContextService.putJwt(jwtHeaderService.getJwt(headers), context);
        context = requestContextService.putLocale(localeResolver.getLocale(headers), context);
        context = requestContextService.putCacheControl(headers.getCacheControl(), context);
        requestContextService.logRequestContext(context);
        return context;
    }

    private String getCorrelationId(HttpHeaders headers) {
        Optional<String> maybeCorrelationId = correlationIdHeaderService.getCorrelationIdFromRequestHeader(headers);
        if (maybeCorrelationId.isPresent()) {
            String correlationIdFoundInHeader = maybeCorrelationId.get();
            logger.debug("Correlation ID found in header: {}", correlationIdFoundInHeader);
            return correlationIdFoundInHeader;
        } else {
            String generatedCorrelationId = correlationIdGenerator.generateCorrelationId();
            logger.debug("Generated Correlation ID: {}", generatedCorrelationId);
            return generatedCorrelationId;
        }
    }

    private String getTenantId(Context context) {
        return tenantContextService.getCurrentTenant().subscriberContext(context).block();
    }

}
