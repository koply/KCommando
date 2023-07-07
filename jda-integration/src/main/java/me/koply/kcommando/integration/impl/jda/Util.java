package me.koply.kcommando.integration.impl.jda;

import net.dv8tion.jda.api.Permission;

public class Util {

    private Util() {}

    public static Permission[] getPermissions(String[] values) {
        Permission[] ret = new Permission[values.length];
        Permission[] perms = Permission.values();

        int i = 0;
        for (Permission perm : perms) {
            for (String value : values) {
                if (perm.name().equalsIgnoreCase(value)) {
                    ret[i] = perm;
                    i++;
                }
            }
        }
        return ret;
    }

}