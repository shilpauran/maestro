package com.sap.slh.tax.maestro.api.v0.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.sap.slh.tax.maestro.api.v0.domain.SaleOrPurchase;

public class SaleOrPurchaseTest {
    @Test
    public void lowercaseSisSale() {
        assertTrue(SaleOrPurchase.s.isSale());
        assertFalse(SaleOrPurchase.s.isPurchase());
    }

    @Test
    public void uppercaseSisSale() {
        assertTrue(SaleOrPurchase.S.isSale());
        assertFalse(SaleOrPurchase.S.isPurchase());
    }

    @Test
    public void lowercasePisPurchase() {
        assertTrue(SaleOrPurchase.p.isPurchase());
        assertFalse(SaleOrPurchase.p.isSale());
    }

    @Test
    public void uppercasePisPurchase() {
        assertTrue(SaleOrPurchase.P.isPurchase());
        assertFalse(SaleOrPurchase.P.isSale());
    }
}
