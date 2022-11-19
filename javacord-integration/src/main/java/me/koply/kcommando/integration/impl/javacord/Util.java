package me.koply.kcommando.integration.impl.javacord;

import org.javacord.api.entity.permission.PermissionType;

public class Util {

     public static PermissionType[] getPermissions(String[] values) {
        PermissionType[] ret = new PermissionType[values.length];
        PermissionType[] perms = PermissionType.values();

        int i = 0;
        for (PermissionType perm : perms) {
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