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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestMenuController {
    @GetMapping(value = "menu/UserSubMenu_allow")
    public String UserSubMenu_allow(){
        return "UserMenu/UserSubMenu_allow";
    }

    @GetMapping(value = "menu/UserSubMenu_deny")
    public String UserSubMenu_deny(){
        return "UserMenu/UserSubMenu_deny";
    }

    @GetMapping(value = "menu/AdminSubMenu_allow")
    public String AdminSubMenu_allow(){
        return "AdminMenu/AdminSubMenu_allow";
    }

    @GetMapping(value = "menu/AdminSubMenu_deny")
    public String AdminSubMenu_deny(){
        return "AdminMenu/AdminSubMenu_deny";
    }

    @GetMapping(value = "menu/SystemMenu")
    public String SystemMenu(){
        return "SystemMenu/SystemMenu";
    }

}
