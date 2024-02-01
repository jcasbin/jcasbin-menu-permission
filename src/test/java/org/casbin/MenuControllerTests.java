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

package org.casbin;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.casbin.entity.MenuEntity;
import org.casbin.service.MenuService;
import org.casbin.util.MenuUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
public class MenuControllerTests {
    @Resource
    private MockMvc mockMvc;
    @Resource
    private MenuService menuService;

    private Map<String, MenuEntity> menuMap;

    @Value("${test.menu.filePath}")
    private String filePath;

    @BeforeEach
    public void setUp() throws IOException {
        menuMap = MenuUtil.parseCsvFile(filePath);
    }

    @ParameterizedTest
    @ValueSource(strings = {"root", "admin", "user"})
    public void testAccessForDifferentUsers(String username) throws Exception {
        testMenuAccess(username);
    }

    public void testMenuAccess(String username) throws Exception {
        MockHttpSession session = createSessionForUser(username);
        List<MenuEntity> accessibleMenus = menuService.findAccessibleMenus(username);
        for (MenuEntity menu : menuMap.values()) {
            if(menu.getParents() == null){
                testMenuAndSubMenus(menu, session, accessibleMenus);
            }
        }
    }

    private void testMenuAndSubMenus(MenuEntity menu, MockHttpSession session, List<MenuEntity> accessibleMenus) throws Exception {
        boolean isAccessible = isMenuAccessibleForUser(menu, accessibleMenus);
        String url = menu.getUrl();
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        mockMvc.perform(MockMvcRequestBuilders.get(url).session(session))
                .andExpect(isAccessible ? MockMvcResultMatchers.status().isOk() : MockMvcResultMatchers.status().isFound())
                .andDo(mvcResult -> {
                    String username = (String) session.getAttribute("username");
                    log.info("Username: {}", username);
                    log.info("Menu Name: {}", menu.getName());
                    log.info("Is Accessible: {}", isAccessible);
                    log.info("Status: {}", mvcResult.getResponse().getStatus());
                });

        for (MenuEntity subMenu : menu.getSubMenus()) {
            testMenuAndSubMenus(subMenu, session, accessibleMenus);
        }
    }

    private boolean isMenuAccessibleForUser(MenuEntity menuTarget, List<MenuEntity> accessibleMenus) {
        for (MenuEntity menu : accessibleMenus) {
            if (menuMatches(menu, menuTarget)) { return true; }
        }
        return false;
    }

    private boolean menuMatches(MenuEntity menu, MenuEntity menuTarget) {
        if (menu.equals(menuTarget)) {
            return true;
        }
        for (MenuEntity subMenu : menu.getSubMenus()) {
            if (menuMatches(subMenu, menuTarget)) { return true; }
        }
        return false;
    }

    private MockHttpSession createSessionForUser(String username) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", username);
        return session;
    }

}
