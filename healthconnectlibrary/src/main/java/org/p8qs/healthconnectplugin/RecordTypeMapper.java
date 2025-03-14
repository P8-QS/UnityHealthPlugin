package org.p8qs.healthconnectplugin;


import androidx.health.connect.client.records.SleepSessionRecord;
import androidx.health.connect.client.records.StepsRecord;
import androidx.health.connect.client.request.ReadRecordsRequest;
import androidx.health.connect.client.time.TimeRangeFilter;

import java.util.Collections;

import kotlin.jvm.JvmClassMappingKt;

public class RecordTypeMapper {
    public static ReadRecordsRequest<?> Map(String recordType, TimeRangeFilter timeRangeFilter) {
        switch (recordType) {
            case "STEPS":
                return new ReadRecordsRequest<>(
                        JvmClassMappingKt.getKotlinClass(StepsRecord.class),
                        timeRangeFilter,
                        Collections.emptySet(),
                        true,
                        100,
                        null
                );
            case "SLEEP_SESSION":
                return new ReadRecordsRequest<>(
                        JvmClassMappingKt.getKotlinClass(SleepSessionRecord.class),
                        timeRangeFilter,
                        Collections.emptySet(),
                        true,
                        100,
                        null
                );
            default:
                return null;
        }
    }
}
