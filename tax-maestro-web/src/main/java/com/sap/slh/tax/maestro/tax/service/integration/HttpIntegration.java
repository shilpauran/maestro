package com.sap.slh.tax.maestro.tax.service.integration;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.sap.slh.tax.maestro.context.RequestContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;

import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationAuthorization;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationResponse;
import com.sap.slh.tax.destinationconfiguration.destinations.models.TokenTypeCode;
import com.sap.slh.tax.maestro.context.correlation.CorrelationIdHeaderService;
import com.sap.slh.tax.maestro.tax.exceptions.partner.FailedRequestFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.GatewayTimeoutFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.NotFoundFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.PartnerResponseTooLargeException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.RequestTimeoutFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.ServiceUnavailableFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.UnauthorizedFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.UnsupportedTokenTypeException;
import com.sap.slh.tax.maestro.tax.jmx.QuoteHttpIntegrationMBeansHandler;

import reactor.core.publisher.Mono;

public abstract class HttpIntegration<I, O> {
    private static final Logger logger = LoggerFactory.getLogger(HttpIntegration.class);

    private static final String HTTP_HEADER_BASIC = "Basic";
    private static final String HTTP_HEADER_BEARER = "Bearer";

    @Autowired
    protected QuoteHttpIntegrationMBeansHandler quoteMBeansHandler;

    protected WebClient partnerWebClient;

    protected RequestContextService requestContextService;

    protected HttpIntegration(WebClient webClient, RequestContextService requestContextService) {
        this.partnerWebClient = webClient;
        this.requestContextService = requestContextService;
    }

    /**
     * @param destinationResponse The destination response containing the REST data
     *                            for HTTP call
     * @param request             The request type to be converted by the
     *                            {@link mapRequest}
     * @param mapRequest          {@link Function} responsible for transforming the
     *                            {@link request} into the Http request
     * @param mapResponse         {@link Function} responsible for transforming the
     *                            Http response into the {@link return} type
     * @return The transformed response
     */
    public <J, K> Mono<K> call(DestinationResponse destinationResponse, Mono<J> request,
            Function<J, Mono<I>> mapRequest, Function<O, Mono<K>> mapResponse) {
        return request.flatMap(mapRequest).flatMap(input -> this.call(destinationResponse, Mono.just(input)))
                .flatMap(mapResponse);
    }

    /**
     * @param destinationResponse The destination response containing the REST data
     *                            for HTTP call
     * @param request             The request type to be converted by the
     *                            {@link mapRequest}
     * @param mapRequest          {@link Function} responsible for transforming the
     *                            {@link request} into the Htpp request
     * @param mapResponse         {@link BiFunction} responsible for transforming
     *                            the Http response and aggregating with the
     *                            {@link request} into the {@link return} type
     * @return The transformed response
     */
    public <J, K> Mono<K> call(DestinationResponse destinationResponse, Mono<J> request,
            Function<J, Mono<I>> mapRequest, BiFunction<O, J, K> mapResponse) {
        return request.flatMap(mapRequest).flatMap(input -> this.call(destinationResponse, Mono.just(input)))
                .zipWith(request, mapResponse);
    }

    /**
     * @param destinationResponse The destination response containing the REST data
     *                            for HTTP call
     * @param request             The request type to be converted by the
     *                            {@link mapAggregator}
     * @param requestAggregate    The type to be aggregated to {@link request} by
     *                            the {@link mapAggregator}
     * @param mapAggregator       {@link BiFunction} responsible for aggregating the
     *                            {@link requestAggregate} to the {@link request}
     *                            transforming into the Http request
     * @return The Http call response
     */
    public <J, G, K> Mono<O> call(DestinationResponse destinationResponse, Mono<J> request, Mono<G> requestAggregate,
            BiFunction<J, G, I> mapAggregator) {
        return request.zipWith(requestAggregate, mapAggregator)
                .flatMap(input -> this.call(destinationResponse, Mono.just(input)));
    }

    /**
     * @param destinationResponse The destination response containing the REST data
     *                            for HTTP call
     * @param input               The Http call request
     * @return The Http call response
     */
    public Mono<O> call(DestinationResponse destinationResponse, Mono<I> input) {
        return handleHttpResponse(callHttp(destinationResponse, input), destinationResponse);
    }

