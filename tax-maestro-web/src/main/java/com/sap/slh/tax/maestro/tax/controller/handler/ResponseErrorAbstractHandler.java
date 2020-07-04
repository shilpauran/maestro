package com.sap.slh.tax.maestro.tax.controller.handler;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ServerWebInputException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.sap.slh.tax.calculation.model.common.TaxCalculationErrorMessage;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.context.tenant.TenantNotRetrievedException;
import com.sap.slh.tax.maestro.i18n.I18N;
import com.sap.slh.tax.maestro.tax.controller.ValidationPropertyErrorMessageFormatter;
import com.sap.slh.tax.maestro.tax.exceptions.JMXException;
import com.sap.slh.tax.maestro.tax.exceptions.NoAmqpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.NoJwtProvidedException;
import com.sap.slh.tax.maestro.tax.exceptions.NoRelevantCountryForDestinationException;
import com.sap.slh.tax.maestro.tax.exceptions.QueueCommunicationException;
import com.sap.slh.tax.maestro.tax.exceptions.TaxMaestroException;
import com.sap.slh.tax.maestro.tax.exceptions.UnknownAmqpResponseTypeException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculateInvalidModelException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculateNoContentException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculatePartialContentException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DetermineInvalidModelException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DetermineNoContentException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DeterminePartialContentException;

public abstract class ResponseErrorAbstractHandler<T> {
    private static final Logger logger = LoggerFactory.getLogger(ResponseErrorAbstractHandler.class);

    private MessageHelper msgHelper;

    /**
     * Generate responses based on exceptions.
     *
     * @param msgSource
     *            {@link MessageSource} used to localize error messages.
     */
    public ResponseErrorAbstractHandler(MessageSource msgSource) {
        this.msgHelper = new MessageHelper(msgSource);
    }

