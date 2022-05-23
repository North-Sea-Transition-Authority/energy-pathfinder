package uk.co.ogauthority.pathfinder.service.project.workoplanprojectcontribution;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.workplanprojectcontributor.ForwardWorkPlanProjectContributorsController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.workplanprojectcontributor.ForwardWorkPlanProjectContributorsView;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class ForwardWorkPlanProjectContributorSectionSummaryService implements ProjectSectionSummaryService {

  static final String TEMPLATE_PATH = "project/workplanprojectcontributors/workPlanProjectContributorsSectionSummary.ftl";
  static final String PAGE_NAME = ForwardWorkPlanProjectContributorsController.PAGE_NAME;
  static final String SECTION_ID = "project-contributors";
  static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  static final int DISPLAY_ORDER = ProjectTask.WORK_PLAN_PROJECT_CONTRIBUTORS.getDisplayOrder();

  private final ForwardWorkPlanProjectContributorFormSectionService forwardWorkPlanProjectContributorFormSectionService;
  private final ForwardWorkPlanProjectContributorSummaryService forwardWorkPlanProjectContributorSummaryService;
  private final ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;
  private final DifferenceService differenceService;

  @Autowired
  public ForwardWorkPlanProjectContributorSectionSummaryService(
      ForwardWorkPlanProjectContributorFormSectionService forwardWorkPlanProjectContributorFormSectionService,
      ForwardWorkPlanProjectContributorSummaryService forwardWorkPlanProjectContributorSummaryService,
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService,
      DifferenceService differenceService) {
    this.forwardWorkPlanProjectContributorFormSectionService = forwardWorkPlanProjectContributorFormSectionService;
    this.forwardWorkPlanProjectContributorSummaryService = forwardWorkPlanProjectContributorSummaryService;
    this.projectSectionSummaryCommonModelService = projectSectionSummaryCommonModelService;
    this.differenceService = differenceService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return forwardWorkPlanProjectContributorFormSectionService.isTaskValidForProjectDetail(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    var summaryModel = projectSectionSummaryCommonModelService.getCommonSummaryModelMap(
        detail,
        PAGE_NAME,
        SECTION_ID
    );

    var currentForwardProjectContributorsView =
        forwardWorkPlanProjectContributorSummaryService.getForwardWorkPlanProjectContributorsView(detail);

    summaryModel.put("projectContributorDiffModel", getForwardProjectContributorDifferenceModel(
        detail,
        currentForwardProjectContributorsView
    ));

    summaryModel.put("showContributorsList", canShowContributorsList(currentForwardProjectContributorsView));

    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private Boolean canShowContributorsList(ForwardWorkPlanProjectContributorsView forwardWorkPlanProjectContributorsView) {
    return BooleanUtils.isTrue(forwardWorkPlanProjectContributorsView.getHasProjectContributors());
  }

  private Map<String, ?> getForwardProjectContributorDifferenceModel(
      ProjectDetail detail,
      ForwardWorkPlanProjectContributorsView currentForwardOrganisationGroupViews
  ) {
    var previousForwardProjectContributorViews =
        forwardWorkPlanProjectContributorSummaryService.getForwardWorkPlanProjectContributorsView(
            detail.getProject(),
            detail.getVersion() - 1
        );

    return differenceService.differentiate(
        currentForwardOrganisationGroupViews,
        previousForwardProjectContributorViews
    );
  }
}
