package org.p8qs.healthconnectplugin;

import android.content.Context;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.health.connect.client.HealthConnectClient;
import androidx.health.connect.client.permission.HealthPermission;
import androidx.health.connect.client.records.StepsRecord;
import androidx.health.connect.client.request.ReadRecordsRequest;
import androidx.health.connect.client.time.TimeRangeFilter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import kotlin.jvm.JvmClassMappingKt;

public class UnityPlugin extends ComponentActivity {
    private HealthConnectClient _healthConnectClient;

    private final Context _context;
    private final Logger _logger = new Logger("HealthConnectPlugin");

    public UnityPlugin(Context context) {
        _context = context;

        if (IsHealthClientAvailable()) {
            _healthConnectClient = HealthConnectClient.getOrCreate(context);
            _logger.i("HealthConnectClient is initialized!");
        }
    }

    private boolean IsHealthClientAvailable() {
        String providerPackageName = "com.google.android.apps.healthdata";
        int status = HealthConnectClient.getSdkStatus(_context, providerPackageName);

        if (status == HealthConnectClient.SDK_UNAVAILABLE) {
            _logger.i("Health Connect SDK is unavailable.");
            return false;
        }

        if (status == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            _logger.i("Health Connect requires an update.");
            return false;
        }

        if (status == HealthConnectClient.SDK_AVAILABLE) {
            _logger.i("Health Connect SDK is available.");
            return true;
        }

        return false;
    }

    private CompletableFuture<Integer> HasPermissionsAsync() {
        CompletableFuture<Integer> result = new CompletableFuture<>();

        if (_healthConnectClient == null) {
            result.complete(0);
            return result;
        }

        Set<String> permissions = Set.of(
                HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(StepsRecord.class))
        );

        var permissionController = _healthConnectClient.getPermissionController();
        HealthConnectHelper.getGrantedPermissionsFuture(permissionController)
                .thenAccept(grants -> {
                    result.complete(grants.containsAll(permissions) ? 1 : 0);
                })
                .exceptionally(e -> {
                    _logger.e(e.toString());
                    result.complete(0);
                    return null;
                });

        return result;
    }

    public int HasPermissions() {
        return HasPermissionsAsync().join();
    }

    public void ReadStepsDay() {
        _logger.i("Start of ReadStepsDay");
        var request = new ReadRecordsRequest<StepsRecord>(
                JvmClassMappingKt.getKotlinClass(StepsRecord.class),
                TimeRangeFilter.between(LocalDateTime.now().minusMonths(1), LocalDateTime.now()),
                Collections.emptySet(),
                true,
                100,
                null
        );

        _logger.i("Reading records...");

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
        _logger.i("END OF READSTEPSDAY");
    }

    private void OnReadStepsPermissionGranted() {

    }
}
