package com.sap.slh.tax.maestro.tax.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse;
import com.sap.slh.tax.maestro.context.RequestContextService;
import com.sap.slh.tax.maestro.tax.service.QuoteV0Service;

import reactor.core.publisher.Mono;

@RestController()
@RequestMapping(path = {"/tax/v0"})
public class TaxV0Controller extends TaxController<TaxRequest, TaxResponse> {

    private QuoteV0Service quoteService;

    @Autowired
    public TaxV0Controller(final ObjectMapper objectMapper, final RequestContextService requestContextService,
            final QuoteV0Service quoteService) {
        super(objectMapper, requestContextService);
        this.quoteService = quoteService;
    }

    protected void validateRequest(TaxRequest request) {
        request.validate();
    }

    protected Class<TaxRequest> getRequestClassType() {
        return TaxRequest.class;
    }

    @Override
    protected Mono<TaxResponse> callQuote(Mono<TaxRequest> request) {
        return quoteService.call(request);
    }

}
