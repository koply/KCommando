package me.koply.kcommando.integration.impl.javacord;

import org.javacord.api.entity.permission.PermissionType;

public class Util {

     public static PermissionType[] getPermissions(long[] values) {
        PermissionType[] ret = new PermissionType[values.length];
        PermissionType[] perms = PermissionType.values();

        int i = 0;
        for (PermissionType perm : perms) {
            for (long value : values) {
                if (perm.getValue() == value) {
                    ret[i] = perm;
                    i++;
                }
            }
        }
        return ret;
    }

}