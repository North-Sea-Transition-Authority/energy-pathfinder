package uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure;

import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.concretemattress.ConcreteMattressFullValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.concretemattress.ConcreteMattressPartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.positivewholenumber.PositiveWholeNumberGreaterThanZero;

public class ConcreteMattressForm {

  @NotNull(message = "Enter the number of mattresses", groups = ConcreteMattressFullValidation.class)
  @PositiveWholeNumberGreaterThanZero(
      messagePrefix = "Number of mattresses",
      groups = { ConcreteMattressFullValidation.class, ConcreteMattressPartialValidation.class }
  )
  private Integer numberOfMattresses;

  @NotNull(message = "Enter the total estimated mass of the mattresses", groups = ConcreteMattressFullValidation.class)
  @PositiveWholeNumberGreaterThanZero(
      messagePrefix = "Total estimated mattresses mass",
      groups = { ConcreteMattressFullValidation.class, ConcreteMattressPartialValidation.class }
  )
  private Integer totalEstimatedMattressMass;

  public Integer getNumberOfMattresses() {
    return numberOfMattresses;
  }

  public void setNumberOfMattresses(Integer numberOfMattresses) {
    this.numberOfMattresses = numberOfMattresses;
  }

  public Integer getTotalEstimatedMattressMass() {
    return totalEstimatedMattressMass;
  }

  public void setTotalEstimatedMattressMass(Integer totalEstimatedMattressMass) {
    this.totalEstimatedMattressMass = totalEstimatedMattressMass;
  }
}
