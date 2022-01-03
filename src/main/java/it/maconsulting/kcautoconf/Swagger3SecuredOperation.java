package it.maconsulting.kcautoconf;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
public class Swagger3SecuredOperation implements SecuredOperation {
    private final static Logger log = LoggerFactory.getLogger(Swagger3SecuredOperation.class);

    private final Operation operation;

    public Swagger3SecuredOperation(Operation operation) {
        this.operation = operation;
    }
    @Override
    public List<String> getScopes() {
        List<String> scopeNames = new ArrayList<>();
        if(operation != null) {
            List<String[]> scopes = Arrays.stream(operation.security()).map(SecurityRequirement::scopes).collect(Collectors.toList());
            scopes.forEach(a -> Arrays.stream(a).forEach(scope -> {
                if (!scope.isBlank()) {
                    log.debug("Found authorization scope: {}", scope);
                    scopeNames.add(scope);
                }
            }));
        }
        return scopeNames;
    }
}
