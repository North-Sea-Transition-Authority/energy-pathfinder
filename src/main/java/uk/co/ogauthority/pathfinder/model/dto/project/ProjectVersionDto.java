package uk.co.ogauthority.pathfinder.model.dto.project;

import java.time.Instant;

public class ProjectVersionDto {

  private final int version;

  private final Instant submittedInstant;

  public ProjectVersionDto(int version, Instant submittedInstant) {
    this.version = version;
    this.submittedInstant = submittedInstant;
  }

  public int getVersion() {
    return version;
  }

  public Instant getSubmittedInstant() {
    return submittedInstant;
  }
}
