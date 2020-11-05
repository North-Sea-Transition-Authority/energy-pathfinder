package uk.co.ogauthority.pathfinder.testutil;

import java.time.Instant;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.view.dashboard.DashboardProjectItemView;

public class DashboardProjectItemTestUtil {
  public static final Integer PROJECT_ID = 1;
  public static final Integer PROJECT_DETAIL_ID = 1;
  public static final Instant CREATED_INSTANT = Instant.now();
  public static final ProjectStatus PROJECT_STATUS = ProjectStatus.DRAFT;
  public static final FieldStage FIELD_STAGE = FieldStage.DECOMMISSIONING;
  public static final String PROJECT_TITLE = "Dummy title";
  public static final String FIELD_NAME = "A field";
  public static final String OPERATOR_NAME = "An Operator";
  public static final PortalOrganisationGroup ORGANISATION_GROUP = ProjectOperatorUtil.ORG_GROUP;


  public static DashboardProjectItem getDashboardProjectItem(){
    var dashboardProjectItem = new DashboardProjectItem();
    dashboardProjectItem.setProjectId(PROJECT_ID);
    dashboardProjectItem.setProjectDetailId(PROJECT_DETAIL_ID);
    dashboardProjectItem.setCreatedDatetime(CREATED_INSTANT);
    dashboardProjectItem.setStatus(PROJECT_STATUS);
    dashboardProjectItem.setProjectTitle(PROJECT_TITLE);
    dashboardProjectItem.setFieldStage(FIELD_STAGE);
    dashboardProjectItem.setFieldName(FIELD_NAME);
    dashboardProjectItem.setOrganisationGroup(ORGANISATION_GROUP);

    return dashboardProjectItem;
  }

  public static DashboardProjectItemView getDashboardProjectItemView() {
    return DashboardProjectItemView.from(getDashboardProjectItem());
  }
}
