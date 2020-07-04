package com.sap.slh.tax.maestro.tax.controller.handler;

import com.sap.slh.tax.maestro.i18n.I18N;
import com.sap.slh.tax.maestro.tax.controller.MessageSourceMock;
import com.sap.slh.tax.maestro.tax.controller.ValidationPropertyErrorMessageFormatter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MessageHelperTest {

    private MessageHelper msgHelper;
    private MessageSource msgSource = new MessageSourceMock();

    @Before
    public void setup() {
        this.msgHelper = new MessageHelper(this.msgSource);
    }

    @Test
    public void testGetFormatter() {
        ValidationPropertyErrorMessageFormatter formatter = this.msgHelper.formatter();

        assertNotNull(formatter);
        assertEquals(ValidationPropertyErrorMessageFormatter.class, formatter.getClass());
    }

    @Test
    public void testGetMessage() {
        String expectedMsg = "'X' is an invalid value for parameter 'any'.";
        String msg = this.msgHelper.message(
                I18N.MESSAGE_INVALID_FORMAT_EXCEPTION,
                new Object[]{'X', "any"},
                Locale.US);

        assertEquals(expectedMsg, msg);
    }

    @Test
    public void testGetMessageWithInvalidLocaleShouldReturnTrueDefaultUs() {
        String expectedMsg = "'X' is an invalid value for parameter 'any'.";
        String msg = this.msgHelper.message(
                I18N.MESSAGE_INVALID_FORMAT_EXCEPTION,
                new Object[]{'X', "any"},
                Locale.JAPAN);

        assertEquals(expectedMsg, msg);
    }
}
