package tungnn.tutor.java.core.lib.net.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientDemo {

  private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

  static void main() throws IOException, InterruptedException {
    simpleGet();
    asyncGet();
  }

  private static void simpleGet() throws IOException, InterruptedException {
    HttpRequest request = buildGetRequest();

    HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println(response.statusCode());
    System.out.println(response.body());
  }

  private static void asyncGet() {
    HttpRequest request = buildGetRequest();

    HTTP_CLIENT
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(HttpResponse::body)
        .thenAccept(System.out::println)
        .join();
  }

  private static HttpRequest buildGetRequest() {
    URI uri = URI.create("https://www.google.com");
    return HttpRequest.newBuilder().uri(uri).GET().build();
  }
}
