package com.sap.slh.tax.maestro.api.v1.schema;

import static com.sap.slh.tax.maestro.api.assertion.HasMandatoryAttribute.hasMandatoryItems;
import static com.sap.slh.tax.maestro.api.v1.schema.StandardClassification.JSONParameter.CLASSIFICATION_SYSTEM;
import static com.sap.slh.tax.maestro.api.v1.schema.StandardClassification.JSONParameter.PRODUCT_CODE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class StandardClassificationTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void nullFields() {
        try {
            StandardClassification.builder().build().validate();
        } catch (InvalidModelException e) {
            assertThat(e.getPropertyErrors(), hasMandatoryItems(CLASSIFICATION_SYSTEM, PRODUCT_CODE));
        }
    }

    @Test
    public void serializeAndDeserialize() throws IOException {
        StandardClassification standardClassification = this.getDefaultStandardClassification();
        String serialized = mapper.writeValueAsString(standardClassification);
        StandardClassification deserialized = mapper.readValue(serialized, StandardClassification.class);

        assertEquals(standardClassification, deserialized);
    }

    @Test
    public void validStandardClassification() {
        StandardClassification.builder().withClassificationSystem("NCM").withProductCode("010").build();
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(StandardClassification.class)
            .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
            .verify();
    }

    private StandardClassification getDefaultStandardClassification() {
        return StandardClassification.builder().withClassificationSystem("classificationSystem").withProductCode("productCode").build();
    }

}