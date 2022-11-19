package me.koply.kcommando.integration.impl.jda;

import net.dv8tion.jda.api.Permission;

public class Util {

    public static Permission[] getPermissions(long[] values) {
        Permission[] ret = new Permission[values.length];
        Permission[] perms = Permission.values();

        int i = 0;
        for (Permission perm : perms) {
            for (long value : values) {
                if (perm.getRawValue() == value) {
                    ret[i] = perm;
                    i++;
                }
            }
        }
        return ret;
    }

}