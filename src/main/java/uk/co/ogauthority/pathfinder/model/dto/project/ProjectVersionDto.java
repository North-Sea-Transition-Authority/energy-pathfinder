package uk.co.ogauthority.pathfinder.model.dto.project;

import java.time.Instant;

public class ProjectVersionDto {

  private final int version;

  private final Instant submittedInstant;

  private final boolean noUpdate;

  public ProjectVersionDto(int version, Instant submittedInstant, boolean noUpdate) {
    this.version = version;
    this.submittedInstant = submittedInstant;
    this.noUpdate = noUpdate;
  }

  public int getVersion() {
    return version;
  }

  public Instant getSubmittedInstant() {
    return submittedInstant;
  }

  public boolean isNoUpdate() {
    return noUpdate;
  }
}
