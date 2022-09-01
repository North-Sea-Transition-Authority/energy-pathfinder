package uk.co.ogauthority.pathfinder.model.form.project.commissionedwell;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;

public class CommissionedWellForm {

  private MinMaxDateInput commissioningSchedule;

  private List<Integer> wells = Collections.emptyList();

  private String wellSelected;

  public MinMaxDateInput getCommissioningSchedule() {
    return commissioningSchedule;
  }

  public void setCommissioningSchedule(MinMaxDateInput commissioningSchedule) {
    this.commissioningSchedule = commissioningSchedule;
  }

  public List<Integer> getWells() {
    return wells;
  }

  public void setWells(List<Integer> wells) {
    this.wells = wells;
  }

  public String getWellSelected() {
    return wellSelected;
  }

  public void setWellSelected(String wellSelected) {
    this.wellSelected = wellSelected;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof CommissionedWellForm)) {
      return false;
    }
    CommissionedWellForm that = (CommissionedWellForm) o;
    return Objects.equals(commissioningSchedule, that.commissioningSchedule)
        && Objects.equals(wells, that.wells)
        && Objects.equals(wellSelected, that.wellSelected);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        commissioningSchedule,
        wells,
        wellSelected
    );
  }
}
