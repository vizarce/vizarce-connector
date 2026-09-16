package com.vizarce.connector.internal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotateStressResult {

  @JsonProperty("accentedLyrics")
  private String accentedLyrics;

  public String getAccentedLyrics() {
    return accentedLyrics;
  }

  public void setAccentedLyrics(String accentedLyrics) {
    this.accentedLyrics = accentedLyrics;
  }
}
