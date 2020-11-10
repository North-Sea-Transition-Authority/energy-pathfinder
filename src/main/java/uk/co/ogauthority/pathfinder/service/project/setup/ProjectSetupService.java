package uk.co.ogauthority.pathfinder.service.project.setup;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.TaskListSectionQuestion;
import uk.co.ogauthority.pathfinder.repository.project.tasks.ProjectTaskListSetupRepository;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;

/**
 * The service used in the 'Set up your project' page to decide what optional features of a project should be included.
 */
@Service
public class ProjectSetupService {

  public static final String MODEL_AND_VIEW_PATH = "";

  private final ProjectTaskListSetupRepository projectTaskListSetupRepository;
  private final ProjectInformationService projectInformationService;

  @Autowired
  public ProjectSetupService(ProjectTaskListSetupRepository projectTaskListSetupRepository,
                             ProjectInformationService projectInformationService) {
    this.projectTaskListSetupRepository = projectTaskListSetupRepository;
    this.projectInformationService = projectInformationService;
  }

  public Map<String, String> getSectionQuestionsForProjectDetail(ProjectDetail detail) {
    return projectInformationService.isDecomRelated(detail)
        ? TaskListSectionQuestion.getAllAsMap()
        : TaskListSectionQuestion.getNonDecommissioningRelatedAsMap();
  }

  //model and view method get model and view and add attributes

}
