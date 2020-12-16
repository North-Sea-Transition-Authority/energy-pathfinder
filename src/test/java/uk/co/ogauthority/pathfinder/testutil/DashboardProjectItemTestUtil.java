package uk.co.ogauthority.pathfinder.testutil;

import java.time.Instant;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;
import uk.co.ogauthority.pathfinder.model.view.dashboard.DashboardProjectItemView;

public class DashboardProjectItemTestUtil {
  public static final Integer PROJECT_ID = 1;
  public static final Integer PROJECT_DETAIL_ID = 1;
  public static final Instant CREATED_INSTANT = Instant.now();
  public static final ProjectStatus PROJECT_STATUS = ProjectStatus.DRAFT;
  public static final FieldStage FIELD_STAGE = FieldStage.DECOMMISSIONING;
  public static final String PROJECT_TITLE = "Dummy title";
  public static final String FIELD_NAME = "A field";
  public static final UkcsArea UKCS_AREA = UkcsArea.WOS;
  public static final PortalOrganisationGroup ORGANISATION_GROUP = ProjectOperatorTestUtil.ORG_GROUP;
  public static final Instant SORT_KEY = Instant.now();

  public static DashboardProjectItem getDashboardProjectItem() {
    return getDashboardProjectItem(PROJECT_TITLE, FIELD_STAGE, FIELD_NAME, UKCS_AREA, PROJECT_STATUS, ORGANISATION_GROUP);
  }

  public static DashboardProjectItem getDashboardProjectItem(PortalOrganisationGroup organisationGroup) {
    return getDashboardProjectItem(PROJECT_TITLE, FIELD_STAGE, FIELD_NAME, UKCS_AREA, PROJECT_STATUS, organisationGroup);
  }

  public static DashboardProjectItem getDashboardProjectItem(String projectTitle){
    return getDashboardProjectItem(projectTitle, FIELD_STAGE, FIELD_NAME, UKCS_AREA, PROJECT_STATUS, ORGANISATION_GROUP);
  }

  public static DashboardProjectItem getDashboardProjectItem(FieldStage fieldStage){
    return getDashboardProjectItem(PROJECT_TITLE, fieldStage, FIELD_NAME, UKCS_AREA, PROJECT_STATUS, ORGANISATION_GROUP);
  }

  public static DashboardProjectItem getDashboardProjectItem_withField(String fieldName){
    return getDashboardProjectItem(PROJECT_TITLE, FIELD_STAGE, fieldName, UKCS_AREA, PROJECT_STATUS, ORGANISATION_GROUP);
  }

  public static DashboardProjectItem getDashboardProjectItem(UkcsArea ukcsArea){
    return getDashboardProjectItem(PROJECT_TITLE, FIELD_STAGE, FIELD_NAME, ukcsArea, PROJECT_STATUS, ORGANISATION_GROUP);
  }

  public static DashboardProjectItem getDashboardProjectItem(ProjectStatus status){
    return getDashboardProjectItem(PROJECT_TITLE, FIELD_STAGE, FIELD_NAME, UKCS_AREA, status, ORGANISATION_GROUP);
  }

  public static DashboardProjectItem getDashboardProjectItem(
      String projectTitle,
      FieldStage fieldStage,
      String fieldName,
      UkcsArea ukcsArea,
      ProjectStatus status,
      PortalOrganisationGroup organisationGroup
      ){
    var dashboardProjectItem = new DashboardProjectItem();
    dashboardProjectItem.setProjectId(PROJECT_ID);
    dashboardProjectItem.setProjectDetailId(PROJECT_DETAIL_ID);
    dashboardProjectItem.setCreatedDatetime(CREATED_INSTANT);
    dashboardProjectItem.setStatus(status);
    dashboardProjectItem.setProjectTitle(projectTitle);
    dashboardProjectItem.setFieldStage(fieldStage);
    dashboardProjectItem.setFieldName(fieldName);
    dashboardProjectItem.setUkcsArea(ukcsArea);
    dashboardProjectItem.setOrganisationGroup(organisationGroup);
    dashboardProjectItem.setSortKey(SORT_KEY);

    return dashboardProjectItem;
  }

  public static DashboardProjectItemView getDashboardProjectItemView() {
    return DashboardProjectItemView.from(getDashboardProjectItem());
  }
}
