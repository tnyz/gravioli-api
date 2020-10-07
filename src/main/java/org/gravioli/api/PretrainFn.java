package org.gravioli.api;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import org.gravioli.Runner;
import scala.jdk.javaapi.CollectionConverters;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

public class PretrainFn implements HttpFunction {

    private static final Gson gson = new Gson();

    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Optional<Request> request = extract(httpRequest.getReader(), httpResponse);
        if (request.isPresent()) {
            Request body = request.get();
            if (body.locations == null) {
                httpResponse.setStatusCode(400, "No location provided");
                return;
            }
            String response = Runner.jsonResponse(body.deviceId,
                    CollectionConverters.asScala(body.locations),
                    body.metaOnly);
            httpResponse.getWriter().write(response);
        }
    }

    private Optional<Request> extract(Reader reader, HttpResponse response) {
        try {
            return Optional.of(gson.fromJson(reader, Request.class));
        } catch (Exception e) {
            response.setStatusCode(400, e.getLocalizedMessage());
            return Optional.empty();
        }
    }

}
