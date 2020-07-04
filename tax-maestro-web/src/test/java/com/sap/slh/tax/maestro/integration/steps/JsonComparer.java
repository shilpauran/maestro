package com.sap.slh.tax.maestro.integration.steps;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

public class JsonComparer {
    static void compareStrict(String expectedBody, String responseBody) {
        try {
            JSONAssert.assertEquals(expectedBody, responseBody, JSONCompareMode.NON_EXTENSIBLE);
        } catch (Exception e) {
            throw new AssertionError("JSON parsing error", e);
        }
    }

    static void compareStrict(String responseBody, Object body) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            compareStrict(responseBody, mapper.writeValueAsString(body));
        } catch (Exception e) {
            throw new AssertionError("JSON parsing error", e);
        }
    }
    
    static String byteToStringUTF8 (byte[] str) {
        return new String(str, StandardCharsets.UTF_8);
    }
}
