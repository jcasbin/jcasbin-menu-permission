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

package org.casbin.util;

import org.casbin.entity.MenuEntity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Extract the menu structure from the CSV file
 */
public class MenuUtil {
    public static Map<String, MenuEntity> parseCsvFile(String filePath) throws IOException {
        Map<String, MenuEntity> menuMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 3 && "g2".equals(values[0].trim())) {
                    String childName = values[1].trim();
                    String parentName = values[2].trim();

                    // Check whether the name of the submenu is "(NULL)"
                    if (!"(NULL)".equals(childName)) {
                        menuMap.putIfAbsent(childName, new MenuEntity(childName));
                        if (!parentName.isEmpty()) {
                            menuMap.putIfAbsent(parentName, new MenuEntity(parentName));
                            MenuEntity childMenu = menuMap.get(childName);
                            MenuEntity parentMenu = menuMap.get(parentName);
                            parentMenu.addSubMenu(childMenu);
                        }
                    } else if (!parentName.isEmpty()) {
                        // Add only the parent menu, no submenu.
                        menuMap.putIfAbsent(parentName, new MenuEntity(parentName));
                    }
                }
            }
        }
        return menuMap;
    }
}