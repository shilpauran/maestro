package com.sap.slh.tax.maestro.api.v0.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class TaxResponseTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void serializationAndDeserialization() throws IOException, ParseException {
        String actualJson = "{\"date\":\"2016-06-11T05:27:00.000Z\",\"total\":\"total\",\"inclusive\":\"inclusive\","
                + "\"subTotal\":\"subTotal\",\"totalTax\":\"totalTax\",\"totalWithholdingTax\":\"totalWithholdingTax\","
                + "\"country\":\"BR\",\"taxLines\":[],\"warning\":[],\"traceLog\":[],\"partnerName\":\"partnerName\"}";
        TaxResponse actualObject = new ObjectMapper().readValue(actualJson, TaxResponse.class);

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = fmt.parse("2016-06-11T05:27:00.000Z");

        TaxResponse expectedObject = TaxResponse.builder().withTotal("total").withInclusive("inclusive")
                .withSubTotal("subTotal").withTotalTax("totalTax").withCountry(CountryCode.BR)
                .withTotalWithholdingTax("totalWithholdingTax").withTaxLines(new ArrayList<TaxLine>())
                .withWarning(new ArrayList<Warning>()).withDate(date).withTraceLog(new ArrayList<String>())
                .withPartnerName("partnerName").build();

        assertEquals(expectedObject, actualObject);
        assertEquals(actualObject,
                new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(expectedObject), TaxResponse.class));
    }

    @Test
    public void objectValidation() {
        TaxResponse.builder().build().validate();
    }

    @Test
    public void checkJsonFieldsOrder() throws IOException, ParseException {
        String expectedJSON = "{\"date\":\"2016-06-11T05:27:00.000Z\",\"total\":\"total\",\"inclusive\":\"inclusive\","
                + "\"subTotal\":\"subTotal\",\"totalTax\":\"totalTax\",\"totalWithholdingTax\":\"totalWithholdingTax\","
                + "\"country\":\"BR\",\"taxLines\":[],\"warning\":[],\"traceLog\":[],\"partnerName\":\"partnerName\"}";

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = fmt.parse("2016-06-11T05:27:00.000Z");

        TaxResponse actualObject = TaxResponse.builder().withTotal("total").withInclusive("inclusive")
                .withSubTotal("subTotal").withTotalTax("totalTax").withCountry(CountryCode.BR)
                .withTotalWithholdingTax("totalWithholdingTax").withTaxLines(new ArrayList<TaxLine>())
                .withWarning(new ArrayList<Warning>()).withDate(date).withTraceLog(new ArrayList<String>())
                .withPartnerName("partnerName").build();

        String actualObjectAsString = new ObjectMapper().writeValueAsString(actualObject);

        assertEquals(expectedJSON, actualObjectAsString);
    }

    @Test
    public void validCountryCodeDomain() throws IOException {
        String actualJson = String.format("{\"country\":\"%s\"}", CountryCode.BR);

        TaxResponse actualObject = new ObjectMapper().readValue(actualJson, TaxResponse.class);
        assertEquals(actualObject.getCountry(), CountryCode.BR);
    }

    @Test
    public void invalidCountryCodeDomain() throws IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("CountryCode");

        String actualJson = String.format("{\"country\":\"%s\"}", "invalid");
        new ObjectMapper().readValue(actualJson, TaxResponse.class);
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(TaxResponse.class)
            .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE, nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS)
            .verify();
    }
}
