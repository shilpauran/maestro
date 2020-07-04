package com.sap.slh.tax.maestro.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class RabbitMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Value("${${rabbitmq.vcap.path:#{null}}:#{null}}")
    private String uri;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();

        if (this.uri != null) {
            URI rabbitUri = getRabbitURI();
            if (rabbitUri != null) {
                factory.setUri(rabbitUri);
            }
        }
        return factory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public Jackson2JsonMessageConverter msgConverter() {
        return new Jackson2JsonMessageConverter();
    }

    private URI getRabbitURI() {
        try {
            logger.debug("Loading RabbitMQ from user-provided URI: {}", this.uri);
            return new URI(this.uri);
        } catch (URISyntaxException e) {
            logger.error("Failure on load RabbitMQ URI.", e);
        }
        return null;
    }

}
