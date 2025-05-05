package org.p8qs.healthconnectplugin;

import android.content.Context;

import androidx.activity.ComponentActivity;
import androidx.health.connect.client.HealthConnectClient;
import androidx.health.connect.client.records.ExerciseSessionRecord;
import androidx.health.connect.client.records.SleepSessionRecord;
import androidx.health.connect.client.records.StepsRecord;
import androidx.health.connect.client.time.TimeRangeFilter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unity3d.player.UnityPlayer;

import org.p8qs.healthconnectplugin.serializers.ExerciseSessionRecordSerializer;
import org.p8qs.healthconnectplugin.serializers.SleepSessionRecordSerializer;
import org.p8qs.healthconnectplugin.serializers.StepsRecordSerializer;

public class UnityPlugin extends ComponentActivity {
    private HealthConnectClient _healthConnectClient;

    private final Context _context;
    private final Logger _logger = new Logger("HCP:Plugin");
    private final Gson _gson;


    public UnityPlugin(Context context) {
        _context = context;

        // TODO: Write serializer for Metadata class
        // TODO: Write serializer for SleepSessionRecord.Stage class
        _gson = new GsonBuilder()
                .registerTypeAdapter(StepsRecord.class, new StepsRecordSerializer())
                .registerTypeAdapter(SleepSessionRecord.class, new SleepSessionRecordSerializer())
                .registerTypeAdapter(ExerciseSessionRecord.class, new ExerciseSessionRecordSerializer())
                .create();
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

    public void ReadHealthRecords(
            final TimeRangeFilter timeRangeFilter,
            final String recordType,
            final String objectName,
            final String callbackName
    ) {
        var request = RecordTypeMapper.Map(recordType, timeRangeFilter);

        if (request == null) {
            _logger.e("Invalid health record type");
            return;
        }

        _logger.d(String.format("Sending health data read request. RecordType: %s", recordType));

        try {
            HealthConnectHelper.readRecordsFuture(_healthConnectClient, request)
                    .thenAccept(response -> {
                        _logger.d("Received health data read response!");

                        var responseJson = _gson.toJson(response.getRecords().toArray());
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
}
