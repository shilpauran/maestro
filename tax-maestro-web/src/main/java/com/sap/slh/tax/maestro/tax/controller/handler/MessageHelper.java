package com.sap.slh.tax.maestro.tax.controller.handler;

import com.sap.slh.tax.maestro.i18n.I18N;
import com.sap.slh.tax.maestro.tax.controller.ValidationPropertyErrorMessageFormatter;
import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;

import java.util.Locale;

public final class MessageHelper {

    private MessageSource msgSource;
    private ValidationPropertyErrorMessageFormatter msgFormatter;

    public MessageHelper(MessageSource msgSource) {
        this.msgSource = msgSource;
    }

    public String message(I18N code, @Nullable Object[] args, Locale locale) {
        return this.msgSource.getMessage(code.getValue(), args, locale);
    }

    public ValidationPropertyErrorMessageFormatter formatter() {
        if (this.msgFormatter == null) {
            this.msgFormatter = new ValidationPropertyErrorMessageFormatter(this.msgSource);
        }
        return this.msgFormatter;
    }

}
