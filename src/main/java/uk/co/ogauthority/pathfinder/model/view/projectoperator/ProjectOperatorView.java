package uk.co.ogauthority.pathfinder.model.view.projectoperator;

import java.util.Objects;

public class ProjectOperatorView {

  private String operatorName;

  private String isPublishedAsOperator;

  private String publishableOrganisationName;

  public String getOperatorName() {
    return operatorName;
  }

  public void setOperatorName(String operatorName) {
    this.operatorName = operatorName;
  }

  public String getIsPublishedAsOperator() {
    return isPublishedAsOperator;
  }

  public void setIsPublishedAsOperator(String isPublishedAsOperator) {
    this.isPublishedAsOperator = isPublishedAsOperator;
  }

  public String getPublishableOrganisationName() {
    return publishableOrganisationName;
  }

  public void setPublishableOrganisationName(String publishableOrganisationName) {
    this.publishableOrganisationName = publishableOrganisationName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ProjectOperatorView projectOperatorView = (ProjectOperatorView) o;

    return Objects.equals(operatorName, projectOperatorView.getOperatorName())
        && Objects.equals(isPublishedAsOperator, projectOperatorView.getIsPublishedAsOperator())
        && Objects.equals(publishableOrganisationName, projectOperatorView.getPublishableOrganisationName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        operatorName,
        isPublishedAsOperator,
        publishableOrganisationName
    );
  }
}
