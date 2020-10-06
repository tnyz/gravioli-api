package org.gravioli.api;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import org.gravioli.Runner;
import scala.jdk.javaapi.CollectionConverters;

import java.io.IOException;
import java.util.Optional;

public class PretrainFn implements HttpFunction {

    private static final Gson gson = new Gson();
    private HttpResponse response;

    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        response = httpResponse;
        Optional<Request> request = extract(httpRequest);
        if (request.isPresent()) {
            Request body = request.get();
            if (body.locations == null) {
                httpResponse.setStatusCode(400, "No location provided");
                return;
            }
            response.getWriter().write(Runner.jsonResponse(body.deviceId, CollectionConverters.asScala(body.locations), body.metaOnly));
        }
    }

    private Optional<Request> extract(HttpRequest httpRequest) {
        try {
            return Optional.of(gson.fromJson(httpRequest.getReader(), Request.class));
        } catch (Exception e) {
            response.setStatusCode(400, e.getLocalizedMessage());
            return Optional.empty();
        }
    }

}
