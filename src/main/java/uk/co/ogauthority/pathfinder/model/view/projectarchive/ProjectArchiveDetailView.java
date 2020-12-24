package uk.co.ogauthority.pathfinder.model.view.projectarchive;

import java.util.Objects;

public class ProjectArchiveDetailView {

  private String archiveReason;

  private String archivedDate;

  private String archivedByUser;

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

  public String getArchivedByUser() {
    return archivedByUser;
  }

  public void setArchivedByUser(String archivedByUser) {
    this.archivedByUser = archivedByUser;
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
        && Objects.equals(getArchivedByUser(), that.getArchivedByUser());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getArchiveReason(), getArchivedDate(), getArchivedByUser());
  }
}
