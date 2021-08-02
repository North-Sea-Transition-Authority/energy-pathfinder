package uk.co.ogauthority.pathfinder.testutil;

import java.time.Instant;
import uk.co.ogauthority.pathfinder.model.entity.quarterlystatistics.ReportableProject;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.quarterlystatistics.ReportableProjectView;

public class ReportableProjectTestUtil {

  private static final FieldStage FIELD_STAGE = FieldStage.DEVELOPMENT;
  private static final int PROJECT_DETAIL_ID = 12;
  private static final int PROJECT_ID = 10;
  private static final Instant LAST_UPDATE_DATETIME = Instant.now();
  private static final String OPERATOR_NAME = "Operator name";
  private static final String PROJECT_TITLE = "Project title";
  private static final ProjectType PROJECT_TYPE = ProjectType.INFRASTRUCTURE;

  public static ReportableProject createReportableProject(FieldStage fieldStage) {
    return createReportableProject(
        fieldStage,
        PROJECT_ID,
        PROJECT_DETAIL_ID,
        LAST_UPDATE_DATETIME,
        OPERATOR_NAME,
        PROJECT_TITLE,
        PROJECT_TYPE
    );
  }

  public static ReportableProject createReportableProject(String operatorName, String projectTitle) {
    return createReportableProject(
        FIELD_STAGE,
        PROJECT_ID,
        PROJECT_DETAIL_ID,
        LAST_UPDATE_DATETIME,
        operatorName,
        projectTitle,
        PROJECT_TYPE
    );
  }

  public static ReportableProject createReportableProject(FieldStage fieldStage,
                                                          int projectId,
                                                          int projectDetailId,
                                                          Instant lastUpdateDatetime,
                                                          String operatorName,
                                                          String projectTitle,
                                                          ProjectType projectType) {
    var reportableProject = new ReportableProject();
    reportableProject.setFieldStage(fieldStage);
    reportableProject.setProjectDetailId(projectDetailId);
    reportableProject.setLastUpdatedDatetime(lastUpdateDatetime);
    reportableProject.setProjectId(projectId);
    reportableProject.setOperatorName(operatorName);
    reportableProject.setProjectDisplayName(projectTitle);
    reportableProject.setProjectType(projectType);
    return reportableProject;
  }

  public static ReportableProjectView createReportableProjectView(FieldStage fieldStage) {
    return createReportableProjectView(
        fieldStage,
        PROJECT_ID,
        PROJECT_DETAIL_ID,
        LAST_UPDATE_DATETIME,
        OPERATOR_NAME,
        PROJECT_TITLE,
        PROJECT_TYPE
    );
  }

  public static ReportableProjectView createReportableProjectView(String operatorName, String projectTitle) {
    return createReportableProjectView(
        FIELD_STAGE,
        PROJECT_ID,
        PROJECT_DETAIL_ID,
        LAST_UPDATE_DATETIME,
        operatorName,
        projectTitle,
        PROJECT_TYPE
    );
  }

  public static ReportableProjectView createReportableProjectView(ProjectType projectType) {
    return createReportableProjectView(
        FIELD_STAGE,
        PROJECT_ID,
        PROJECT_DETAIL_ID,
        LAST_UPDATE_DATETIME,
        OPERATOR_NAME,
        PROJECT_TITLE,
        projectType
    );
  }

  public static ReportableProjectView createReportableProjectView(ProjectType projectType,
                                                                  Instant lastUpdateDatetime) {
    return createReportableProjectView(
        FIELD_STAGE,
        PROJECT_ID,
        PROJECT_DETAIL_ID,
        lastUpdateDatetime,
        OPERATOR_NAME,
        PROJECT_TITLE,
        projectType
    );
  }

  public static ReportableProjectView createReportableProjectView(FieldStage fieldStage,
                                                                  int projectId,
                                                                  Instant lastUpdateDatetime) {
    return createReportableProjectView(
        fieldStage,
        projectId,
        PROJECT_DETAIL_ID,
        lastUpdateDatetime,
        OPERATOR_NAME,
        PROJECT_TITLE,
        PROJECT_TYPE
    );
  }

  public static ReportableProjectView createReportableProjectView(FieldStage fieldStage,
                                                                  int projectId,
                                                                  int projectDetailId,
                                                                  Instant lastUpdateDatetime,
                                                                  String operatorName,
                                                                  String projectTitle,
                                                                  ProjectType projectType) {
    final var reportableProject = createReportableProject(
        fieldStage,
        projectId,
        projectDetailId,
        lastUpdateDatetime,
        operatorName,
        projectTitle,
        projectType
    );
    return new ReportableProjectView(reportableProject);
  }

}
