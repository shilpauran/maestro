package com.sap.slh.tax.maestro.tax.jmx;

import static org.junit.Assert.assertEquals;

import java.lang.management.ManagementFactory;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.Before;
import org.junit.Test;

import com.sap.slh.tax.maestro.tax.exceptions.JMXException;

public class QuoteHttpIntegrationMBeansHandlerTest {

    private final static String HOST_1 = "connector-dev.vertexconnectors.com";
    private final static String HOST_2 = "taas.taxweb.com.br";

    QuoteHttpIntegrationMBeansHandler handler;

    @Before
    public void setUp() {
        handler = new QuoteHttpIntegrationMBeansHandler();
    }

    @Test(expected = JMXException.class)
    public void checkInvalidHost() {
        handler.getInternalServerErrorMBeanByHost("connector-dev.vertexconnectors.com:8098");
    }

    @Test
    public void checkInternalServerErrorMBean() throws MalformedObjectNameException {
        ObjectName expectedObjectName = new ObjectName(
                "com.sap.slh.tax.maestro.tax.jmx:type=QuoteHttpInternalServerError,name=" + HOST_1);

        unregisterMBean(expectedObjectName);

        QuoteHttpInternalServerError bean = handler.getInternalServerErrorMBeanByHost(HOST_1);

        assertEquals(bean, handler.internalServerErrorMBeans.get(expectedObjectName));
        assertEquals(1, handler.internalServerErrorMBeans.size());
        assertEquals(0, handler.unavailabilityErrorMBeans.size());

    }

    @Test
    public void checkInternalServerErrorMBeanDifferentHosts() throws MalformedObjectNameException {
        ObjectName expectedObjectName1 = new ObjectName(
                "com.sap.slh.tax.maestro.tax.jmx:type=QuoteHttpInternalServerError,name=" + HOST_1);
        ObjectName expectedObjectName2 = new ObjectName(
                "com.sap.slh.tax.maestro.tax.jmx:type=QuoteHttpInternalServerError,name=" + HOST_2);

        unregisterMBean(expectedObjectName1);
        unregisterMBean(expectedObjectName2);

        QuoteHttpInternalServerError bean1 = handler.getInternalServerErrorMBeanByHost(HOST_1);
        QuoteHttpInternalServerError bean2 = handler.getInternalServerErrorMBeanByHost(HOST_2);

        assertEquals(bean1, handler.internalServerErrorMBeans.get(expectedObjectName1));
        assertEquals(bean2, handler.internalServerErrorMBeans.get(expectedObjectName2));
        assertEquals(2, handler.internalServerErrorMBeans.size());
        assertEquals(0, handler.unavailabilityErrorMBeans.size());

    }

    @Test
    public void checkInternalServerErrorMBeanSameHost() throws MalformedObjectNameException {
        ObjectName expectedObjectName = new ObjectName(
                "com.sap.slh.tax.maestro.tax.jmx:type=QuoteHttpInternalServerError,name=" + HOST_1);

        unregisterMBean(expectedObjectName);

        QuoteHttpInternalServerError bean1 = handler.getInternalServerErrorMBeanByHost(HOST_1);
        QuoteHttpInternalServerError bean2 = handler.getInternalServerErrorMBeanByHost(HOST_1);

        assertEquals(bean1, handler.internalServerErrorMBeans.get(expectedObjectName));
        assertEquals(bean2, handler.internalServerErrorMBeans.get(expectedObjectName));
        assertEquals(bean1, bean2);
        assertEquals(1, handler.internalServerErrorMBeans.size());
        assertEquals(0, handler.unavailabilityErrorMBeans.size());

    }

    @Test
    public void checkUnavailabilityErrorMBean() throws MalformedObjectNameException {
        ObjectName expectedObjectName = new ObjectName(
                "com.sap.slh.tax.maestro.tax.jmx:type=QuoteHttpUnavailabilityError,name=" + HOST_1);

        unregisterMBean(expectedObjectName);

        QuoteHttpUnavailabilityError bean = handler.getUnavailabilityErrorMBeanByHost(HOST_1);

        assertEquals(bean, handler.unavailabilityErrorMBeans.get(expectedObjectName));
        assertEquals(1, handler.unavailabilityErrorMBeans.size());
        assertEquals(0, handler.internalServerErrorMBeans.size());

    }

    @Test
    public void checkUnavailabilityErrorMBeanDifferentHosts() throws MalformedObjectNameException {
        ObjectName expectedObjectName1 = new ObjectName(
                "com.sap.slh.tax.maestro.tax.jmx:type=QuoteHttpUnavailabilityError,name=" + HOST_1);
        ObjectName expectedObjectName2 = new ObjectName(
                "com.sap.slh.tax.maestro.tax.jmx:type=QuoteHttpUnavailabilityError,name=" + HOST_2);

        unregisterMBean(expectedObjectName1);
        unregisterMBean(expectedObjectName2);

        QuoteHttpUnavailabilityError bean1 = handler.getUnavailabilityErrorMBeanByHost(HOST_1);
        QuoteHttpUnavailabilityError bean2 = handler.getUnavailabilityErrorMBeanByHost(HOST_2);

        assertEquals(bean1, handler.unavailabilityErrorMBeans.get(expectedObjectName1));
        assertEquals(bean2, handler.unavailabilityErrorMBeans.get(expectedObjectName2));
        assertEquals(2, handler.unavailabilityErrorMBeans.size());
        assertEquals(0, handler.internalServerErrorMBeans.size());

    }

    @Test
    public void checkUnavailabilityErrorMBeanSameHost() throws MalformedObjectNameException {
        ObjectName expectedObjectName = new ObjectName(
                "com.sap.slh.tax.maestro.tax.jmx:type=QuoteHttpUnavailabilityError,name=" + HOST_1);

        unregisterMBean(expectedObjectName);

        QuoteHttpUnavailabilityError bean1 = handler.getUnavailabilityErrorMBeanByHost(HOST_1);
        QuoteHttpUnavailabilityError bean2 = handler.getUnavailabilityErrorMBeanByHost(HOST_1);

        assertEquals(bean1, handler.unavailabilityErrorMBeans.get(expectedObjectName));
        assertEquals(bean2, handler.unavailabilityErrorMBeans.get(expectedObjectName));
        assertEquals(bean1, bean2);
        assertEquals(1, handler.unavailabilityErrorMBeans.size());
        assertEquals(0, handler.internalServerErrorMBeans.size());

    }

    private void unregisterMBean(ObjectName objectName) {
        try {
            ManagementFactory.getPlatformMBeanServer().unregisterMBean(objectName);
        } catch (Exception e) {
        }
    }

}
