package com.sap.slh.tax.maestro.context.tenant;

import com.sap.slh.tax.maestro.tax.exceptions.TaxMaestroException;

public class TenantNotRetrievedException extends TaxMaestroException {
    private static final long serialVersionUID = 9175463863147532795L;

    public TenantNotRetrievedException() {
        super("Tenant identification could not be resolved");
    }
}
