package com.sap.slh.tax.maestro.tax.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteResultDocument;
import com.sap.slh.tax.maestro.context.RequestContextService;
import com.sap.slh.tax.maestro.tax.service.QuoteV1Service;

import reactor.core.publisher.Mono;

@RestController()
@RequestMapping(path = { "/tax/v1", "/tax/latest" })
public class TaxV1Controller extends TaxController<QuoteDocument, QuoteResultDocument> {

    private QuoteV1Service quoteService;

    @Autowired
    public TaxV1Controller(final ObjectMapper objectMapper, final RequestContextService requestContextService,
            final QuoteV1Service quoteService) {
        super(objectMapper, requestContextService);
        this.quoteService = quoteService;
    }

    @Override
    protected void validateRequest(QuoteDocument request) {
        request.validate();
    }

    @Override
    protected Class<QuoteDocument> getRequestClassType() {
        return QuoteDocument.class;
    }

    @Override
    protected Mono<QuoteResultDocument> callQuote(Mono<QuoteDocument> request) {
        return quoteService.call(request);
    }

}
