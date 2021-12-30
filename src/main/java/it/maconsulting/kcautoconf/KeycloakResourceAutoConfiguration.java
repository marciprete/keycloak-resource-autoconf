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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class KeycloakResourceAutoConfiguration {
    private final static Logger log = LoggerFactory.getLogger(KeycloakResourceAutoConfiguration.class);

    @Autowired
    private ApplicationContext context;

    @Bean
    @Primary
    public KeycloakSpringBootProperties kcProperties() {
        log.info("Automatic resources and scopes configuration process started.");
        KeycloakSpringBootProperties keycloakSpringBootProperties = new KeycloakSpringBootProperties();
        PolicyEnforcerConfig policyEnforcerConfig = new PolicyEnforcerConfig();

        keycloakSpringBootProperties.setPolicyEnforcerConfig(policyEnforcerConfig);

        Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(RestController.class);
        beansWithAnnotation.forEach((name, bean) -> {
            final Class<?> targetClass = AopUtils.getTargetClass(bean);
            final RequestMapping requestMappingAnnotation = AnnotationUtils.getAnnotation(targetClass, RequestMapping.class);
            List<String> paths = getClassLevelAnnotatedPaths(requestMappingAnnotation);

            log.debug("Parsing controller {}", name);
            Arrays.asList(targetClass.getDeclaredMethods()).forEach(method -> {
                final Operation apiOperationAnnotation = AnnotationUtils.getAnnotation(method, Operation.class);
                final RequestMapping requestMappingOnMethod = AnnotationUtils.getAnnotation(method, RequestMapping.class);
                if (requestMappingOnMethod != null /*&& apiOperationAnnotation != null*/) {
                    log.trace("Found method: {}", method);
                    List<String> methodPaths = extractExtraPathsFromClassMethod(requestMappingOnMethod, method);
                    List<RequestMethod> httpMethods = Arrays.asList(requestMappingOnMethod.method());

                    paths.forEach(path -> {
                        httpMethods.forEach(verb -> {
                            methodPaths.forEach(methodPath -> {
                                String policyEnforcmentPath = buildHttpPath(path, methodPath);
                                log.debug("Configuring {} request for path: {}", verb, policyEnforcmentPath);

                                PolicyEnforcerConfig.PathConfig pathConfig = new PolicyEnforcerConfig.PathConfig();
                                pathConfig.setPath(policyEnforcmentPath);
                                PolicyEnforcerConfig.MethodConfig methodConfig = new PolicyEnforcerConfig.MethodConfig();
                                methodConfig.setMethod(verb.name());
                                List<String> scopeNames = new ArrayList<>();
                                if (apiOperationAnnotation != null) {
                                    List<String[]> scopes = Arrays.stream(apiOperationAnnotation.security()).map(SecurityRequirement::scopes).collect(Collectors.toList());
                                    scopes.forEach(a -> Arrays.stream(a).forEach(scope -> {
                                        if (!scope.isBlank()) {
                                            log.debug("Found authorization scope: {}", scope);
                                            scopeNames.add(scope);
                                        }
                                    }));
                                    methodConfig.setScopes(scopeNames);
                                }
                                pathConfig.getMethods().add(methodConfig);
                                keycloakSpringBootProperties.getPolicyEnforcerConfig().getPaths().add(pathConfig);
                            });
                        });
                    });
                }
            });
        });
        return keycloakSpringBootProperties;
    }

    private List<String> getClassLevelAnnotatedPaths(RequestMapping requestMappingAnnotation) {
        List<String> paths = new ArrayList<>();
        paths.add("");
        if (requestMappingAnnotation != null &&
                requestMappingAnnotation.path().length > 0) {
            paths = Arrays.asList(requestMappingAnnotation.path());
        }
        return paths;
    }

    private String buildHttpPath(String... paths) {
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            if (path.length() > 0) {
                sb.append(addLeadingSlash(path));
            }
        }
        String path = addLeadingSlash(sb.toString());
        return (path.length() > 1 && path.endsWith("/")) ? path.substring(0, path.lastIndexOf("/")) : path;
    }

    private List<String> extractExtraPathsFromClassMethod(RequestMapping annotation, Method method) {
        List<String> extraPaths = Arrays.asList("");
        RequestMapping merged = AnnotatedElementUtils.getMergedAnnotation(method, RequestMapping.class);
        if (merged != null &&
                merged.path() != null &&
                merged.path().length > 0) {
            extraPaths = Arrays.asList(merged.path());
        }
        return extraPaths;
    }

    private String addLeadingSlash(String path) {
        return !path.startsWith("/") ? "/" + path : path;
    }

}

