package com.sap.slh.tax.maestro.tax.service.integration;

import com.sap.slh.tax.maestro.api.v0.domain.ErrorType;
import com.sap.slh.tax.maestro.api.v0.schema.ErrorResponse;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse;
import com.sap.slh.tax.maestro.context.RequestContextService;
import com.sap.slh.tax.maestro.tax.exceptions.partner.BadRequestFailedHttpResponseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class QuoteV0PartnerHttpIntegration extends HttpIntegration<TaxRequest, TaxResponse> {

    private static final Logger logger = LoggerFactory.getLogger(QuoteV0PartnerHttpIntegration.class);

    @Autowired
    protected QuoteV0PartnerHttpIntegration(WebClient webClient, RequestContextService requestContextService) {
        super(webClient, requestContextService);
    }

    @Override
    protected Mono<Void> handleHttpBadRequest(String url, ClientResponse clientResponse) {
        return clientResponse.bodyToMono(ErrorResponse.class).map(response -> {
            logger.info("Response body from HTTP call: {}", response);
            return response;
        }).onErrorResume(error -> {
            logger.error("Error when reading response body to ErrorResponse class", error);
            return Mono.empty();
        }).flatMap(response -> {
            if (response.getType() == ErrorType.INVALID_INPUT)
                throw new BadRequestFailedHttpResponseException(url, response.getMessage());
            return Mono.empty();
        });
    }

    @Override
    protected Class<TaxRequest> getInputClassType() {
        return TaxRequest.class;
    }

    @Override
    protected Class<TaxResponse> getOutputClassType() {
        return TaxResponse.class;
    }

}
