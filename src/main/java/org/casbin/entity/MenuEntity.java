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

package org.casbin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Menu Entity
 *
 */

@Setter
@Getter
public class MenuEntity {
    private String name;
    private String url;
    @JsonIgnore
    private List<MenuEntity> subMenus=new ArrayList<>();
    @JsonIgnore
    private MenuEntity parents;

    public MenuEntity(String name) {
        this.name = name;
        this.url = "menu/" + name;  // Set the URL
        this.subMenus = new ArrayList<>();
    }

    public void addSubMenu(MenuEntity subMenu) {
        this.subMenus.add(subMenu);
        subMenu.setParents(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuEntity that = (MenuEntity) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, subMenus);
    }

}