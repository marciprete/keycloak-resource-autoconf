package it.maconsulting.kcautoconf.services;

import it.maconsulting.kcautoconf.services.KeycloakConfigurationGeneratorService;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JsonKeycloakConfigurationGenerator implements KeycloakConfigurationGeneratorService {

    //    @Autowired
//    @Qualifier("internalKcProperties")
    private final KeycloakSpringBootProperties kcProperties;

    @Autowired
    public JsonKeycloakConfigurationGenerator(KeycloakSpringBootProperties kcProperties) {
        this.kcProperties = kcProperties;
    }

    @Override
    public String generateConfigurationAsJson() {
        List<PolicyEnforcerConfig.PathConfig> paths = kcProperties.getPolicyEnforcerConfig().getPaths();
        paths.forEach(pathConfig -> {
            System.out.println(pathConfig);
            pathConfig.getScopes().forEach(System.out::println);

        });


//        Reflections reflections = new Reflections(Exporter.class,
//                Scanners.MethodsAnnotated);
//
////        Reflections reflections = new Reflections(new ConfigurationBuilder()
////                .setUrls(ClasspathHelper.forPackage("com.baeldung.reflections"))
////                .setScanners(Scanners.MethodsAnnotated));
//
//
//        Set<Method> methods = reflections.getMethodsAnnotatedWith(ApiOperation.class);
//
//        Set<AuthorizationScope> authorizationScopeSet = methods.stream().map(m -> m.getAnnotation(ApiOperation.class).authorizations())
//                .flatMap(Stream::of)
//                .map(Authorization::scopes)
//                .flatMap(Stream::of)
//                .collect(Collectors.toSet());
//
//        List<Map<String, Object>> permissions = new ArrayList<>();
//
//        List<Map<String, String>> authorizationScopes = new ArrayList<>();
//
//        authorizationScopeSet.forEach(as -> {
//            if (!"".equals(as.scope())) {
//                Map<String, String> authScope = new HashMap<>();
//                authScope.put("name", as.scope());
//                authScope.put("displayName", as.description());
//                authorizationScopes.add(authScope);
//
//                Map<String, Object> permission = new HashMap<>();
//                permission.put("name", as.description() + " Permission");
//                permission.put("type","scope");
//                permission.put("logic","POSITIVE");
//                permission.put("decisionStrategy","UNANIMOUS");
//                Map config = new HashMap<String, String>();
//                config.put("scopes",as.scope());
//                config.put("applyPolicies", "\"Only Doctor Policy\"");
//                permission.put("config", config);
//                permissions.add(permission);
//            }
//        });
//        System.out.println("authorizationScopeSet:");
//        System.out.println(authorizationScopeSet);
//        System.out.println("---------------------");
//        ObjectMapper om = new ObjectMapper();
//
//        try {
//            System.out.println("authorizationScopes:");
//            System.out.println(om.writeValueAsString(authorizationScopes));
//            System.out.println("---");
//            System.out.println("permissions:");
//            System.out.println(om.writeValueAsString(permissions));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
        return "GIAO";
    }
}
