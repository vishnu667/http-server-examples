package org.cybergen.blog;

import com.google.inject.Singleton;
//import org.jboss.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpMethod;
import org.restexpress.Request;
import org.restexpress.Response;
import org.restexpress.RestExpress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by vishnu on 19/11/14.
 */
@Singleton
public class RestExpressMain {
    private static final String SERVICE_NAME = "Test Service";
    private static final String RESPONSECODE = "\"status\":\"ok\"";
    private static final String HELLOWORLD_RESPONSECODE = "hello world";

    private static final int DEFAULT_EXECUTOR_THREAD_POOL_SIZE = 8;
    private static final int SERVER_PORT = 9008;
    private static final Logger LOG = LoggerFactory.getLogger(RestExpressMain.class);
    private static final RestExpressMain INSTANCE = new RestExpressMain();

    RestExpressMain() {
    }

    public static void main(String[] args) {
        RestExpress server = null;
        try {
            server = initializeServer(args);
            server.awaitShutdown();
        } catch (IOException e) {
            LOG.info(e.getMessage());
        }
    }

    public static RestExpress initializeServer(String[] args) throws IOException {
        RestExpress server = new RestExpress()
                .setName(SERVICE_NAME)
                .setBaseUrl("http://localhost:" + SERVER_PORT)
                .setKeepAlive(true)
                .setReuseAddress(true)
                .setReceiveBufferSize(52428800)
                .setSoLinger(0);

        server.uri("/ping", INSTANCE).action("ping", HttpMethod.GET).noSerialization();
        server.uri("/testN", INSTANCE).action("helloWorldNioHandler", HttpMethod.GET).noSerialization();
        server.bind(SERVER_PORT);
        return server;
    }

    public void ping(Request request, Response response) {
        response.setBody(RESPONSECODE);
    }

    public void helloWorldNioHandler(Request request, Response response) {
      response.setBody(HELLOWORLD_RESPONSECODE);
    }


}
