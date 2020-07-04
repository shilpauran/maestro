package com.sap.slh.tax.maestro.i18n;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.i18n.LocaleContextResolver;

@Component
public class LocaleResolver implements LocaleContextResolver {

    @Override
    public LocaleContext resolveLocaleContext(ServerWebExchange exchange) {
        return getResolvedLocaleContext(exchange.getRequest().getHeaders());
    }

    @Override
    public void setLocaleContext(ServerWebExchange exchange, LocaleContext localeContext) {
        throw new UnsupportedOperationException("Not Supported");
    }

    private LocaleContext getResolvedLocaleContext(HttpHeaders headers) {
        Locale locale;
        if (!headers.getAcceptLanguage().isEmpty()) {
            locale = headers.getAcceptLanguageAsLocales().get(0);
        } else {
            locale = Locale.ENGLISH;
        }

        return new SimpleLocaleContext(locale);
    }

    public Locale getLocale(HttpHeaders headers) {
        return getResolvedLocaleContext(headers).getLocale();
    }

}