A new version of this project is available here:
[spring-boot-keycloak-policy-enforcer](https://github.com/marciprete/spring-boot-keycloak-policy-enforcer)

It works with `keycloak-policy-enforcer` and `Spring Boot 3`

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


## Requirements
To take advantage of the autoconfiguration process, the resource server application must be a based on SpringBoot 2, 
and make use of the following:
  * Swagger annotations (v1.5 or v2)
  * RestController annotations
  * Keycloak spring-boot adapter

## Installation
Just add it as maven dependency:
```
<dependency>
  <groupId>it.maconsultingitalia.keycloak</groupId>
  <artifactId>keycloak-resource-autoconf</artifactId>
  <version>0.4.0</version>
</dependency>
```

## Features
| Library Version | Keycloak version  |
|-----------------|:-----------------:|
| 0.3.0           |     <=16.0.0      |
| 0.4.0           |     <=19.0.3      |


From Version 0.3.0 this library adds 2 different features:

  * Runtime Configuration
  * Keycloak Settings Generator

### Automatic Configuration
Any controller annotated with `@RestController` is scanned from the autoconfigurator, then all its methods are parsed too,
searching for any `@RequestMapping` alias.
The found endpoints are added to the `KeycloakSpringBootProperties`, in the policyEnforcementConfigurations.
Since the bean is lazy loaded, the configurations in the application.properties or application.yml files are kept.
The authorization scopes defined within the `@ApiOperation` or `@Operation` annotations are added too, according to the http verb of the 
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

##### Auth Scope Based Controller (Swagger v3 / Annotations v2)
```
@RestController
public Class AuthzController {

    @GetMapping
    @Operation(
            summary = "Read my awesome entity",
            operationId = "Entity Getter",
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

##### Auth Scope Based Controller (Swagger v2 / Annotations v1.5)
```
@RestController
public Class AuthzController {

    @GetMapping
    @ApiOperation(
            nickname = "Entity Reader",
            value = "Read my awesome entity",
            authorizations = {
                    @Authorization(
                            value = "get",
                            scopes = {@AuthorizationScope(scope = "entity:read", description = "read entity")})
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
> [!WARNING]<br>
> **If you are using nickname**: Be very careful, because keycloak adapter will **only** search for resources 
> with this value as resource name, and it will **skip the search by path**!

## Keycloak Settings Generator

The `@EnableKeycloakConfigurationExportController` annotation enables an endpoint with a simple Thymeleaf page
that prints on screen the Json Settings.
The service behind this controller uses the keycloak configuration to generate the script that can be imported in 
Keycloak, whose structure is the following:
```
{
  "allowRemoteResourceManagement": false,
  "policyEnforcementMode": "ENFORCING",
  "decisionStrategy": "AFFIRMATIVE",
  "policies": [],
  "resources": [
    {
      "name": "ResourceName",
      "ownerManagedAccess": false,
      "displayName": "ResourceName",
      "uris": [
        "/path/as/defined/in/controller"
      ],
      "scopes": [
        {
          "name": "resource:operation"
        }
      ]
    }
  ],
  "scopes": [
    {
      "name": "resource:operation"
    }
  ]
}
```
At the moment, the export functions creates a file where the global decision strategy is always `AFFIRMATIVE`,
 and no policies are defined.

All the resources and the Authorization Scopes can be imported from the Keycloak's console.

  * _ResourceName_ and _DisplayName_ are the same, as defined in the Api (#ApiOperation.nickname or #Operation.operationId).
  If the field is not present, the method name will be used in place.
  * OwnerManagedAccess is false by default.

**NOTE**: Existing resources are not added to the export file. That is, if a resource uri is present in the keycloak client, 
it will be skipped

### Configuration export controller 
By default, the controller is available under `/mac/configuration/export` but it can be changed via `application.properties`:
```
kcautoconf.export-path=/my/custom/export/path
```

This endpoint will be available to all the authenticated user. For security reasons, it's strongly recommended to disable
the Json Configuration export in production. 

From version 0.4.0 on, 2 new configuration parameters have been added:

* kcautoconf.protect-export-path (`boolean`, default to `false`)
* kcautoconf.export-path-access-scope (`String`, default to `configuration:expport`, only meaningful when `protect-export-path` is ste to `true`)

By setting these values, the autoconfigurator assigns the `export-path-access-scope` to the configuration endpoint, and enables the policy enforcement.


## Known limitations
At the moment, the endpoints are added only if the methods are mapped with `@GetMapping`, `@PostMapping`, `@PutMapping` etc.
If the method is annotated via `@RequestMapping`, then the http verb is not inferred thus the endpoint is not added. 
   
