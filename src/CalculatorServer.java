import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CalculatorServer {
    private static final Calculator calculator = new Calculator();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/calculate", CalculatorServer::handleCalculation);
        server.createContext("/", CalculatorServer::serveFiles);
        server.start();
        System.out.println("Calculator running at http://localhost:8080");
    }

    private static void handleCalculation(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            send(exchange, 405, "Method not allowed");
            return;
        }

        try {
            Map<String, String> params =
                    parseQuery(exchange.getRequestURI().getRawQuery());

            double a = Double.parseDouble(params.get("a"));
            double b = Double.parseDouble(params.get("b"));
            String op = params.get("op");

            double result = calculator.calculate(a, b, op);
            if (!Double.isFinite(result)) {
                throw new ArithmeticException("Result is outside the supported range");
            }
            send(exchange, 200, Double.toString(result));

        } catch (ArithmeticException | IllegalArgumentException e) {
            send(exchange, 400, e.getMessage() == null
                    ? "Invalid calculation" : e.getMessage());
        } catch (Exception e) {
            send(exchange, 400, "Invalid input");
        }
    }

    private static void serveFiles(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/")) {
            path = "/index.html";
        }

        if (!path.equals("/index.html") && !path.equals("/style.css")) {
            send(exchange, 404, "File not found");
            return;
        }

        Path file = Path.of("public", path.substring(1));
        if (!Files.exists(file)) {
            send(exchange, 404, "Frontend file not found");
            return;
        }

        String contentType = path.endsWith(".css")
                ? "text/css; charset=UTF-8"
                : "text/html; charset=UTF-8";
        byte[] content = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, content.length);

        try (var output = exchange.getResponseBody()) {
            output.write(content);
        }
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null) {
            throw new IllegalArgumentException("Missing input");
        }

        for (String part : query.split("&")) {
            String[] pair = part.split("=", 2);
            if (pair.length == 2) {
                params.put(
                        URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(pair[1], StandardCharsets.UTF_8)
                );
            }
        }
        return params;
    }

    private static void send(HttpExchange exchange, int status, String message)
            throws IOException {
        byte[] content = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(status, content.length);

        try (var output = exchange.getResponseBody()) {
            output.write(content);
        }
    }
}
