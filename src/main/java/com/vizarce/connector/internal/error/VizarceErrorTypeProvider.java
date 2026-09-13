package com.vizarce.connector.internal.error;

import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.HashSet;
import java.util.Set;

/**
 * Declares the full set of error types an operation may raise, referenced via
 * {@code @Throws(VizarceErrorTypeProvider.class)} on each operation method. This is what
 * makes VIZARCE:RATE_LIMITED / VIZARCE:CONNECTIVITY / VIZARCE:API_ERROR / VIZARCE:INVALID_RESPONSE
 * selectable as distinct error types in the "On Error Continue/Propagate" scope's Type
 * field in Anypoint Studio's flow designer — not just visible in a stack trace.
 */
public class VizarceErrorTypeProvider implements ErrorTypeProvider {

  @Override
  public Set<ErrorTypeDefinition> getErrorTypes() {
    Set<ErrorTypeDefinition> errors = new HashSet<>();
    errors.add(VizarceErrorType.CONNECTIVITY);
    errors.add(VizarceErrorType.RATE_LIMITED);
    errors.add(VizarceErrorType.API_ERROR);
    errors.add(VizarceErrorType.INVALID_RESPONSE);
    return errors;
  }
}
