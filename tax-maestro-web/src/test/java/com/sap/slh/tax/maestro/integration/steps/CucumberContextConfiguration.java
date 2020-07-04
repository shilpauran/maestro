package com.sap.slh.tax.maestro.integration.steps;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

import cucumber.api.java.Before;

@SpringBootTest
@AutoConfigureWebTestClient(timeout="2500000")
public class CucumberContextConfiguration  {

    @MockBean
    RabbitTemplate rabbitTemplate;
    
    @MockBean
    WebClient partnerWebClient;
    
    @Before
    public void setup_cucumber_spring_context(){
        // Dummy method so cucumber will recognize this class as glue
        // and use its context configuration.
    }
}
