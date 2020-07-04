package com.sap.slh.tax.maestro.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;

import com.sap.cloud.security.xsuaa.test.JwtGenerator;
import com.sap.cloud.security.xsuaa.token.AuthenticationToken;

public class SecurityContextMock {
	private static final String DEFAULT_CLIENT_ID = "id1";
	private static final String DEFAULT_SUBDOMAIN = "subdomain";
	private static final String DEFAULT_TENANT_ID = "Tenant1";
	private static final String ZONE_ID = "zid";

	public static SecurityContextImpl mock(String clientId, String subdomain, String tenantId, Jwt jwt) {
		SecurityContextImpl securityContext = new SecurityContextImpl();

		AuthenticationToken auth = new AuthenticationToken(jwt, null);

		securityContext.setAuthentication(auth);
		return securityContext;
	}

	public static SecurityContextImpl mock(String clientId, String subdomain, String tenantId) {
		Map<String, Object> cusClaims = new HashMap<>();
		cusClaims.put(ZONE_ID, tenantId);

		Jwt jwt = getJwt(clientId, subdomain, cusClaims);
		return mock(clientId, subdomain, tenantId, jwt);
	}

	public static SecurityContextImpl mock(String tenantId) {
		return mock(DEFAULT_CLIENT_ID, DEFAULT_SUBDOMAIN, tenantId);
	}

	public static SecurityContextImpl mock() {
		return mock(DEFAULT_TENANT_ID);
	}

	private static Jwt getJwt(String clientId, String subdomain, Map<String, Object> cusClaims) {
		JwtGenerator jwtGenerator = new JwtGenerator(clientId, subdomain);
		jwtGenerator.addCustomClaims(cusClaims);
		return jwtGenerator.getToken();
	}
}
