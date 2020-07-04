package com.sap.slh.tax.maestro.api.v1.schema;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.sap.slh.tax.maestro.api.common.domain.CurrencyCode;
import com.sap.slh.tax.maestro.api.v1.domain.AmountType;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class QuoteResultDocumentTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private QuoteResultDocument.Builder classUnderTestBuilder = QuoteResultDocument.builder();
    private ObjectMapper objectMapper = new ObjectMapper();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Test
    public void objectValidation() {
        classUnderTestBuilder.build().validate();
    }

    @Test
    public void serializationAndDeserialization()
            throws JsonParseException, JsonMappingException, IOException, ParseException {
        String actualJson = "{\"id\":\"id\",\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"currencyCode\":\"BRL\",\"amountTypeCode\":\"GROSS\",\"items\":[],"
                + "\"additionalInformation\": {\"aKey\": \"itsValue\"}}";
        QuoteResultDocument actualObject = objectMapper.readValue(actualJson, QuoteResultDocument.class);

        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = dateFormat.parse("2016-06-11T05:27:00.000Z");
        Map<String, String> additionalInformation = new HashMap<String, String>();
        additionalInformation.put("aKey", "itsValue");

        List<ResultDocumentItem> items = new ArrayList<ResultDocumentItem>();

        QuoteResultDocument expectedObject = classUnderTestBuilder.withId("id").withDate(date)
                .withCurrencyCode(CurrencyCode.BRL).withAmountTypeCode(AmountType.GROSS).withItems(items)
                .withAdditionalInformation(additionalInformation).build();

        assertEquals(expectedObject, actualObject);
        assertEquals(expectedObject.toString(), actualObject.toString());
        assertEquals(expectedObject.hashCode(), actualObject.hashCode());
        assertEquals(actualObject, objectMapper.readValue(new ObjectMapper().writeValueAsString(expectedObject),
                QuoteResultDocument.class));
    }

    @Test
    public void validDateDomain() throws JsonParseException, JsonMappingException, IOException, ParseException {
        String actualJson = "{\"id\":\"id\",\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"currencyCode\":\"BRL\",\"amountTypeCode\":\"GROSS\",\"items\":[]}";
        QuoteResultDocument actualObject = new ObjectMapper().readValue(actualJson, QuoteResultDocument.class);

        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = dateFormat.parse("2016-06-11T05:27:00.000Z");

        new ObjectMapper().readValue(actualJson, QuoteResultDocument.class).validate();
        assertEquals(actualObject.getDate(), date);
    }

    @Test
    public void invalidDateDomain() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("\"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\"");

        String actualJson = "{\"id\":\"id\",\"date\":\"2016-13-11T05:27:00.000Z\","
                + "\"currencyCode\":\"BRL\",\"amountTypeCode\":\"GROSS\",\"items\":[]}";

        new ObjectMapper().readValue(actualJson, QuoteResultDocument.class).validate();
    }

    @Test
    public void validCurrencyDomain() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"currencyCode\":\"BRL\",\"amountTypeCode\":\"GROSS\",\"items\":[]}";
        QuoteResultDocument actualObject = new ObjectMapper().readValue(actualJson, QuoteResultDocument.class);

        assertEquals(actualObject.getCurrencyCode(), CurrencyCode.BRL);
    }

    @Test
    public void invalidCurrencyDomain() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("CurrencyCode");

        String actualJson = "{\"id\":\"id\",\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"currencyCode\":\"CURENC\",\"amountTypeCode\":\"GROSS\",\"items\":[]}";
        new ObjectMapper().readValue(actualJson, QuoteResultDocument.class);
    }

    @Test
    public void validAmountTypeDomain() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"currencyCode\":\"BRL\",\"amountTypeCode\":\"GROSS\",\"items\":[]}";
        QuoteResultDocument actualObject = new ObjectMapper().readValue(actualJson, QuoteResultDocument.class);

        assertEquals(actualObject.getAmountTypeCode(), AmountType.GROSS);
    }

    @Test
    public void invalidAmountTypeDomain() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("AmountType");

        String actualJson = "{\"id\":\"id\",\"date\":\"2016-06-11T05:27:00.000Z\","
                + "\"currencyCode\":\"BRL\",\"amountTypeCode\":\"SOGROSS\",\"items\":[]}";
        new ObjectMapper().readValue(actualJson, QuoteResultDocument.class);
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(QuoteResultDocument.class).suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
                .verify();
    }
}