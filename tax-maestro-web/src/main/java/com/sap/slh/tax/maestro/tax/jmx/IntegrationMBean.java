package com.sap.slh.tax.maestro.tax.jmx;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource
public abstract class IntegrationMBean extends Counter {

    @ManagedAttribute
    @Override
    public int getCount() {
        return super.getCount();
    }

    @ManagedOperation
    @Override    
    public void resetCount() {
        super.resetCount();
    }

}