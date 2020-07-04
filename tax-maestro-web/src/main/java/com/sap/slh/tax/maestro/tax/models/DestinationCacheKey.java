package com.sap.slh.tax.maestro.tax.models;

import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationRequest;

public class DestinationCacheKey {

    private String tenantId;
    private DestinationRequest destinationRequest;

    private DestinationCacheKey(Builder builder) {
        this.tenantId = builder.tenantId;
        this.destinationRequest = builder.destinationRequest;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String tenantId;
        private DestinationRequest destinationRequest;

        public Builder withTenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder withDestinationRequest(DestinationRequest destinationRequest) {
            this.destinationRequest = destinationRequest;
            return this;
        }

        public DestinationCacheKey build() {
            return new DestinationCacheKey(this);
        }
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public DestinationRequest getDestinationRequest() {
        return destinationRequest;
    }

    public void setDestinationRequest(DestinationRequest destinationRequest) {
        this.destinationRequest = destinationRequest;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DestinationCacheKey [tenantId=");
        sb.append(tenantId);
        sb.append(", destinationRequest=");
        sb.append(destinationRequest);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((destinationRequest == null) ? 0 : destinationRequest.hashCode());
        result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DestinationCacheKey))
            return false;
        DestinationCacheKey other = (DestinationCacheKey)obj;
        if (destinationRequest == null) {
            if (other.destinationRequest != null)
                return false;
        } else if (!destinationRequest.equals(other.destinationRequest))
            return false;
        if (tenantId == null) {
            if (other.tenantId != null)
                return false;
        } else if (!tenantId.equals(other.tenantId))
            return false;
        return true;
    }

}
