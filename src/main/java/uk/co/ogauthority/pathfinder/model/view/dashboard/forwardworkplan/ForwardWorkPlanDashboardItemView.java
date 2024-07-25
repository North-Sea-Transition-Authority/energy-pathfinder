package uk.co.ogauthority.pathfinder.model.view.dashboard.forwardworkplan;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.form.useraction.DashboardLink;
import uk.co.ogauthority.pathfinder.model.view.dashboard.DashboardProjectItemView;

public class ForwardWorkPlanDashboardItemView extends DashboardProjectItemView {

  private final DashboardLink dashboardLink;

  public ForwardWorkPlanDashboardItemView(DashboardProjectItemView dashboardProjectItemView,
                                          DashboardLink dashboardLink) {
    super(
        dashboardProjectItemView.getProjectId(),
        dashboardProjectItemView.getProjectTitle(),
        dashboardProjectItemView.getOperatorName(),
        dashboardProjectItemView.getStatus(),
        dashboardProjectItemView.isUpdateRequested(),
        dashboardProjectItemView.getUpdateDeadlineDate(),
        dashboardProjectItemView.getProjectType()
    );
    this.dashboardLink = dashboardLink;
  }

  public DashboardLink getDashboardLink() {
    return dashboardLink;
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

    ForwardWorkPlanDashboardItemView that = (ForwardWorkPlanDashboardItemView) o;
    return Objects.equals(dashboardLink, that.dashboardLink);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        dashboardLink
    );
  }
}
