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

import it.maconsulting.kcautoconf.fixtures.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michele Arciprete
 * @since 1.0-SNAPSHOT
 */
@ExtendWith(MockitoExtension.class)
class KeycloakResourceAutoConfigurationTest {

    @Spy
    private ApplicationContext context;

    @InjectMocks
    private KeycloakResourceAutoConfiguration sut;

    @Test
    void givenControllerWithSwagger3AuthzScopes_resourcesAreCreated() throws Exception {
        Map<String, Object> beansWithAnnotation = new HashMap<>();
        beansWithAnnotation.put("ControllerWithSwagger3AuthzScopes", new ControllerWithSwagger3AuthzScopes());

        Mockito.when(context.getBeansWithAnnotation(Mockito.any())).thenReturn(beansWithAnnotation);

        KeycloakSpringBootProperties properties = sut.kcProperties();
        Assertions.assertNotNull(properties);
        Assertions.assertNotNull(properties.getPolicyEnforcerConfig());
        List<PolicyEnforcerConfig.PathConfig> paths = properties.getPolicyEnforcerConfig().getPaths();
        Assertions.assertNotNull(paths);
        Assertions.assertFalse(paths.isEmpty());
        Assertions.assertEquals(1, paths.size());
        paths.forEach(path -> {
            Assertions.assertEquals("/authorized", path.getPath());

            Assertions.assertEquals(1, path.getMethods().get(0).getScopes().size());
            Assertions.assertEquals("entity:read", path.getMethods().get(0).getScopes().get(0));
        });
    }

    @Test
    void givenControllerWithSwagger2AuthzScopes_resourcesAreCreated() throws Exception {
        Map<String, Object> beansWithAnnotation = new HashMap<>();
        beansWithAnnotation.put("ControllerWithSwagger2AuthzScopes", new ControllerWithSwagger2AuthzScopes());

        Mockito.when(context.getBeansWithAnnotation(Mockito.any())).thenReturn(beansWithAnnotation);

        KeycloakSpringBootProperties properties = sut.kcProperties();
        Assertions.assertNotNull(properties);
        Assertions.assertNotNull(properties.getPolicyEnforcerConfig());
        List<PolicyEnforcerConfig.PathConfig> paths = properties.getPolicyEnforcerConfig().getPaths();
        Assertions.assertNotNull(paths);
        Assertions.assertFalse(paths.isEmpty());
        Assertions.assertEquals(1, paths.size());
        paths.forEach(path -> {
            Assertions.assertEquals("/authorized", path.getPath());

            Assertions.assertEquals(1, path.getMethods().get(0).getScopes().size());
            Assertions.assertEquals("entity:read", path.getMethods().get(0).getScopes().get(0));
        });
    }

    @Test
    void givenAnnotatedController_resourcesAreCreated() throws Exception {
        Map<String, Object> beansWithAnnotation = new HashMap<>();
        beansWithAnnotation.put("ControllerWithSingleRequestMapping", new ControllerWithSingleRequestMapping());

        Mockito.when(context.getBeansWithAnnotation(Mockito.any())).thenReturn(beansWithAnnotation);

        KeycloakSpringBootProperties properties = sut.kcProperties();
        Assertions.assertNotNull(properties);
        Assertions.assertNotNull(properties.getPolicyEnforcerConfig());
        List<PolicyEnforcerConfig.PathConfig> paths = properties.getPolicyEnforcerConfig().getPaths();
        Assertions.assertNotNull(paths);
        Assertions.assertFalse(paths.isEmpty());
        Assertions.assertEquals(5, paths.size());
        paths.forEach(path -> {
            Assertions.assertEquals("/myAwesomeMapping", path.getPath());
        });
    }

    @Test
    void givenControllerWithMultiplePaths_resourcesAreCreated() throws Exception {
        Map<String, Object> beansWithAnnotation = new HashMap<>();
        beansWithAnnotation.put("ControllerWithMultiplePathsInRequestMapping", new ControllerWithMultiplePathsInRequestMapping());

        Mockito.when(context.getBeansWithAnnotation(Mockito.any())).thenReturn(beansWithAnnotation);

        KeycloakSpringBootProperties properties = sut.kcProperties();
        Assertions.assertNotNull(properties);
        Assertions.assertNotNull(properties.getPolicyEnforcerConfig());
        List<PolicyEnforcerConfig.PathConfig> paths = properties.getPolicyEnforcerConfig().getPaths();
        Assertions.assertNotNull(paths);
        Assertions.assertFalse(paths.isEmpty());
        Assertions.assertEquals(3, paths.size());
        Assertions.assertEquals("/foo", paths.get(1).getPath());
        Assertions.assertEquals("/bar", paths.get(2).getPath());
        Assertions.assertEquals("/myAwesomeMapping", paths.get(0).getPath());
    }

    @Test
    void givenAMethodWthMultiplePaths_withoutRequestMapping_resourcesAreCreated() throws Exception {
        Map<String, Object> beansWithAnnotation = new HashMap<>();
        beansWithAnnotation.put("ControllerWithMultiplePathOnMethod", new ControllerWithMultiplePathOnMethod());

        Mockito.when(context.getBeansWithAnnotation(Mockito.any())).thenReturn(beansWithAnnotation);

        KeycloakSpringBootProperties properties = sut.kcProperties();
        Assertions.assertNotNull(properties);
        Assertions.assertNotNull(properties.getPolicyEnforcerConfig());
        List<PolicyEnforcerConfig.PathConfig> paths = properties.getPolicyEnforcerConfig().getPaths();
        Assertions.assertNotNull(paths);
        Assertions.assertFalse(paths.isEmpty());
        Assertions.assertEquals(2, paths.size());
        Assertions.assertEquals("/foo", paths.get(0).getPath());
        Assertions.assertEquals("/bar", paths.get(1).getPath());
    }

    @Test
    void givenAnnotatedController_withoutRequestMapping_resourcesAreCreated() throws Exception {
        Map<String, Object> beansWithAnnotation = new HashMap<>();
        beansWithAnnotation.put("ControllerWithoutRequestMapping", new ControllerWithoutRequestMapping());

        Mockito.when(context.getBeansWithAnnotation(Mockito.any())).thenReturn(beansWithAnnotation);

        KeycloakSpringBootProperties properties = sut.kcProperties();
        Assertions.assertNotNull(properties);
        Assertions.assertNotNull(properties.getPolicyEnforcerConfig());
        List<PolicyEnforcerConfig.PathConfig> paths = properties.getPolicyEnforcerConfig().getPaths();
        Assertions.assertNotNull(paths);
        Assertions.assertFalse(paths.isEmpty());
        Assertions.assertEquals(5, paths.size());
        paths.forEach(path -> {
            Assertions.assertEquals("/", path.getPath());
        });
    }




}
