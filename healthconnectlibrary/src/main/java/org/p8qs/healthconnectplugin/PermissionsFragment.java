package org.p8qs.healthconnectplugin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.health.connect.client.HealthConnectClient;
import androidx.health.connect.client.PermissionController;
import androidx.health.connect.client.permission.HealthPermission;
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord;
import androidx.health.connect.client.records.ExerciseSessionRecord;
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord;
import androidx.health.connect.client.records.SleepSessionRecord;
import androidx.health.connect.client.records.StepsRecord;
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord;
import androidx.health.connect.client.records.Vo2MaxRecord;
import androidx.health.connect.client.time.TimeRangeFilter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unity3d.player.UnityPlayer;

import org.p8qs.healthconnectplugin.serializers.ExerciseSessionRecordSerializer;
import org.p8qs.healthconnectplugin.serializers.SleepSessionRecordSerializer;
import org.p8qs.healthconnectplugin.serializers.StepsRecordSerializer;
import org.p8qs.healthconnectplugin.serializers.TotalCaloriesBurnedRecordSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import kotlin.jvm.JvmClassMappingKt;

public class PermissionsFragment extends Fragment {
    private final String[] PERMISSIONS = {
            HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(StepsRecord.class)),
            HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(SleepSessionRecord.class)),
            HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(ExerciseSessionRecord.class)),
            HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(HeartRateVariabilityRmssdRecord.class)),
            HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(ActiveCaloriesBurnedRecord.class)),
            HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(TotalCaloriesBurnedRecord.class)),
            HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(Vo2MaxRecord.class)),
    };

    private HealthConnectClient _healthConnectClient;
    private ActivityResultLauncher<Set<String>> permissionLauncher;
    private Gson _gson;
    private final String _providerPackageName = "com.google.android.apps.healthdata";
    private final Logger _logger = new Logger("HCP:PFR");
    private static final Map<String, String> PermissionToRecordType = Map.of(
            "android.permission.health.READ_STEPS", RecordType.STEPS,
            "android.permission.health.READ_SLEEP", RecordType.SLEEP_SESSION,
            "android.permission.health.READ_EXERCISE", RecordType.EXERCISE_SESSION,
            "android.permission.health.READ_HEART_RATE_VARIABILITY", RecordType.HEART_RATE_VARIABILITY_RMSSD,
            "android.permission.health.READ_ACTIVE_CALORIES_BURNED", RecordType.ACTIVE_CALORIES_BURNED,
            "android.permission.health.READ_TOTAL_CALORIES_BURNED", RecordType.TOTAL_CALORIES_BURNED,
            "android.permission.health.READ_VO2_MAX", RecordType.VO2_MAX
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _gson = new GsonBuilder()
                .registerTypeAdapter(StepsRecord.class, new StepsRecordSerializer())
                .registerTypeAdapter(SleepSessionRecord.class, new SleepSessionRecordSerializer())
                .registerTypeAdapter(ExerciseSessionRecord.class, new ExerciseSessionRecordSerializer())
                .registerTypeAdapter(TotalCaloriesBurnedRecord.class, new TotalCaloriesBurnedRecordSerializer())
                .create();

        var requestPermissionActivityContract = PermissionController.createRequestPermissionResultContract();

        permissionLauncher = registerForActivityResult(
                requestPermissionActivityContract,
                this::onPermissionResult
        );


        HealthConnectHelper
            .getGrantedPermissionsFuture(_healthConnectClient)
                .thenAccept(granted -> {
                    if (granted.containsAll(Set.of(PERMISSIONS))) {
                        // Permissions are already granted
                        _logger.d("USER ALREADY HAS PERMISSIONS");

                        for (String permission : granted) {
                            readDataRecords(permission, 1, false);
                        }
                    }
                    else {
                        // Request permissions from user
                        permissionLauncher.launch(Set.of(PERMISSIONS));
                    }
                    cleanup();
                })
                .exceptionally(e -> {
                    _logger.e(e.toString());
                    return null;
                });
    }

    public void setHealthConnectClient(HealthConnectClient client) {
        _healthConnectClient = client;
    }

    private void cleanup() {
        // Clean up: remove this fragment from the activity
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }

    private void readDataRecords(String permission, int minusDays, boolean isHistory) {
        String recordType = PermissionToRecordType.get(permission);

        if (recordType != null) {
            LocalDateTime start = LocalDate.now().minusDays(minusDays).atStartOfDay();
            LocalDateTime end = LocalDate.now().atStartOfDay();
            var tmf = TimeRangeFilter.between(start, end);

            ReadHealthRecords(tmf, recordType, isHistory);
        }
        else {
            _logger.d("No record type mapped for permission: " + permission);
        }
    }

    private void onPermissionResult(Set<String> grantedPermissions) {
        for (String permission : grantedPermissions) {
            readDataRecords(permission, 7, true);
            readDataRecords(permission, 1, false);
        }

        if (grantedPermissions.containsAll(Arrays.asList(PERMISSIONS))) {
            Toast.makeText(requireContext(), "All permissions granted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Some health permissions denied.", Toast.LENGTH_LONG).show();
        }

        cleanup();
    }

    private void ReadHealthRecords(
            final TimeRangeFilter timeRangeFilter,
            final String recordType,
            final boolean isHistory
    ) {
        var mappedValue = RecordTypeMapper.Map(recordType, timeRangeFilter, isHistory);

        if (mappedValue == null || mappedValue.request == null) {
            _logger.e("Invalid health record type");
            return;
        }

        _logger.d(String.format("Sending health data read request. RecordType: %s", recordType));

        try {
            HealthConnectHelper.readRecordsFuture(_healthConnectClient, mappedValue.request)
                    .thenAccept(response -> {
                        _logger.d("Received health data read response!");

                        var responseJson = _gson.toJson(response.getRecords().toArray());
                        UnityPlayer.UnitySendMessage("HealthConnectManager", mappedValue.callback, responseJson);
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
}
