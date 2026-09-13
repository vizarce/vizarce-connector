package com.vizarce.connector.internal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mirrors VIZARCE's ComposeResponseBody shape (see apps/web/api/compose.ts) — the two
 * generated prompts plus their length/limit metadata. Mapped from raw JSON via Jackson
 * so downstream DataWeave scripts in the orchestration flow can reference fields
 * directly (e.g. {@code payload.lyricsPrompt}) instead of parsing a raw string.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComposeSongResult {

  @JsonProperty("lyricsPrompt")
  private String lyricsPrompt;

  @JsonProperty("stylePrompt")
  private String stylePrompt;

  @JsonProperty("lyricsPromptLength")
  private int lyricsPromptLength;

  @JsonProperty("stylePromptLength")
  private int stylePromptLength;

  @JsonProperty("lyricsOverLimit")
  private boolean lyricsOverLimit;

  @JsonProperty("styleOverLimit")
  private boolean styleOverLimit;

  public String getLyricsPrompt() {
    return lyricsPrompt;
  }

  public void setLyricsPrompt(String lyricsPrompt) {
    this.lyricsPrompt = lyricsPrompt;
  }

  public String getStylePrompt() {
    return stylePrompt;
  }

  public void setStylePrompt(String stylePrompt) {
    this.stylePrompt = stylePrompt;
  }

  public int getLyricsPromptLength() {
    return lyricsPromptLength;
  }

  public void setLyricsPromptLength(int lyricsPromptLength) {
    this.lyricsPromptLength = lyricsPromptLength;
  }

  public int getStylePromptLength() {
    return stylePromptLength;
  }

  public void setStylePromptLength(int stylePromptLength) {
    this.stylePromptLength = stylePromptLength;
  }

  public boolean isLyricsOverLimit() {
    return lyricsOverLimit;
  }

  public void setLyricsOverLimit(boolean lyricsOverLimit) {
    this.lyricsOverLimit = lyricsOverLimit;
  }

  public boolean isStyleOverLimit() {
    return styleOverLimit;
  }

  public void setStyleOverLimit(boolean styleOverLimit) {
    this.styleOverLimit = styleOverLimit;
  }
}
