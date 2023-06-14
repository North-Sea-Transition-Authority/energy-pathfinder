package uk.co.ogauthority.pathfinder.service.project.platformsfpsos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.platformsfpsos.PlatformsFpsosController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.platformfpso.PlatformFpsoView;
import uk.co.ogauthority.pathfinder.model.view.platformfpso.PlatformFpsoViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.upcomingtender.UpcomingTenderView;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class PlatformsFpsosSectionSummaryService implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH = "project/platformsfpsos/platformsFpsoSectionSummary.ftl";
  public static final String PAGE_NAME = PlatformsFpsosController.TASK_LIST_NAME;
  public static final String SECTION_ID = "platform-floating-units";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = ProjectTask.PLATFORM_FPSO.getDisplayOrder();

  private final PlatformsFpsosService platformsFpsosService;
  private final DifferenceService differenceService;
  private final ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Autowired
  public PlatformsFpsosSectionSummaryService(
      PlatformsFpsosService platformsFpsosService,
      DifferenceService differenceService,
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService
  ) {
    this.platformsFpsosService = platformsFpsosService;
    this.differenceService = differenceService;
    this.projectSectionSummaryCommonModelService = projectSectionSummaryCommonModelService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return platformsFpsosService.isTaskValidForProjectDetail(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {

    final var summaryModel = projectSectionSummaryCommonModelService.getCommonSummaryModelMap(
        detail,
        PAGE_NAME,
        SECTION_ID
    );

    var platformFpsos = platformsFpsosService.getPlatformsFpsosByProjectDetail(detail);
    var platformFpsoViews = getPlatformFpsoViews(detail, platformFpsos);
    summaryModel.put("platformFpsoDiffModel", getPlatformFpsoDifferenceModel(
        detail,
        platformFpsoViews
    ));
    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private List<Map<String, ?>> getPlatformFpsoDifferenceModel(
      ProjectDetail projectDetail,
      List<PlatformFpsoView> currentPlatformFpsoViews
  ) {
    var previousPlatformFpsos = platformsFpsosService.getPlatformsFpsosByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    );
    var previousPlatformFpsoViews = getPlatformFpsoViews(projectDetail, previousPlatformFpsos);

    List<Map<String, ?>> platformFpsoDiffList = new ArrayList<>();

    var diff = differenceService.getDiffableList(currentPlatformFpsoViews, previousPlatformFpsoViews);

    diff.forEach(platformFpsoView -> {
      var platformFpsoModel = new HashMap<String, Object>();

      var currentPlatformFpsoView = currentPlatformFpsoViews
          .stream()
          .filter(view -> view.getDisplayOrder().equals(platformFpsoView.getDisplayOrder()))
          .findFirst()
          .orElse(new PlatformFpsoView());

      var previousPlatformFpsoView = previousPlatformFpsoViews
          .stream()
          .filter(view -> view.getDisplayOrder().equals(platformFpsoView.getDisplayOrder()))
          .findFirst()
          .orElse(new PlatformFpsoView());

      var platformFpsoDiffModel = differenceService.differentiate(
          currentPlatformFpsoView,
          previousPlatformFpsoView,
          Set.of("summaryLinks")
      );

      platformFpsoModel.put("platformFpsoDiff", platformFpsoDiffModel);
      platformFpsoModel.put("fpso", platformFpsoView.isFpso());
      platformFpsoModel.put("areSubstructuresExpectedToBeRemoved",
          Boolean.TRUE.equals(platformFpsoView.getSubstructuresExpectedToBeRemoved()));

      platformFpsoDiffList.add(platformFpsoModel);
    });

    return platformFpsoDiffList;
  }

  private List<PlatformFpsoView> getPlatformFpsoViews(ProjectDetail projectDetail, List<PlatformFpso> platformFpsos) {
    return IntStream.range(0, platformFpsos.size())
        .mapToObj(index -> {
          var platformFpso = platformFpsos.get(index);
          var displayIndex = index + 1;

          return PlatformFpsoViewUtil.createView(platformFpso, displayIndex, projectDetail.getProject().getId());
        })
        .collect(Collectors.toList());
  }
}
