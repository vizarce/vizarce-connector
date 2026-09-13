package com.vizarce.connector.internal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Represents an authenticated connection to the VIZARCE API.
 * <p>
 * A single {@link HttpClient} is created and reused for the lifetime of the connection
 * (per Connection Management best practice — avoid creating a new client per request).
 */
public class VizarceConnection {

  private final String apiKey;
  private final String baseUrl;
  private final HttpClient httpClient;

  public VizarceConnection(String apiKey, String baseUrl) {
    this.apiKey = apiKey;
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
  }

  /**
   * Issues an authenticated POST request to the given VIZARCE endpoint path.
   *
   * @param path        the API path, e.g. "/compose" (leading slash required)
   * @param jsonBody    the raw JSON request body
   * @param timeoutSecs how long to wait for a response before timing out — VIZARCE's
   *                    /compose endpoint chains several sequential AI calls and can take
   *                    well over a minute, so this is caller-configurable per operation
   * @return the raw JSON response body as a string
   */
  public String post(String path, String jsonBody, int timeoutSecs) throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + path))
        .timeout(Duration.ofSeconds(timeoutSecs))
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + apiKey)
        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() >= 400) {
      throw new VizarceApiException(response.statusCode(), response.body());
    }

    return response.body();
  }

  /**
   * Simple reachability check used by the Connection Provider's connectivity testing —
   * hits the base API root. VIZARCE does not currently expose a dedicated /health endpoint,
   * so this performs a lightweight GET against the base URL and treats any non-5xx response
   * (including 404) as "the API is reachable and our credentials made it past the network").
   */
  public void testConnectivity() throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl))
        .timeout(Duration.ofSeconds(10))
        .header("Authorization", "Bearer " + apiKey)
        .GET()
        .build();
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() >= 500) {
      throw new VizarceApiException(response.statusCode(), response.body());
    }
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getBaseUrl() {
    return baseUrl;
  }
}
