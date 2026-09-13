package com.vizarce.connector.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizarce.connector.internal.error.VizarceErrorTypeProvider;
import com.vizarce.connector.internal.error.VizarceErrors;
import com.vizarce.connector.internal.model.ArtistDnaResult;
import com.vizarce.connector.internal.model.ComposeSongResult;
import com.vizarce.connector.internal.model.GenerateLyricsResult;
import com.vizarce.connector.internal.model.GenerateStructureResult;
import com.vizarce.connector.internal.model.RegenerateSectionResult;

import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import java.net.http.HttpTimeoutException;
import java.util.List;
import java.util.Map;

/**
 * The five operations this connector exposes. Each is a thin wrapper: build the JSON
 * request body, POST it via the shared {@link VizarceConnection}, and deserialize the
 * typed result — the actual AI generation logic lives entirely in VIZARCE's backend,
 * this connector's job is purely transport + typed mapping + error translation.
 */
public class VizarceOperations {

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Runs a full Master Prompt compose — VIZARCE's main generation call, producing both
   * a lyrics-prompt and a style-prompt for the given concept/genre/structure. This is
   * the slowest operation (VIZARCE's own docs note it chains several sequential AI
   * calls internally), so the default timeout is generous — 300 seconds, matching
   * VIZARCE's own serverless function's configured maxDuration.
   */
  @Throws(VizarceErrorTypeProvider.class)
  @Summary("Compose a full song prompt (lyrics + style) from a concept, genre, BPM, and optional structure/vocal/bass context.")
  public ComposeSongResult composeSong(@Connection VizarceConnection connection,
                                        String concept,
                                        String genreTag,
                                        String bpm,
                                        String musicalKey,
                                        @Optional String vocalTag,
                                        @Optional String bassTag,
                                        @Optional String language,
                                        @Optional(defaultValue = "300") int timeoutSeconds) {
    Map<String, Object> body = new java.util.HashMap<>();
    body.put("concept", concept);
    body.put("genreTag", genreTag);
    body.put("bpm", bpm);
    body.put("musicalKey", musicalKey);
    if (vocalTag != null) body.put("vocalTag", vocalTag);
    if (bassTag != null) body.put("bassTag", bassTag);
    if (language != null) body.put("language", language);

    return execute(connection, "/compose", body, timeoutSeconds, ComposeSongResult.class);
  }

  /**
   * Runs VIZARCE's standalone Lyrics Generator — a lighter-weight call than
   * {@link #composeSong} for when only lyrics (no style-prompt) are needed.
   */
  @Throws(VizarceErrorTypeProvider.class)
  @Summary("Generate standalone lyrics from a concept and genre, without a full style-prompt compose.")
  public GenerateLyricsResult generateLyrics(@Connection VizarceConnection connection,
                                              String concept,
                                              String genreTag,
                                              @Optional String language,
                                              @Optional(defaultValue = "60") int timeoutSeconds) {
    Map<String, Object> body = new java.util.HashMap<>();
    body.put("concept", concept);
    body.put("genreTag", genreTag);
    if (language != null) body.put("language", language);

    return execute(connection, "/generate", body, timeoutSeconds, GenerateLyricsResult.class);
  }

  /**
   * Builds a descriptive-only Vocal DNA / Sound DNA pair for a custom artist name.
   * The artist's real name is stripped from the descriptors server-side before this
   * connector ever receives the response (VIZARCE's stripArtistName() safeguard).
   */
  @Throws(VizarceErrorTypeProvider.class)
  @Summary("Generate a descriptive (name-free) Vocal DNA / Sound DNA pair for a custom artist reference.")
  public ArtistDnaResult buildArtistDNA(@Connection VizarceConnection connection,
                                         String artistName,
                                         @Optional(defaultValue = "30") int timeoutSeconds) {
    Map<String, Object> body = Map.of("artistName", artistName);
    return execute(connection, "/artist-dna", body, timeoutSeconds, ArtistDnaResult.class);
  }

  /**
   * Regenerates a single named section of an already-composed lyrics-prompt in place,
   * given the full current lyrics-prompt as context.
   */
  @Throws(VizarceErrorTypeProvider.class)
  @Summary("Regenerate one named section of an existing lyrics-prompt, using the full prompt as context.")
  public RegenerateSectionResult regenerateSection(@Connection VizarceConnection connection,
                                                     String sectionName,
                                                     String fullLyricsPrompt,
                                                     String genreTag,
                                                     @Optional String vocalTag,
                                                     @Optional String bassTag,
                                                     @Optional(defaultValue = "60") int timeoutSeconds) {
    Map<String, Object> body = new java.util.HashMap<>();
    body.put("sectionName", sectionName);
    body.put("fullLyricsPrompt", fullLyricsPrompt);
    body.put("genreTag", genreTag);
    if (vocalTag != null) body.put("vocalTag", vocalTag);
    if (bassTag != null) body.put("bassTag", bassTag);

    return execute(connection, "/regenerate-section", body, timeoutSeconds, RegenerateSectionResult.class);
  }

  /**
   * Given a free-text style/genre description, asks VIZARCE's AI to choose an ordered
   * sequence of section names from its full section taxonomy — the caller must supply
   * that valid-names list (VIZARCE keeps it as the frontend's single source of truth,
   * not duplicated server-side), matching how VIZARCE's own web UI calls this endpoint.
   */
  @Throws(VizarceErrorTypeProvider.class)
  @Summary("Generate a full song structure (ordered section names) from a free-text style/genre description.")
  public GenerateStructureResult generateStructure(@Connection VizarceConnection connection,
                                                     String concept,
                                                     List<String> validSectionNames,
                                                     @Optional(defaultValue = "45") int timeoutSeconds) {
    Map<String, Object> body = Map.of("concept", concept, "validSectionNames", validSectionNames);
    return execute(connection, "/generate-structure-from-concept", body, timeoutSeconds, GenerateStructureResult.class);
  }

  // ---------------------------------------------------------------------------------
  // Shared execute + error-translation logic — kept in one place so all five
  // operations handle connectivity/rate-limiting/API/parse errors identically.
  // ---------------------------------------------------------------------------------

  private <T> T execute(VizarceConnection connection, String path, Map<String, Object> body,
                         int timeoutSeconds, Class<T> resultType) {
    String requestJson;
    try {
      requestJson = objectMapper.writeValueAsString(body);
    } catch (Exception e) {
      throw VizarceErrors.invalidResponse("Failed to serialize request body for " + path, e);
    }

    String responseJson;
    try {
      responseJson = connection.post(path, requestJson, timeoutSeconds);
    } catch (VizarceApiException e) {
      if (e.getStatusCode() == 429) {
        throw VizarceErrors.rateLimited(
            "VIZARCE rate limit hit calling " + path + " — retry with backoff.", e);
      }
      throw VizarceErrors.apiError("VIZARCE API call to " + path + " failed", e);
    } catch (HttpTimeoutException e) {
      throw VizarceErrors.connectivity("Timed out calling VIZARCE " + path
          + " after " + timeoutSeconds + "s", e);
    } catch (Exception e) {
      throw VizarceErrors.connectivity("Could not reach VIZARCE API at " + path, e);
    }

    try {
      return objectMapper.readValue(responseJson, resultType);
    } catch (Exception e) {
      throw VizarceErrors.invalidResponse(
          "VIZARCE " + path + " returned a 2xx response that did not match the expected shape", e);
    }
  }
}
