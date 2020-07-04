package com.sap.slh.tax.maestro.api.common.deserializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;

public class JSONParserMock extends JsonParser {

    Object value;

    public JSONParserMock(Object value) {
        this.value = value;
    }

    @Override
    public ObjectCodec getCodec() {
        return null;
    }

    @Override
    public void setCodec(ObjectCodec c) {

    }

    @Override
    public Version version() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public JsonStreamContext getParsingContext() {
        return null;
    }

    @Override
    public JsonLocation getTokenLocation() {
        return null;
    }

    @Override
    public JsonLocation getCurrentLocation() {
        return null;
    }

    @Override
    public JsonToken nextToken() throws IOException {
        return null;
    }

    @Override
    public JsonToken nextValue() throws IOException {
        return null;
    }

    @Override
    public JsonParser skipChildren() throws IOException {
        return null;
    }

    @Override
    public JsonToken getCurrentToken() {
        if (value instanceof Integer) {
            return JsonToken.VALUE_NUMBER_INT;
        } else if (value instanceof String) {
            return JsonToken.VALUE_STRING;
        } else if (value instanceof Double) {
            return JsonToken.VALUE_NUMBER_FLOAT;
        } else if (value == null) {
            return JsonToken.VALUE_NULL;
        }
        return JsonToken.VALUE_TRUE;
    }

    @Override
    public int getCurrentTokenId() {
        return 0;
    }

    @Override
    public boolean hasCurrentToken() {
        return false;
    }

    @Override
    public boolean hasTokenId(int id) {
        return false;
    }

    @Override
    public boolean hasToken(JsonToken t) {
        return true;
    }

    @Override
    public void clearCurrentToken() {

    }

    @Override
    public JsonToken getLastClearedToken() {
        return null;
    }

    @Override
    public void overrideCurrentName(String name) {

    }

    @Override
    public String getCurrentName() throws IOException {
        return null;
    }

    @Override
    public String getText() throws IOException {
        if (value instanceof Integer) {
            return String.valueOf((Integer)value);
        } else if (value instanceof Double) {
            return String.valueOf((Double)value);
        } else if (value instanceof String) {
            return (String)value;
        }
        return "true";
    }

    @Override
    public char[] getTextCharacters() throws IOException {
        return new char[0];
    }

    @Override
    public int getTextLength() throws IOException {
        if (value instanceof Integer) {
            return String.valueOf((Integer)value).length();
        } else if (value instanceof Double) {
            return String.valueOf((Double)value).length();
        } else if (value instanceof String) {
            return ((String)value).length();
        }
        return "true".length();
    }

    @Override
    public int getTextOffset() throws IOException {
        return 0;
    }

    @Override
    public boolean hasTextCharacters() {
        return false;
    }

    @Override
    public Number getNumberValue() throws IOException {
        return null;
    }

    @Override
    public NumberType getNumberType() throws IOException {
        return null;
    }

    @Override
    public int getIntValue() throws IOException {
        return 0;
    }

    @Override
    public long getLongValue() throws IOException {
        return 0;
    }

    @Override
    public BigInteger getBigIntegerValue() throws IOException {
        return null;
    }

    @Override
    public float getFloatValue() throws IOException {
        return 0;
    }

    @Override
    public double getDoubleValue() throws IOException {
        return 0;
    }

    @Override
    public BigDecimal getDecimalValue() throws IOException {
        return null;
    }

    @Override
    public byte[] getBinaryValue(Base64Variant bv) throws IOException {
        return new byte[0];
    }

    @Override
    public String getValueAsString(String def) throws IOException {
        if (value == null)
            return null;
        else
            return value.toString();
    }

}
