package com.vizarce.connector.internal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Mirrors /api/structure's {@code action: "regenerate-section"} response — the replacement text for a single section. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegenerateSectionResult {

  @JsonProperty("sectionText")
  private String sectionText;

  public String getSectionText() {
    return sectionText;
  }

  public void setSectionText(String sectionText) {
    this.sectionText = sectionText;
  }
}
