package org.p8qs.healthconnectplugin.serializers;

import androidx.health.connect.client.records.Vo2MaxRecord;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class Vo2MaxRecordSerializer implements JsonSerializer<Vo2MaxRecord> {
    @Override
    public JsonElement serialize(Vo2MaxRecord src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("time", src.getTime().toString());
        jsonObject.addProperty("zoneOffset", src.getZoneOffset() != null ? src.getZoneOffset().toString() : null);
        jsonObject.add("metadata", context.serialize(src.getMetadata()));
        jsonObject.addProperty("vo2MillilitersPerMinuteKilogram", src.getVo2MillilitersPerMinuteKilogram());
        jsonObject.addProperty("measurementMethod", src.getMeasurementMethod());
        return jsonObject;
    }
}
