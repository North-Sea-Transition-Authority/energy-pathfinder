package uk.co.ogauthority.pathfinder.service.project.setup;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.setup.ProjectSetupController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionAnswer;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.setup.ProjectSetupSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
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

  private final ProjectService projectService;
  private final ProjectSetupService projectSetupService;
  private final DifferenceService differenceService;
  private final ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Autowired
  public ProjectSetupSectionSummaryService(
      ProjectService projectService,
      ProjectSetupService projectSetupService,
      DifferenceService differenceService,
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService
  ) {
    this.projectService = projectService;
    this.projectSetupService = projectSetupService;
    this.differenceService = differenceService;
    this.projectSectionSummaryCommonModelService = projectSectionSummaryCommonModelService;
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {

    final var summaryModel = projectSectionSummaryCommonModelService.getCommonSummaryModelMap(
        detail,
        PAGE_NAME,
        SECTION_ID
    );

    var summaryItems = getSummaryItems(detail);
    summaryModel.put("projectSetupDiffModel", getProjectSetupDifferenceModel(
        detail,
        summaryItems
    ));
    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private List<Map<String, ?>> getProjectSetupDifferenceModel(
      ProjectDetail projectDetail,
      List<ProjectSetupSummaryItem> summaryItems
  ) {
    var previousSummaryItems = projectService.getDetail(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )
        .map(this::getSummaryItems)
        .orElse(Collections.emptyList());

    return differenceService.differentiateComplexLists(
        summaryItems,
        previousSummaryItems,
        Set.of("question"),
        ProjectSetupSummaryItem::getQuestion,
        ProjectSetupSummaryItem::getQuestion
    );
  }

  protected List<ProjectSetupSummaryItem> getSummaryItems(ProjectDetail projectDetail) {

    var setupSummaryItems = projectSetupService.getSectionQuestionsForProjectDetail(projectDetail)
        .stream()
        .map(sectionQuestion -> new ProjectSetupSummaryItem(sectionQuestion, sectionQuestion.getPrompt()))
        .collect(Collectors.toList());

    projectSetupService.getProjectTaskListSetup(projectDetail).ifPresent(
        taskListSetup -> taskListSetup.getTaskListAnswers().forEach(
            taskListSectionAnswer -> setAnswer(taskListSectionAnswer, setupSummaryItems)
        ));

    return setupSummaryItems;
  }

  //For the given answer set its value in the list
  private void setAnswer(TaskListSectionAnswer answer, List<ProjectSetupSummaryItem> summaryItems) {
    summaryItems.stream()
        .filter(si -> si.getQuestion().getYesAnswer().equals(answer) || si.getQuestion().getNoAnswer().equals(answer))
        .findFirst() //Will only have answered each question once
        .ifPresent(a -> a.setAnswerValue(answer.getAnswerValue()));
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return projectSetupService.isTaskValidForProjectDetail(detail);
  }
}
