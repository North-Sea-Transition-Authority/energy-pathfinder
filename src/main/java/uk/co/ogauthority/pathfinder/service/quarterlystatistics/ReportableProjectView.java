package uk.co.ogauthority.pathfinder.service.quarterlystatistics;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.model.entity.quarterlystatistics.ReportableProject;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public final class ReportableProjectView {

  private final Integer projectId;

  private final String viewProjectUrl;

  private final FieldStage fieldStage;

  private final Instant lastUpdatedDatetime;

  private final String lastUpdatedDatetimeFormatted;

  private final String operatorName;

  private final String projectTitle;

  private final boolean hasUpdateInQuarter;

  public ReportableProjectView(ReportableProject reportableProject) {
    this(
        reportableProject.getProjectId(),
        reportableProject.getFieldStage(),
        reportableProject.getLastUpdatedDatetime(),
        reportableProject.getOperatorName(),
        reportableProject.getProjectTitle()
    );
  }

  private ReportableProjectView(Integer projectId,
                                FieldStage fieldStage,
                                Instant lastUpdatedDatetime,
                                String operatorName,
                                String projectTitle) {
    this.projectId = projectId;
    this.viewProjectUrl = ReverseRouter.route(on(ManageProjectController.class).getProject(
        projectId,
        null,
        null,
        null
    ));
    this.fieldStage = fieldStage;
    this.lastUpdatedDatetime = lastUpdatedDatetime;
    this.lastUpdatedDatetimeFormatted = DateUtil.formatInstant(lastUpdatedDatetime);
    this.operatorName = operatorName;
    this.projectTitle = projectTitle;
    this.hasUpdateInQuarter = DateUtil.isInCurrentQuarter(lastUpdatedDatetime);
  }

  public Integer getProjectId() {
    return projectId;
  }

  public String getViewProjectUrl() {
    return viewProjectUrl;
  }

  public FieldStage getFieldStage() {
    return fieldStage;
  }

  public Instant getLastUpdatedDatetime() {
    return lastUpdatedDatetime;
  }

  public String getLastUpdatedDatetimeFormatted() {
    return lastUpdatedDatetimeFormatted;
  }

  public String getOperatorName() {
    return operatorName;
  }

  public String getProjectTitle() {
    return projectTitle;
  }

  public boolean hasUpdateInQuarter() {
    return hasUpdateInQuarter;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ReportableProjectView that = (ReportableProjectView) o;
    return Objects.equals(projectId, that.projectId)
        && Objects.equals(fieldStage, that.fieldStage)
        && Objects.equals(lastUpdatedDatetime, that.lastUpdatedDatetime)
        && Objects.equals(operatorName, that.operatorName)
        && Objects.equals(projectTitle, that.projectTitle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(projectId, fieldStage, lastUpdatedDatetime, operatorName, projectTitle);
  }
}
