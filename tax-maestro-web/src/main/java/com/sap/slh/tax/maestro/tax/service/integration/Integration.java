package com.sap.slh.tax.maestro.tax.service.integration;

import reactor.core.publisher.Mono;

public abstract class Integration<I, O> {

    protected Integration() {
    }

    public abstract Mono<O> call(Mono<I> input);

}
