package com.sap.slh.tax.maestro.context.correlation;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;

@Service
public class CorrelationIdHeaderService {

    public static final String HTTP_HEADER_CORRELATION_ID = "X-CorrelationID";
    public static final String HTTP_HEADER_VCAP_REQUEST_ID = "X-Vcap-Request-Id";
    
    private CorrelationIdValidator correlationIdValidator;
    
    public CorrelationIdHeaderService(CorrelationIdValidator correlationIdValidator) {
        this.correlationIdValidator = correlationIdValidator;
    }

    public void putCorrelationIdInResponseHeader(String correlationId, ServerHttpResponse response) {
        response.getHeaders().set(HTTP_HEADER_CORRELATION_ID, correlationId);
    }

    public Optional<String> getCorrelationIdFromRequestHeader(HttpHeaders headers) {
        String correlationId = headers.getFirst(HTTP_HEADER_CORRELATION_ID);

        return correlationIdValidator.isValid(correlationId)
                ? Optional.of(correlationId)
                : getVcapRequestIdFromRequestHeader(headers);
    }

    private Optional<String> getVcapRequestIdFromRequestHeader(HttpHeaders headers) {
        String vcapRequestId = headers.getFirst(HTTP_HEADER_VCAP_REQUEST_ID);
        return correlationIdValidator.isValid(vcapRequestId)
                ? Optional.of(vcapRequestId)
                : Optional.empty();
    }
}
