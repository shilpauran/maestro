package com.sap.slh.tax.maestro.context.tenant;

import com.sap.cloud.security.xsuaa.token.ReactiveSecurityContext;
import com.sap.cloud.security.xsuaa.token.XsuaaToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TenantContextService {

    private static final String ZONE_ID = "zid";
    private static final Logger logger = LoggerFactory.getLogger(TenantContextService.class);

    public Mono<String> getCurrentTenant() {
        return ReactiveSecurityContext
                .getToken()
                .map(this::getTenantFromToken)
                .onErrorMap(AccessDeniedException.class, t -> new TenantNotRetrievedException());
    }

    private String getTenantFromToken(XsuaaToken xsuaaToken) {
        if (!xsuaaToken.hasClaim(ZONE_ID)) {
            throw new TenantNotRetrievedException();
        }
        String tenant = xsuaaToken.getClaimAsString(ZONE_ID);
        logger.info("Tenant from xsuaa token retrieved {}", tenant);
        return tenant;
    }
}
