package org.p8qs.healthconnectplugin;

import android.content.Context;

import androidx.activity.ComponentActivity;
import androidx.health.connect.client.HealthConnectClient;
import androidx.health.connect.client.records.Record;
import androidx.health.connect.client.records.StepsRecord;
import androidx.health.connect.client.request.ReadRecordsRequest;
import androidx.health.connect.client.time.TimeRangeFilter;

import com.google.gson.Gson;
import com.unity3d.player.UnityPlayer;

import java.time.LocalDateTime;
import java.util.Collections;

import kotlin.jvm.JvmClassMappingKt;

public class UnityPlugin extends ComponentActivity {
    private HealthConnectClient _healthConnectClient;

    private final Context _context;
    private final Logger _logger = new Logger("HealthConnectPlugin");
    private final Gson _gson = new Gson();


    public UnityPlugin(Context context) {
        _context = context;
    }

    public void CheckHealthConnectAvailability(
            final String objectName,
            final String unavailableCallbackName,
            final String updateCallbackName,
            final String availableCallbackName
    ) {
        String providerPackageName = "com.google.android.apps.healthdata";
        int status = HealthConnectClient.getSdkStatus(_context, providerPackageName);

        if (status == HealthConnectClient.SDK_UNAVAILABLE) {
            _logger.d("Health Connect SDK is unavailable");
            UnityPlayer.UnitySendMessage(objectName, unavailableCallbackName, "SDK_UNAVAILABLE");
        }

        if (status == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            _logger.d("Health Connect requires an update");
            UnityPlayer.UnitySendMessage(objectName, updateCallbackName, "SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED");
        }

        if (status == HealthConnectClient.SDK_AVAILABLE) {
            _logger.d("Health Connect SDK is available. Client initialized");
            _healthConnectClient = HealthConnectClient.getOrCreate(_context);
            UnityPlayer.UnitySendMessage(objectName, availableCallbackName, "SDK_AVAILABLE");
        }
    }

    public void ReadStepsRecords(
            final String objectName,
            final String callbackName,
            final TimeRangeFilter timeRange
    ) {
        var request = new ReadRecordsRequest<StepsRecord>(
                JvmClassMappingKt.getKotlinClass(StepsRecord.class),
                timeRange,
                Collections.emptySet(),
                true,
                100,
                null
        );
        ReadHealthRecords(objectName, callbackName, request);
    }

    private<T extends Record> void ReadHealthRecords(
            final String objectName,
            final String callbackName,
            final ReadRecordsRequest<T> request
    ) {
        _logger.d("Sending health data read request");

        try {
            HealthConnectHelper.readRecordsFuture(_healthConnectClient, request)
                    .thenAccept(response -> {
                        _logger.d("Received health data read response!");
                        _logger.d(response.toString());
                        _logger.d("List size: " + response.getRecords().size());
                        _logger.d(response.getRecords().toString());

                        var responseJson = _gson.toJson(response.getRecords());
                        UnityPlayer.UnitySendMessage(objectName, callbackName, responseJson);
                    })
                    .exceptionally(e -> {
                        _logger.e(e.toString());
                        return null;
                    });
        }
        catch (Exception e) {
            _logger.e(e.toString());
        }
    }


//        var request = new ReadRecordsRequest<StepsRecord>(
//                JvmClassMappingKt.getKotlinClass(StepsRecord.class),
//                TimeRangeFilter.between(LocalDateTime.now().minusMonths(1), LocalDateTime.now()),
//                Collections.emptySet(),
//                true,
//                100,
//                null
//        );


//    public void GetPermissionsStatus(
//            final String objectName,
//            final String permissionsCallbackName,
//            final String[] requiredPermissions
//    ) {
//        if (_healthConnectClient == null) {
//            UnityPlayer.UnitySendMessage(objectName, permissionsCallbackName, "");
//        }
//
//        var permissions = PermissionMapper.Map(requiredPermissions);
//        var permissionController = _healthConnectClient.getPermissionController();
//        HealthConnectHelper.getGrantedPermissionsFuture(permissionController)
//                .thenAccept(grants -> {
//                    _logger.i("Received granted permissions");
//                    UnityPlayer.UnitySendMessage(objectName, permissionsCallbackName, grants.toString());
//                })
//                .exceptionally(e -> {
//                    _logger.e(e.toString());
//                    UnityPlayer.UnitySendMessage(objectName, permissionsCallbackName, "none");
//                    return null;
//                });
//    }

}
