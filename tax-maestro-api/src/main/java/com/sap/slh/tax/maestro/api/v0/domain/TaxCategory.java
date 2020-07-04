package com.sap.slh.tax.maestro.api.v0.domain;

public enum TaxCategory {
    PRODUCT_TAXES("1"), WITHHOLDING("2");

    private String byDCode;

    /**
     * {@link TaxCategory} constructor.
     * 
     * @param byDCode
     *            ByDesign code representing the {@link TaxCategory}
     */
    private TaxCategory(String byDCode) {
        this.byDCode = byDCode;
    }

    /**
     * Return the ByDesign code that represents the {@link TaxCategory}.
     * 
     * @return ByDesign code representing the {@link TaxCategory}
     */
    public String getByDCode() {
        return byDCode;
    }
}
