package com.sap.slh.tax.maestro.security.mock;

import com.sap.cloud.security.xsuaa.XsuaaServiceConfigurationDefault;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("uaamock")
public class XsuaaMockServiceConfig extends XsuaaServiceConfigurationDefault {

    @Value("${mockxsuaaserver.url:}")
    private String mockXsuaaServerUrl;

    @Override
    public String getUaaDomain() {
        if (!mockXsuaaServerUrl.isEmpty()) {
            return "localhost";
        }
        return super.getUaaDomain();
    }

    @Override
    public String getUaaUrl() {
        if (!mockXsuaaServerUrl.isEmpty()) {
            return mockXsuaaServerUrl;
        }
        return super.getUaaUrl();
    }

    //@Override
    public String getTokenKeyUrl(String zid, String subdomain) {
        // Please note this method is deprecated and will be deleted with version 2.0
        if ("uaa".equals(zid)) {
            return getUaaUrl() + "/token_keys";
        } else {
            return String.format("%s/%s/token_keys", getUaaUrl(), subdomain);
            //return String.format("https://%s/%s/token_keys", subdomain, getUaaDomain());
        }
    }

}
