package com.sap.slh.tax.maestro.integration.steps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class TestDataLoader {

    protected static final String TEST_DATA_PATH = "src/test/resources/com/sap/slh/tax/maestro/integration/testData/";
    protected static final String QUOTE_REQUEST_FILE = "QuoteRequest.json";
    protected static final String QUOTE_RESPONSE_FILE = "QuoteResponse.json";
    protected static final String ENHANCE_REQUEST_FILE = "EnhanceRequest.json";
    protected static final String ENHANCE_RESPONSE_FILE = "EnhanceResponse.json";
    protected static final String DETERMINE_REQUEST_FILE = "DetermineRequest.json";
    protected static final String DETERMINE_RESPONSE_FILE = "DetermineResponse.json";
    protected static final String CALCULATE_REQUEST_FILE = "CalculateRequest.json";
    protected static final String CALCULATE_RESPONSE_FILE = "CalculateResponse.json";
    protected static final String PARTNER_REQUEST_FILE = "PartnerRequest.json";
    protected static final String PARTNER_RESPONSE_FILE = "PartnerResponse.json";
    protected static final String DESTINATION_REQUEST_FILE = "DestinationRequest.json";
    protected static final String DESTINATION_RESPONSE_FILE = "DestinationResponse.json";
    protected static final String DESTINATION_CACHE_RESPONSE_FILE = "DestinationCacheResponse.json";

    private static TestDataLoader instance;

    private TestDataLoader() {

    }

    public static TestDataLoader getInstance() {
        if (instance == null) {
            instance = new TestDataLoader();
        }
        return instance;
    }

    public String loadScenario(String scenario, ScenarioType type) throws IOException {
        return loadScenario(scenario, type, true);
    }

    public String loadScenario(String scenario, ScenarioType type, boolean optional) throws IOException {
        String path = this.getPath(scenario, type);

        File file = new File(path);
        if (file.exists()) {
            return new String(Files.readAllBytes(file.toPath()));
        } else {
            if (!optional) {
                throw new FileNotFoundException(String.format("File: %s", path));
            }
            return null;
        }
    }

    private String getPath(String scenario, ScenarioType type) {
        String fileName;

        switch (type) {
            case QUOTE_REQUEST:
                fileName = QUOTE_REQUEST_FILE;
                break;
            case QUOTE_RESPONSE:
                fileName = QUOTE_RESPONSE_FILE;
                break;
            case ENHANCE_REQUEST:
                fileName = ENHANCE_REQUEST_FILE;
                break;
            case ENHANCE_RESPONSE:
                fileName = ENHANCE_RESPONSE_FILE;
                break;
            case DETERMINE_REQUEST:
                fileName = DETERMINE_REQUEST_FILE;
                break;
            case DETERMINE_RESPONSE:
                fileName = DETERMINE_RESPONSE_FILE;
                break;
            case CALCULATE_REQUEST:
                fileName = CALCULATE_REQUEST_FILE;
                break;
            case CALCULATE_RESPONSE:
                fileName = CALCULATE_RESPONSE_FILE;
                break;
            case PARTNER_REQUEST:
                fileName = PARTNER_REQUEST_FILE;
                break;
            case PARTNER_RESPONSE:
                fileName = PARTNER_RESPONSE_FILE;
                break;
            case DESTINATION_REQUEST:
                fileName = DESTINATION_REQUEST_FILE;
                break;
            case DESTINATION_RESPONSE:
                fileName = DESTINATION_RESPONSE_FILE;
                break;
            case DESTINATION_CACHE_RESPONSE:
                fileName = DESTINATION_CACHE_RESPONSE_FILE;
                break;                
            default:
                throw new IllegalStateException("Unknown Scenario type provided");
        }

        return String.join(File.separator, TEST_DATA_PATH, scenario, fileName);
    }

    public enum ScenarioType {
        QUOTE_REQUEST,
        QUOTE_RESPONSE,
        ENHANCE_REQUEST,
        ENHANCE_RESPONSE,
        DETERMINE_REQUEST,
        DETERMINE_RESPONSE,
        CALCULATE_REQUEST,
        CALCULATE_RESPONSE,
        PARTNER_REQUEST,
        PARTNER_RESPONSE,
        DESTINATION_REQUEST,
        DESTINATION_RESPONSE,
        DESTINATION_CACHE_RESPONSE
    }
}
