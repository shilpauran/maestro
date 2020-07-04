package com.sap.slh.tax.maestro.context.tenant;

import com.sap.slh.tax.maestro.context.tenant.TenantContextService;
import com.sap.slh.tax.maestro.context.tenant.TenantNotRetrievedException;
import com.sap.slh.tax.maestro.security.SecurityContextMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(MockitoJUnitRunner.class)
public class TenantContextServiceTest {

    private static final String TENANT_ID = "Tenant1";

    @InjectMocks
    TenantContextService tenantContextService;

    @Test
    public void retrieveTenant() {
        Mono<String> result = tenantContextService.getCurrentTenant().subscriberContext(
                (ReactiveSecurityContextHolder.withSecurityContext(Mono.just(SecurityContextMock.mock(TENANT_ID)))));

        StepVerifier.create(result).expectNext(TENANT_ID).verifyComplete();
    }

    @Test
    public void retrieveWithEmptySecurityContext() {
        Mono<String> result = tenantContextService.getCurrentTenant();

        StepVerifier.create(result).expectError(TenantNotRetrievedException.class).verify();
    }

    @Test
    public void retrieveWithoutAuthenticationInSecurityContext() {
        ReactiveSecurityContextHolder.withSecurityContext(Mono.just(new SecurityContextImpl()));
        Mono<String> result = tenantContextService.getCurrentTenant();

        StepVerifier.create(result).expectError(TenantNotRetrievedException.class).verify();
    }

    @Test
    public void retrieveWithNullTenant() {
        Mono<String> result = tenantContextService.getCurrentTenant().subscriberContext(
                (ReactiveSecurityContextHolder.withSecurityContext(Mono.just(SecurityContextMock.mock(null)))));

        StepVerifier.create(result).expectError(TenantNotRetrievedException.class).verify();
    }
}
