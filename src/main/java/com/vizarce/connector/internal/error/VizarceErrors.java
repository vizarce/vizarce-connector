package com.vizarce.connector.internal.error;

import org.mule.runtime.extension.api.exception.ModuleException;

/**
 * Connector-level error types, following the same typed-error philosophy already used
 * in the Order Processing EDA project (APP:VALIDATION_ERROR, APP:ORDER_NOT_FOUND, ...)
 * rather than a single generic catch-all. Each maps to a {@link VizarceErrorType} so
 * downstream Mule flows can branch with an On Error Continue/Propagate scoped to
 * VIZARCE:RATE_LIMITED specifically (e.g. inside the Until Successful retry scope),
 * VIZARCE:CONNECTIVITY (e.g. inside the Circuit Breaker), or VIZARCE:API_ERROR generally.
 */
public class VizarceErrors {

  private VizarceErrors() {
  }

  public static ModuleException rateLimited(String message, Throwable cause) {
    return new ModuleException(message, VizarceErrorType.RATE_LIMITED, cause);
  }

  public static ModuleException apiError(String message, Throwable cause) {
    return new ModuleException(message, VizarceErrorType.API_ERROR, cause);
  }

  public static ModuleException connectivity(String message, Throwable cause) {
    return new ModuleException(message, VizarceErrorType.CONNECTIVITY, cause);
  }

  public static ModuleException invalidResponse(String message, Throwable cause) {
    return new ModuleException(message, VizarceErrorType.INVALID_RESPONSE, cause);
  }
}
