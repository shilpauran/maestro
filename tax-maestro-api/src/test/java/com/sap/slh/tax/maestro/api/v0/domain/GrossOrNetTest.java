package com.sap.slh.tax.maestro.api.v0.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GrossOrNetTest {
    @Test
    public void lowercaseGisGross() {
        assertTrue(GrossOrNet.g.isGross());
        assertFalse(GrossOrNet.g.isNet());
    }

    @Test
    public void uppercaseGisGross() {
        assertTrue(GrossOrNet.G.isGross());
        assertFalse(GrossOrNet.G.isNet());
    }

    @Test
    public void lowercaseNisNet() {
        assertTrue(GrossOrNet.n.isNet());
        assertFalse(GrossOrNet.n.isGross());
    }

    @Test
    public void uppercaseNisNet() {
        assertTrue(GrossOrNet.N.isNet());
        assertFalse(GrossOrNet.N.isGross());
    }
}
