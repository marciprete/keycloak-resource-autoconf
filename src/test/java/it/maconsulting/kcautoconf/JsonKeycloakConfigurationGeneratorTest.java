package it.maconsulting.kcautoconf;

import it.maconsulting.kcautoconf.pojo.AuthorizationSettingsDTO;
import it.maconsulting.kcautoconf.services.JsonKeycloakConfigurationGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michele Arciprete
 * @since 0.3.0-SNAPSHOT
 */
@ExtendWith(MockitoExtension.class)
class JsonKeycloakConfigurationGeneratorTest {
    private JsonKeycloakConfigurationGenerator sut;


    @BeforeEach
    public void setup() {
        KeycloakSpringBootProperties keycloakSpringBootProperties = new KeycloakSpringBootProperties();
        PolicyEnforcerConfig policyEnforcerConfig = new PolicyEnforcerConfig();
        keycloakSpringBootProperties.setPolicyEnforcerConfig(policyEnforcerConfig);
        sut = new JsonKeycloakConfigurationGenerator(keycloakSpringBootProperties);
    }

    @Test
    void givenExistingPath_isNotAdded() {
        KeycloakSpringBootProperties keycloakSpringBootProperties = new KeycloakSpringBootProperties();
        PolicyEnforcerConfig policyEnforcerConfig = new PolicyEnforcerConfig();
        keycloakSpringBootProperties.setPolicyEnforcerConfig(policyEnforcerConfig);
        List<PolicyEnforcerConfig.PathConfig> pathConfigurations = new ArrayList<>();
        PolicyEnforcerConfig.PathConfig existingPath = new PolicyEnforcerConfig.PathConfig();
        existingPath.setPath("/my/path");
        existingPath.setId("123456");
        pathConfigurations.add(existingPath);
        policyEnforcerConfig.setPaths(pathConfigurations);
        sut = new JsonKeycloakConfigurationGenerator(keycloakSpringBootProperties);
        AuthorizationSettingsDTO settings = sut.generateConfigurationAsJson();
        Assertions.assertTrue(settings.getResources().isEmpty());

    }

    @Test
    void givenProperties_exportObjectIsCreated() {
        AuthorizationSettingsDTO settings = sut.generateConfigurationAsJson();
        Assertions.assertEquals("ENFORCING", settings.getPolicyEnforcementMode());
    }
}
