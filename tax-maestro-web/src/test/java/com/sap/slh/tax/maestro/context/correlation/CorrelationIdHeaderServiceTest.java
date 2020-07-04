package com.sap.slh.tax.maestro.context.correlation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;

@RunWith(MockitoJUnitRunner.class)
public class CorrelationIdHeaderServiceTest {

    private static final String DUMMY_CORRELATION_ID = "38fc2962-ceaa-4e32-b4d3-5e66cbd25790";
    private static final String DUMMY_VCAP_REQUEST_ID = "4d477cf7-2df2-4e2e-9393-e776dee723eb";
    private static final String CORRELATION_ID_TOO_LARGE = "38fc2962-ceaa-4e32-b4d3-5e66cbd25790-480059bf-2b24-4ba6-ba24-b76777da43f8";
    private static final String CORRELATION_ID_MAX_SIZE = "38fc2962-ceaa-4e32-b4d3-5e66cbd25790-480059bf-2b24-4ba6-ba24-b76777da43f";
    private static final String INVALID_CORRELATION_ID = "<..>";
    
    @InjectMocks
    private CorrelationIdHeaderService correlationIdHeaderService;
    
    @Mock
    private CorrelationIdValidator correlationIdValidator;
    
    private HttpHeaders headers;
    
    @Before
    public void init() {
        headers = new HttpHeaders();
    }

    @Test
    public void shouldPutCorrelationIdInResponseHeader() {
        ServerHttpResponse response = new MockServerHttpResponse();

        correlationIdHeaderService.putCorrelationIdInResponseHeader(DUMMY_CORRELATION_ID, response);

        assertEquals(Arrays.asList(DUMMY_CORRELATION_ID),
                response.getHeaders().get(CorrelationIdHeaderService.HTTP_HEADER_CORRELATION_ID));
    }

    @Test
    public void shouldReturnEmptyWhenCorrelationIdIsNotFoundInRequestHeader() {
        Optional<String> maybeCorrelationId = correlationIdHeaderService.getCorrelationIdFromRequestHeader(headers);

        assertEquals(Optional.empty(), maybeCorrelationId);
    }
    
    @Test
    public void shouldReturnEmptyWhenBothCorrelationIdAndVcapRequestIdAreInvalid() {
        headers.add(CorrelationIdHeaderService.HTTP_HEADER_CORRELATION_ID, INVALID_CORRELATION_ID);
        headers.add(CorrelationIdHeaderService.HTTP_HEADER_VCAP_REQUEST_ID, INVALID_CORRELATION_ID);

        Optional<String> maybeCorrelationId = correlationIdHeaderService.getCorrelationIdFromRequestHeader(headers);

        assertEquals(Optional.empty(), maybeCorrelationId);
    }
    
    @Test
    public void shouldReturnVcapRequestIdWhenCorrelationIdIsInvalid() {
        headers.add(CorrelationIdHeaderService.HTTP_HEADER_CORRELATION_ID, INVALID_CORRELATION_ID);
        headers.add(CorrelationIdHeaderService.HTTP_HEADER_VCAP_REQUEST_ID, DUMMY_VCAP_REQUEST_ID);
        when(correlationIdValidator.isValid(DUMMY_VCAP_REQUEST_ID)).thenReturn(true);

        Optional<String> maybeCorrelationId = correlationIdHeaderService.getCorrelationIdFromRequestHeader(headers);

        assertEquals(Optional.of(DUMMY_VCAP_REQUEST_ID), maybeCorrelationId);
    }

    @Test
    public void shouldReturnVcapRequestIdWhenCorrelationIdIsOversized() {
        headers.add(CorrelationIdHeaderService.HTTP_HEADER_CORRELATION_ID, CORRELATION_ID_TOO_LARGE);
        headers.add(CorrelationIdHeaderService.HTTP_HEADER_VCAP_REQUEST_ID, DUMMY_VCAP_REQUEST_ID);
        when(correlationIdValidator.isValid(DUMMY_VCAP_REQUEST_ID)).thenReturn(true);

        Optional<String> maybeCorrelationId = correlationIdHeaderService.getCorrelationIdFromRequestHeader(headers);

        assertEquals(Optional.of(DUMMY_VCAP_REQUEST_ID), maybeCorrelationId);
    }

    @Test
    public void shouldReturnVcapRequestIdWhenFoundInHeader() {
        headers.add(CorrelationIdHeaderService.HTTP_HEADER_VCAP_REQUEST_ID, DUMMY_VCAP_REQUEST_ID);
        when(correlationIdValidator.isValid(DUMMY_VCAP_REQUEST_ID)).thenReturn(true);

        Optional<String> maybeCorrelationId = correlationIdHeaderService.getCorrelationIdFromRequestHeader(headers);

        assertEquals(Optional.of(DUMMY_VCAP_REQUEST_ID), maybeCorrelationId);
    }

    @Test
    public void shouldNotChangeCorrelationIdSmallerThanMaxSize() {
        headers.add(CorrelationIdHeaderService.HTTP_HEADER_CORRELATION_ID, DUMMY_CORRELATION_ID);
        headers.add(CorrelationIdHeaderService.HTTP_HEADER_VCAP_REQUEST_ID, DUMMY_VCAP_REQUEST_ID);
        when(correlationIdValidator.isValid(DUMMY_CORRELATION_ID)).thenReturn(true);
        
        Optional<String> maybeCorrelationId = correlationIdHeaderService.getCorrelationIdFromRequestHeader(headers);

        assertEquals(Optional.of(DUMMY_CORRELATION_ID), maybeCorrelationId);
    }

    @Test
    public void shouldNotChangeCorrelationIdWithMaxSize() {
        headers.add(CorrelationIdHeaderService.HTTP_HEADER_CORRELATION_ID, CORRELATION_ID_MAX_SIZE);
        when(correlationIdValidator.isValid(CORRELATION_ID_MAX_SIZE)).thenReturn(true);

        Optional<String> maybeCorrelationId = correlationIdHeaderService.getCorrelationIdFromRequestHeader(headers);

        assertEquals(Optional.of(CORRELATION_ID_MAX_SIZE), maybeCorrelationId);
    }
    
}
