package com.vizarce.connector.internal;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;

import com.vizarce.connector.internal.error.VizarceErrorType;

/**
 * VIZARCE Connector — a custom Anypoint Connector built with the Mule SDK, wrapping
 * VIZARCE's production REST API (https://vizarce.com) as native Mule operations.
 * <p>
 * VIZARCE is an AI prompt engineering studio for Suno/AI Songmaker platforms: this
 * connector exposes its Master Prompt Compose engine, standalone Lyrics Generator,
 * Artist DNA descriptor builder, per-section regeneration, and AI-driven Song
 * Structure generator, so a Mule integration flow can drive AI music-prompt
 * generation as one step in a larger orchestration (e.g. fan-out to Salesforce, a
 * DAM, or a notification channel via Scatter-Gather).
 */
@Xml(prefix = "vizarce")
@Extension(name = "VIZARCE")
@Configurations(VizarceConfiguration.class)
@ErrorTypes(VizarceErrorType.class)
public class VizarceConnector {
}
