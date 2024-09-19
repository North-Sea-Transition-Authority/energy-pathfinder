package uk.co.ogauthority.pathfinder.model.form.project.selectoperator;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class ProjectOperatorForm {

  @NotNull(message = "Select the operator of the project", groups = FullValidation.class)
  private String operator;

  @NotNull(
      message = "Select if this is the operator you want shown on the supply chain interface",
      groups = FullValidation.class
  )
  private Boolean isPublishedAsOperator;

  private String publishableOrganisation;

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public Boolean isPublishedAsOperator() {
    return isPublishedAsOperator;
  }

  public Boolean getIsPublishedAsOperator() {
    return isPublishedAsOperator();
  }

  public void setIsPublishedAsOperator(Boolean isPublishedAsOperator) {
    this.isPublishedAsOperator = isPublishedAsOperator;
  }

  public String getPublishableOrganisation() {
    return publishableOrganisation;
  }

  public void setPublishableOrganisation(String publishableOrganisation) {
    this.publishableOrganisation = publishableOrganisation;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ProjectOperatorForm that = (ProjectOperatorForm) o;
    return Objects.equals(operator, that.operator)
        && Objects.equals(isPublishedAsOperator, that.isPublishedAsOperator)
        && Objects.equals(publishableOrganisation, that.publishableOrganisation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        operator,
        isPublishedAsOperator,
        publishableOrganisation
    );
  }
}
