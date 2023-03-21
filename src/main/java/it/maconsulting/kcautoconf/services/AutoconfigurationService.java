package it.maconsulting.kcautoconf.services;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;


@Slf4j
@Service
public class AutoconfigurationService {

    private final ApplicationContext context;
    private final KeycloakSpringBootProperties keycloakSpringBootProperties;
    private final List<SwaggerOperationService> swaggerOperationServices;

    @Value("${kcautoconf.export-path:/mac/configuration/export}")
    private String exportPath;

    @Autowired
    public AutoconfigurationService(ApplicationContext context, KeycloakSpringBootProperties keycloakSpringBootProperties, List<SwaggerOperationService> swaggerOperationServices) {
        this.context = context;
        this.keycloakSpringBootProperties = keycloakSpringBootProperties;
        this.swaggerOperationServices = swaggerOperationServices;
    }

    public void updateKeycloakConfiguration() {
        log.info("Automatic resources and scopes configuration process started.");
        keycloakSpringBootProperties.getPolicyEnforcerConfig().getPaths().addAll(getPathConfigurations());
    }

    public ApplicationContext getContext() {
        return context;
    }

    public List<SwaggerOperationService> getSwaggerOperationServices() {
        return swaggerOperationServices;
    }

    public KeycloakSpringBootProperties getKeycloakSpringBootProperties() {
        return keycloakSpringBootProperties;
    }

    private List<PolicyEnforcerConfig.PathConfig> getPathConfigurations() {
        log.info("Automatic resources and scopes configuration process started.");
        List<PolicyEnforcerConfig.PathConfig> pathConfigList = new ArrayList<>();
        Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(RestController.class);

        beansWithAnnotation.forEach((name, bean) -> {
            final Class<?> targetClass = AopUtils.getTargetClass(bean);
            final RequestMapping requestMappingAnnotation = AnnotationUtils.getAnnotation(targetClass, RequestMapping.class);
            List<String> paths = getClassLevelAnnotatedPaths(requestMappingAnnotation);

            log.debug("Parsing controller {}", name);
            Arrays.asList(targetClass.getDeclaredMethods()).forEach(method -> {
                final RequestMapping requestMappingOnMethod = AnnotationUtils.getAnnotation(method, RequestMapping.class);
                if (requestMappingOnMethod != null) {
                    log.trace("Found method: {}", method);
                    List<String> methodPaths = extractExtraPathsFromClassMethod(requestMappingOnMethod, method);
                    List<RequestMethod> httpMethods = Arrays.asList(requestMappingOnMethod.method());

                    paths.forEach(path -> {
                        httpMethods.forEach(verb -> {
                            methodPaths.forEach(methodPath -> {
                                String policyEnforcementPath = buildHttpPath(path, methodPath);
                                log.debug("Configuring {} request for path: {}", verb, policyEnforcementPath);

                                PolicyEnforcerConfig.PathConfig pathConfig = new PolicyEnforcerConfig.PathConfig();
                                pathConfig.setPath(policyEnforcementPath);
                                PolicyEnforcerConfig.MethodConfig methodConfig = new PolicyEnforcerConfig.MethodConfig();
                                methodConfig.setMethod(verb.name());
                                Optional<SwaggerOperationService> operationServiceOption = swaggerOperationServices.stream().findFirst();
                                if(operationServiceOption.isPresent()) {
                                    SwaggerOperationService swaggerOperationService = operationServiceOption.get();
                                    List<String> scopes = swaggerOperationService.getScopes(method);
                                    if (!scopes.isEmpty()) {
                                        scopes.stream().filter(Predicate.not(String::isBlank))
                                                .forEach(scope -> log.debug("Found authorization scope: {}", scope));
                                        methodConfig.setScopes(scopes);
                                    }
                                    pathConfig.getMethods().add(methodConfig);
                                    pathConfig.setName(swaggerOperationService.getName(method));
                                }
                                pathConfigList.add(pathConfig);
                            });
                        });
                    });
                }
            });
        });
        return pathConfigList;
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

    public void enableConfigurationPage() {
        PolicyEnforcerConfig.PathConfig configurationPath = new PolicyEnforcerConfig.PathConfig();
        configurationPath.setPath(exportPath);
        configurationPath.setEnforcementMode(PolicyEnforcerConfig.EnforcementMode.DISABLED);
        getKeycloakSpringBootProperties().getPolicyEnforcerConfig().getPaths().add(configurationPath);

    }
}
