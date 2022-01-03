package it.maconsulting.kcautoconf;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.annotation.MergedAnnotations;

import java.util.Optional;

/**
 * @author Michele Arciprete
 * @since 0.0.1-SNAPSHOT
 */
public class SecuredOperationFactory {
    public Optional<SecuredOperation> getSecuredAnnotation(MergedAnnotations annotations) {
        if(annotations.isPresent(Operation.class)) {
            return Optional.of(new Swagger3SecuredOperation(annotations.get(Operation.class).synthesize()));
        }
        if(annotations.isPresent(ApiOperation.class)) {
            return Optional.of(new Swagger2SecuredOperation(annotations.get(ApiOperation.class).synthesize()));
        }
     return Optional.empty();
    }
}
