package com.sap.slh.tax.maestro.api.common.deserializer;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import org.mockito.Mockito;

public class CustomBigDecimalDeserializerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DeserializationContext ctxt = Mockito.mock(DeserializationContext.class);
    private CustomBigDecimalDeserializer deserializer = new CustomBigDecimalDeserializer();

    @Test
    public void testDeserializerWithString() throws IOException {
        Mockito.when(ctxt.handleUnexpectedToken(Mockito.any(), Mockito.any(JsonParser.class)))
                .thenThrow(MismatchedInputException.class);

        expectedException.expect(MismatchedInputException.class);

        deserializer.deserialize(new JSONParserMock("abc"), ctxt);

        fail("Exception should have been thrown");
    }

    @Test
    public void testDeserializerWithNullString() throws IOException {
        assertNull(deserializer.deserialize(new JSONParserMock(null), ctxt));
    }

    @Test
    public void testDeserializerWithEmptyString() throws IOException {
        assertNull(deserializer.deserialize(new JSONParserMock(""), ctxt));
    }

    @Test
    public void testDeserializerWithBoolean() throws IOException {
        Mockito.when(ctxt.handleUnexpectedToken(Mockito.any(), Mockito.any(JsonParser.class)))
                .thenThrow(MismatchedInputException.class);

        expectedException.expect(MismatchedInputException.class);

        deserializer.deserialize(new JSONParserMock(true), ctxt);

        fail("Exception should have been thrown");
    }

    @Test
    public void testDeserializerWithInteger() throws IOException {
        Assert.assertEquals(new BigDecimal(85), deserializer.deserialize(new JSONParserMock(85), ctxt));
    }

    @Test
    public void testDeserializerWithDouble() throws IOException {
        Assert.assertEquals(BigDecimal.valueOf(8.5), deserializer.deserialize(new JSONParserMock(8.5), ctxt));
    }

    @Test
    public void testDeserializerWithStringInt() throws IOException {
        Assert.assertEquals(new BigDecimal(85), deserializer.deserialize(new JSONParserMock("85"), ctxt));
    }

    @Test
    public void testDeserializerWithStringDouble() throws IOException {
        Assert.assertEquals(BigDecimal.valueOf(5.1234), deserializer.deserialize(new JSONParserMock("5.1234"), ctxt));
    }
}
