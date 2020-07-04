package com.sap.slh.tax.maestro.api.common.exception;

public class PropertyErrorValueOutOfBoundsDetail extends PropertyErrorDetail {
    private static final String MSG_FORMAT = "Value for property '%s' out of specified interval [%s, %s]";

    private String leftBound;
    private String rightBound;

    /**
     * {@link PropertyErrorValueOutOfBoundsDetail} constructor.
     * 
     * @param property property name
     * @param leftBound left bound accepted by the property
     * @param rightBound right bound value accepted by the property
     */
    public PropertyErrorValueOutOfBoundsDetail(String property, String leftBound, String rightBound) {
        super(property);
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }
    
    public String getLeftBound() {
        return leftBound;
    }

    public String getRightBound() {
        return rightBound;
    }

    @Override
    public Object[] getAttributesAsArgs() {
        return new Object[] { this.property, this.leftBound, this.rightBound };
    }
    
    @Override
    public String getMessage() {
        return this.getMessageWithReferences(String.format(MSG_FORMAT, property, leftBound, rightBound));
    }
}
