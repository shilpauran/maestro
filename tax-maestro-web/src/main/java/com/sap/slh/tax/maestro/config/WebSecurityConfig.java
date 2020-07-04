package com.sap.slh.tax.maestro.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.sap.cloud.security.xsuaa.XsuaaServiceConfigurationDefault;
import com.sap.cloud.security.xsuaa.XsuaaServicePropertySourceFactory;
import com.sap.cloud.security.xsuaa.token.ReactiveTokenAuthenticationConverter;
import com.sap.cloud.security.xsuaa.token.authentication.XsuaaJwtDecoderBuilder;

import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@PropertySource(factory = XsuaaServicePropertySourceFactory.class, value = { "classpath:" })
public class WebSecurityConfig {
	@Autowired
	XsuaaServiceConfigurationDefault xsuaaServiceConfiguration;

	@Bean
	@Profile("!NoSecurity")
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		http.formLogin().disable();
		http.csrf().disable();
		http.httpBasic().disable();
		http.logout().disable();
		http.cors();

		http.authorizeExchange().anyExchange().authenticated().and().oauth2ResourceServer().jwt()
				.jwtAuthenticationConverter(new ReactiveTokenAuthenticationConverter(xsuaaServiceConfiguration))
				.jwtDecoder(new XsuaaJwtDecoderBuilder(xsuaaServiceConfiguration).buildAsReactive());

		return http.build();
	}

	@Bean
	@Profile("NoSecurity")
	public SecurityWebFilterChain securityWebFilterChainNoSecurity(ServerHttpSecurity http) {
		http.authorizeExchange().anyExchange().permitAll();

		return http.build();
	}

	/** Load Dummy instantiation instead of UserDetailsServiceAutoConfiguration */
	@Bean
	ReactiveUserDetailsService userDetailsService() {
		return new ReactiveUserDetailsService() {
			@Override
			public Mono<UserDetails> findByUsername(String username) {
				return Mono.empty();
			}
		};
	}

	@Bean
	XsuaaServiceConfigurationDefault xsuaaConfig() {
		return new XsuaaServiceConfigurationDefault();
	}
}
