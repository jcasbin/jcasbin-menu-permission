// Copyright 2024 The casbin Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.casbin.config;

import org.casbin.jcasbin.main.Enforcer;
import org.casbin.util.ResourceUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * Creates a Casbin Enforcer bean for accessing and managing access control policies.
 * Loads access control rules from the specified model and policy files.
 *
 */
@Configuration
public class CasbinConfig {

    @Bean
    public Enforcer enforcer() throws IOException {
        File modelFile = ResourceUtil.getTempFileFromResource("casbin/model.conf");
        File policyFile = ResourceUtil.getTempFileFromResource("casbin/policy.csv");
        return new Enforcer(modelFile.getAbsolutePath(), policyFile.getAbsolutePath());
    }
}
