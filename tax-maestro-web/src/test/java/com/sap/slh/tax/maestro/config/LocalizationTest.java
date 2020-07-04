package com.sap.slh.tax.maestro.config;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;

import com.sap.slh.tax.maestro.context.tenant.TenantContextService;
import com.sap.slh.tax.maestro.context.tenant.TenantNotRetrievedException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class LocalizationTest {

	private static final String KEY_MESSAGE = "message.tenantNotRetrievedException";
	private static final String MESSAGE = "Tenant identification could not be resolved";

	private MessageSource messageSource = Mockito.mock(MessageSource.class);

	@Test
	public void testExceptionMessage() {
		Mono<String> result = new TenantContextService().getCurrentTenant();

		StepVerifier.create(result).expectError(TenantNotRetrievedException.class).verify();
		StepVerifier.create(result).expectErrorMessage(MESSAGE).verify();

	}

	@Test
	public void testLocalizedMessageSource() {
		Mockito.when(messageSource.getMessage(Mockito.anyString(), Mockito.any(), Mockito.any(Locale.class)))
				.thenReturn(MESSAGE);

		String message = messageSource.getMessage(KEY_MESSAGE, null, Locale.ENGLISH);

		assertEquals(MESSAGE, message);
	}
}
