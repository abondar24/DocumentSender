package org.abondar.experimental.documentsender.writer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record UploadResponse(
      @JsonProperty("message")
      @Schema(description = "file upload message")
      String message
) {
}
