package com.vizarce.connector;

import com.vizarce.connector.internal.VizarceApiException;
import com.vizarce.connector.internal.VizarceConnection;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Lightweight unit tests for {@link VizarceConnection}. Full integration testing
 * (real HTTP calls against a live VIZARCE sandbox/instance) belongs in a separate
 * MUnit test suite once this connector is consumed inside an actual Mule application —
 * these tests cover the connection object's own logic in isolation.
 */
public class VizarceConnectionTest {

  @Test
  public void trailingSlashIsStrippedFromBaseUrl() {
    VizarceConnection connection = new VizarceConnection("test-key", "https://www.vizarce.com/api/");
    assertEquals("https://www.vizarce.com/api", connection.getBaseUrl());
  }

  @Test
  public void baseUrlWithoutTrailingSlashIsUnchanged() {
    VizarceConnection connection = new VizarceConnection("test-key", "https://www.vizarce.com/api");
    assertEquals("https://www.vizarce.com/api", connection.getBaseUrl());
  }

  @Test
  public void apiKeyIsStoredAsGiven() {
    VizarceConnection connection = new VizarceConnection("my-secret-key", "https://www.vizarce.com/api");
    assertEquals("my-secret-key", connection.getApiKey());
  }

  @Test
  public void apiExceptionCarriesStatusCodeAndBody() {
    VizarceApiException exception = new VizarceApiException(429, "{\"error\":\"rate limited\"}");
    assertEquals(429, exception.getStatusCode());
    assertTrue(exception.getResponseBody().contains("rate limited"));
    assertTrue(exception.getMessage().contains("429"));
  }

  @Test
  public void connectingToAnUnreachableHostFailsFast() {
    // A reserved, non-routable address (TEST-NET-1, RFC 5737) — guaranteed not to
    // resolve to a real VIZARCE instance, used here purely to exercise the
    // connectivity-failure path without depending on network mocking infrastructure.
    VizarceConnection connection = new VizarceConnection("test-key", "http://192.0.2.1");
    try {
      connection.testConnectivity();
      fail("Expected an exception connecting to a non-routable address");
    } catch (Exception e) {
      // Expected — any exception here (timeout, connect refused, etc.) confirms the
      // method does not silently succeed against an unreachable host.
    }
  }
}
