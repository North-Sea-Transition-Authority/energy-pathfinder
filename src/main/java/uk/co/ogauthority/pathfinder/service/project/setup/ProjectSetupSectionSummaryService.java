package uk.co.ogauthority.pathfinder.service.project.setup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.setup.ProjectSetupController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionAnswer;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionQuestion;
import uk.co.ogauthority.pathfinder.model.view.setup.ProjectSetupSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.summary.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class ProjectSetupSectionSummaryService implements ProjectSectionSummaryService {
  public static final String TEMPLATE_PATH = "project/setup/projectSetupSectionSummary.ftl";
  public static final String PAGE_NAME = ProjectSetupController.PAGE_NAME;
  public static final String SECTION_ID = "projectSetup";
  public static final int DISPLAY_ORDER = ProjectTask.PROJECT_SETUP.getDisplayOrder();
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );


  private final ProjectSetupService projectSetupService;

  @Autowired
  public ProjectSetupSectionSummaryService(ProjectSetupService projectSetupService) {
    this.projectSetupService = projectSetupService;
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("sectionTitle", PAGE_NAME);
    summaryModel.put("sectionId", SECTION_ID);
    var answers = getAllSummaryItems(projectSetupService.isDecomRelated(detail));

    projectSetupService.getProjectTaskListSetup(detail).ifPresent(
        ts -> ts.getTaskListAnswers().forEach(
            a -> setAnswer(a, answers)
        ));

    summaryModel.put("answers", answers);
    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private List<ProjectSetupSummaryItem> getAllSummaryItems(boolean isDecomRelated) {
    var items = isDecomRelated
        ? TaskListSectionQuestion.getAllValues()
        : TaskListSectionQuestion.getNonDecommissioningRelatedValues();

    return items.stream()
        .map(tlq -> new ProjectSetupSummaryItem(tlq, tlq.getPrompt()))
        .collect(Collectors.toList());
  }

  //For the given answer set it's value in the list
  private void setAnswer(TaskListSectionAnswer answer, List<ProjectSetupSummaryItem> summaryItems) {
    summaryItems.stream()
        .filter(si -> si.getQuestion().getYesAnswer().equals(answer) || si.getQuestion().getNoAnswer().equals(answer))
        .findFirst() //Will only have answered each question once
        .ifPresent(a -> a.setAnswerValue(answer.getAnswerValue()));
  }
}
