package com.vizarce.connector.internal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mirrors /api/artist-dna's response — a descriptive-only Vocal DNA / Sound DNA pair,
 * with the artist's real name already stripped server-side (stripArtistName()) before
 * this connector ever sees the payload.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtistDnaResult {

  @JsonProperty("vocal")
  private String vocal;

  @JsonProperty("sound")
  private String sound;

  public String getVocal() {
    return vocal;
  }

  public void setVocal(String vocal) {
    this.vocal = vocal;
  }

  public String getSound() {
    return sound;
  }

  public void setSound(String sound) {
    this.sound = sound;
  }
}
