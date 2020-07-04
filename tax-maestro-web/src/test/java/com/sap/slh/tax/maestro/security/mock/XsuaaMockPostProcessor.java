package com.sap.slh.tax.maestro.security.mock;

import com.sap.cloud.security.xsuaa.mock.XsuaaMockWebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Profiles;

public class XsuaaMockPostProcessor implements EnvironmentPostProcessor, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(XsuaaMockPostProcessor.class);

    private final XsuaaMockWebServer mockAuthorizationServer;

    public XsuaaMockPostProcessor() {
        mockAuthorizationServer = new XsuaaMockWebServer(new XsuaaMockRequestDispatcher());
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (environment.acceptsProfiles(Profiles.of("uaamock"))) {
            environment.getPropertySources().addFirst(this.mockAuthorizationServer);
            logger.info("Adding mockAuthorizationServer to post processor");
        }
    }

    @Override
    public void destroy() throws Exception {
        this.mockAuthorizationServer.destroy();
    }

}
