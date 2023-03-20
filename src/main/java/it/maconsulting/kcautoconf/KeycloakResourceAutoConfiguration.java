/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.maconsulting.kcautoconf;

import it.maconsulting.kcautoconf.services.AutoconfigurationService;
import it.maconsulting.kcautoconf.services.SwaggerOperationService;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * This is the implementation of the autoconfig annotation.
 * It integrates the {@link org.keycloak.adapters.springboot.KeycloakSpringBootProperties} and performs the automatic
 * resources and scopes configuration.<br>
 * The process is additive and not destructive. It means that if a policy-enforcement configuration section is present
 * in the application.properties or application.yaml file, it is kept.
 *
 * @author Michele Arciprete
 * @since 1.0-SNAPSHOT
 */
@Configuration
@ComponentScan(basePackages = {"it.maconsulting.kcautoconf.*"})
public class KeycloakResourceAutoConfiguration {
    private final static Logger log = LoggerFactory.getLogger(KeycloakResourceAutoConfiguration.class);

    private final ApplicationContext context;

    private final List<SwaggerOperationService> swaggerOperationServices;

    private final AutoconfigurationService autoconfigurationService;

    @Autowired
    public KeycloakResourceAutoConfiguration(AutoconfigurationService autoconfigurationService) {
        this.autoconfigurationService = autoconfigurationService;
        this.context = autoconfigurationService.getContext();
        this.swaggerOperationServices = autoconfigurationService.getSwaggerOperationServices();
        autoconfigurationService.updateKeycloakConfiguration();
    }

    public KeycloakSpringBootProperties kcProperties() {
        return autoconfigurationService.getKeycloakSpringBootProperties();
    }

}

