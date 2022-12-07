package uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.service.entityduplication.ParentEntity;

@Entity
@Table(name = "plug_abandonment_schedules")
public class PlugAbandonmentSchedule extends ProjectDetailEntity implements ParentEntity {

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
    if (!(o instanceof PlugAbandonmentSchedule)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    PlugAbandonmentSchedule that = (PlugAbandonmentSchedule) o;
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
}
