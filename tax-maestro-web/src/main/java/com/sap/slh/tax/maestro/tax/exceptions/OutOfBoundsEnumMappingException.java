package com.sap.slh.tax.maestro.tax.exceptions;

public final class OutOfBoundsEnumMappingException extends TaxMaestroException {

    private static final long serialVersionUID = 508073735547702034L;

    private final Class<?> enumClass;
    private final String enumValue;

    public OutOfBoundsEnumMappingException(Class<?> enumClass, String enumValue) {
        super(String.format("Mapping error while transforming enum value %s to enum %s", enumValue,
                enumClass.getName()));
        this.enumClass = enumClass;
        this.enumValue = enumValue;
    }

    public Class<?> getEnumClass() {
        return enumClass;
    }

    public String getEnumValue() {
        return enumValue;
    }
}
