package it.maconsulting.kcautoconf.controller;

import it.maconsulting.kcautoconf.services.KeycloakConfigurationGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mac/configuration/export")
public class ConfigurationExportController {

    private final KeycloakConfigurationGeneratorService keycloakConfigurationGeneratorService;

    @Autowired
    public ConfigurationExportController(KeycloakConfigurationGeneratorService keycloakConfigurationGeneratorService) {
        this.keycloakConfigurationGeneratorService = keycloakConfigurationGeneratorService;
    }

    //    @Autowired
//    public ConfigurationExportController(ApplicationContext applicationContext, KeycloakConfigurationGeneratorService keycloakConfigurationGeneratorService) {
//        this.applicationContext = applicationContext;
//
////        this.keycloakConfigurationGeneratorService = beanFactory.getBean(KeycloakConfigurationGeneratorService.class);
//        this.keycloakConfigurationGeneratorService = keycloakConfigurationGeneratorService;
//    }


//    @Bean
//    KeycloakConfigurationGeneratorService keycloakConfigurationGeneratorService(KeycloakSpringBootProperties kcProperties) {
//        return new JsonKeycloakConfigurationGenerator(kcProperties);
//    }

    @GetMapping
    public String configure() {
        System.out.println("WELCOME MAN!");
        System.out.println("§§§§§§§§§§");
        System.out.println("§§§§§§§§§§");
        System.out.println(keycloakConfigurationGeneratorService.generateConfigurationAsJson());
        return "index";
    }
}
