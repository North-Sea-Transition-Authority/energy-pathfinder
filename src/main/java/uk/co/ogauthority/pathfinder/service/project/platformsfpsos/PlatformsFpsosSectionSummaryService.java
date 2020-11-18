package uk.co.ogauthority.pathfinder.service.project.platformsfpsos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.platformsfpsos.PlatformsFpsosController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.platformfpso.PlatformFpsoViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.summary.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class PlatformsFpsosSectionSummaryService implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH = "project/platformsfpsos/platformsFpsoSectionSummary.ftl";
  public static final String PAGE_NAME = PlatformsFpsosController.SUMMARY_PAGE_NAME;
  public static final String SECTION_ID = "platformFpso";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = ProjectTask.PLATFORM_FPSO.getDisplayOrder();

  private final PlatformsFpsosService platformsFpsosService;

  @Autowired
  public PlatformsFpsosSectionSummaryService(PlatformsFpsosService platformsFpsosService) {
    this.platformsFpsosService = platformsFpsosService;
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("sectionTitle", PAGE_NAME);
    summaryModel.put("sectionId", SECTION_ID);
    var platformsFpsos = platformsFpsosService.getPlatformsFpsosForDetail(detail);
    var platformFpsoViews = IntStream.range(0, platformsFpsos.size())
        .mapToObj(index -> {
          var platformFpso = platformsFpsos.get(index);
          var displayIndex = index + 1;

          return PlatformFpsoViewUtil.createView(platformFpso, displayIndex, detail.getProject().getId());
        })
        .collect(Collectors.toList());
    summaryModel.put("platformFpsoViews", platformFpsoViews);
    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }
}
