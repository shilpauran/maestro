package com.sap.slh.tax.maestro.integration.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

import java.io.IOException;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;

import com.sap.slh.tax.attributes.determination.model.request.TaxAttributesDeterminationRequest;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.calculation.model.common.TaxCalculationRequest;
import com.sap.slh.tax.calculation.model.common.TaxCalculationResponse;
import com.sap.slh.tax.product.tax.classification.models.ProductIdsForProductClassification;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationRequest;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationResponse;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteResultDocument;
import com.sap.slh.tax.maestro.integration.steps.TestDataLoader.ScenarioType;
import com.sap.slh.tax.maestro.tax.models.DestinationCacheKey;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import reactor.core.publisher.Mono;

public class AllStepsDefinition extends AbstractStepDefinition {

    private static final Logger logger = LoggerFactory.getLogger(AllStepsDefinition.class);

    @Before
    public void beforeScenarios() {
        logger.info("Preparing the scenario -------------------------------------------------");
        Mockito.reset(rabbitTemplate);
        Mockito.reset(partnerWebClient);
        mockPartnerWebClientRequest();
    }

    @Given("I have a {string} method to the endpoint {string}")
    public void i_make_a_request_with_method_and_path(String method, String path) {
        this.setMethodAndPath(HttpMethod.resolve(method), path);
    }

    @Given("I want to perform a tax quote using {string} version")
    public void i_want_to_perform_a_tax_quote_using_version(String version) {
        cacheDestination.invalidateAll();
        this.i_want_to_perform_a_tax_quote_using_version_keeping_the_cache_from_previous_scenarios(version);
    }

    @Given("I want to perform a tax quote using {string} version keeping the cache from previous scenarios")
    public void i_want_to_perform_a_tax_quote_using_version_keeping_the_cache_from_previous_scenarios(String version) {
        this.apiVersion = version;
        this.setMethodAndPath(HttpMethod.POST, "/tax/" + version + "/quote");
    }

    @Given("I want to perform a health check")
    public void i_want_to_perform_a_health_check() {
        this.setMethodAndPath(HttpMethod.GET, PING_ENDPOINT);
    }

