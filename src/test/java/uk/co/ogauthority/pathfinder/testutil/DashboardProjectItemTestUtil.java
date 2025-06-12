package uk.co.ogauthority.pathfinder.testutil;

import java.time.Instant;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;

public class DashboardProjectItemTestUtil {
  public static final Integer PROJECT_ID = 1;
  public static final Integer PROJECT_DETAIL_ID = 1;
  public static final Instant CREATED_INSTANT = Instant.now();
  public static final ProjectStatus PROJECT_STATUS = ProjectStatus.DRAFT;
  public static final Integer VERSION = 1;
  public static final FieldStage FIELD_STAGE = FieldStage.HYDROGEN;
  public static final String PROJECT_TITLE = "Dummy title";
  public static final String FIELD_NAME = "A field";
  public static final UkcsArea UKCS_AREA = UkcsArea.WOS;
  public static final PortalOrganisationGroup ORGANISATION_GROUP = ProjectOperatorTestUtil.ORG_GROUP;
  public static final Instant SORT_KEY = Instant.now();
  public static final Instant UPDATE_SORT_KEY = Instant.now();

  public static TestDashboardProjectItem getDashboardProjectItem() {
    return getDashboardProjectItem(PROJECT_TITLE, FIELD_STAGE, FIELD_NAME, UKCS_AREA, PROJECT_STATUS, ORGANISATION_GROUP);
  }

  public static TestDashboardProjectItem getDashboardProjectItem(PortalOrganisationGroup organisationGroup) {
    return getDashboardProjectItem(PROJECT_TITLE, FIELD_STAGE, FIELD_NAME, UKCS_AREA, PROJECT_STATUS, organisationGroup);
  }

  public static TestDashboardProjectItem getDashboardProjectItem(String projectTitle){
    return getDashboardProjectItem(projectTitle, FIELD_STAGE, FIELD_NAME, UKCS_AREA, PROJECT_STATUS, ORGANISATION_GROUP);
  }

  public static TestDashboardProjectItem getDashboardProjectItem(FieldStage fieldStage){
    return getDashboardProjectItem(PROJECT_TITLE, fieldStage, FIELD_NAME, UKCS_AREA, PROJECT_STATUS, ORGANISATION_GROUP);
  }

  public static TestDashboardProjectItem getDashboardProjectItem_withField(String fieldName){
    return getDashboardProjectItem(PROJECT_TITLE, FIELD_STAGE, fieldName, UKCS_AREA, PROJECT_STATUS, ORGANISATION_GROUP);
  }

  public static TestDashboardProjectItem getDashboardProjectItem(UkcsArea ukcsArea){
    return getDashboardProjectItem(PROJECT_TITLE, FIELD_STAGE, FIELD_NAME, ukcsArea, PROJECT_STATUS, ORGANISATION_GROUP);
  }

  public static TestDashboardProjectItem getDashboardProjectItem(ProjectStatus status){
    return getDashboardProjectItem(PROJECT_TITLE, FIELD_STAGE, FIELD_NAME, UKCS_AREA, status, ORGANISATION_GROUP);
  }

  public static TestDashboardProjectItem getDashboardProjectItem(
      String projectTitle,
      FieldStage fieldStage,
      String fieldName,
      UkcsArea ukcsArea,
      ProjectStatus status,
      PortalOrganisationGroup organisationGroup
      ){
    var dashboardProjectItem = new TestDashboardProjectItem();
    dashboardProjectItem.setProjectId(PROJECT_ID);
    dashboardProjectItem.setProjectDetailId(PROJECT_DETAIL_ID);
    dashboardProjectItem.setCreatedDatetime(CREATED_INSTANT);
    dashboardProjectItem.setStatus(status);
    dashboardProjectItem.setVersion(VERSION);
    dashboardProjectItem.setProjectTitle(projectTitle);
    dashboardProjectItem.setFieldStage(fieldStage);
    dashboardProjectItem.setFieldName(fieldName);
    dashboardProjectItem.setUkcsArea(ukcsArea);
    dashboardProjectItem.setOrganisationGroup(organisationGroup);
    dashboardProjectItem.setSortKey(SORT_KEY);
    dashboardProjectItem.setUpdateSortKey(UPDATE_SORT_KEY);
    dashboardProjectItem.setProjectType(ProjectType.INFRASTRUCTURE);
    return dashboardProjectItem;
  }
}