    @ExceptionHandler(QueueCommunicationException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public abstract T handleQueueCommunicationException(QueueCommunicationException ex, Locale locale);

    @ExceptionHandler(UnknownAmqpResponseTypeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public abstract T handleUnknowAmqpResponseType(UnknownAmqpResponseTypeException ex, Locale locale);

    @ExceptionHandler(TenantNotRetrievedException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public abstract T handleTenantNotRetrievedException(TenantNotRetrievedException ex, Locale locale);

    @ExceptionHandler(NoAmqpResponseException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public abstract T handleNoAmqpResponseException(NoAmqpResponseException ex, Locale locale);

    @ExceptionHandler(TaxMaestroException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public abstract T handleUnexpectedTaxException(TaxMaestroException ex, Locale locale);

    @ExceptionHandler(JMXException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public abstract T handleJMXException(JMXException ex, Locale locale);
    
    @ExceptionHandler(InvalidModelException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public abstract T handleMandatoryAttributesMissingError(InvalidModelException ex, Locale locale);

    @ExceptionHandler(DecodingException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public abstract T handleDecodingError(DecodingException ex, Locale locale);

    @ExceptionHandler({ JsonParseException.class, ServerWebInputException.class, JsonMappingException.class })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public abstract T handleInvalidJson(Exception ex, Locale locale);

    @ExceptionHandler(MismatchedInputException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public abstract T handleInvalidFormatError(MismatchedInputException ex, Locale locale);

    @ExceptionHandler(NoJwtProvidedException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public abstract T handleJwtNotProvided(NoJwtProvidedException ex, Locale locale);

    @ExceptionHandler(NoRelevantCountryForDestinationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public abstract T handleNoRelevantCountryForDestination(NoRelevantCountryForDestinationException ex, Locale locale);

    @ExceptionHandler(DetermineNoContentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public abstract T handleDetermineNoContentError(DetermineNoContentException ex, Locale locale);

    @ExceptionHandler(DeterminePartialContentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public abstract T handleDeterminePartialContentError(DeterminePartialContentException ex, Locale locale);

    @ExceptionHandler(DetermineInvalidModelException.class)
    @ResponseBody
    public abstract ResponseEntity<T> handleDetermineInvalidModelError(DetermineInvalidModelException ex,
            Locale locale);

    @ExceptionHandler(CalculateNoContentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public abstract T handleCalculateNoContentError(CalculateNoContentException ex, Locale locale);

    @ExceptionHandler(CalculatePartialContentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public abstract T handleCalculatePartialContentError(CalculatePartialContentException ex, Locale locale);

    @ExceptionHandler(CalculateInvalidModelException.class)
    @ResponseBody
    public abstract ResponseEntity<T> handleCalculateInvalidModelError(CalculateInvalidModelException ex,
            Locale locale);
    
    @ExceptionHandler(DataBufferLimitException.class)
    @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
    @ResponseBody
    public abstract T handleDataBufferLimitException(DataBufferLimitException ex, Locale locale);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public abstract T handleUnexpectedException(Exception ex, Locale locale);

    protected String message(I18N code, @Nullable Object[] args, Locale locale) {
        return this.msgHelper.message(code, args, locale);
    }

    protected ValidationPropertyErrorMessageFormatter formatter() {
        return this.msgHelper.formatter();
    }

    protected Set<TaxCalculationErrorMessage> getCalculateBadRequestExceptionCodes() {
        return Stream
                .of(TaxCalculationErrorMessage.DATE_INVALID, TaxCalculationErrorMessage.ERROR_DOCUMENT_DATE_REQUIRED,
                        TaxCalculationErrorMessage.ERROR_DOCUMENT_AMOUNTTYPECODE_REQUIRED,
                        TaxCalculationErrorMessage.ERROR_DOCUMENT_CURRENCYCODE_REQUIRED,
                        TaxCalculationErrorMessage.ERROR_ITEM_QUANTITY_REQUIRED,
                        TaxCalculationErrorMessage.ERROR_ITEM_UNITPRICE_REQUIRED)
                .collect(Collectors.toCollection(HashSet::new));
    }

    protected static class MismatchedInputExceptionError {
        private MismatchedInputException error;

        private MismatchedInputExceptionError(MismatchedInputException error) {
            this.error = error;
        }

        public static MismatchedInputExceptionError from(MismatchedInputException error) {
            return new MismatchedInputExceptionError(error);
        }

        public String mismatchedValue() {
            String value = "";
            if (this.error.getProcessor() instanceof JsonParser) {
                JsonParser parser = (JsonParser)this.error.getProcessor();
                try {
                    value = parser.getCurrentToken() != null ? parser.getText() : "";
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            return value;
        }

        public String jsonPath() {
            if (CollectionUtils.isEmpty(this.error.getPath())) {
                return StringUtils.EMPTY;
            }

            StringBuilder jsonPathBuilder = new StringBuilder();
            boolean firstPath = true;

            for (Reference path : this.error.getPath()) {

                if (path.getIndex() == -1) {
                    if (!firstPath) {
                        jsonPathBuilder.append(".");
                    }
                    jsonPathBuilder.append(path.getFieldName());
                } else {
                    jsonPathBuilder.append(String.format("[%d]", path.getIndex()));
                }

                firstPath = false;
            }

            return jsonPathBuilder.toString();
        }

        public String expectedValue() {
            String clazz = this.error.getTargetType().getCanonicalName();
            String expected = null;

            if (this.error.getTargetType().isEnum()) {
                List<String> values = Stream.of(this.error.getTargetType().getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.toList());
                expected = String.join(", ", values);
            } else if (clazz.equals("java.lang.Boolean")) {
                expected = "boolean";
            } else if (clazz.equals("java.math.BigDecimal")) {
                expected = "number";
            } else {
                expected = clazz;
            }

            return expected;
        }

        public I18N messageCode() {
            if (StringUtils.isEmpty(this.mismatchedValue())) {
                return I18N.MESSAGE_INVALID_JSON_REQUEST;
            } else {
                return this.error.getTargetType().isEnum() ? I18N.MESSAGE_INVALID_JSON_ENUM_TYPE
                        : I18N.MESSAGE_INVALID_JSON_VALUE_TYPE;
            }
        }
    }
}
