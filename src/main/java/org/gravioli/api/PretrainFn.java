package org.gravioli.api;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import org.gravioli.Runner;
import scala.jdk.javaapi.CollectionConverters;

import java.io.BufferedWriter;
import java.io.IOException;

public class PretrainFn implements HttpFunction {

    private static final Gson gson = new Gson();
    private BufferedWriter writer;

    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        writer = httpResponse.getWriter();
        Request body = extract(httpRequest);
        String response = Runner.jsonResponse("tony", CollectionConverters.asScala(body.locations), body.metaOnly);
        writer.write(response);
    }

    private Request extract(HttpRequest httpRequest) throws IOException {
        try {
            return gson.fromJson(httpRequest.getReader(), Request.class);
        } catch (IOException e) {
            writer.write("Error parsing JSON: " + e.getMessage());
            throw e;
        }
    }

}
