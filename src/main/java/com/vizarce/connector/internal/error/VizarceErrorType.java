package com.vizarce.connector.internal.error;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

/**
 * VIZARCE:CONNECTIVITY   — the VIZARCE API could not be reached at all (network/DNS/timeout).
 *                          Intended to be caught by a Circuit Breaker scope in the
 *                          orchestration flow.
 * VIZARCE:RATE_LIMITED   — HTTP 429 from VIZARCE's Upstash-backed rate limiter.
 *                          Intended to be caught by an Until Successful retry scope
 *                          with backoff.
 * VIZARCE:API_ERROR      — any other non-2xx response from VIZARCE (400/401/500/...).
 * VIZARCE:INVALID_RESPONSE — VIZARCE returned 2xx but the JSON body did not match the
 *                          expected shape (defensive — mirrors the "never trust the AI
 *                          output blindly" structural-safety-net pattern already used
 *                          throughout the VIZARCE backend itself).
 */
public enum VizarceErrorType implements ErrorTypeDefinition<VizarceErrorType> {
  CONNECTIVITY,
  RATE_LIMITED,
  API_ERROR,
  INVALID_RESPONSE
}
