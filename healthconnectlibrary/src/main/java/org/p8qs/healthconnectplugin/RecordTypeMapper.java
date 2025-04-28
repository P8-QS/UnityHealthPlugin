package org.p8qs.healthconnectplugin;


import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord;
import androidx.health.connect.client.records.ExerciseSessionRecord;
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord;
import androidx.health.connect.client.records.SleepSessionRecord;
import androidx.health.connect.client.records.StepsRecord;
import androidx.health.connect.client.records.Vo2MaxRecord;
import androidx.health.connect.client.request.ReadRecordsRequest;
import androidx.health.connect.client.time.TimeRangeFilter;

import java.util.Collections;

import kotlin.jvm.JvmClassMappingKt;

public class RecordTypeMapper {
    public static ReadRecordsRequest<?> Map(String recordType, TimeRangeFilter timeRangeFilter) {
        switch (recordType) {
            case RecordType.STEPS:
                return new ReadRecordsRequest<>(
                        JvmClassMappingKt.getKotlinClass(StepsRecord.class),
                        timeRangeFilter,
                        Collections.emptySet(),
                        false,
                        100,
                        null
                );
            case RecordType.SLEEP_SESSION:
                return new ReadRecordsRequest<>(
                        JvmClassMappingKt.getKotlinClass(SleepSessionRecord.class),
                        timeRangeFilter,
                        Collections.emptySet(),
                        false,
                        100,
                        null
                );
            case RecordType.EXERCISE_SESSION:
                return new ReadRecordsRequest<>(
                        JvmClassMappingKt.getKotlinClass(ExerciseSessionRecord.class),
                        timeRangeFilter,
                        Collections.emptySet(),
                        false,
                        100,
                        null
                );
            case RecordType.HEART_RATE_VARIABILITY_RMSSD:
                return new ReadRecordsRequest<>(
                        JvmClassMappingKt.getKotlinClass(HeartRateVariabilityRmssdRecord.class),
                        timeRangeFilter,
                        Collections.emptySet(),
                        false,
                        100,
                        null
                );
            case RecordType.ACTIVE_CALORIES_BURNED:
                return new ReadRecordsRequest<>(
                        JvmClassMappingKt.getKotlinClass(ActiveCaloriesBurnedRecord.class),
                        timeRangeFilter,
                        Collections.emptySet(),
                        false,
                        100,
                        null
                );
            case RecordType.VO2_MAX:
                return new ReadRecordsRequest<>(
                        JvmClassMappingKt.getKotlinClass(Vo2MaxRecord.class),
                        timeRangeFilter,
                        Collections.emptySet(),
                        false,
                        100,
                        null
                );
            default:
                return null;
        }
    }
}
