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

package org.casbin.service;

import lombok.extern.slf4j.Slf4j;
import org.casbin.entity.MenuEntity;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.util.MenuUtil;
import org.casbin.util.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The MenuService class handles menu-related business logic, including permission control and menu filtering, using the jCasbin permission library.
 *
 */

@Service
@Slf4j
public class MenuService {
    @Autowired
    private Enforcer enforcer;
    private Map<String, MenuEntity> menuMap;
    private Map<String, Boolean> accessMap;

    public List<MenuEntity> findAccessibleMenus(String username) {
        try {
            File policyFile = ResourceUtil.getTempFileFromResource("casbin/policy.csv");
            this.menuMap = MenuUtil.parseCsvFile(policyFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            // Processing file reading errors
            this.menuMap = new HashMap<>();
        }
        this.accessMap = new HashMap<>();
        // Check if there is ALL_ROOT permission
        if (checkUserAccess(username, "ALL_ROOT")) {
            return menuMap.values().stream()
                    .filter(this::isTopLevelMenu)
                    .collect(Collectors.toList());
        }
        List<MenuEntity> accessibleMenus = new ArrayList<>();
        for (MenuEntity menu : menuMap.values()) {
            checkAndSetMenuAccess(menu, username);
        }
        for (MenuEntity menu : menuMap.values()) {
            if (isTopLevelMenu(menu) && accessMap.getOrDefault(menu.getName(), false)) {
                filterAndAddMenu(menu, accessibleMenus);
            }
        }
        return accessibleMenus;
    }

    private void checkAndSetMenuAccess(MenuEntity menu, String username) {
        boolean hasAccess = checkUserAccess(username, menu.getName());
        for (MenuEntity child : new ArrayList<>(menu.getSubMenus())) {
            accessMap.remove(child.getName());
            checkAndSetMenuAccess(child, username); // Recursive check submenu
            if (!accessMap.getOrDefault(child.getName(), false)) {
                menu.getSubMenus().remove(child);
            } else {
                hasAccess = true;
            }
        }
        accessMap.put(menu.getName(), hasAccess);
    }

    private void filterAndAddMenu(MenuEntity menu, List<MenuEntity> accessibleMenus) {
        if (accessMap.getOrDefault(menu.getName(), false)) {
            MenuEntity filteredMenu = new MenuEntity(menu.getName());
            filteredMenu.setUrl(menu.getUrl());
            for (MenuEntity subMenu : menu.getSubMenus()) {
                if (accessMap.getOrDefault(subMenu.getName(), false)) {
                    filteredMenu.addSubMenu(subMenu);
                }
            }
            accessibleMenus.add(filteredMenu);
        }
    }
    private boolean isTopLevelMenu(MenuEntity menu) {
        // Check whether the menu has a parent menu.
        return menu.getParents() == null;
    }

    public boolean checkUserAccess(String username, String menuName) {
        // Integrate Casbin to check user access to specific menus
        return enforcer.enforce(username, menuName, "read");
    }
}

