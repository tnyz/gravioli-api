package org.gravioli.api;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import org.gravioli.Runner;
import org.gravioli.io.Geo;

import java.io.BufferedWriter;

public class PretrainFn implements HttpFunction {
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        Runner<Geo> runner = new Runner<Geo>()

        BufferedWriter writer = httpResponse.getWriter();
        writer.write("Instance execution count: ");
    }
}
