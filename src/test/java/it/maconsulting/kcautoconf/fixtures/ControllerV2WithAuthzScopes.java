/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.maconsulting.kcautoconf.fixtures;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Michele Arciprete
 * @since 1.0-SNAPSHOT
 */
@RestController
@RequestMapping("authorized")
public class ControllerV2WithAuthzScopes {

    @GetMapping
    @ApiOperation(
            value = "GET",
            nickname = "Entity Reader",
            authorizations = {
                    @Authorization(
                            value = "get",
                            scopes = {@AuthorizationScope(
                                    scope = "entity:read",
                                    description = "Read entity"
                            )})
            })
    public void get() {}
}
