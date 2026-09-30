package com.vizarce.connector.internal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Mirrors /api/structure's {@code action: "from-concept"} response — an ordered list of section
 * names chosen by the AI from VIZARCE's ALL_SECTIONS taxonomy, already filtered
 * server-side against hallucinated names.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateStructureResult {

  @JsonProperty("sections")
  private List<String> sections;

  public List<String> getSections() {
    return sections;
  }

  public void setSections(List<String> sections) {
    this.sections = sections;
  }
}
