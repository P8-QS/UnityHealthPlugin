package org.p8qs.healthconnectplugin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.health.connect.client.PermissionController;
import androidx.health.connect.client.permission.HealthPermission;
import androidx.health.connect.client.records.SleepSessionRecord;
import androidx.health.connect.client.records.StepsRecord;


import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import kotlin.jvm.JvmClassMappingKt;


public class PermissionsFragment extends Fragment {
    private final String[] PERMISSIONS = {
            HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(StepsRecord.class)),
            HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(SleepSessionRecord.class))
    };
    private ActivityResultLauncher<String[]> permissionLauncher;
    private final Logger _logger = new Logger("HCP:PFR");


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        permissionLauncher = registerForActivityResult(
//                new ActivityResultContracts.RequestMultiplePermissions(),
//                this::onPermissionResult
//        );
        var rPAC = PermissionController.createRequestPermissionResultContract();
        var pl = registerForActivityResult(rPAC, this::onGranted);
        pl.launch(Set.of(PERMISSIONS));
//        permissionLauncher.launch(PERMISSIONS);
    }

    private void onGranted(Set<String> result) {
        if (result.containsAll(Arrays.asList(PERMISSIONS))) {
            // All required permissions granted
            Toast.makeText(requireContext(), "All permissions granted!", Toast.LENGTH_SHORT).show();
            // Proceed with the feature that requires permissions
        } else {
            // Some permissions denied
            Toast.makeText(requireContext(), "Missing required permissions.", Toast.LENGTH_LONG).show();
            // Handle lack of permissions (disable features, show dialog, etc.)
        }
    }

    private void onPermissionResult(Map<String, Boolean> result) {
        boolean allGranted = true;

        for (Boolean granted : result.values()) {
            if (!granted) {
                allGranted = false;
                break;
            }
        }

        if (allGranted) {
            _logger.w("ALL PERMS GRANTED!");
        } else {
            _logger.w("PERM DENIED!");
        }

        // Clean up: remove this fragment from the activity
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        }
    }
}
