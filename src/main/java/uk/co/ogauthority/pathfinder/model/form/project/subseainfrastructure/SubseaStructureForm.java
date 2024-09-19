package uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure;

import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure.SubseaStructureMass;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.subseastructure.SubseaStructureFullValidation;

public class SubseaStructureForm {

  @NotNull(
      message = "Select the total estimated mass of the subsea structure",
      groups = SubseaStructureFullValidation.class
  )
  private SubseaStructureMass totalEstimatedSubseaMass;

  public SubseaStructureMass getTotalEstimatedSubseaMass() {
    return totalEstimatedSubseaMass;
  }

  public void setTotalEstimatedSubseaMass(SubseaStructureMass totalEstimatedSubseaMass) {
    this.totalEstimatedSubseaMass = totalEstimatedSubseaMass;
  }
}
