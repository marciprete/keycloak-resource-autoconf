package it.maconsulting.kcautoconf;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Michele Arciprete
 * @since 0.0.1-SNAPSHOT
 */

public class Swagger2SecuredOperation implements SecuredOperation {
    private final static Logger log = LoggerFactory.getLogger(Swagger2SecuredOperation.class);
    private final ApiOperation operation;

    public Swagger2SecuredOperation(ApiOperation operation) {
        this.operation = operation;
    }

    @Override
    public List<String> getScopes() {
        List<String> scopeNames = new ArrayList<>();
        if (operation != null) {
            List<AuthorizationScope[]> scopes = Arrays.stream(operation.authorizations()).map(Authorization::scopes).collect(Collectors.toList());
            scopes.forEach(a -> Arrays.stream(a).forEach(scope -> {
                if (!scope.scope().isBlank()) {
                    log.debug("Found authorization scope: {}", scope.scope());
                    scopeNames.add(scope.scope());
                }
            }));
        }
        return scopeNames;
    }
}
