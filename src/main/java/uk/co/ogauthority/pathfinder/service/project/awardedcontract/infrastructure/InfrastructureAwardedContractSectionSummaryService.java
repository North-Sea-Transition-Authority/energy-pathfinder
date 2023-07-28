package uk.co.ogauthority.pathfinder.service.project.awardedcontract.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.InfrastructureAwardedContract;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.infrastructure.InfrastructureAwardedContractView;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.infrastructure.InfrastructureAwardedContractViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.OrganisationGroupIdWrapper;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractSectionSummaryService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class InfrastructureAwardedContractSectionSummaryService
    extends AwardedContractSectionSummaryService
    implements ProjectSectionSummaryService {

  private final InfrastructureAwardedContractService awardedContractService;
  public static final String TEMPLATE_PATH = "project/awardedcontract/infrastructure/infrastructureAwardedContractSectionSummary.ftl";

  @Autowired
  public InfrastructureAwardedContractSectionSummaryService(DifferenceService differenceService,
                                                            ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService,
                                                            ProjectSectionItemOwnershipService projectSectionItemOwnershipService,
                                                            PortalOrganisationAccessor portalOrganisationAccessor,
                                                            InfrastructureAwardedContractService awardedContractService) {
    super(
        differenceService,
        projectSectionSummaryCommonModelService,
        projectSectionItemOwnershipService,
        portalOrganisationAccessor
    );
    this.awardedContractService = awardedContractService;
  }


  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return awardedContractService.isTaskValidForProjectDetail(detail);
  }

  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    var awardedContracts = awardedContractService.getAwardedContracts(detail);
    var awardedContractViews = getAwardedContractViews(awardedContracts);
    var awardedContractViewDifferenceModel = getAwardedContractDifferenceModel(
        detail,
        awardedContractViews
    );

    return super.getSummary(detail, awardedContractViewDifferenceModel, TEMPLATE_PATH);
  }

  private List<Map<String, ?>> getAwardedContractDifferenceModel(
      ProjectDetail projectDetail,
      List<InfrastructureAwardedContractView> currentAwardedContractViews
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
        InfrastructureAwardedContractView::getDisplayOrder,
        InfrastructureAwardedContractView::getDisplayOrder
    );
  }


  private List<InfrastructureAwardedContractView> getAwardedContractViews(List<InfrastructureAwardedContract> awardedContracts) {
    return IntStream.range(0, awardedContracts.size())
        .mapToObj(index -> {
          var awardedContract = awardedContracts.get(index);
          var displayIndex = index + 1;

          return getAwardedContractView(awardedContract, displayIndex);
        })
        .collect(Collectors.toList());
  }

  private InfrastructureAwardedContractView getAwardedContractView(InfrastructureAwardedContract awardedContract, int displayOrder) {
    return getAwardedContractViewBuilder(awardedContract, displayOrder).build();
  }

  private InfrastructureAwardedContractViewUtil.InfrastructureAwardedContractViewBuilder getAwardedContractViewBuilder(
      InfrastructureAwardedContract awardedContract,
      int displayNumber) {
    var includeSummaryLinks = projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        awardedContract.getProjectDetail(),
        new OrganisationGroupIdWrapper(awardedContract.getAddedByOrganisationGroup())
    );
    var addedByPortalOrganisationGroup =
        portalOrganisationAccessor.getOrganisationGroupById(awardedContract.getAddedByOrganisationGroup())
            .orElse(new PortalOrganisationGroup());
    return new InfrastructureAwardedContractViewUtil.InfrastructureAwardedContractViewBuilder(
        awardedContract,
        displayNumber,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(includeSummaryLinks);
  }

}
