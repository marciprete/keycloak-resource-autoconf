package it.maconsulting.kcautoconf.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.maconsulting.kcautoconf.pojo.AuthorizationSettingsDTO;
import it.maconsulting.kcautoconf.services.KeycloakConfigurationGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String configure(Model model) {
        AuthorizationSettingsDTO paths = keycloakConfigurationGeneratorService.generateConfigurationAsJson();


        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.disableHtmlEscaping()
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(paths);
        model.addAttribute("paths", json);
        return "index";
    }
}
