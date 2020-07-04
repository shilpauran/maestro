package com.sap.slh.tax.maestro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

@Configuration
@EnableMBeanExport(registration= RegistrationPolicy.REPLACE_EXISTING)
public class JMXConfig {
}