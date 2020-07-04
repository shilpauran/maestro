package com.sap.slh.tax.maestro.api.v0.domain;

public enum GrossOrNet {
    g, n, G, N;

    /**
     * Check if the {@link GrossOrNet} value is gross.
     * 
     * @return <code>true</code> if the {@link GrossOrNet} value is gross
     */
    public boolean isGross() {
        return GrossOrNet.G.toString().equalsIgnoreCase(this.toString());
    }

    /**
     * Check if the {@link GrossOrNet} value is net.
     * 
     * @return <code>true</code> if the {@link GrossOrNet} value is net
     */
    public boolean isNet() {
        return GrossOrNet.N.toString().equalsIgnoreCase(this.toString());
    }
}
