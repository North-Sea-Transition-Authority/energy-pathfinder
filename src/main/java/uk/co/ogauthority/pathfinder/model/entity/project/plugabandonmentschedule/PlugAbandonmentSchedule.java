package uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule;

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
}
