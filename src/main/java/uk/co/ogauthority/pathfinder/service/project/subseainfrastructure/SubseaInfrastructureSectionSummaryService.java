package uk.co.ogauthority.pathfinder.service.project.subseainfrastructure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.subseainfrastructure.SubseaInfrastructureController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.subseainfrastructure.SubseaInfrastructureViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.summary.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class SubseaInfrastructureSectionSummaryService implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH = "project/subseainfrastructure/subseaInfrastructureSectionSummary.ftl";
  public static final String PAGE_NAME = SubseaInfrastructureController.SUMMARY_PAGE_NAME;
  public static final String SECTION_ID = "subseaInfrastructure";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = 8;

  private final SubseaInfrastructureService subseaInfrastructureService;

  @Autowired
  public SubseaInfrastructureSectionSummaryService(SubseaInfrastructureService subseaInfrastructureService) {
    this.subseaInfrastructureService = subseaInfrastructureService;
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("sectionTitle", PAGE_NAME);
    summaryModel.put("sectionId", SECTION_ID);
    var subseaInfrastructures = subseaInfrastructureService.getSubseaInfrastructures(detail);
    var subseaInfrastructureViews = IntStream.range(0, subseaInfrastructures.size())
        .mapToObj(index -> {
          var subseaInfrastructure = subseaInfrastructures.get(index);
          var displayIndex = index + 1;

          return SubseaInfrastructureViewUtil.from(subseaInfrastructure, displayIndex);
        })
        .collect(Collectors.toList());
    summaryModel.put("subseaInfrastructureViews", subseaInfrastructureViews);
    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }
}
