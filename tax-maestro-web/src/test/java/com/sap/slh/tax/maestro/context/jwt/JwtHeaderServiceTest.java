package com.sap.slh.tax.maestro.context.jwt;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import com.sap.slh.tax.maestro.tax.exceptions.NoJwtProvidedException;

public class JwtHeaderServiceTest {

	private static final String DUMMY_JWT = "jwt";

	private JwtHeaderService jwtHeaderService;

	@Before
	public void setUp() {
		jwtHeaderService = new JwtHeaderService();
	}

	@Test
	public void shouldReturnJwtWhenHeaderIsProvided() throws Exception {
		HttpHeaders headersWithAuthorization = new HttpHeaders();
		headersWithAuthorization.add(HttpHeaders.AUTHORIZATION, DUMMY_JWT);

		String jwt = jwtHeaderService.getJwt(headersWithAuthorization);

		assertEquals(DUMMY_JWT, jwt);
	}

	@Test(expected = NoJwtProvidedException.class)
	public void shouldThrowExceptionWhenHeaderIsNotProvided() throws Exception {
		HttpHeaders emptyHeaders = new HttpHeaders();

		jwtHeaderService.getJwt(emptyHeaders);
	}
	
}
