package uk.co.ogauthority.pathfinder.service.project.awardedcontract.infrastructure;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.InfrastructureAwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.infrastructure.InfrastructureAwardedContractView;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.infrastructure.InfrastructureAwardedContractViewUtil;
import uk.co.ogauthority.pathfinder.service.project.OrganisationGroupIdWrapper;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractSummaryService;

@Service
public class InfrastructureAwardedContractSummaryService extends AwardedContractSummaryService {

  private final InfrastructureAwardedContractService awardedContractService;

  public InfrastructureAwardedContractSummaryService(
      ProjectSectionItemOwnershipService projectSectionItemOwnershipService,
      PortalOrganisationAccessor portalOrganisationAccessor,
      InfrastructureAwardedContractService awardedContractService) {
    super(projectSectionItemOwnershipService, portalOrganisationAccessor);
    this.awardedContractService = awardedContractService;
  }

  @Override
  public List<InfrastructureAwardedContractView> getAwardedContractViews(ProjectDetail projectDetail) {
    return constructAwardedContractViews(projectDetail, ValidationType.NO_VALIDATION);
  }

  @Override
  public List<InfrastructureAwardedContractView> getValidatedAwardedContractViews(ProjectDetail projectDetail) {
    return constructAwardedContractViews(projectDetail, ValidationType.FULL);
  }

  public InfrastructureAwardedContractView getAwardedContractView(Integer awardedContractId,
                                                          ProjectDetail projectDetail,
                                                          Integer displayOrder) {
    var awardedContract = awardedContractService.getAwardedContract(awardedContractId, projectDetail);
    return getAwardedContractView(awardedContract, displayOrder);
  }

  private InfrastructureAwardedContractView getAwardedContractView(InfrastructureAwardedContract awardedContract, int displayNumber) {
    return getAwardedContractViewBuilder(awardedContract, displayNumber).build();
  }


  private InfrastructureAwardedContractView getAwardedContractView(InfrastructureAwardedContract awardedContract,
                                                           int displayNumber,
                                                           boolean isValid) {
    return getAwardedContractViewBuilder(awardedContract, displayNumber)
        .isValid(isValid)
        .build();
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

  private List<InfrastructureAwardedContractView> constructAwardedContractViews(ProjectDetail projectDetail,
                                                                        ValidationType validationType) {
    var awardedContracts = awardedContractService.getAwardedContracts(projectDetail);
    return IntStream.range(0, awardedContracts.size())
        .mapToObj(index -> {

          InfrastructureAwardedContractView awardedContractView;
          var awardedContract = awardedContracts.get(index);
          var displayIndex = index + 1;

          if (validationType.equals(ValidationType.NO_VALIDATION)) {
            awardedContractView = getAwardedContractView(awardedContract, displayIndex);
          } else {
            var isValid = awardedContractService.isValid(awardedContract, validationType);
            awardedContractView = getAwardedContractView(awardedContract, displayIndex, isValid);
          }

          return awardedContractView;

        })
        .collect(Collectors.toList());
  }
}
