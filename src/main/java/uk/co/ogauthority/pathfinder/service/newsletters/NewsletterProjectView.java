package uk.co.ogauthority.pathfinder.service.newsletters;

import uk.co.ogauthority.pathfinder.model.entity.quarterlystatistics.ReportableProject;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;

public final class NewsletterProjectView {

  private final String project;

  private final FieldStage fieldStage;

  public NewsletterProjectView(ReportableProject reportableProject) {
    this(
        reportableProject.getOperatorName(),
        reportableProject.getProjectDisplayName(),
        reportableProject.getFieldStage()
        );
  }

  private NewsletterProjectView(
      String operatorName,
      String projectDisplayName,
      FieldStage fieldStage
  ) {
    this.project = String.format(
        "%s - %s",
        operatorName,
        projectDisplayName
    );

    this.fieldStage = fieldStage;
  }

  public String getProject() {
    return project;
  }

  public FieldStage getFieldStage() {
    return fieldStage;
  }
}
