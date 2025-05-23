package org.p8qs.healthconnectplugin;

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord;
import androidx.health.connect.client.records.ExerciseSessionRecord;
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord;
import androidx.health.connect.client.records.SleepSessionRecord;
import androidx.health.connect.client.records.StepsRecord;
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord;
import androidx.health.connect.client.records.Vo2MaxRecord;
import androidx.health.connect.client.request.ReadRecordsRequest;
import androidx.health.connect.client.time.TimeRangeFilter;

import java.util.Collections;

import kotlin.jvm.JvmClassMappingKt;

public class RecordTypeMapper {
    public static class MappedRecordType {
        public final ReadRecordsRequest<?> request;
        public final String callback;

        public MappedRecordType(ReadRecordsRequest<?> request, String callback) {
            this.request = request;
            this.callback = callback;
        }
    }

    private static final int PageSize = 5000;

    public static MappedRecordType Map(String recordType, TimeRangeFilter timeRangeFilter, boolean isHistory) {
        switch (recordType) {
            case RecordType.STEPS:
                return new MappedRecordType(
                    new ReadRecordsRequest<>(
                        JvmClassMappingKt.getKotlinClass(StepsRecord.class),
                        timeRangeFilter,
                        Collections.emptySet(),
                        false,
                            PageSize,
                        null
                    ), isHistory ? "OnStepsHistoryReceived" : "OnStepsRecordsReceived"
                );
            case RecordType.SLEEP_SESSION:
                return new MappedRecordType(
                    new ReadRecordsRequest<>(
                        JvmClassMappingKt.getKotlinClass(SleepSessionRecord.class),
                        timeRangeFilter,
                        Collections.emptySet(),
                        false,
                            PageSize,
                        null
                    ), isHistory ? "OnSleepHistoryReceived" : "OnSleepRecordsReceived"
                );
            case RecordType.EXERCISE_SESSION:
                return new MappedRecordType(
                    new ReadRecordsRequest<>(
                        JvmClassMappingKt.getKotlinClass(ExerciseSessionRecord.class),
                        timeRangeFilter,
                        Collections.emptySet(),
                        false,
                            PageSize,
                        null
                    ), isHistory ? "OnExerciseHistoryReceived" : "OnExerciseRecordsReceived"
                );
            case RecordType.HEART_RATE_VARIABILITY_RMSSD:
                return new MappedRecordType(
                    new ReadRecordsRequest<>(
                        JvmClassMappingKt.getKotlinClass(HeartRateVariabilityRmssdRecord.class),
                        timeRangeFilter,
                        Collections.emptySet(),
                        false,
                            PageSize,
                        null
                    ), isHistory ? "OnHrvHistoryReceived" : "OnHeartRateVariabilityRecordsReceived"
                );
            case RecordType.ACTIVE_CALORIES_BURNED:
                return new MappedRecordType(
                    new ReadRecordsRequest<>(
                        JvmClassMappingKt.getKotlinClass(ActiveCaloriesBurnedRecord.class),
                        timeRangeFilter,
                        Collections.emptySet(),
                        false,
                            PageSize,
                        null
                    ), isHistory ? "OnActiveCaloriesHistoryReceived" : "OnActiveCaloriesRecordsReceived"
                );
            case RecordType.TOTAL_CALORIES_BURNED:
                return new MappedRecordType(
                    new ReadRecordsRequest<>(
                        JvmClassMappingKt.getKotlinClass(TotalCaloriesBurnedRecord.class),
                        timeRangeFilter,
                        Collections.emptySet(),
                        false,
                            PageSize,
                        null
                    ), isHistory ? "OnTotalCaloriesHistoryReceived" : "OnTotalCaloriesRecordsReceived"
                );
            case RecordType.VO2_MAX:
                return new MappedRecordType(
                    new ReadRecordsRequest<>(
                        JvmClassMappingKt.getKotlinClass(Vo2MaxRecord.class),
                        timeRangeFilter,
                        Collections.emptySet(),
                        false,
                            PageSize,
                        null
                    ), isHistory ? "OnVo2HistoryReceived" : "OnVo2RecordsReceived"
                );
            default:
                return null;
        }
    }
}
