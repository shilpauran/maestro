package com.sap.slh.tax.maestro.tax.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.calculation.model.common.TaxCalculationResponse;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteResultDocument;
import com.sap.slh.tax.maestro.tax.mapper.AggregateObjectsToQuoteResultDocumentMapper;
import com.sap.slh.tax.maestro.tax.mapper.DeterminationResponseQuoteDocumentToCalculationRequestMapper;
import com.sap.slh.tax.maestro.tax.mapper.EnhancerToQuoteDocumentMapper;
import com.sap.slh.tax.maestro.tax.mapper.QuoteDocumentToDetermineRequestMapper;
import com.sap.slh.tax.maestro.tax.mapper.QuoteDocumentToEnhancerMapper;
import com.sap.slh.tax.maestro.tax.service.integration.CalculateQueueIntegration;
import com.sap.slh.tax.maestro.tax.service.integration.DetermineQueueIntegration;
import com.sap.slh.tax.maestro.tax.service.integration.EnhanceQueueIntegration;

import reactor.core.publisher.Mono;

@Service
public class QuoteV1Service extends QuoteService<QuoteDocument, QuoteResultDocument> {

    private EnhanceQueueIntegration enhanceIntegration;

    private static final Logger logger = LoggerFactory.getLogger(QuoteV1Service.class);

    @Autowired
    public QuoteV1Service(EnhanceQueueIntegration enhanceIntegration, DetermineQueueIntegration determineIntegration,
            CalculateQueueIntegration calculateIntegration) {
        super(determineIntegration, calculateIntegration);
        this.enhanceIntegration = enhanceIntegration;
    }

    @Override
    public Mono<QuoteResultDocument> call(Mono<QuoteDocument> request) {
        logger.debug("Quote via tax-service");
        return quoteTaxService(request);
    }

    private Boolean isEnhanceable(QuoteDocument request) {
        return request.getProducts().stream().anyMatch(product -> product.getMasterDataProductId() != null);
    }

    @Override
    protected Mono<QuoteResultDocument> quoteTaxService(Mono<QuoteDocument> request) {
        return request.transformDeferred(req -> enhanceQuoteDocument(req)).cache()
                .transformDeferred(quoteDocument -> determineTaxes(quoteDocument).cache().transformDeferred(
                        determineResponse -> calculateTaxes(determineResponse, quoteDocument).transformDeferred(
                                calcResponse -> Mono.zip(AggregateObjectsToQuoteResultDocumentMapper.getInstance(),
                                        quoteDocument, determineResponse, calcResponse))));
    }

    @Override
    protected Mono<TaxCalculationResponse> calculateTaxes(Mono<TaxAttributesDeterminationResponseModel> detResponse,
            Mono<QuoteDocument> quote) {
        return calculateIntegration.call(detResponse, quote,
                DeterminationResponseQuoteDocumentToCalculationRequestMapper.getInstance());
    }

    @Override
    protected Mono<TaxAttributesDeterminationResponseModel> determineTaxes(Mono<QuoteDocument> quoteDocument) {
        return determineIntegration.call(quoteDocument, QuoteDocumentToDetermineRequestMapper.getInstance(),
                tmpMapDeterminationResponse);
    }

    private Mono<QuoteDocument> enhanceQuoteDocument(Mono<QuoteDocument> request) {
        return request
                .filter(this::isEnhanceable).flatMap(quote -> enhanceIntegration.call(Mono.just(quote),
                        QuoteDocumentToEnhancerMapper.getInstance(), EnhancerToQuoteDocumentMapper.getInstance()))
                .switchIfEmpty(request);
    }

}
