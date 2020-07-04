package com.sap.slh.tax.maestro.tax.exceptions.partner;

import com.sap.slh.tax.maestro.tax.exceptions.TaxMaestroException;

public class PartnerResponseTooLargeException extends TaxMaestroException{
    
    private final String destinationName;

    private static final long serialVersionUID = -2115060681511850870L;

    public PartnerResponseTooLargeException(String destinationName) {
        super(destinationName);
        this.destinationName = destinationName;
    }
    
    public String getDestinationName() {
        return this.destinationName;
    }

    public String[] getAttributesAsArgs() {
        return new String[] { this.destinationName};
    }
    
    

}
