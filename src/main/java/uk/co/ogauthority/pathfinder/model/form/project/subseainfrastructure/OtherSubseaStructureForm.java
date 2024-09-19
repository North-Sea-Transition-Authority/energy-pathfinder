package uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.othersubseastructure.OtherSubseaStructureFullValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.othersubseastructure.OtherSubseaStructurePartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring.LengthRestrictedString;
import uk.co.ogauthority.pathfinder.model.form.validation.positivewholenumber.PositiveWholeNumberGreaterThanZero;

public class OtherSubseaStructureForm {

  @NotEmpty(message = "Enter the type of structure", groups = OtherSubseaStructureFullValidation.class)
  @LengthRestrictedString(
      messagePrefix = "Type of structure",
      groups = { OtherSubseaStructureFullValidation.class, OtherSubseaStructurePartialValidation.class }
  )
  private String typeOfStructure;

  @NotNull(message = "Enter the total estimated mass of the structure", groups = OtherSubseaStructureFullValidation.class)
  @PositiveWholeNumberGreaterThanZero(
      messagePrefix = "Total estimated mass",
      groups = { OtherSubseaStructureFullValidation.class, OtherSubseaStructurePartialValidation.class }
  )
  private Integer totalEstimatedMass;

  public String getTypeOfStructure() {
    return typeOfStructure;
  }

  public void setTypeOfStructure(String typeOfStructure) {
    this.typeOfStructure = typeOfStructure;
  }

  public Integer getTotalEstimatedMass() {
    return totalEstimatedMass;
  }

  public void setTotalEstimatedMass(Integer totalEstimatedMass) {
    this.totalEstimatedMass = totalEstimatedMass;
  }
}
