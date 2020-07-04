package com.sap.slh.tax.maestro.api.common.deserializer;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.apache.commons.lang3.StringUtils;

public class CustomBigDecimalDeserializer extends JsonDeserializer<BigDecimal> {

    protected static final Class<?> valueClass = BigDecimal.class;

    @Override
    public BigDecimal deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (StringUtils.isBlank(parser.getValueAsString())) {
            return null;
        } else {
            try {
                return new BigDecimal(parser.getValueAsString().trim());
            } catch (NumberFormatException | NullPointerException e) {
                context.handleUnexpectedToken(valueClass, parser);
                return null;
            }
        }
    }
}
