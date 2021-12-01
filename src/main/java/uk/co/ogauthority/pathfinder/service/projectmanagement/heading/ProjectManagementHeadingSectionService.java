package uk.co.ogauthority.pathfinder.service.projectmanagement.heading;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.ProjectManagementHeadingServiceImplementationException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.management.ProjectManagementSectionType;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectManagementSectionService;

@Service
public class ProjectManagementHeadingSectionService implements ProjectManagementSectionService {

  public static final String TEMPLATE_PATH = "projectmanagement/heading/heading.ftl";
  public static final int DISPLAY_ORDER = ProjectManagementSectionType.HEADING.getDisplayOrder();
  public static final ProjectManagementPageSectionType SECTION_TYPE = ProjectManagementPageSectionType.STATIC_CONTENT;

  protected static final String HEADING_TEXT_MODEL_ATTR_NAME = "headingText";
  protected static final String CAPTION_TEXT_MODEL_ATTR_NAME = "captionText";

  private final List<ProjectManagementHeadingService> projectManagementHeadingServiceList;

  @Autowired
  public ProjectManagementHeadingSectionService(
      List<ProjectManagementHeadingService> projectManagementHeadingServiceList
  ) {
    this.projectManagementHeadingServiceList = projectManagementHeadingServiceList;
  }

  @Override
  public ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount user) {

    Map<String, Object> summaryModel = new HashMap<>();

    final var projectManagementHeadingService = projectManagementHeadingServiceList
        .stream()
        .filter(headingService -> headingService.getSupportedProjectType().equals(projectDetail.getProjectType()))
        .findFirst()
        .orElseThrow(() -> new ProjectManagementHeadingServiceImplementationException(String.format(
            "Could not find implementation of ProjectManagementHeadingService that supports ProjectDetail with ID %d and type %s ",
            projectDetail.getId(),
            projectDetail.getProjectType()
        )));

    summaryModel.put(HEADING_TEXT_MODEL_ATTR_NAME, projectManagementHeadingService.getHeadingText(projectDetail));
    summaryModel.put(CAPTION_TEXT_MODEL_ATTR_NAME, projectManagementHeadingService.getCaptionText(projectDetail));

    return new ProjectManagementSection(
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER,
        SECTION_TYPE
    );
  }
}
