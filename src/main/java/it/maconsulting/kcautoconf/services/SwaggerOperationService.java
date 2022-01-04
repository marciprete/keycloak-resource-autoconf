package it.maconsulting.kcautoconf.services;

import java.lang.reflect.Method;
import java.util.List;

public interface SwaggerOperationService {

    /**
     * Gets the list of scopes associated with a method
     * @param method the method to get scopes for
     * @return the list of scopes
     */
    List<String> getScopes(Method method);

}
