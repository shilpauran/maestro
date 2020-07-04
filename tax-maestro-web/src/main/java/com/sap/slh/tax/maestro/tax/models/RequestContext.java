package com.sap.slh.tax.maestro.tax.models;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class RequestContext {

    private static final String NO_CACHE = "no-cache";

    private String tenantId;
    private Locale locale;
    private String jwt;
    private String cacheControl;
    private String correlationId;
    
    public RequestContext() {
    }

    private RequestContext(Builder builder) {
        this.tenantId = builder.tenantId;
        this.locale = builder.locale;
        this.jwt = builder.jwt;
        this.cacheControl = builder.cacheControl;
        this.correlationId = builder.correlationId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String tenantId;
        private Locale locale;
        private String jwt;
        private String cacheControl;
        private String correlationId;

        public Builder withTenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder withLocale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public Builder withJwt(String jwt) {
            this.jwt = jwt;
            return this;
        }

        public Builder withCacheControl(String cacheControl) {
            this.cacheControl = cacheControl;
            return this;
        }

        public Builder withCorrelationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public RequestContext build() {
            return new RequestContext(this);
        }
    }

    public String getTenantId() {
        return tenantId;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getJwt() {
        return jwt;
    }

    public String getCacheControl() {
        return cacheControl;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public void setCacheControl(String cacheControl) {
        this.cacheControl = cacheControl;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Boolean shouldUseCache() {
        return StringUtils.isEmpty(cacheControl) || !cacheControl.contains(NO_CACHE);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cacheControl == null) ? 0 : cacheControl.hashCode());
        result = prime * result + ((jwt == null) ? 0 : jwt.hashCode());
        result = prime * result + ((locale == null) ? 0 : locale.hashCode());
        result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
        result = prime * result + ((correlationId == null) ? 0 : correlationId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RequestContext other = (RequestContext) obj;
        if (cacheControl == null) {
            if (other.cacheControl != null)
                return false;
        } else if (!cacheControl.equals(other.cacheControl))
            return false;
        if (jwt == null) {
            if (other.jwt != null)
                return false;
        } else if (!jwt.equals(other.jwt))
            return false;
        if (locale == null) {
            if (other.locale != null)
                return false;
        } else if (!locale.equals(other.locale))
            return false;
        if (tenantId == null) {
            if (other.tenantId != null)
                return false;
        } else if (!tenantId.equals(other.tenantId))
            return false;
        if (correlationId == null) {
            if (other.correlationId != null)
                return false;
        } else if (!correlationId.equals(other.correlationId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RequestContext [tenantId=");
        sb.append(tenantId);
        sb.append(", locale=");
        sb.append(locale);
        sb.append(", jwt=");
        sb.append("***");
        sb.append(", cacheControl=");
        sb.append(cacheControl);
        sb.append(", correlationId=");
        sb.append(correlationId);
        sb.append("]");
        return sb.toString();
    }

}
