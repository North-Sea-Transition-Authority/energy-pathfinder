package uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.service.entityduplication.ParentEntity;

@Entity
@Table(name = "commissioned_well_schedules")
public class CommissionedWellSchedule extends ProjectDetailEntity implements ParentEntity {

  private Integer earliestStartYear;

  private Integer latestCompletionYear;

  public Integer getEarliestStartYear() {
    return earliestStartYear;
  }

  public void setEarliestStartYear(Integer earliestStartYear) {
    this.earliestStartYear = earliestStartYear;
  }

  public Integer getLatestCompletionYear() {
    return latestCompletionYear;
  }

  public void setLatestCompletionYear(Integer latestCompletionYear) {
    this.latestCompletionYear = latestCompletionYear;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!super.equals(o)) {
      return false;
    }

    if (!(o instanceof CommissionedWellSchedule)) {
      return false;
    }

    CommissionedWellSchedule that = (CommissionedWellSchedule) o;
    return Objects.equals(earliestStartYear, that.earliestStartYear)
        && Objects.equals(latestCompletionYear, that.latestCompletionYear);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        earliestStartYear,
        latestCompletionYear
    );
  }

  @Override
  public String toString() {
    return "CommissionedWellSchedule{" +
        "id=" + id +
        ", projectDetail=" + projectDetail +
        ", earliestStartYear=" + earliestStartYear +
        ", latestCompletionYear=" + latestCompletionYear +
        '}';
  }
}
