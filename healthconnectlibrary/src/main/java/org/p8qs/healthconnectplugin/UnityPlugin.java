package org.p8qs.healthconnectplugin;

import android.content.Context;

import androidx.activity.ComponentActivity;
import androidx.health.connect.client.HealthConnectClient;
import androidx.health.connect.client.records.StepsRecord;
import androidx.health.connect.client.request.ReadRecordsRequest;
import androidx.health.connect.client.time.TimeRangeFilter;

import com.unity3d.player.UnityPlayer;

import java.time.LocalDateTime;
import java.util.Collections;

import kotlin.jvm.JvmClassMappingKt;

public class UnityPlugin extends ComponentActivity {
    private HealthConnectClient _healthConnectClient;

    private final Context _context;
    private final Logger _logger = new Logger("HealthConnectPlugin");


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

    public void GetPermissionsStatus(
            final String objectName,
            final String permissionsCallbackName,
            final String[] requiredPermissions
    ) {
        if (_healthConnectClient == null) {
            UnityPlayer.UnitySendMessage(objectName, permissionsCallbackName, "");
        }

        var permissions = PermissionMapper.Map(requiredPermissions);
        var permissionController = _healthConnectClient.getPermissionController();
        HealthConnectHelper.getGrantedPermissionsFuture(permissionController)
                .thenAccept(grants -> {
                    _logger.i("Received granted permissions");
                    UnityPlayer.UnitySendMessage(objectName, permissionsCallbackName, grants.toString());
                })
                .exceptionally(e -> {
                    _logger.e(e.toString());
                    UnityPlayer.UnitySendMessage(objectName, permissionsCallbackName, "none");
                    return null;
                });
    }

    public void ReadHealthRecords(
            final String objectName,
            final String callbackName,
            final RecordType recordType,
            final TimeRangeFilter timeSpan
    ) {
        _logger.i("Start of ReadStepsDay");
        var request = new ReadRecordsRequest<StepsRecord>(
                JvmClassMappingKt.getKotlinClass(StepsRecord.class),
                TimeRangeFilter.between(LocalDateTime.now().minusMonths(1), LocalDateTime.now()),
                Collections.emptySet(),
                true,
                100,
                null
        );

        try {
            HealthConnectHelper.readRecordsFuture(_healthConnectClient, request)
                    .thenAccept(response -> {
                        _logger.i("GOT RESPONSE!");
                        _logger.i(response.toString());
                        _logger.i("List size: " + response.getRecords().size());
                        _logger.i(response.getRecords().toString());

                        response.getRecords().forEach(record -> {
                            _logger.i(record.toString());
                        });
                    })
                    .exceptionally(e -> {
                        _logger.e(e.toString());
                        return null;
                    }).join();
        }
        catch (Exception e) {
            _logger.e(e.toString());
        }
    }

    private void OnReadStepsPermissionGranted() {

    }
}
