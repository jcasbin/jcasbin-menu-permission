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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.casbin.service.MenuService;
import org.casbin.util.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private MenuService menuService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register login interceptor
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/"); // Exclude the login page

        registry.addInterceptor(new HandlerInterceptor() {

            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                HttpSession session = request.getSession();
                String username = (String) session.getAttribute("username");
                String requestURI = request.getRequestURI();

                // Extract the menu name, assuming it is the last part of the URI.
                String menuName = requestURI.substring(requestURI.lastIndexOf('/') + 1);

                if(menuService.checkUserAccess(username, "ALL_ROOT")) return true;

                if (username == null) {
                    // The user is not logged in
                    response.sendRedirect(request.getContextPath() + "/denied");
                    return false;
                }

                if (!menuService.checkUserAccess(username, menuName)) {
                    // Users do not have access to the menu.
                    response.sendRedirect(request.getContextPath() + "/denied");
                    return false;
                }

                return true;
            }
        }).addPathPatterns("/menu/*"); // Applied to menu-related paths
    }
}
