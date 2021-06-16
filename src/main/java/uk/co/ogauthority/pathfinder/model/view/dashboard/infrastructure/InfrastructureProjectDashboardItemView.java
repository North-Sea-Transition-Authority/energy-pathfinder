package uk.co.ogauthority.pathfinder.model.view.dashboard.infrastructure;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.form.useraction.DashboardLink;
import uk.co.ogauthority.pathfinder.model.view.dashboard.DashboardProjectItemView;

public class InfrastructureProjectDashboardItemView extends DashboardProjectItemView {

  private DashboardLink dashboardLink;

  private String fieldStage;

  private String fieldName;

  public InfrastructureProjectDashboardItemView(DashboardProjectItemView dashboardProjectItemView,
                                                DashboardLink dashboardLink,
                                                String fieldStage,
                                                String fieldName) {
    super(
        dashboardProjectItemView.getProjectTitle(),
        dashboardProjectItemView.getOperatorName(),
        dashboardProjectItemView.getStatus(),
        dashboardProjectItemView.isUpdateRequested(),
        dashboardProjectItemView.getUpdateDeadlineDate(),
        dashboardProjectItemView.getProjectType()
    );
    this.dashboardLink = dashboardLink;
    this.fieldStage = fieldStage;
    this.fieldName = fieldName;
  }

  public DashboardLink getDashboardLink() {
    return dashboardLink;
  }

  public void setDashboardLink(DashboardLink dashboardLink) {
    this.dashboardLink = dashboardLink;
  }

  public String getFieldStage() {
    return fieldStage;
  }

  public void setFieldStage(String fieldStage) {
    this.fieldStage = fieldStage;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    if (!super.equals(o)) {
      return false;
    }

    InfrastructureProjectDashboardItemView that = (InfrastructureProjectDashboardItemView) o;
    return Objects.equals(dashboardLink, that.dashboardLink)
        && Objects.equals(fieldStage, that.fieldStage)
        && Objects.equals(fieldName, that.fieldName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        dashboardLink,
        fieldStage,
        fieldName
    );
  }
}
