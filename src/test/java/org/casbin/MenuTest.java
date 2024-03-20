package org.casbin;

import org.casbin.jcasbin.main.Enforcer;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MenuTest {
    @Test
    public void testMenu() {
        Enforcer enforcer = new Enforcer("examples/casbin/model.conf","examples/casbin/policy.csv");
        List<String> perms = Arrays.asList(
            "SystemMenu",         //	✅	❌	❌
            "UserMenu",           //	❌	✅	✅
            "UserSubMenu_allow",  //	❌	✅	✅
            "UserSubSubMenu",     //	❌	✅	✅
            "UserSubMenu_deny",   //	❌	✅	❌
            "AdminMenu",          //	✅	✅	❌
            "AdminSubMenu_allow", //	✅	✅	❌
            "AdminSubMenu_deny"   //	✅	❌	❌
        );

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        // Store the expected results
        List<String> expectedPermissions = Arrays.asList("✅", "❌", "❌", "❌", "✅", "✅", "❌","✅", "✅","❌","✅","✅","❌","✅","❌","✅","✅","❌","✅","✅","❌","✅","❌","❌");
        List<String> actualPermissions = new ArrayList<>();
        // Read permissions for testing
        for (String obj : perms) {
            try {
                // Write test results in format
                writer.write(obj + repeat(" ",30 - obj.length()));
                for (String sub : new String[]{"ROLE_ROOT", "ROLE_ADMIN", "ROLE_USER"}) {
                    boolean ok = enforcer.enforce(sub, obj, "read");
                    actualPermissions.add(ok ? "✅":  "❌");
                    writer.write(ok ? "✅\t":  "❌\t");
                }
                writer.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Compare expected results with actual results, if they do not match, the test fails
        if (!expectedPermissions.toString().equals(actualPermissions.toString())) {
           throw new RuntimeException("NOT MATCHED");
        }
    }



    private String repeat(String str, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }

        return sb.toString();
    }
}

