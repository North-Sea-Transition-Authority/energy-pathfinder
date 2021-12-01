package uk.co.ogauthority.pathfinder.service.projectmanagement.details;

import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.ProjectTypeDetailServiceImplementationException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.management.ProjectManagementSectionType;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectManagementSectionService;

@Service
public class ProjectManagementDetailSectionService implements ProjectManagementSectionService {

  protected static final int DISPLAY_ORDER = ProjectManagementSectionType.PROJECT_DETAILS.getDisplayOrder();
  protected static final ProjectManagementPageSectionType SECTION_TYPE = ProjectManagementPageSectionType.STATIC_CONTENT;

  private final List<ProjectManagementDetailService> projectManagementDetailServices;

  @Autowired
  public ProjectManagementDetailSectionService(
      List<ProjectManagementDetailService> projectManagementDetailServices
  ) {
    this.projectManagementDetailServices = projectManagementDetailServices;
  }

  @Override
  public ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount user) {

    final var projectType = projectDetail.getProjectType();

    final var projectManagementDetailSectionService = projectManagementDetailServices
        .stream()
        .filter(projectManagementDetailService ->
            projectManagementDetailService.getSupportedProjectType().equals(projectType)
        )
        .findFirst()
        .orElseThrow(() -> new ProjectTypeDetailServiceImplementationException(
            String.format(
                "Could not find implementation of ProjectManagementDetailService to summarise ProjectDetail with ID %d and type %s ",
                projectDetail.getId(),
                projectType
            )
        ));

    final var summaryModel = new HashMap<String, Object>();
    summaryModel.put(
        "projectManagementDetailView",
        projectManagementDetailSectionService.getManagementDetailView(projectDetail)
    );

    return new ProjectManagementSection(
        projectManagementDetailSectionService.getTemplatePath(),
        summaryModel,
        DISPLAY_ORDER,
        SECTION_TYPE
    );
  }
}
