package uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.wellbore.Wellbore;

@Entity
@Table(name = "plug_abandonment_wells")
public class PlugAbandonmentWell {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "plug_abandonment_schedule_id")
  private PlugAbandonmentSchedule plugAbandonmentSchedule;

  @ManyToOne
  @JoinColumn(name = "wellbore_id")
  private Wellbore wellbore;

  public Integer getId() {
    return id;
  }

  public PlugAbandonmentSchedule getPlugAbandonmentSchedule() {
    return plugAbandonmentSchedule;
  }

  public void setPlugAbandonmentSchedule(
      PlugAbandonmentSchedule plugAbandonmentSchedule) {
    this.plugAbandonmentSchedule = plugAbandonmentSchedule;
  }

  public Wellbore getWellbore() {
    return wellbore;
  }

  public void setWellbore(Wellbore wellbore) {
    this.wellbore = wellbore;
  }
}
