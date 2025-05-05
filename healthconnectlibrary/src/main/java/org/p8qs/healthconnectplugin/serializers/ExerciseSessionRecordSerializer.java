package org.p8qs.healthconnectplugin.serializers;

import androidx.health.connect.client.records.ExerciseSessionRecord;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ExerciseSessionRecordSerializer implements JsonSerializer<ExerciseSessionRecord> {
    @Override
    public JsonElement serialize(ExerciseSessionRecord src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("startTime", src.getStartTime().toString());
        jsonObject.addProperty("startZoneOffset", src.getStartZoneOffset() != null ? src.getStartZoneOffset().toString() : null);
        jsonObject.addProperty("endTime", src.getEndTime().toString());
        jsonObject.addProperty("endZoneOffset", src.getEndZoneOffset() != null ? src.getEndZoneOffset().toString() : null);
        jsonObject.add("metadata", context.serialize(src.getMetadata()));
        jsonObject.addProperty("exerciseType", src.getExerciseType());
        jsonObject.addProperty("title", src.getTitle());
        jsonObject.addProperty("notes", src.getNotes());
        jsonObject.add("segments", context.serialize(src.getSegments()) );
        jsonObject.add("laps", context.serialize(src.getLaps()));
        jsonObject.addProperty("exerciseRoute", src.getExerciseRouteResult().toString());
        jsonObject.addProperty("plannedExerciseSessionId", src.getPlannedExerciseSessionId());
        return jsonObject;
    }
}
