package com.sap.slh.tax.maestro.api.v0.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sap.slh.tax.maestro.api.v0.domain.TaxCategory;

public class TaxCategoryTest {
    @Test
    public void productTaxCategoryCode1() {
        assertEquals(TaxCategory.PRODUCT_TAXES.getByDCode(), "1");
    }

    @Test
    public void withholdingTaxCategoryCode1() {
        assertEquals(TaxCategory.WITHHOLDING.getByDCode(), "2");
    }
}
