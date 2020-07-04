package com.sap.slh.tax.maestro.context.correlation;

import org.springframework.stereotype.Service;

@Service
public class CorrelationIdValidator {
    
    private static final String CORRELATION_ID_REGEX = "^[0-9a-zA-Z\\-]{1,72}+$";

    public boolean isValid(String correlationId) {
        return correlationId != null && correlationId.matches(CORRELATION_ID_REGEX);
    }

}
