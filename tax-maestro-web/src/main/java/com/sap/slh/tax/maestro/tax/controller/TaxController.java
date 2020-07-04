package com.sap.slh.tax.maestro.tax.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.slh.tax.maestro.context.RequestContextService;

import reactor.core.publisher.Mono;

public abstract class TaxController<T, S> {

    private static final Logger logger = LoggerFactory.getLogger(TaxController.class);

    protected ObjectMapper mapper;
    protected RequestContextService requestContextService;

	protected TaxController(final ObjectMapper objectMapper, final RequestContextService requestContextService) {
		this.mapper = objectMapper;
		this.requestContextService = requestContextService;
	}

	@PostMapping(path = {
			"/quote" }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<S>> quote(@RequestBody String requestBody) throws JsonProcessingException {

        T request = mapper.readValue(requestBody, getRequestClassType());
        logger.info("Quote request: {}", request);

        validateRequest(request);

        return callQuote(Mono.just(request)).map(response -> {
                    logger.info("Quote response: {}", response);
                    return ResponseEntity.ok().body(response);
                });
    };

    protected abstract void validateRequest(T request);

    protected abstract Class<T> getRequestClassType();

    protected abstract Mono<S> callQuote(Mono<T> request);

}
