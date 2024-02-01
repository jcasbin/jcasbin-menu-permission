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

package org.casbin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.casbin.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String index() {
        return "main/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        boolean isAuthenticated = authenticationService.authenticate(username, password);

        if (isAuthenticated) {
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute("username", username);
            return "redirect:/menu";
        } else {
            redirectAttributes.addAttribute("error", true);
            return "redirect:/login";
        }
    }

    @RequestMapping(value = {"/denied"}, method = RequestMethod.GET)
    public String denied() {
        return "main/denied";
    }
}

