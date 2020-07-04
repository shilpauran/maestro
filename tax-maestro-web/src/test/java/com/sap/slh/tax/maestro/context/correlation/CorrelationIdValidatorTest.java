package com.sap.slh.tax.maestro.context.correlation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CorrelationIdValidatorTest {

    private CorrelationIdValidator correlationIdValidator;

    @Before
    public void setup() {
        correlationIdValidator = new CorrelationIdValidator();
    }

    @Test
    public void shouldReturnTrueWhenCorrelationIdIsValid() {
        String validCorrelationId = "ABCXYZ-13f70b82-f9a6-4b42-97ca-3d6bfe5d20fb";
        
        boolean isValid = correlationIdValidator.isValid(validCorrelationId);

        assertTrue(isValid);
    }

    @Test
    public void shouldReturnFalseWhenCorrelationIdContainsInvalidCharacters() {
        String invalidCorrelationId = "<...>";
        
        boolean isValid = correlationIdValidator.isValid(invalidCorrelationId);

        assertFalse(isValid);
    }
    
    @Test
    public void shouldReturnFalseWhenCorrelationIdIsEmpty() {
        String invalidCorrelationId = "";
        
        boolean isValid = correlationIdValidator.isValid(invalidCorrelationId);

        assertFalse(isValid);
    }

    @Test
    public void shouldReturnFalseWhenCorrelationIdIsOversized() {
        String invalidCorrelationId = "ABCXYZ-13f70b82-f9a6-4b42-97ca-3d6bfe5d20fb-ABCXYZ-13f70b82-f9a6-4b42-97ca-3d6b";

        boolean isValid = correlationIdValidator.isValid(invalidCorrelationId);

        assertFalse(isValid);
    }

}
