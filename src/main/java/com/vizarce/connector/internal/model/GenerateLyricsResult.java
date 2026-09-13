package com.vizarce.connector.internal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Mirrors the standalone /api/generate lyrics-generator response shape. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateLyricsResult {

  @JsonProperty("lyrics")
  private String lyrics;

  @JsonProperty("length")
  private int length;

  public String getLyrics() {
    return lyrics;
  }

  public void setLyrics(String lyrics) {
    this.lyrics = lyrics;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }
}
