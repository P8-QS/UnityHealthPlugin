package org.p8qs.healthconnectplugin;

import android.app.Activity;
import android.content.Context;

import androidx.activity.ComponentActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.health.connect.client.HealthConnectClient;

import com.unity3d.player.UnityPlayer;

public class UnityPlugin extends ComponentActivity {
    private HealthConnectClient _healthConnectClient;

    private final Context _context;
    private final Logger _logger = new Logger("HCP:Plugin");


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

    public void RequestPermissions(Activity activity) {
        if (activity instanceof FragmentActivity) {
            FragmentActivity fa = (FragmentActivity) activity;

            PermissionsFragment fragment = new PermissionsFragment();
            fragment.setHealthConnectClient(_healthConnectClient);

            fa.getSupportFragmentManager()
                    .beginTransaction()
                    .add(fragment, "PermissionsFragment")
                    .commit();
        }
    }
}
