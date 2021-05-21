package uk.co.ogauthority.pathfinder.service.project.submission;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.exception.UnsupportedProjectSubmissionSummaryServiceException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.submission.ProjectNoUpdateSubmissionSummaryView;
import uk.co.ogauthority.pathfinder.model.view.submission.ProjectSubmissionSummaryView;

@Service
public class ProjectSubmissionSummaryViewService {

  private final List<ProjectSubmissionSummaryService> projectSubmissionSummaryServices;

  @Autowired
  public ProjectSubmissionSummaryViewService(List<ProjectSubmissionSummaryService> projectSubmissionSummaryServices) {
    this.projectSubmissionSummaryServices = projectSubmissionSummaryServices;
  }

  public ProjectSubmissionSummaryView getProjectSubmissionSummaryView(ProjectDetail projectDetail) {
    final var submissionSummaryService = getProjectSubmissionSummaryService(
        projectDetail.getProjectType()
    );
    return submissionSummaryService.getSubmissionSummaryView(projectDetail);
  }

  public ProjectNoUpdateSubmissionSummaryView getProjectNoUpdateSubmissionSummaryView(ProjectDetail projectDetail) {
    final var submissionSummaryService = getProjectSubmissionSummaryService(
        projectDetail.getProjectType()
    );
    return submissionSummaryService.getNoUpdateSubmissionSummaryView(projectDetail);
  }

  private ProjectSubmissionSummaryService getProjectSubmissionSummaryService(ProjectType projectType) {
    return projectSubmissionSummaryServices
        .stream()
        .filter(
            projectSubmissionSummaryService -> projectSubmissionSummaryService.getSupportedProjectType().equals(projectType)
        )
        .findFirst()
        .orElseThrow(() -> new UnsupportedProjectSubmissionSummaryServiceException(
            String.format(
                "Could not find implementation of ProjectSubmissionSummaryService supporting project type %s",
                projectType
            )
        ));
  }
}