    @Given("It has body as {string}")
    public void it_has_body_as(String body) {
        requestHeaderSpec = requestBodySpec.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(body));
    }

    @Given("It has a request body as from scenario {string}")
    public void it_has_a_request_json_as_from_scenario(String scenario) throws IOException {
        String body = dataLoader.loadScenario(scenario, ScenarioType.QUOTE_REQUEST, false);

        requestHeaderSpec = requestBodySpec.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(body));

    }

    @Given("It has a destination response as from scenario {string}")
    public void it_has_a_destination_response_as_from_scenario(String scenario) throws Exception {
        String body = dataLoader.loadScenario(scenario, ScenarioType.DESTINATION_RESPONSE);

        Message destinationMsg = null;

        if (body != null) {
            try {
                destinationMsg = convertJsonToDestinationResponse(body);
            } catch (Exception exceptionDestinationConversion) {
                destinationMsg = convertJsonToErrorResponse(body);
            }
        }

        this.mockSendAndReceive(EXCHANGE_NAME_DESTINATION, getCurrentTenant() + ROUTING_KEY_DESTINATION,
                destinationMsg);
    }

    @Given("It has a partner response as from scenario {string}")
    public void it_has_a_partner_response_as_from_scenario(String scenario) throws IOException {
        String body = dataLoader.loadScenario(scenario, ScenarioType.PARTNER_RESPONSE);

        if (body != null) {
            if ("v0".equals(apiVersion)) {
                this.convertAndMockV0PartnerWebClientResponse(body);
            }
            if ("v1".equals(apiVersion)) {
                QuoteResultDocument response = mapper.readValue(body, QuoteResultDocument.class);
                this.mockPartnerWebClientResponse(response);
            }
        }
    }

    @Given("It has a partner response too large")
    public void it_has_a_partner_response_too_large() {
        mockResponseThrowsException(new DataBufferLimitException("Limit Exceeded"), HttpStatus.OK);
    }

    @Given("It has a partner response status as {int}")
    public void it_has_a_partner_response_status_as(int status) {
        this.mockPartnerResponseStatus(HttpStatus.valueOf(status));
    }

    @Given("It has an enhance response as from scenario {string}")
    public void it_has_an_enhance_response_as_from_scenario(String scenario) throws IOException {
        String body = dataLoader.loadScenario(scenario, ScenarioType.ENHANCE_RESPONSE);

        if (body != null) {
            this.convertAndMockEnhanceResponse(body);
        }

    }

    @Given("It has a determine response as from scenario {string}")
    public void it_has_a_determine_response_as_from_scenario(String scenario) throws IOException {
        String body = dataLoader.loadScenario(scenario, ScenarioType.DETERMINE_RESPONSE);

        if (body != null) {
            Message determineMsg = msgConverter.toMessage(
                    mapper.readValue(body, TaxAttributesDeterminationResponseModel.class), new MessageProperties());

            this.mockSendAndReceive(EXCHANGE_NAME, getCurrentTenant() + ROUTING_KEY_DETERMINATION, determineMsg);
        }
    }

    @Given("It has a calculate response as from scenario {string}")
    public void it_has_a_calculate_response_as_from_scenario(String scenario) throws IOException {
        String body = dataLoader.loadScenario(scenario, ScenarioType.CALCULATE_RESPONSE);

        if (body != null) {
            Message calcMsg = msgConverter.toMessage(mapper.readValue(body, TaxCalculationResponse.class),
                    new MessageProperties());
            this.mockSendAndReceive(EXCHANGE_NAME, getCurrentTenant() + ROUTING_KEY_CALCULATION, calcMsg);
        }
    }

    @Given("It has an empty body")
    public void it_has_an_empty_body() {
        requestHeaderSpec = requestBodySpec.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(""));
    }

    @Given("It is an authorized request")
    public void i_have_an_authorized_request() {
        this.i_have_an_authorized_request_for_the_tenant(null);
    }

    @Given("It is an authorized request for the tenant {string}")
    public void i_have_an_authorized_request_for_the_tenant(String tenant) {
        requestHeaderSpec.header(HttpHeaders.AUTHORIZATION, getJwtGenerator(tenant).getTokenForAuthorizationHeader());
    }

    @Given("It is an unauthorized request")
    public void i_have_an_unauthorized_request() {
        requestHeaderSpec.header(HttpHeaders.AUTHORIZATION, "notValidToken");
    }

    @Given("Client has language configured as {string}")
    public void client_has_language_configured_as(String language) {
        requestHeaderSpec.header(HttpHeaders.ACCEPT_LANGUAGE, language);
    }

    @Given("It has CacheControl HTTP header set as {string}")
    public void it_has_cachecontrol_http_header_set_as(String cacheControl) {
        if (!StringUtils.isEmpty(cacheControl))
            requestHeaderSpec.header(HttpHeaders.CACHE_CONTROL, cacheControl);
    }

    @Given("It has a Correlation ID in the header")
    public void it_has_a_correlation_id_in_the_header() {
        requestHeaderSpec.header(CORRELATION_ID_HEADER_NAME, DUMMY_CORRELATION_ID);
    }

    @Given("It has a Correlation ID too large in the header")
    public void it_has_a_correlation_id_too_large_in_the_header() {
        requestHeaderSpec.header(CORRELATION_ID_HEADER_NAME, CORRELATION_ID_TOO_LARGE);
        requestHeaderSpec.header(VCAP_REQUEST_ID_HEADER_NAME, DUMMY_VCAP_REQUEST_ID);
    }

    @Given("It does not have a Correlation ID in the header")
    public void it_does_not_have_a_correlation_id_in_the_header() {
        requestHeaderSpec.header(VCAP_REQUEST_ID_HEADER_NAME, DUMMY_VCAP_REQUEST_ID);
    }
    
    @Given("It has an invalid Correlation ID in the header")
    public void it_has_an_invalid_correlation_id_in_the_header() {
        requestHeaderSpec.header(CORRELATION_ID_HEADER_NAME, INVALID_CORRELATION_ID);
        requestHeaderSpec.header(VCAP_REQUEST_ID_HEADER_NAME, DUMMY_VCAP_REQUEST_ID);
    }

    @When("I send the request")
    public void i_send_the_request() {
        logger.info("Executing the scenario -------------------------------------------------");
        responseSpec = requestHeaderSpec.exchange();
    }

    @Then("Response should be that request succeeded")
    public void response_should_be_that_request_succeeded() {
        this.verifyStatusCode(STATUS_OK);
    }

    @Then("Response should be that request failed because it is invalid")
    public void response_should_be_that_request_failed_because_it_is_invalid() {
        this.verifyStatusCode(STATUS_BAD_REQUEST);
    }

    @Then("Response should be that request is unauthenticated")
    public void response_should_be_that_request_is_unauthenticated() {
        this.verifyStatusCode(STATUS_UNAUTHORIZED);
    }

    @Then("Response should be that resource was not found")
    public void response_should_be_that_resource_was_not_found() {
        this.verifyStatusCode(STATUS_NOT_FOUND);
    }

    @Then("Response should be that method is not allowed")
    public void response_should_be_that_method_is_not_allowed() {
        this.verifyStatusCode(STATUS_METHOD_NOT_ALLOWED);
    }

    @Then("Response should be that server could not process the request")
    public void response_should_be_that_server_could_not_process_the_request() {
        this.verifyStatusCode(STATUS_SERVER_ERROR);
    }

    @Then("Response should be that request failed because no content was found")
    public void response_should_be_that_request_failed_because_no_content_found() {
        this.verifyStatusCode(NO_CONTENT_FOUND);
    }

    @Then("Response should be that request failed because partial content was found")
    public void response_should_be_that_request_failed_because_partial_content_found() {
        this.verifyStatusCode(PARTIAL_CONTENT_FOUND);
    }

    @Then("Response should be that request failed because payload is too large")
    public void response_should_be_that_request_failed_because_payload_is_too_large() {
        this.verifyStatusCode(PAYLOAD_TOO_LARGE);
    }

    @Then("Response should be with status {int}")
    public void response_should_be_with_status(int status) {
        this.verifyStatusCode(status);
    }

    @Given("Response should have the same Correlation ID in the header")
    public void response_should_have_the_same_correlation_id_in_the_header() {
        this.verifyHeader(CORRELATION_ID_HEADER_NAME, DUMMY_CORRELATION_ID);
    }

    @Given("Response should have a valid Correlation ID in the header")
    public void response_should_have_a_valid_correlation_id_in_the_header() {
        this.verifyHeaderMatches(CORRELATION_ID_HEADER_NAME, CORRELATION_ID_PATTERN);
    }

    @Then("It should return json body {string}")
    public void it_should_return_json_body(String body) {
        responseSpec.expectBody().json(body);
    }

    @Then("It should have sent a partner request as from scenario {string}")
    public void it_should_have_sent_a_partner_request_as_from_scenario(String scenario) throws IOException {
        String body = dataLoader.loadScenario(scenario, ScenarioType.PARTNER_REQUEST);

        if (body != null) {
            Mockito.verify(requestBodySpecMock).body(argThat((Mono<Object> request) -> {
                JsonComparer.compareStrict(body, request.block());
                return true;
            }), any(Class.class));
        }

    }

    @Then("It should have sent a destination request as from scenario {string}")
    public void it_should_have_sent_a_destination_request_as_from_scenario(String scenario) throws IOException {
        this.it_should_have_sent_a_destination_request_as_from_scenario(this.getCurrentTenant(), scenario);
    }

    @Then("Partner request should have received HTTP Authorization header as {string}")
    public void partner_request_should_have_received_http_authorization_header_as(String expectedToken)
            throws IOException {
        if (!StringUtils.isEmpty(expectedToken)) {
            Mockito.verify(requestBodySpecMock).header(eq(HttpHeaders.AUTHORIZATION), argThat((String token) -> {
                return expectedToken.equals(token);
            }));
        } else {
            Mockito.verify(requestBodySpecMock, Mockito.times(0)).header(eq(HttpHeaders.AUTHORIZATION), any());
        }

    }

    @Then("It should have sent a destination request to tenant {string} as from scenario {string}")
    public void it_should_have_sent_a_destination_request_as_from_scenario(String tenant, String scenario)
            throws IOException {
        String body = dataLoader.loadScenario(scenario, ScenarioType.DESTINATION_REQUEST);

        if (body != null) {
            DestinationRequest expected = mapper.readValue(body, DestinationRequest.class);

            Mockito.verify(rabbitTemplate).sendAndReceive(eq(EXCHANGE_NAME_DESTINATION),
                    eq(tenant + ROUTING_KEY_DESTINATION), argThat((Message msg) -> {
                        DestinationRequest actual = (DestinationRequest)msgConverter.fromMessage(msg);
                        ObjectVerification.verifyEqualsString(expected, actual);
                        return true;
                    }));
        }
    }

    @Then("It should have sent an enhance request as from scenario {string}")
    public void it_should_have_sent_an_enhance_request_as_from_scenario(String scenario) throws IOException {
        this.it_should_have_sent_an_enhance_request_as_from_scenario(this.getCurrentTenant(), scenario);
    }

    @Then("It should have sent an enhance request to tenant {string} as from scenario {string}")
    public void it_should_have_sent_an_enhance_request_as_from_scenario(String tenant, String scenario)
            throws IOException {
        String body = dataLoader.loadScenario(scenario, ScenarioType.ENHANCE_REQUEST);

        if (body != null) {
            ProductIdsForProductClassification expected = mapper.readValue(body,
                    ProductIdsForProductClassification.class);

            Mockito.verify(rabbitTemplate).sendAndReceive(eq(EXCHANGE_NAME_ENHANCER), eq(tenant + ROUTING_KEY_ENHANCER),
                    argThat((Message msg) -> {
                        ProductIdsForProductClassification actual = (ProductIdsForProductClassification)msgConverter
                                .fromMessage(msg);
                        ObjectVerification.verifyEqualsString(expected, actual);
                        return true;
                    }));
        }
    }

    @Then("It should have sent a determine request as from scenario {string}")
    public void it_should_have_sent_a_determine_request_as_from_scenario(String scenario) throws IOException {
        this.it_should_have_sent_a_determine_request_as_from_scenario(scenario, this.getCurrentTenant());
    }

    @Then("It should have sent a determine request to tenant {string} as from scenario {string}")
    public void it_should_have_sent_a_determine_request_as_from_scenario(String scenario, String tenant)
            throws IOException {
        String body = dataLoader.loadScenario(scenario, ScenarioType.DETERMINE_REQUEST);

        if (body != null) {
            TaxAttributesDeterminationRequest expected = mapper.readValue(body,
                    TaxAttributesDeterminationRequest.class);

            Mockito.verify(rabbitTemplate).sendAndReceive(eq(EXCHANGE_NAME), eq(tenant + ROUTING_KEY_DETERMINATION),
                    argThat((Message msg) -> {
                        TaxAttributesDeterminationRequest detRequest = (TaxAttributesDeterminationRequest)msgConverter
                                .fromMessage(msg);
                        ObjectVerification.verifyEqualsString(expected, detRequest);
                        return true;
                    }));
        }
    }

    @Then("It should have sent a calculate request as from scenario {string}")
    public void it_should_have_sent_a_calculate_request_as_from_scenario(String scenario) throws IOException {
        this.it_should_have_sent_a_calculate_request_as_from_scenario(scenario, this.getCurrentTenant());
    }

    @Then("It should have sent a calculate request to tenant {string} as from scenario {string}")
    public void it_should_have_sent_a_calculate_request_as_from_scenario(String scenario, String tenant)
            throws IOException {
        String body = dataLoader.loadScenario(scenario, ScenarioType.CALCULATE_REQUEST);

        if (body != null) {
            TaxCalculationRequest expected = mapper.readValue(body, TaxCalculationRequest.class);

            Mockito.verify(rabbitTemplate).sendAndReceive(eq(EXCHANGE_NAME), eq(tenant + ROUTING_KEY_CALCULATION),
                    argThat((Message msg) -> {
                        TaxCalculationRequest calcRequest = (TaxCalculationRequest)msgConverter.fromMessage(msg);
                        ObjectVerification.verifyEqualsString(expected, calcRequest);
                        return true;
                    }));
        }
    }

    @Then("It should have sent {int} destination requests to tenant {string}")
    public void it_should_have_sent_times_destination_request_to_tenant(int callCount, String tenant) {
        this.verifySendReceiveCalls(callCount, EXCHANGE_NAME_DESTINATION, tenant + ROUTING_KEY_DESTINATION);
    }

    @Then("It should have sent {int} enhance requests to tenant {string}")
    public void it_should_have_sent_times_enhance_request_to_tenant(int callCount, String tenant) {
        this.verifySendReceiveCalls(callCount, EXCHANGE_NAME_ENHANCER, tenant + ROUTING_KEY_ENHANCER);
    }

    @Then("It should have sent {int} determine requests to tenant {string}")
    public void it_should_have_sent_times_determine_request_to_tenant(int callCount, String tenant) {
        this.verifySendReceiveCalls(callCount, EXCHANGE_NAME, tenant + ROUTING_KEY_DETERMINATION);
    }

    @Then("It should have sent {int} calculate requests to tenant {string}")
    public void it_should_have_sent_times_calculate_request_to_tenant(int callCount, String tenant) {
        this.verifySendReceiveCalls(callCount, EXCHANGE_NAME, tenant + ROUTING_KEY_CALCULATION);
    }

    @Then("It should return response body as from scenario {string}")
    public void it_should_return_json_as_from_scenario(String scenario) throws IOException {
        String expectedBody = dataLoader.loadScenario(scenario, ScenarioType.QUOTE_RESPONSE);

        responseSpec.expectBody().consumeWith(response -> {
            String responseBody = JsonComparer.byteToStringUTF8(response.getResponseBody());
            JsonComparer.compareStrict(expectedBody, responseBody);
        });
    }

    @Then("Destination queue should have received language as {string}")
    public void destination_queue_should_have_language_as(String language) {
        this.hasQueueMessageProperty(EXCHANGE_NAME_DESTINATION, ROUTING_KEY_DESTINATION, HttpHeaders.ACCEPT_LANGUAGE,
                language);
    }

    @Then("It should have destination cache content as from scenario {string}")
    public void it_should_have_destination_cache_content_as_from_scenario(String scenario) throws IOException {
        DestinationCacheKey destinationCacheKey = getDestinationCacheKey(scenario);
        DestinationResponse destinationCacheValue = getDestinationCacheValue(scenario);
        assertEquals(destinationCacheValue, cacheDestination.getIfPresent(destinationCacheKey));
    }

    @Then("Destination cache should have {int} entries")
    public void destination_cache_should_have_n_entries(int size) {
        cacheDestination.cleanUp();
        assertEquals(size, cacheDestination.estimatedSize());
    }

    @Then("Enhance queue should have received language as {string}")
    public void enhance_queue_should_have_language_as(String language) {
        this.hasQueueMessageProperty(EXCHANGE_NAME_ENHANCER, ROUTING_KEY_ENHANCER, HttpHeaders.ACCEPT_LANGUAGE,
                language);
    }

    @Then("Determine queue should have received language as {string}")
    public void determine_queue_should_have_language_as(String language) {
        this.hasQueueMessageProperty(EXCHANGE_NAME, ROUTING_KEY_DETERMINATION, HttpHeaders.ACCEPT_LANGUAGE, language);
    }

    @Then("Calculate queue should have received language as {string}")
    public void calculate_queue_should_have_language_as(String language) {
        this.hasQueueMessageProperty(EXCHANGE_NAME, ROUTING_KEY_CALCULATION, HttpHeaders.ACCEPT_LANGUAGE, language);
    }

    @Then("Destination queue should have the same Correlation ID from HTTP response header")
    public void destination_should_have_the_same_correlation_id_from_http_response_header() {
        this.hasQueueMessagePropertyCorrelationId(EXCHANGE_NAME_DESTINATION, ROUTING_KEY_DESTINATION);
    }

    @Then("Enhance queue should have the same Correlation ID from HTTP response header")
    public void enhance_should_have_the_same_correlation_id_from_http_response_header() {
        this.hasQueueMessagePropertyCorrelationId(EXCHANGE_NAME_ENHANCER, ROUTING_KEY_ENHANCER);
    }

    @Then("Determine queue should have the same Correlation ID from HTTP response header")
    public void determine_should_have_the_same_correlation_id_from_http_response_header() {
        this.hasQueueMessagePropertyCorrelationId(EXCHANGE_NAME, ROUTING_KEY_DETERMINATION);
    }

    @Then("Calculate queue should have the same Correlation ID from HTTP response header")
    public void calculate_queue_should_have_the_same_correlation_id_from_http_response_header() {
        this.hasQueueMessagePropertyCorrelationId(EXCHANGE_NAME, ROUTING_KEY_CALCULATION);
    }

    @Then("Partner request should have the same Correlation ID from HTTP response header")
    public void partner_request_should_have_the_same_correlation_id_from_http_response_header() {
        Mockito.verify(requestBodySpecMock).header(eq(CORRELATION_ID_HEADER_NAME), argThat((String correlationId) -> {
            this.verifyHeader(CORRELATION_ID_HEADER_NAME, correlationId);
            return true;
        }));
    }

}