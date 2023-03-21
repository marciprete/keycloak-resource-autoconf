package it.maconsulting.kcautoconf;

import it.maconsulting.kcautoconf.pojo.AuthorizationSettingsDTO;
import it.maconsulting.kcautoconf.pojo.AuthorizedResourceDTO;
import it.maconsulting.kcautoconf.services.JsonKeycloakConfigurationGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Michele Arciprete
 * @since 0.3.0-SNAPSHOT
 */
@ExtendWith(MockitoExtension.class)
class JsonKeycloakConfigurationGeneratorTest {
    private JsonKeycloakConfigurationGenerator sut;

    @Mock
    private KeycloakSpringBootProperties keycloakSpringBootProperties;

    @BeforeEach
    public void setup() {
//        KeycloakSpringBootProperties keycloakSpringBootProperties = new KeycloakSpringBootProperties();
//        PolicyEnforcerConfig policyEnforcerConfig = new PolicyEnforcerConfig();
//        keycloakSpringBootProperties.setPolicyEnforcerConfig(policyEnforcerConfig);
        sut = new JsonKeycloakConfigurationGenerator(keycloakSpringBootProperties);
    }

    @Test
    void givenExistingPath_isNotAdded() {
        PolicyEnforcerConfig policyEnforcerConfig = new PolicyEnforcerConfig();
        List<PolicyEnforcerConfig.PathConfig> pathConfigurations = new ArrayList<>();
        PolicyEnforcerConfig.PathConfig existingPath = new PolicyEnforcerConfig.PathConfig();
        existingPath.setPath("/my/path");
        existingPath.setId("123456");
        pathConfigurations.add(existingPath);
        policyEnforcerConfig.setPaths(pathConfigurations);

        Mockito.when(keycloakSpringBootProperties.getPolicyEnforcerConfig()).thenReturn(policyEnforcerConfig);

        AuthorizationSettingsDTO settings = sut.generateConfigurationAsJson();
        Assertions.assertTrue(settings.getResources().isEmpty());

    }

    @Test
    void givenProperties_exportObjectIsCreated() {
        PolicyEnforcerConfig policyEnforcerConfig = new PolicyEnforcerConfig();
        List<PolicyEnforcerConfig.PathConfig> pathConfigurations = new ArrayList<>();
        PolicyEnforcerConfig.PathConfig existingPath = new PolicyEnforcerConfig.PathConfig();
        existingPath.setPath("/my/path");
        existingPath.setName("Add User");
        existingPath.setEnforcementMode(PolicyEnforcerConfig.EnforcementMode.ENFORCING);
        PolicyEnforcerConfig.MethodConfig myMethod = new PolicyEnforcerConfig.MethodConfig();
        myMethod.setMethod("POST");
        myMethod.setScopes(Arrays.asList("user:add"));

        existingPath.getMethods().add(myMethod);
        pathConfigurations.add(existingPath);
        policyEnforcerConfig.setPaths(pathConfigurations);

        Mockito.when(keycloakSpringBootProperties.getPolicyEnforcerConfig()).thenReturn(policyEnforcerConfig);
        AuthorizationSettingsDTO settings = sut.generateConfigurationAsJson();
        Assertions.assertEquals("ENFORCING", settings.getPolicyEnforcementMode());
        Assertions.assertFalse(settings.isAllowRemoteResourceManagement());
        Assertions.assertEquals("AFFIRMATIVE", settings.getDecisionStrategy());
        Assertions.assertTrue(settings.getPolicies().isEmpty());
        List<AuthorizedResourceDTO> resources = settings.getResources();
        Assertions.assertFalse(resources.isEmpty());
        AuthorizedResourceDTO resource = resources.get(0);

        Assertions.assertEquals("Add User", resource.getName());
        Assertions.assertEquals("Add User", resource.getDisplayName());
        Assertions.assertTrue(resource.getUris().contains("/my/path"));
        Assertions.assertEquals("user:add", resource.getScopes().iterator().next().getName());

    }
}
