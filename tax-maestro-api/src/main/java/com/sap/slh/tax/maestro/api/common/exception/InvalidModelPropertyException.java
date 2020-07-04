package com.sap.slh.tax.maestro.api.common.exception;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class InvalidModelPropertyException extends RuntimeException {

    private static final long serialVersionUID = 5575458046002764456L;
    private static final String PATH_SEPARATOR = ".";

    protected final String message;
    protected final String path;
    protected final String property;

    public InvalidModelPropertyException(String message, String path, String property) {
        super(message);
        this.property = property;
        this.path = path;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public String getProperty() {
        return property;
    }

    public String getFullPath() {
        return this.path + PATH_SEPARATOR + this.property;
    }

    public static String combinePath(String... args) {
        return Stream.of(args).filter(s -> s != null && !s.isEmpty()).collect(Collectors.joining(PATH_SEPARATOR));
    }
}
