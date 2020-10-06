package org.gravioli.api;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.*;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PretrainFnTest {
    @Mock
    HttpRequest request;

    @Mock
    HttpResponse response;

    @Test
    public void testService() throws IOException {
        PretrainFn api = new PretrainFn();
        StringWriter stringWriter = new StringWriter();
        BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);
        String input = "{\"deviceId\": \"tony\", \"metaOnly\": false, \"locations\":[{\"latitude\":1,\"longitude\":1,\"timestamp\":1}, " +
                "{\"latitude\":1.00000001, \"longitude\":1, \"timestamp\":100}, " +
                "{\"latitude\":1.00000002, \"longitude\":1, \"timestamp\":120}]}";
        Reader reader = new StringReader(input);

        when(request.getReader()).thenReturn(new BufferedReader(reader));
        when(response.getWriter()).thenReturn(bufferedWriter);
        api.service(request, response);
        
        bufferedWriter.flush();
        Assert.assertEquals(stringWriter.getBuffer().toString(),
                "{\"deviceId\":\"tony\",\"activities\":[{\"label\":\"DWELL\",\"size\":3,\"startDate\":\"1970-01-01\",\"startTime\":\"00:00:01\",\"zone\":\"Africa/Sao_Tome\",\"duration\":0.03305555555555555,\"timeUnit\":\"HOURS\",\"locations\":{\"latitude\":[1.0,1.00000001,1.00000002],\"longitude\":[1.0,1.0,1.0]}}]}");
    }
}
