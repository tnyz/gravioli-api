package org.gravioli.api;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.gravioli.Runner;
import org.gravioli.core.Geo;
import scala.jdk.javaapi.CollectionConverters;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class PretrainFn implements HttpFunction {

    private static final Gson gson = new Gson();
    private BufferedWriter writer;

    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        writer = httpResponse.getWriter();
        JsonObject json = extract(httpRequest);
        String id = extractId(json);
        Iterable<Geo> points = extractLocations(json);
        String response = Runner.runLocalZone(id, CollectionConverters.asScala(points));
        writer.write(response);
    }

    private JsonObject extract(HttpRequest httpRequest) throws IOException {
        try {
            return gson.fromJson(httpRequest.getReader(), JsonElement.class).getAsJsonObject();
        } catch (IOException e) {
            writer.write("Error parsing JSON: " + e.getMessage());
            throw e;
        }
    }

    private String extractId(JsonObject jsonObject) {
        try {
            return jsonObject.getAsJsonObject("config").get("deviceId").getAsString();
        } catch (Exception e) {
            return null;
        }
    }

    private Iterable<Geo> extractLocations(JsonObject jsonObject) throws IOException {
        try {
            JsonElement locations = jsonObject.get("locations");
            Type geoList = new TypeToken<ArrayList<Geo>>() {}.getType();
            return gson.fromJson(locations, geoList);
        } catch (Exception e) {
            writer.write("Error parsing JSON: " + e.getMessage());
            throw e;
        }
    }
}
