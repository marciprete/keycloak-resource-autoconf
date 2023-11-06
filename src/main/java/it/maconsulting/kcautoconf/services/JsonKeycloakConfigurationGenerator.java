package it.maconsulting.kcautoconf.services;

import it.maconsulting.kcautoconf.pojo.AuthorizationScopeDTO;
import it.maconsulting.kcautoconf.pojo.AuthorizationSettingsDTO;
import it.maconsulting.kcautoconf.pojo.AuthorizedResourceDTO;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JsonKeycloakConfigurationGenerator implements KeycloakConfigurationGeneratorService {

    private final KeycloakSpringBootProperties keycloakSpringBootProperties;

    @Autowired
    public JsonKeycloakConfigurationGenerator(KeycloakSpringBootProperties keycloakSpringBootProperties) {
        this.keycloakSpringBootProperties = keycloakSpringBootProperties;
    }

    @Override
    public AuthorizationSettingsDTO generateConfigurationAsJson() {
        AuthorizationSettingsDTO settings = new AuthorizationSettingsDTO();
        //DEFAULTS
        settings.setDecisionStrategy("AFFIRMATIVE");
        settings.setPolicyEnforcementMode("ENFORCING");

        List<PolicyEnforcerConfig.PathConfig> paths = keycloakSpringBootProperties.getPolicyEnforcerConfig().getPaths();
        List<AuthorizedResourceDTO> resourceDTOS = new ArrayList<>();
        paths.forEach(pathConfig -> {
            if(!PolicyEnforcerConfig.EnforcementMode.DISABLED.equals(pathConfig.getEnforcementMode()) &&
                    //skip existing pathconfigs
                    pathConfig.getId() == null) {
                AuthorizedResourceDTO resourceDTO = new AuthorizedResourceDTO();
                resourceDTO.setName(pathConfig.getName());
                resourceDTO.setDisplayName(pathConfig.getName());
                resourceDTO.getUris().add(pathConfig.getPath());
                Set<String> scopes = pathConfig.getMethods().stream().flatMap(methodConfig -> methodConfig.getScopes().stream()).collect(Collectors.toSet());
                scopes.forEach(scope -> {
                    if(!scope.isEmpty()) {
                        AuthorizationScopeDTO scopeDTO = new AuthorizationScopeDTO();
                        scopeDTO.setName(scope);
                        settings.getScopes().add(scopeDTO);
                        resourceDTO.getScopes().add(scopeDTO);
                    }
                });
                resourceDTOS.add(resourceDTO);
            }
        });

        settings.setResources(resourceDTOS);
        return settings;
    }
}
