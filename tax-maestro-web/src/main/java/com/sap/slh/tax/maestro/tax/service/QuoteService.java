package com.sap.slh.tax.maestro.tax.service;

import java.util.function.Function;

import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.calculation.model.common.TaxCalculationResponse;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationResponse;
import com.sap.slh.tax.maestro.tax.service.integration.CalculateQueueIntegration;
import com.sap.slh.tax.maestro.tax.service.integration.DestinationQueueIntegration;
import com.sap.slh.tax.maestro.tax.service.integration.DetermineQueueIntegration;

import reactor.core.publisher.Mono;

public abstract class QuoteService<T, S> {

    protected DetermineQueueIntegration determineIntegration;
    protected CalculateQueueIntegration calculateIntegration;
    protected DestinationQueueIntegration destinationIntegration;

    protected Function<TaxAttributesDeterminationResponseModel, Mono<TaxAttributesDeterminationResponseModel>> tmpMapDeterminationResponse = Mono::just;
    protected Function<DestinationResponse, Mono<DestinationResponse>> tmpMapDestinationResponse = Mono::just;
    protected Function<T, Mono<T>> tmpMapRequest = Mono::just;
    protected Function<S, Mono<S>> tmpMapResponse = Mono::just;

    protected QuoteService(DetermineQueueIntegration determineIntegration,
            CalculateQueueIntegration calculateIntegration) {
        this.determineIntegration = determineIntegration;
        this.calculateIntegration = calculateIntegration;
    }

    protected QuoteService(DetermineQueueIntegration determineIntegration,
            CalculateQueueIntegration calculateIntegration, DestinationQueueIntegration destinationQueueIntegration) {
        this.determineIntegration = determineIntegration;
        this.calculateIntegration = calculateIntegration;
        this.destinationIntegration = destinationQueueIntegration;
    }

    public abstract Mono<S> call(Mono<T> request);

    protected abstract Mono<S> quoteTaxService(Mono<T> request);

    protected abstract Mono<TaxAttributesDeterminationResponseModel> determineTaxes(Mono<T> request);

    protected abstract Mono<TaxCalculationResponse> calculateTaxes(
            Mono<TaxAttributesDeterminationResponseModel> detResponse, Mono<T> request);

}
