package com.sap.slh.tax.maestro.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Configuration;

import com.sap.slh.tax.maestro.context.RequestContextService;

import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

@Configuration
public class LoggingContextConfiguration {

    static final String MDC_CONTEXT_REACTOR_KEY = LoggingContextConfiguration.class.getName();

    private RequestContextService requestContextService;

    public LoggingContextConfiguration(RequestContextService requestContextService) {
        this.requestContextService = requestContextService;
    }

    @PostConstruct
    void contextOperatorHook() {
        Hooks.onEachOperator(MDC_CONTEXT_REACTOR_KEY,
                Operators.lift((__, subscriber) -> new MdcContextLifter<>(subscriber, requestContextService)));
    }

    @PreDestroy
    void cleanupHook() {
        Hooks.resetOnEachOperator(MDC_CONTEXT_REACTOR_KEY);
    }

}