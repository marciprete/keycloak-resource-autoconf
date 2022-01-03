# Keycloak Resource Autoconfigurator for Spring Boot 2 

[Keycloak](https://www.keycloak.org) is an Open Source Identity and Access Management solution for modern Applications and Services.

When it's used as an **authorization server**, it can be necessary to configure the **policy enforcement** in a configuration file, like this:
```
keycloak.policy-enforcer-config.enforcement-mode=PERMISSIVE
keycloak.policy-enforcer-config.paths[0].name=Car Resource
keycloak.policy-enforcer-config.paths[0].path=/cars/create
keycloak.policy-enforcer-config.paths[0].scopes[0]=car:create

keycloak.policy-enforcer-config.paths[1].path=/cars/{id}
keycloak.policy-enforcer-config.paths[1].methods[0].method=GET
keycloak.policy-enforcer-config.paths[1].methods[0].scopes[0]=car:view-detail
keycloak.policy-enforcer-config.paths[1].methods[1].method=DELETE
keycloak.policy-enforcer-config.paths[1].methods[1].scopes[0]=car:delete
```

Anyway, this approach can lead to a lot of configuration, possible duplications (especially when using api-documentation annotations)
and possibly errors.
  
The aim of this project is to provide an automatic configuration process, based on the Swagger api annotations and on the 
 Spring web annotations, to have one single configuration point.

The current version supports both Swagger 2 and Swagger 3


## Requirements
To take advantage of the autoconfiguration process, the resource server application must be a based on SpringBoot 2, 
and make use of the following:
* Swagger annotations
* RestController annotations
* Keycloak spring-boot adapter

## Installation
Just add it as maven dependency:
```
<dependency>
  <groupId>it.maconsultingitalia.keycloak</groupId>
  <artifactId>keycloak-resource-autoconf</artifactId>
  <version>0.1.0</version>
</dependency>
```

## How it works
Any controller annotated with `@RestController` is scanned from the autoconfigurator, then all its methods are parsed too,
searching for any `@RequestMapping` alias.
The found endpoints are added to the `KeycloakSpringBootProperties`, in the policyEnforcementConfigurations.
Since the bean is lazy loaded, the configurations in the application.properties or application.yml files are kept.
The authorization scopes defined within the `@ApiOperation` annotation are added too, according to the http verb of the 
annotated method. This means that if the rest controllers are correctly annotated with swagger, no extra configuration is required.

## Examples
##### SimplestRestController
```
@RestController
public Class SimplestController {

    @GetMapping
    public ResponseEntity<String> getString() { ... }

}
```
In the simplest case the autoconfigurer will create the single endpoint `/` with no extra information.

##### Multiple Mappings Controller
```
@RestController
@RequestMapping("/foo", "bar")
public Class MultipleMappingController {

    @GetMapping
    public ResponseEntity<String> getString() { ... }

}
```
Regardless of the trailing slash in the `@RequestMapping`, the configurator will add 2 endpoints in the policy enforcement:
`/foo` and `/bar`
The same happens if the method mapping has more than one path.

##### Auth Scope Based Controller with Swagger 2
```
@RestController
public Class AuthzController {

    @GetMapping
    @ApiOperation(
            nickname = "getEntity",
            value = "Read my awesome entity",
            authorizations = {
                    @Authorization(
                            value = "get",
                            scopes = {@AuthorizationScope(scope = "entity:read", description = "read entity")})
            })
    public ResponseEntity<String> getString() { ... }

}
##### Auth Scope Based Controller with Swagger 3
```
@RestController
public Class AuthzController {

    @GetMapping
    @Operation(
            summary = "Read my awesome entity",
            security = {
                    @SecurityRequirement(
                            name = "get",
                            scopes = "entity:read")
            })
    public ResponseEntity<String> getString() { ... }

}
```
This example will produce the equivalent of the yaml
```
keycloak:
  ...
  policy-enforcer-config:
      enforcement-mode: ENFORCING
      paths:
        - path: /
          methods:
            - method: GET
              scopes:
                - entity:read
```

## Known limitations
At the moment, the endpoints are added only if the methods are mapped with @GetMapping, @PostMapping, @PutMapping etc.
If the method is annotated via @RequestMapping, then the http verb is not inferred thus the endpoint is not added. 
   
