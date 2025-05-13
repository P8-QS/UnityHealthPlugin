package org.p8qs.healthconnectplugin.serializers;

import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class HeartRateVariabilityRmssdRecordSerializer implements JsonSerializer<HeartRateVariabilityRmssdRecord> {
    @Override
    public JsonElement serialize(HeartRateVariabilityRmssdRecord src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("time", src.getTime().toString());
        jsonObject.addProperty("zoneOffset", src.getZoneOffset() != null ? src.getZoneOffset().toString() : null);
        jsonObject.addProperty("heartRateVariabilityMillis", src.getHeartRateVariabilityMillis());
        jsonObject.add("metadata", context.serialize(src.getMetadata()));
        return jsonObject;
    }
}
