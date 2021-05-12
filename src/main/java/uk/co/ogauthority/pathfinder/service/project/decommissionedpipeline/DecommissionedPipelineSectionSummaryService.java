package uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.decommissionedpipeline.DecommissionedPipelineController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedpipeline.DecommissionedPipeline;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.decommissionedpipeline.DecommissionedPipelineView;
import uk.co.ogauthority.pathfinder.model.view.decommissionedpipeline.DecommissionedPipelineViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
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
  public static final int DISPLAY_ORDER = ProjectTask.PIPELINES.getDisplayOrder();

  private final DecommissionedPipelineService decommissionedPipelineService;
  private final DifferenceService differenceService;
  private final ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Autowired
  public DecommissionedPipelineSectionSummaryService(
      DecommissionedPipelineService decommissionedPipelineService,
      DifferenceService differenceService,
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService
  ) {
    this.decommissionedPipelineService = decommissionedPipelineService;
    this.differenceService = differenceService;
    this.projectSectionSummaryCommonModelService = projectSectionSummaryCommonModelService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return decommissionedPipelineService.canShowInTaskList(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {

    final var summaryModel = projectSectionSummaryCommonModelService.getCommonSummaryModelMap(
        detail,
        PAGE_NAME,
        SECTION_ID
    );

    var decommissionedPipelines = decommissionedPipelineService.getDecommissionedPipelines(detail);
    var decommissionedPipelineViews = getDecommissionedPipelineViews(decommissionedPipelines);
    summaryModel.put("decommissionedPipelineDiffModel", getDecommissionedPipelineDifferenceModel(
        detail,
        decommissionedPipelineViews
    ));
    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private List<Map<String, ?>> getDecommissionedPipelineDifferenceModel(
      ProjectDetail projectDetail,
      List<DecommissionedPipelineView> currentDecommissionedPipelineViews
  ) {
    var previousDecommissionedPipelines = decommissionedPipelineService.getDecommissionedPipelinesByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    );
    var previousDecommissionedPipelineViews = getDecommissionedPipelineViews(previousDecommissionedPipelines);

    return differenceService.differentiateComplexLists(
        currentDecommissionedPipelineViews,
        previousDecommissionedPipelineViews,
        Set.of("summaryLinks"),
        DecommissionedPipelineView::getDisplayOrder,
        DecommissionedPipelineView::getDisplayOrder
    );
  }

  private List<DecommissionedPipelineView> getDecommissionedPipelineViews(List<DecommissionedPipeline> decommissionedPipelines) {
    return IntStream.range(0, decommissionedPipelines.size())
        .mapToObj(index -> {
          var decommissionedPipeline = decommissionedPipelines.get(index);
          var displayIndex = index + 1;

          return DecommissionedPipelineViewUtil.from(decommissionedPipeline, displayIndex);
        })
        .collect(Collectors.toList());
  }
}
