package uk.co.ogauthority.pathfinder.model.view.projectarchive;

import java.util.Objects;

public class ProjectArchiveDetailView {

  private String archiveReason;

  private String archivedDate;

  private String archivedByUserName;

  private String archivedByUserEmailAddress;

  public String getArchiveReason() {
    return archiveReason;
  }

  public void setArchiveReason(String archiveReason) {
    this.archiveReason = archiveReason;
  }

  public String getArchivedDate() {
    return archivedDate;
  }

  public void setArchivedDate(String archivedDate) {
    this.archivedDate = archivedDate;
  }

  public String getArchivedByUserName() {
    return archivedByUserName;
  }

  public void setArchivedByUserName(String archivedByUserName) {
    this.archivedByUserName = archivedByUserName;
  }

  public String getArchivedByUserEmailAddress() {
    return archivedByUserEmailAddress;
  }

  public void setArchivedByUserEmailAddress(String archivedByUserEmailAddress) {
    this.archivedByUserEmailAddress = archivedByUserEmailAddress;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectArchiveDetailView that = (ProjectArchiveDetailView) o;
    return Objects.equals(getArchiveReason(), that.getArchiveReason())
        && Objects.equals(getArchivedDate(), that.getArchivedDate())
        && Objects.equals(getArchivedByUserName(), that.getArchivedByUserName())
        && Objects.equals(getArchivedByUserEmailAddress(), that.getArchivedByUserEmailAddress());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getArchiveReason(), getArchivedDate(), getArchivedByUserName(), getArchivedByUserEmailAddress());
  }
}
