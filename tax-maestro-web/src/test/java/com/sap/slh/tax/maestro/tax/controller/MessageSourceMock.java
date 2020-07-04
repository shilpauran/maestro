package com.sap.slh.tax.maestro.tax.controller;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.context.support.AbstractMessageSource;

public class MessageSourceMock extends AbstractMessageSource {

    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        return super.resolveCodeWithoutArguments(code, locale);
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        ResourceBundle messages = ResourceBundle.getBundle("i18n.messages", locale);
        MessageFormat messageFormat = new MessageFormat(messages.getString(code));
        return messageFormat;
    }
}
