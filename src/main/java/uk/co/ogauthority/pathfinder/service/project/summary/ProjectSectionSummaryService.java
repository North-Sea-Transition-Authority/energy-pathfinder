package uk.co.ogauthority.pathfinder.service.project.summary;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;

public interface ProjectSectionSummaryService {

  default boolean canShowSection(ProjectDetail detail) {
    return false;
  }

  ProjectSectionSummary getSummary(ProjectDetail detail);

}