    private Mono<O> handleHttpResponse(Mono<ClientResponse> httpResponse, DestinationResponse destinationResponse) {
        String url = destinationResponse.getUrl().toString();
        String host = destinationResponse.getUrl().getHost();
        String destinationName = destinationResponse.getName();

        return httpResponse.flatMap(response -> {

            logger.info("HTTP Status code from HTTP call: {}", response.statusCode());

            switch (response.statusCode()) {
            case OK:
                return response.bodyToMono(getOutputClassType())
                        .doOnNext(body -> logger.info("Response body from HTTP call: {}", body))
                        .onErrorMap(DataBufferLimitException.class,
                                t -> new PartnerResponseTooLargeException(destinationName));
            case BAD_REQUEST:
                return this.handleHttpBadRequest(url, response)
                        .switchIfEmpty(Mono.error(new FailedRequestFailedHttpResponseException(destinationName)))
                        .flatMap(noException -> Mono
                                .error(new FailedRequestFailedHttpResponseException(destinationName)));
            case UNAUTHORIZED:
                return Mono.error(new UnauthorizedFailedHttpResponseException(destinationName));
            case NOT_FOUND:
                return Mono.error(new NotFoundFailedHttpResponseException(destinationName));
            case REQUEST_TIMEOUT:
                return Mono.error(new RequestTimeoutFailedHttpResponseException(destinationName));
            case SERVICE_UNAVAILABLE:
                quoteMBeansHandler.getUnavailabilityErrorMBeanByHost(host).incrementCount();
                return Mono.error(new ServiceUnavailableFailedHttpResponseException(destinationName));
            case GATEWAY_TIMEOUT:
                quoteMBeansHandler.getUnavailabilityErrorMBeanByHost(host).incrementCount();
                return Mono.error(new GatewayTimeoutFailedHttpResponseException(destinationName));
            default:
                if (response.statusCode().is5xxServerError()) {
                    quoteMBeansHandler.getInternalServerErrorMBeanByHost(host).incrementCount();
                }
                return Mono.error(new FailedRequestFailedHttpResponseException(destinationName));
            }
        });
    }

    protected abstract Mono<Void> handleHttpBadRequest(String url, ClientResponse clientResponse);

    private Mono<ClientResponse> callHttp(DestinationResponse destinationResponse, Mono<I> input) {

        return input.flatMap(body -> {
            logger.info("HTTP request: {}", body);
            return getRequestBodySpec(destinationResponse)
                    .flatMap(requestBodySpec -> requestBodySpec.body(input, getInputClassType()).exchange());
        }).onErrorMap(UnknownHostException.class,
                t -> new NotFoundFailedHttpResponseException(destinationResponse.getName()))
                .onErrorMap(ConnectException.class,
                        t -> new ServiceUnavailableFailedHttpResponseException(destinationResponse.getName()));

    }

    private Mono<RequestBodySpec> getRequestBodySpec(DestinationResponse destinationResponse) {
        String url = destinationResponse.getUrl().toString();
        logger.info("Sending HTTP request to endpoint {}", url);

        return Mono.just(this.partnerWebClient.post().uri(url).accept(MediaType.APPLICATION_JSON))
                .flatMap(requestBodySpec -> setHttpHeaders(requestBodySpec, destinationResponse));
    }

    private Mono<RequestBodySpec> setHttpHeaders(RequestBodySpec requestBodySpec,
            DestinationResponse destinationResponse) {
        return addHttpHeaderAuthorization(requestBodySpec, destinationResponse).flatMap(this::addHttpHeaderCacheControl)
                .flatMap(this::addHttpHeaderCorrelationId);
    }

    private Mono<RequestBodySpec> addHttpHeaderAuthorization(RequestBodySpec requestBodySpec,
            DestinationResponse destinationResponse) {
        return Mono.fromCallable(() -> {
            DestinationAuthorization destinationAuthorization = destinationResponse.getAuthorization();

            if (destinationAuthorization != null) {
                TokenTypeCode type = destinationAuthorization.getTokenTypeCode();
                String token = destinationAuthorization.getToken();

                if (type != null) {
                    switch (type) {
                    case BASIC:
                        logger.info("Basic authentication will be used in this request");
                        return requestBodySpec.header(HttpHeaders.AUTHORIZATION,
                                String.format("%s %s", HTTP_HEADER_BASIC, token));
                    case BEARER:
                        logger.info("Oauth authentication will be used in this request");
                        return requestBodySpec.header(HttpHeaders.AUTHORIZATION,
                                String.format("%s %s", HTTP_HEADER_BEARER, token));
                    case NONE:
                        logger.info("No authentication HTTP header will be used in this request");
                        return requestBodySpec;
                    default:
                        throw new UnsupportedTokenTypeException(type.toString(), destinationResponse.getName());
                    }
                } else {
                    throw new UnsupportedTokenTypeException(destinationResponse.getName());
                }

            } else {
                logger.info("No authentication HTTP header will be used in this request");
                return requestBodySpec;
            }
        });
    }

    private Mono<RequestBodySpec> addHttpHeaderCacheControl(RequestBodySpec requestBodySpec) {
        return requestContextService.getCacheControl().map(cacheControl -> {
            logger.info("CacheControl header will be send in this HTTP request: {}", cacheControl);
            return requestBodySpec.header(HttpHeaders.CACHE_CONTROL, cacheControl);
        }).switchIfEmpty(Mono.just(requestBodySpec));
    }

    private Mono<RequestBodySpec> addHttpHeaderCorrelationId(RequestBodySpec requestBodySpec) {
        return requestContextService.getCorrelationId().map(correlationId -> {
            logger.info("X-CorrelationID header will be send in this HTTP request: {}", correlationId);
            return requestBodySpec.header(CorrelationIdHeaderService.HTTP_HEADER_CORRELATION_ID, correlationId);
        }).switchIfEmpty(Mono.just(requestBodySpec));
    }

    protected abstract Class<I> getInputClassType();

    protected abstract Class<O> getOutputClassType();
}
