package org.p8qs.healthconnectplugin.serializers;

import androidx.health.connect.client.records.StepsRecord;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class StepsRecordSerializer implements JsonSerializer<StepsRecord> {
    @Override
    public JsonElement serialize(StepsRecord src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("startTime", src.getStartTime().toString());
        jsonObject.addProperty("startZoneOffset", src.getStartZoneOffset() != null ? src.getStartZoneOffset().toString() : null);
        jsonObject.addProperty("endTime", src.getEndTime().toString());
        jsonObject.addProperty("endZoneOffset", src.getEndZoneOffset() != null ? src.getEndZoneOffset().toString() : null);
        jsonObject.addProperty("count", src.getCount());
        jsonObject.add("metadata", context.serialize(src.getMetadata()));
        return jsonObject;
    }
}
