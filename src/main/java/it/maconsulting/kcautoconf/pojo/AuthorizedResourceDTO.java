package it.maconsulting.kcautoconf.pojo;

import lombok.Data;

import java.util.List;

@Data
public class AuthorizedResourceDTO {

    private String name;
    private String displayName;
    private boolean ownedManagedAccess;
    private List<String> authorizationScopes;
    private List<String> uris;

}
