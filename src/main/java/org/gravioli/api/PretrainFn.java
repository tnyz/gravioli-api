package org.gravioli.api;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import org.gravioli.Runner;
import scala.jdk.javaapi.CollectionConverters;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Optional;

public class PretrainFn implements HttpFunction {

    private static final Gson gson = new Gson();
    private BufferedWriter writer;

    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        writer = httpResponse.getWriter();
        Optional<Request> request = extract(httpRequest);
        if (request.isPresent()) {
            Request body = request.get();
            if (body.locations == null) {
                writer.write("No location provided");
                return;
            }
            String response = Runner.jsonResponse(body.deviceId, CollectionConverters.asScala(body.locations), body.metaOnly);
            writer.write(response);
        }
    }

    private Optional<Request> extract(HttpRequest httpRequest) throws IOException {
        try {
            return Optional.of(gson.fromJson(httpRequest.getReader(), Request.class));
        } catch (Exception e) {
            writer.write(e.getLocalizedMessage());
            return Optional.empty();
        }
    }

}
