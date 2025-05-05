package org.p8qs.healthconnectplugin

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.response.ReadRecordsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future

import java.util.concurrent.CompletableFuture

object HealthConnectHelper {
    @JvmStatic
    fun <T : Record> readRecordsFuture(
        client: HealthConnectClient,
        request: ReadRecordsRequest<T>
    ): CompletableFuture<ReadRecordsResponse<T>> {
        return CoroutineScope(Dispatchers.IO).future {
            client.readRecords(request)
        }
    }

    @JvmStatic
    fun getGrantedPermissionsFuture(
        permissionController: PermissionController
    ): CompletableFuture<Set<String>> {
        return CoroutineScope(Dispatchers.IO).future {
            permissionController.getGrantedPermissions()
        }
    }
}
