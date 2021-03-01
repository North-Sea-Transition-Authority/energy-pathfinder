package uk.co.ogauthority.pathfinder.service.project.subseainfrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.subseainfrastructure.SubseaInfrastructureController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.subseainfrastructure.SubseaInfrastructure;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.subseainfrastructure.SubseaInfrastructureView;
import uk.co.ogauthority.pathfinder.model.view.subseainfrastructure.SubseaInfrastructureViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class SubseaInfrastructureSectionSummaryService implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH = "project/subseainfrastructure/subseaInfrastructureSectionSummary.ftl";
  public static final String PAGE_NAME = SubseaInfrastructureController.TASK_LIST_NAME;
  public static final String SECTION_ID = "subseaInfrastructure";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = ProjectTask.SUBSEA_INFRASTRUCTURE.getDisplayOrder();

  private final SubseaInfrastructureService subseaInfrastructureService;
  private final DifferenceService differenceService;

  @Autowired
  public SubseaInfrastructureSectionSummaryService(SubseaInfrastructureService subseaInfrastructureService,
                                                   DifferenceService differenceService) {
    this.subseaInfrastructureService = subseaInfrastructureService;
    this.differenceService = differenceService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return subseaInfrastructureService.canShowInTaskList(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("sectionTitle", PAGE_NAME);
    summaryModel.put("sectionId", SECTION_ID);
    var subseaInfrastructures = subseaInfrastructureService.getSubseaInfrastructures(detail);
    var subseaInfrastructureViews = getSubseaInfrastructureViews(subseaInfrastructures);
    summaryModel.put("subseaInfrastructureDiffModel", getSubseaInfrastructureDifferenceModel(
        detail,
        subseaInfrastructureViews
    ));
    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private List<Map<String, ?>> getSubseaInfrastructureDifferenceModel(
      ProjectDetail projectDetail,
      List<SubseaInfrastructureView> currentSubseaInfrastructureViews
  ) {
    var previousSubseaInfrastructures = subseaInfrastructureService.getSubseaInfrastructuresByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    );
    var previousSubseaInfrastructureViews = getSubseaInfrastructureViews(previousSubseaInfrastructures);

    List<Map<String, ?>> subseaInfrastructureDiffList = new ArrayList<>();

    currentSubseaInfrastructureViews.forEach(subseaInfrastructureView -> {
      var subseaInfrastructureModel = new HashMap<String, Object>();

      var previousSubseaInfrastructureView = previousSubseaInfrastructureViews
          .stream()
          .filter(view -> view.getDisplayOrder().equals(subseaInfrastructureView.getDisplayOrder()))
          .findFirst()
          .orElse(new SubseaInfrastructureView());

      var subseaInfrastructureDiffModel = differenceService.differentiate(
          subseaInfrastructureView,
          previousSubseaInfrastructureView,
          Set.of("summaryLinks")
      );

      subseaInfrastructureModel.put("subseaInfrastructureDiff", subseaInfrastructureDiffModel);
      subseaInfrastructureModel.put(
          "concreteMattress",
          Boolean.TRUE.equals(subseaInfrastructureView.getConcreteMattress())
      );
      subseaInfrastructureModel.put(
          "subseaStructure",
          Boolean.TRUE.equals(subseaInfrastructureView.getSubseaStructure())
      );
      subseaInfrastructureModel.put(
          "otherInfrastructure",
          Boolean.TRUE.equals(subseaInfrastructureView.getOtherInfrastructure())
      );

      subseaInfrastructureDiffList.add(subseaInfrastructureModel);
    });

    return subseaInfrastructureDiffList;
  }

  private List<SubseaInfrastructureView> getSubseaInfrastructureViews(List<SubseaInfrastructure> subseaInfrastructures) {
    return IntStream.range(0, subseaInfrastructures.size())
        .mapToObj(index -> {
          var subseaInfrastructure = subseaInfrastructures.get(index);
          var displayIndex = index + 1;

          return SubseaInfrastructureViewUtil.from(subseaInfrastructure, displayIndex);
        })
        .collect(Collectors.toList());
  }
}
