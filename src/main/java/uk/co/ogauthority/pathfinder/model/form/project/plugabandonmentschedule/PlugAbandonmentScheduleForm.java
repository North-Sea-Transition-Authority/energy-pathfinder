package uk.co.ogauthority.pathfinder.model.form.project.plugabandonmentschedule;

import jakarta.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class PlugAbandonmentScheduleForm {

  private MinMaxDateInput plugAbandonmentDate;

  @NotEmpty(message = "Select at least one well", groups = FullValidation.class)
  private List<Integer> wells = Collections.emptyList();

  private String wellsSelect;

  public MinMaxDateInput getPlugAbandonmentDate() {
    return plugAbandonmentDate;
  }

  public void setPlugAbandonmentDate(
      MinMaxDateInput plugAbandonmentDate) {
    this.plugAbandonmentDate = plugAbandonmentDate;
  }

  public List<Integer> getWells() {
    return wells;
  }

  public void setWells(List<Integer> wells) {
    this.wells = wells;
  }

  public String getWellsSelect() {
    return wellsSelect;
  }

  public void setWellsSelect(String wellsSelect) {
    this.wellsSelect = wellsSelect;
  }
}
