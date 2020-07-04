package com.sap.slh.tax.maestro.api;

import java.util.function.Predicate;

import org.junit.Test;

import com.jparams.verifier.tostring.ToStringVerifier;

public class ToStringTest {
    @Test
    public void testToString() {
        Predicate<Class<?>> predicate = (clazz) -> {
            boolean shouldTestClass;

            // Remove test classes
            shouldTestClass = !clazz.getName().endsWith("Test");

            // Remove inner builder classes
            shouldTestClass = shouldTestClass && !clazz.getName().endsWith("$Builder");

            // Remove inner class (builder)
            shouldTestClass = shouldTestClass && !clazz.getName().endsWith("$1");

            // Remove unsupported class
            /*
            shouldTestClass = shouldTestClass && !clazz.getName().equals("");
            */

            return shouldTestClass;
        };

        ToStringVerifier.forPackage("com.sap.slh.tax.maestro.api.v0.schema", false, predicate)
            .verify();

        ToStringVerifier.forPackage("com.sap.slh.tax.maestro.api.v1.schema", false, predicate)
            .verify();
    }
}
