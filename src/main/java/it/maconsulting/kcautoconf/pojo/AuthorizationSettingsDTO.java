package it.maconsulting.kcautoconf.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AuthorizationSettingsDTO {
    private boolean allowRemoteResourceManagement;
    private String policyEnforcementMode;
    private String decisionStrategy;

    private List<PolicyDTO> policies = new ArrayList<>();
    private List<AuthorizedResourceDTO> resources = new ArrayList<>();
    private List<AuthorizationScopeDTO> scopes = new ArrayList<>();

}
