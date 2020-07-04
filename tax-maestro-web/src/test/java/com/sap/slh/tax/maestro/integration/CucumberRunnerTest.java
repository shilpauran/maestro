package com.sap.slh.tax.maestro.integration;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        glue = {"com.sap.slh.tax.maestro.integration.steps"},
        plugin = {"pretty", "json:target/cucumber.json"})
public class CucumberRunnerTest {

}
