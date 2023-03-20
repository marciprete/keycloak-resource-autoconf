package it.maconsulting.kcautoconf;

import it.maconsulting.kcautoconf.controller.ConfigurationExportController;
import it.maconsulting.kcautoconf.services.AutoconfigurationService;
import it.maconsulting.kcautoconf.services.KeycloakConfigurationGeneratorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
public class KeycloakSettingsControllerConfiguration {
    private final AutoconfigurationService autoconfigurationService;

    public KeycloakSettingsControllerConfiguration(AutoconfigurationService autoconfigurationService) {
        this.autoconfigurationService = autoconfigurationService;
        autoconfigurationService.enableConfigurationPage();
    }

    @Bean
    @Description("Thymeleaf Template Resolver")
    public SpringResourceTemplateResolver templateResolver(){
        // SpringResourceTemplateResolver automatically integrates with Spring's own
        // resource resolution infrastructure, which is highly recommended.
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(autoconfigurationService.getContext());
        templateResolver.setPrefix("classpath:/WEB-INF/views/");
        templateResolver.setSuffix(".html");
        // HTML is the default value, added here for the sake of clarity.
        templateResolver.setTemplateMode(TemplateMode.HTML);
        // Template cache is true by default. Set to false if you want
        // templates to be automatically updated when modified.
        templateResolver.setCacheable(true);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(){
        // SpringTemplateEngine automatically applies SpringStandardDialect and
        // enables Spring's own MessageSource message resolution mechanisms.
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        // Enabling the SpringEL compiler with Spring 4.2.4 or newer can
        // speed up execution in most scenarios, but might be incompatible
        // with specific cases when expressions in one template are reused
        // across different data types, so this flag is "false" by default
        // for safer backwards compatibility.
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }

    @Bean
    public ConfigurationExportController configurationExportController(KeycloakConfigurationGeneratorService service) {
        return new ConfigurationExportController(service);
    }

//    @Bean
//    @Order(50)
//    public KeycloakConfigurationGeneratorService keycloakConfigurationGeneratorService(KeycloakSpringBootProperties kcProperties) {
//        return new JsonKeycloakConfigurationGenerator(kcProperties);
//    }
}
