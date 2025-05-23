package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.upcomingtender.UpcomingTendersController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.upcomingtender.UpcomingTenderView;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class UpcomingTenderSectionSummaryService implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH = "project/upcomingtender/upcomingTenderSectionSummary.ftl";
  public static final String PAGE_NAME = UpcomingTendersController.PAGE_NAME;
  public static final String SECTION_ID = "upcoming-tenders";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = ProjectTask.UPCOMING_TENDERS.getDisplayOrder();

  private final UpcomingTenderSummaryService upcomingTenderSummaryService;

  private final DifferenceService differenceService;

  private final ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Autowired
  public UpcomingTenderSectionSummaryService(UpcomingTenderSummaryService upcomingTenderSummaryService,
                                             DifferenceService differenceService,
                                             ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService) {
    this.upcomingTenderSummaryService = upcomingTenderSummaryService;
    this.differenceService = differenceService;
    this.projectSectionSummaryCommonModelService = projectSectionSummaryCommonModelService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return upcomingTenderSummaryService.isTaskValidForProjectDetail(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {

    final var summaryModel = projectSectionSummaryCommonModelService.getCommonSummaryModelMap(
        detail,
        PAGE_NAME,
        SECTION_ID
    );

    var currentUpcomingTenderViews = upcomingTenderSummaryService.getSummaryViews(detail);
    var previousUpcomingTenderViews = upcomingTenderSummaryService.getSummaryViews(
        detail.getProject(),
        detail.getVersion() - 1
    );

    List<Map<String, ?>> upcomingTendersDiffList = new ArrayList<>();

    var diffList = differenceService.getDiffableList(currentUpcomingTenderViews, previousUpcomingTenderViews);

    diffList.forEach(upcomingTenderView -> {

      var upcomingTenderModel = new HashMap<String, Object>();

      var currentUpcomingTenderView = currentUpcomingTenderViews
          .stream()
          .filter(view -> view.getDisplayOrder().equals(upcomingTenderView.getDisplayOrder()))
          .findFirst()
          .orElse(new UpcomingTenderView());

      var previousUpcomingTenderView = previousUpcomingTenderViews
          .stream()
          .filter(view -> view.getDisplayOrder().equals(upcomingTenderView.getDisplayOrder()))
          .findFirst()
          .orElse(new UpcomingTenderView());

      var upcomingTenderDiffModel = differenceService.differentiate(
          currentUpcomingTenderView,
          previousUpcomingTenderView,
          Set.of("summaryLinks", "uploadedFileViews")
      );

      var uploadedFileDiffModel = differenceService.differentiateComplexLists(
          currentUpcomingTenderView.getUploadedFileViews(),
          previousUpcomingTenderView.getUploadedFileViews(),
          Set.of("fileUploadedTime"),
          Set.of("fileUrl"),
          UploadedFileView::getFileId,
          UploadedFileView::getFileId
      );

      upcomingTenderModel.put("upcomingTenderDiff", upcomingTenderDiffModel);
      upcomingTenderModel.put("upcomingTenderFiles", uploadedFileDiffModel);

      upcomingTendersDiffList.add(upcomingTenderModel);

    });

    summaryModel.put("upcomingTenderDiffModel", upcomingTendersDiffList);

    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }
}
