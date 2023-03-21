package it.maconsulting.kcautoconf.services;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import it.maconsulting.kcautoconf.conditions.SwaggerV3Condition;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Conditional(SwaggerV3Condition.class)
public class SwaggerV3OperationService implements SwaggerOperationService {

    public List<String> getScopes(Method method) {
        final Operation apiOperationAnnotation = AnnotationUtils.getAnnotation(method, Operation.class);
        if (apiOperationAnnotation != null) {
            return Arrays.stream(apiOperationAnnotation.security()).flatMap(scope -> Arrays.stream(scope.scopes())).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String getName(Method method) {
        final Operation apiOperationAnnotation = AnnotationUtils.getAnnotation(method, Operation.class);;
        String name = method.getName();
        if (apiOperationAnnotation != null &&
                apiOperationAnnotation.operationId() != null &&
                !apiOperationAnnotation.operationId().isEmpty()) {
            name = apiOperationAnnotation.operationId();
        }
        return name;
    }
}
