package com.sap.slh.tax.maestro.tax.jmx;

import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sap.slh.tax.maestro.tax.exceptions.JMXException;

@Component
public class QuoteHttpIntegrationMBeansHandler {

    private static Logger logger = LoggerFactory.getLogger(QuoteHttpIntegrationMBeansHandler.class);

    private static final String OBJECT_NAME_UNAVAILABILITY = "com.sap.slh.tax.maestro.tax.jmx:type=QuoteHttpUnavailabilityError,name=%s";
    private static final String OBJECT_NAME_INTERNAL_ERROR = "com.sap.slh.tax.maestro.tax.jmx:type=QuoteHttpInternalServerError,name=%s";

    protected LinkedHashMap<ObjectName, QuoteHttpUnavailabilityError> unavailabilityErrorMBeans = new LinkedHashMap<>();
    protected LinkedHashMap<ObjectName, QuoteHttpInternalServerError> internalServerErrorMBeans = new LinkedHashMap<>();

    private MBeanServer server = ManagementFactory.getPlatformMBeanServer();

    public QuoteHttpUnavailabilityError getUnavailabilityErrorMBeanByHost(String host) {
        return getUnavailabilityErrorMBeanByObjectName(
                createObjectName(String.format(OBJECT_NAME_UNAVAILABILITY, host)));
    }

    public QuoteHttpInternalServerError getInternalServerErrorMBeanByHost(String host) {
        return getInternalServerErrorMBeanByObjectName(
                createObjectName(String.format(OBJECT_NAME_INTERNAL_ERROR, host)));
    }

    private QuoteHttpUnavailabilityError getUnavailabilityErrorMBeanByObjectName(ObjectName objectName) {
        if (!server.isRegistered(objectName)) {
            QuoteHttpUnavailabilityError mBean = new QuoteHttpUnavailabilityError();
            registerMBean(objectName, mBean);
            unavailabilityErrorMBeans.put(objectName, mBean);
            return mBean;
        } else {
            logger.info("Object Name {} already registered", objectName);
            return unavailabilityErrorMBeans.get(objectName);
        }
    }

    private QuoteHttpInternalServerError getInternalServerErrorMBeanByObjectName(ObjectName objectName) {
        if (!server.isRegistered(objectName)) {
            QuoteHttpInternalServerError mBean = new QuoteHttpInternalServerError();
            registerMBean(objectName, mBean);
            internalServerErrorMBeans.put(objectName, mBean);
            return mBean;
        } else {
            logger.info("Object Name {} already registered", objectName);
            return internalServerErrorMBeans.get(objectName);
        }
    }

    private void registerMBean(ObjectName objectName, CounterMBean mBean) {
        try {
            server.registerMBean(mBean, objectName);
            logger.info("MBean {} was registered with Object Name {}", mBean, objectName);
        } catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e) {
            logger.error("Error when registering JMX MBean", e);
            throw new JMXException("Error when registering MBean " + mBean + "with Object Name " + objectName);
        }
    }

    private ObjectName createObjectName(String objectName) {
        try {
            return new ObjectName(objectName);
        } catch (MalformedObjectNameException e) {
            logger.error("Error when creating JMX Object Name", e);
            throw new JMXException("Error when creating JMX Object Name: " + objectName);
        }
    }

}