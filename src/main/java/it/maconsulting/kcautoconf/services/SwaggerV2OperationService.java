package it.maconsulting.kcautoconf.services;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import it.maconsulting.kcautoconf.conditions.SwaggerV2Condition;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Conditional(SwaggerV2Condition.class)
public class SwaggerV2OperationService implements SwaggerOperationService {

    public List<String> getScopes(Method method) {
        final ApiOperation apiOperationAnnotation = AnnotationUtils.getAnnotation(method, ApiOperation.class);
        if (apiOperationAnnotation != null) {
            List<AuthorizationScope[]> scopes = Arrays.stream(apiOperationAnnotation.authorizations()).map(Authorization::scopes).collect(Collectors.toList());
            return scopes.stream().flatMap(inner -> Arrays.stream(inner).map(AuthorizationScope::scope)).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String getName(Method method) {
        final ApiOperation apiOperationAnnotation = AnnotationUtils.getAnnotation(method, ApiOperation.class);
        String name = method.getName();
        if (apiOperationAnnotation != null &&
                apiOperationAnnotation.nickname() != null &&
                !apiOperationAnnotation.nickname().isEmpty()) {
            name = apiOperationAnnotation.nickname();
        }
        return name;
    }
}
