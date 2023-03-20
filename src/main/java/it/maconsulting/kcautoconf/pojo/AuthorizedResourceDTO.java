package it.maconsulting.kcautoconf.pojo;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class AuthorizedResourceDTO {

    private String name;
    private String displayName;
    private boolean ownedManagedAccess;
    private Set<String> authorizationScopes = new HashSet<>();
    private Set<String> uris = new HashSet<>();

}
