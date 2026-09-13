package com.vizarce.connector.internal;

import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

/**
 * Connection Provider for the VIZARCE Connector.
 * <p>
 * Uses {@link CachedConnectionProvider} — one {@link VizarceConnection} (and its
 * underlying {@code HttpClient}) is created per distinct configuration and reused
 * across all operation invocations, rather than establishing a new connection per call.
 */
@Alias("connection")
public class VizarceConnectionProvider implements CachedConnectionProvider<VizarceConnection> {

  @Parameter
  @Password
  @Summary("A scoped VIZARCE API key, generated from the VIZARCE Settings > API Keys page.")
  private String apiKey;

  @Parameter
  @Optional(defaultValue = "https://www.vizarce.com/api")
  @Summary("The base URL of the VIZARCE API. Override only for local/staging testing.")
  private String baseUrl;

  @Override
  public VizarceConnection connect() throws ConnectionException {
    VizarceConnection connection = new VizarceConnection(apiKey, baseUrl);
    try {
      connection.testConnectivity();
    } catch (Exception e) {
      throw new ConnectionException("Unable to reach the VIZARCE API at " + baseUrl, e);
    }
    return connection;
  }

  @Override
  public void disconnect(VizarceConnection connection) {
    // The underlying java.net.http.HttpClient has no explicit close/shutdown lifecycle
    // to release — it is garbage-collected once dereferenced. Nothing to do here, but the
    // method is kept (rather than omitted) to make that a documented decision, not an
    // oversight, for anyone maintaining this connector later.
  }

  @Override
  public ConnectionValidationResult validate(VizarceConnection connection) {
    try {
      connection.testConnectivity();
      return ConnectionValidationResult.success();
    } catch (Exception e) {
      return ConnectionValidationResult.failure("VIZARCE API is not reachable", e);
    }
  }
}
