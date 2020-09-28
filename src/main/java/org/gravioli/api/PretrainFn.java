package org.gravioli.api;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.*;
import org.gravioli.Runner;
import org.gravioli.core.Geo;
import scala.jdk.javaapi.CollectionConverters;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PretrainFn implements HttpFunction {

    private static final Gson gson = new Gson();

    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        Map<String, List<String>> qp = httpRequest.getQueryParameters();
        System.out.println("query parameters: " + qp);

        ArrayList<Geo> points = validateInputAndExtract(httpRequest);
        System.out.println(points.size());
        String response = Runner.runLocalZone("", CollectionConverters.asScala(points));
        BufferedWriter writer = httpResponse.getWriter();
        writer.write(response);
    }

    private ArrayList<Geo> validateInputAndExtract(HttpRequest httpRequest) {
        ArrayList<Geo> geoData = new ArrayList<>();
        try {
            JsonElement requestParsed = gson.fromJson(httpRequest.getReader(), JsonElement.class);
            JsonObject requestJson = requestParsed.getAsJsonObject();

            JsonArray locations = requestJson.getAsJsonArray("locations");
            for (JsonElement point : locations) {
                JsonObject pointObject = point.getAsJsonObject();
                Double lat = pointObject.get("latitude").getAsDouble();
                Double lon = pointObject.get("longitude").getAsDouble();
                Long ts = pointObject.get("timestamp").getAsLong();
                geoData.add(new Geo(lat, lon, ts));
            }
        } catch (JsonParseException | IOException e) {
            System.out.println("Error parsing JSON: " + e.getMessage());
        }
        return geoData;
    }
}
