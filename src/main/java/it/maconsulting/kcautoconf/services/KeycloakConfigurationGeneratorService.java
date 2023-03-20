package it.maconsulting.kcautoconf.services;

import it.maconsulting.kcautoconf.pojo.AuthorizationSettingsDTO;

public interface KeycloakConfigurationGeneratorService {

    AuthorizationSettingsDTO generateConfigurationAsJson();
}
