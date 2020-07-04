package com.sap.slh.tax.maestro.tax.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.calculation.model.common.TaxCalculationResponse;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationResponse;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse;
import com.sap.slh.tax.maestro.tax.mapper.AggregateObjectsToTaxResponseMapper;
import com.sap.slh.tax.maestro.tax.mapper.DestinationResponsePartnerResponseToTaxResponseMapper;
import com.sap.slh.tax.maestro.tax.mapper.DeterminationResponseTaxRequestToCalculationRequestMapper;
import com.sap.slh.tax.maestro.tax.mapper.TaxRequestToDestinationRequestMapper;
import com.sap.slh.tax.maestro.tax.mapper.TaxRequestToDetermineRequestMapper;
import com.sap.slh.tax.maestro.tax.service.integration.CalculateQueueIntegration;
import com.sap.slh.tax.maestro.tax.service.integration.DestinationQueueIntegration;
import com.sap.slh.tax.maestro.tax.service.integration.DetermineQueueIntegration;
import com.sap.slh.tax.maestro.tax.service.integration.QuoteV0PartnerHttpIntegration;

import reactor.core.publisher.Mono;

@Service
public class QuoteV0Service extends QuoteService<TaxRequest, TaxResponse> {

    private static final Logger logger = LoggerFactory.getLogger(QuoteV0Service.class);

    private static final String DEFAULT_TAX_SERVICE_ENGINE = "tax-service";

    private QuoteV0PartnerHttpIntegration quoteIntegration;

    @Autowired
    public QuoteV0Service(DetermineQueueIntegration determineIntegration,
            CalculateQueueIntegration calculateIntegration, QuoteV0PartnerHttpIntegration quoteIntegration,
            DestinationQueueIntegration destinationQueueIntegration) {
        super(determineIntegration, calculateIntegration, destinationQueueIntegration);
        this.quoteIntegration = quoteIntegration;
    }

    @Override
    public Mono<TaxResponse> call(Mono<TaxRequest> request) {
        return getDestination(request).flatMap(destResponse -> {
            if (destResponse.getName().equals(DEFAULT_TAX_SERVICE_ENGINE)) {
                logger.debug("Quote via tax-service");
                return quoteTaxService(request);
            } else {
                logger.debug("Quote via partner");
                return quotePartner(destResponse, request);
            }
        });
    }

    @Override
    protected Mono<TaxResponse> quoteTaxService(Mono<TaxRequest> request) {
        return determineTaxes(request).cache().transformDeferred(
                detResponse -> calculateTaxes(detResponse, request).transformDeferred(calcResponse -> Mono
                        .zip(AggregateObjectsToTaxResponseMapper.getInstance(), request, detResponse, calcResponse)));
    }

    protected Mono<TaxResponse> quotePartner(DestinationResponse destinationResponse, Mono<TaxRequest> request) {
        return quoteIntegration.call(destinationResponse, request, tmpMapRequest, tmpMapResponse)
                .map(response -> DestinationResponsePartnerResponseToTaxResponseMapper.getInstance()
                        .apply(destinationResponse, response));
    }

    private Mono<DestinationResponse> getDestination(Mono<TaxRequest> request) {
        return destinationIntegration.call(request, TaxRequestToDestinationRequestMapper.getInstance(),
                tmpMapDestinationResponse);
    }

    @Override
    protected Mono<TaxAttributesDeterminationResponseModel> determineTaxes(Mono<TaxRequest> request) {
        return determineIntegration.call(request, TaxRequestToDetermineRequestMapper.getInstance(),
                tmpMapDeterminationResponse);
    }

    @Override
    protected Mono<TaxCalculationResponse> calculateTaxes(Mono<TaxAttributesDeterminationResponseModel> detResponse,
            Mono<TaxRequest> request) {
        return calculateIntegration.call(detResponse, request,
                DeterminationResponseTaxRequestToCalculationRequestMapper.getInstance());
    }
}
