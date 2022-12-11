import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class VirtualThreadApplication implements HttpHandler {
    final HttpClient httpClient = HttpClient
            .newBuilder()
            .executor(Executors.newVirtualThreadPerTaskExecutor())
            .build();

    public static void main(String[] args) throws IOException {
        new VirtualThreadApplication().start();
    }

    private void start() throws IOException {
        final InetSocketAddress serverAddress = new InetSocketAddress("0.0.0.0", 7070);
        final HttpServer localhost = HttpServer.create(serverAddress, 100);
        localhost.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        localhost.createContext("/", this);
        localhost.start();
        log("Server started.");
    }

    private static void log(String message) {
        System.out.println(message);
    }

    @Override
    public void handle(HttpExchange exchange) {
        log("Request: %s".formatted(exchange.getRequestURI()));
        handleRequest(exchange);
    }

    private void handleRequest(final HttpExchange exchange) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder().GET().uri(URI.create("http://192.168.68.118:9090/test.txt")).build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String responseMessage = "Response: %s, isVirtual: %s".formatted(httpResponse.body().replace("\n", ""), Thread.currentThread().isVirtual());
            exchange.sendResponseHeaders(200, responseMessage.length());
            exchange.getResponseBody().write(responseMessage.getBytes(StandardCharsets.UTF_8));
            exchange.getResponseBody().close();
        } catch (Exception e) {
            log("Error; %s".formatted(e.getMessage()));
        }
    }
}
