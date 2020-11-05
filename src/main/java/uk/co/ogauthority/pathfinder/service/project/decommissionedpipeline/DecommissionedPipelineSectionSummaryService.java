package uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.decommissionedpipeline.DecommissionedPipelineController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.decommissionedpipeline.DecommissionedPipelineViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.summary.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class DecommissionedPipelineSectionSummaryService implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH = "project/decommissionedpipeline/decommissionedPipelineSectionSummary.ftl";
  public static final String PAGE_NAME = DecommissionedPipelineController.TASK_LIST_NAME;
  public static final String SECTION_ID = "pipeline";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = 10;

  private final DecommissionedPipelineService decommissionedPipelineService;

  @Autowired
  public DecommissionedPipelineSectionSummaryService(DecommissionedPipelineService decommissionedPipelineService) {
    this.decommissionedPipelineService = decommissionedPipelineService;
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("sectionTitle", PAGE_NAME);
    summaryModel.put("sectionId", SECTION_ID);
    var decommissionedPipelines = decommissionedPipelineService.getDecommissionedPipelines(detail);
    var decommissionedPipelineViews = IntStream.range(0, decommissionedPipelines.size())
        .mapToObj(index -> {
          var decommissionedPipeline = decommissionedPipelines.get(index);
          var displayIndex = index + 1;

          return DecommissionedPipelineViewUtil.from(decommissionedPipeline, displayIndex);
        })
        .collect(Collectors.toList());
    summaryModel.put("decommissionedPipelineViews", decommissionedPipelineViews);
    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }
}
