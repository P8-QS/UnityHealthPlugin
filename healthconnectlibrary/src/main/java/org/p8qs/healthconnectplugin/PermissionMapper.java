package org.p8qs.healthconnectplugin;

import androidx.health.connect.client.permission.HealthPermission;
import androidx.health.connect.client.records.SleepSessionRecord;
import androidx.health.connect.client.records.StepsRecord;

import java.util.HashSet;
import java.util.Set;

import kotlin.jvm.JvmClassMappingKt;

public class PermissionMapper {
    public static Set<String> Map(String[] permissions) {
        var result = new HashSet<String>();
        for (var permission : permissions) {
            result.add(GetPermissionKotlinClass(permission));
        }
        return result;
    }

    private static String GetPermissionKotlinClass(String permission) {
        switch (permission) {
            case "android.permission.health.READ_STEPS":
                return HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(StepsRecord.class));
            case "android.permission.health.READ_SLEEP":
                return HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(SleepSessionRecord.class));
            default:
                throw new IllegalStateException("Unexpected value: " + permission);
        }
    }
}
