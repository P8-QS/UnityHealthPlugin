package org.p8qs.healthconnectplugin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import org.p8qs.healthconnectplugin.unity.UnityPlayerActivity;

public class PermissionsRationaleActivity extends UnityPlayerActivity {
    private final Logger _logger = new Logger("HCP:PRA");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _logger.w("CALLED ACTIVITY!");
        Intent incomingIntent = getIntent();
        String action = incomingIntent != null ? incomingIntent.getAction() : null;

        if (!Intent.ACTION_VIEW_PERMISSION_USAGE.equals(action)
                || incomingIntent.getCategories() == null
                || !incomingIntent.getCategories().contains("android.intent.category.HEALTH_PERMISSIONS")) {
            _logger.w("Unexpected intent received. Finishing activity.");
            finish();
            return;
        }

        setContentView(R.layout.activity_permissions_rationale);
    }
}
