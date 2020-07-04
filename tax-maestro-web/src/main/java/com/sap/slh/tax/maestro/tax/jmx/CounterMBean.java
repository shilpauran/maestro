package com.sap.slh.tax.maestro.tax.jmx;

public interface CounterMBean {

    public void incrementCount();
    
    public int getCount();

    public void resetCount();
    
}
