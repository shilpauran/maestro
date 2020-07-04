package com.sap.slh.tax.maestro.config;

import com.sap.slh.tax.maestro.i18n.LocaleResolver;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;
import org.springframework.web.server.i18n.LocaleContextResolver;

import java.nio.charset.StandardCharsets;

@Configuration
public class LocaleResolverConfig extends WebFluxConfigurationSupport {

    private static final String MESSAGES_RESOURCES = "i18n/messages";

    @Override
    protected LocaleContextResolver createLocaleContextResolver() {
      return new LocaleResolver();
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(MESSAGES_RESOURCES);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return messageSource;
    }

}
