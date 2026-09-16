package com.vizarce.connector.internal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RefineResult {

  @JsonProperty("revisedText")
  private String revisedText;

  public String getRevisedText() {
    return revisedText;
  }

  public void setRevisedText(String revisedText) {
    this.revisedText = revisedText;
  }
}
