package com.sap.slh.tax.maestro.api.v0.domain;

public enum SaleOrPurchase {
    s, p, S, P;

    /**
     * Check if the {@link SaleOrPurchase} value is a purchase.
     * 
     * @return <code>true</code> if the {@link SaleOrPurchase} value is a purchase
     */
    public boolean isPurchase() {
        return SaleOrPurchase.P.toString().equalsIgnoreCase(this.toString());
    }

    /**
     * Check if the {@link SaleOrPurchase} value is a sale.
     * 
     * @return <code>true</code> if the {@link SaleOrPurchase} value is a sale
     */
    public boolean isSale() {
        return SaleOrPurchase.S.toString().equalsIgnoreCase(this.toString());
    }
}
