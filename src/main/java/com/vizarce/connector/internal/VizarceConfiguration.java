package com.vizarce.connector.internal;

import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;

/**
 * A single, default configuration — VIZARCE's API surface does not currently need
 * multiple named configs (e.g. no separate "read" vs "write" credential tiers), so
 * this stays intentionally minimal: it exists to wire the Connection Provider and the
 * Operations container together, per the standard Mule SDK config/operations/connection
 * separation of concerns.
 */
@Operations(VizarceOperations.class)
@ConnectionProviders(VizarceConnectionProvider.class)
public class VizarceConfiguration {
}
