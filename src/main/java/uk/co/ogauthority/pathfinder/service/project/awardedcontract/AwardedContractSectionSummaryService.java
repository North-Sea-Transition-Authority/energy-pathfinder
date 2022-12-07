package uk.co.ogauthority.pathfinder.service.project.awardedcontract;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.AwardedContractController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractView;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.OrganisationGroupIdWrapper;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
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
  private final DifferenceService differenceService;
  private final ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;
  private final ProjectSectionItemOwnershipService projectSectionItemOwnershipService;
  private final PortalOrganisationAccessor portalOrganisationAccessor;

  @Autowired
  public AwardedContractSectionSummaryService(
      AwardedContractService awardedContractService,
      DifferenceService differenceService,
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService,
      ProjectSectionItemOwnershipService projectSectionItemOwnershipService,
      PortalOrganisationAccessor portalOrganisationAccessor) {
    this.awardedContractService = awardedContractService;
    this.differenceService = differenceService;
    this.projectSectionSummaryCommonModelService = projectSectionSummaryCommonModelService;
    this.projectSectionItemOwnershipService = projectSectionItemOwnershipService;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return awardedContractService.isTaskValidForProjectDetail(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {

    final var summaryModel = projectSectionSummaryCommonModelService.getCommonSummaryModelMap(
        detail,
        PAGE_NAME,
        SECTION_ID
    );

    var awardedContracts = awardedContractService.getAwardedContracts(detail);
    var awardedContractViews = getAwardedContractViews(awardedContracts);
    summaryModel.put("awardedContractDiffModel", getAwardedContractDifferenceModel(
        detail,
        awardedContractViews
    ));
    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private List<Map<String, ?>> getAwardedContractDifferenceModel(
      ProjectDetail projectDetail,
      List<AwardedContractView> currentAwardedContractViews
  ) {
    var previousAwardedContracts = awardedContractService.getAwardedContractsByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    );
    var previousAwardedContractViews = getAwardedContractViews(previousAwardedContracts);

    return differenceService.differentiateComplexLists(
        currentAwardedContractViews,
        previousAwardedContractViews,
        Set.of("summaryLinks"),
        AwardedContractView::getDisplayOrder,
        AwardedContractView::getDisplayOrder
    );
  }

  private List<AwardedContractView> getAwardedContractViews(List<AwardedContract> awardedContracts) {
    return IntStream.range(0, awardedContracts.size())
        .mapToObj(index -> {
          var awardedContract = awardedContracts.get(index);
          var displayIndex = index + 1;

          return getAwardedContractView(awardedContract, displayIndex);
        })
        .collect(Collectors.toList());
  }

  private AwardedContractView getAwardedContractView(AwardedContract awardedContract, int displayOrder) {
    return getAwardedContractViewBuilder(awardedContract, displayOrder).build();
  }

  private AwardedContractViewUtil.AwardedContractViewBuilder getAwardedContractViewBuilder(AwardedContract awardedContract,
                                                                                           int displayNumber) {
    var includeSummaryLinks = projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        awardedContract.getProjectDetail(),
        new OrganisationGroupIdWrapper(awardedContract.getAddedByOrganisationGroup())
    );
    var addedByPortalOrganisationGroup =
        portalOrganisationAccessor.getOrganisationGroupById(awardedContract.getAddedByOrganisationGroup())
            .orElse(new PortalOrganisationGroup());
    return new AwardedContractViewUtil.AwardedContractViewBuilder(
        awardedContract,
        displayNumber,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(includeSummaryLinks);
  }
}
