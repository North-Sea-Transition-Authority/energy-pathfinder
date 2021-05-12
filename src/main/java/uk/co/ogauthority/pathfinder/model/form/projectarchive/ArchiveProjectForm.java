package uk.co.ogauthority.pathfinder.model.form.projectarchive;

import javax.validation.constraints.NotEmpty;

public class ArchiveProjectForm {

  @NotEmpty(message = "Enter the reason for archiving")
  private String archiveReason;

  public String getArchiveReason() {
    return archiveReason;
  }

  public void setArchiveReason(String archiveReason) {
    this.archiveReason = archiveReason;
  }
}
