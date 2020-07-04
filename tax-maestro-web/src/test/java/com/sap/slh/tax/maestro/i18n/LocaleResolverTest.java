package com.sap.slh.tax.maestro.i18n;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

public class LocaleResolverTest {

    private LocaleResolver localeResolver = new LocaleResolver();

    @Test
    public void shouldGetLocaleAsPassedOnHeader() {
        Locale expectedLocale = Locale.FRENCH;

        List<Locale> locales = Arrays.asList(expectedLocale);

        HttpHeaders headers = new HttpHeaders();
        headers.setAcceptLanguageAsLocales(locales);

        Locale actualLocale = localeResolver.getLocale(headers);

        assertEquals(expectedLocale, actualLocale);
    }

    @Test
    public void shouldGetLocaleWithLanguageAndCountry() {
        Locale expectedLocale = Locale.forLanguageTag("en-US");

        List<Locale> locales = Arrays.asList(expectedLocale);

        HttpHeaders headers = new HttpHeaders();
        headers.setAcceptLanguageAsLocales(locales);

        Locale actualLocale = localeResolver.getLocale(headers);

        assertEquals(expectedLocale, actualLocale);
    }

    @Test
    public void shouldGetFirstLocaleAsPassedOnHeader() {
        Locale expectedLocale = Locale.FRENCH;

        List<Locale> locales = Arrays.asList(expectedLocale, Locale.CHINESE);

        HttpHeaders headers = new HttpHeaders();
        headers.setAcceptLanguageAsLocales(locales);

        Locale actualLocale = localeResolver.getLocale(headers);

        assertEquals(expectedLocale, actualLocale);
    }

    @Test
    public void shouldGetDefaultLocale() {
        Locale actualLocale = localeResolver.getLocale(new HttpHeaders());

        assertEquals(Locale.ENGLISH, actualLocale);
    }

    @Test
    public void shouldReturnDefaultWithResolveLocaleContext() {

        ServerWebExchange serverExchange = Mockito.mock(ServerWebExchange.class);
        ServerHttpRequest serverHttpRequest = Mockito.mock(ServerHttpRequest.class);

        HttpHeaders headers = new HttpHeaders();

        Mockito.when(serverHttpRequest.getHeaders()).thenReturn(headers);
        Mockito.when(serverExchange.getRequest()).thenReturn(serverHttpRequest);

        Locale actualLocale = localeResolver.resolveLocaleContext(serverExchange).getLocale();

        assertEquals(Locale.ENGLISH, actualLocale);
    }

}
