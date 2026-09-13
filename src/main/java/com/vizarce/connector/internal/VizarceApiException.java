package com.vizarce.connector.internal;

/**
 * Thrown when the VIZARCE API returns a non-2xx response. Carries the HTTP status
 * code and raw response body so operation-level error handlers (and the connector's
 * typed {@code VIZARCE:API_ERROR} error type) can inspect exactly what went wrong —
 * e.g. a 429 from Upstash rate limiting vs. a 400 validation error vs. a 500.
 */
public class VizarceApiException extends Exception {

  private final int statusCode;
  private final String responseBody;

  public VizarceApiException(int statusCode, String responseBody) {
    super("VIZARCE API returned HTTP " + statusCode + ": " + responseBody);
    this.statusCode = statusCode;
    this.responseBody = responseBody;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getResponseBody() {
    return responseBody;
  }
}
