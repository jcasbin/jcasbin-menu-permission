package org.casbin;

import org.casbin.jcasbin.main.Enforcer;
import org.junit.jupiter.api.Test;
import  static org.junit.jupiter.api.Assertions.*;

public class MenuTest {
    @Test
    public void testMenu() {
        //model.conf and policy.csv are in the examples/casbin directory
        Enforcer enforcer = new Enforcer("examples/casbin/model.conf","examples/casbin/policy.csv");
        // Test each permission individually

        assertTrue(enforcer.enforce("ROLE_ROOT", "SystemMenu", "read"));
        assertFalse(enforcer.enforce("ROLE_ADMIN", "SystemMenu", "read"));
        assertFalse(enforcer.enforce("ROLE_USER", "SystemMenu", "read"));

        assertFalse(enforcer.enforce("ROLE_ROOT", "UserMenu", "read"));
        assertTrue(enforcer.enforce("ROLE_ADMIN", "UserMenu", "read"));
        assertTrue(enforcer.enforce("ROLE_USER", "UserMenu", "read"));

        assertFalse(enforcer.enforce("ROLE_ROOT", "UserSubMenu_allow", "read"));
        assertTrue(enforcer.enforce("ROLE_ADMIN", "UserSubMenu_allow", "read"));
        assertTrue(enforcer.enforce("ROLE_USER", "UserSubMenu_allow", "read"));

        assertFalse(enforcer.enforce("ROLE_ROOT", "UserSubSubMenu", "read"));
        assertTrue(enforcer.enforce("ROLE_ADMIN", "UserSubSubMenu", "read"));
        assertTrue(enforcer.enforce("ROLE_USER", "UserSubSubMenu", "read"));

        assertFalse(enforcer.enforce("ROLE_ROOT", "UserSubMenu_deny", "read"));
        assertTrue(enforcer.enforce("ROLE_ADMIN", "UserSubMenu_deny", "read"));
        assertFalse(enforcer.enforce("ROLE_USER", "UserSubMenu_deny", "read"));

        assertTrue(enforcer.enforce("ROLE_ROOT", "AdminMenu", "read"));
        assertTrue(enforcer.enforce("ROLE_ADMIN", "AdminMenu", "read"));
        assertFalse(enforcer.enforce("ROLE_USER", "AdminMenu", "read"));

        assertTrue(enforcer.enforce("ROLE_ROOT", "AdminSubMenu_allow", "read"));
        assertTrue(enforcer.enforce("ROLE_ADMIN", "AdminSubMenu_allow", "read"));
        assertFalse(enforcer.enforce("ROLE_USER", "AdminSubMenu_allow", "read"));

        assertTrue(enforcer.enforce("ROLE_ROOT", "AdminSubMenu_deny", "read"));
        assertFalse(enforcer.enforce("ROLE_ADMIN", "AdminSubMenu_deny", "read"));
        assertFalse(enforcer.enforce("ROLE_USER", "AdminSubMenu_deny", "read"));
    }
}

