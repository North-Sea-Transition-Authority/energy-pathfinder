package uk.co.ogauthority.pathfinder.service.project.awardedcontract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.AwardedContractController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.summary.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class AwardedContractSectionSummaryService implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH = "project/awardedcontract/awardedContractSectionSummary.ftl";
  public static final String PAGE_NAME = AwardedContractController.PAGE_NAME;
  public static final String SECTION_ID = "awardedContract";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = ProjectTask.AWARDED_CONTRACTS.getDisplayOrder();

  private final AwardedContractService awardedContractService;

  @Autowired
  public AwardedContractSectionSummaryService(AwardedContractService awardedContractService) {
    this.awardedContractService = awardedContractService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return awardedContractService.canShowInTaskList(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("sectionTitle", PAGE_NAME);
    summaryModel.put("sectionId", SECTION_ID);
    var awardedContracts = awardedContractService.getAwardedContracts(detail);
    var awardedContractViews = IntStream.range(0, awardedContracts.size())
        .mapToObj(index -> {
          var awardedContract = awardedContracts.get(index);
          var displayIndex = index + 1;

          return AwardedContractViewUtil.from(awardedContract, displayIndex);
        })
        .collect(Collectors.toList());
    summaryModel.put("awardedContractViews", awardedContractViews);
    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }
}
