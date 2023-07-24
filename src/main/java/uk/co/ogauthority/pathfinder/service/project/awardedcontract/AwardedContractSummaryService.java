package uk.co.ogauthority.pathfinder.service.project.awardedcontract;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.AwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractView;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractViewUtil;
import uk.co.ogauthority.pathfinder.service.project.OrganisationGroupIdWrapper;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class AwardedContractSummaryService {

  public static final String ERROR_FIELD_NAME = "awarded-contract-%d";
  public static final String ERROR_MESSAGE = "Awarded contract %d is incomplete";
  public static final String EMPTY_LIST_ERROR = "You must add at least one awarded contract";

  private final AwardedContractServiceCommon awardedContractService;
  private final ProjectSectionItemOwnershipService projectSectionItemOwnershipService;
  private final PortalOrganisationAccessor portalOrganisationAccessor;

  @Autowired
  public AwardedContractSummaryService(AwardedContractServiceCommon awardedContractService,
                                       ProjectSectionItemOwnershipService projectSectionItemOwnershipService,
                                       PortalOrganisationAccessor portalOrganisationAccessor) {
    this.awardedContractService = awardedContractService;
    this.projectSectionItemOwnershipService = projectSectionItemOwnershipService;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
  }

  public List<AwardedContractView> getAwardedContractViews(ProjectDetail projectDetail) {
    return constructAwardedContractViews(projectDetail, ValidationType.NO_VALIDATION);
  }

  public List<AwardedContractView> getValidatedAwardedContractViews(ProjectDetail projectDetail) {
    return constructAwardedContractViews(projectDetail, ValidationType.FULL);
  }

  public List<ErrorItem> getAwardedContractViewErrors(List<AwardedContractView> awardedContractViews) {
    return SummaryUtil.getErrors(new ArrayList<>(awardedContractViews), EMPTY_LIST_ERROR, ERROR_FIELD_NAME, ERROR_MESSAGE);
  }

  public ValidationResult validateViews(List<AwardedContractView> views) {
    return SummaryUtil.validateViews(new ArrayList<>(views));
  }

  public AwardedContractView getAwardedContractView(Integer awardedContractId,
                                                    ProjectDetail projectDetail,
                                                    Integer displayOrder) {
    var awardedContract = awardedContractService.getAwardedContract(awardedContractId, projectDetail);
    return getAwardedContractView(awardedContract, displayOrder);
  }

  private AwardedContractView getAwardedContractView(AwardedContract  awardedContract, int displayNumber) {
    return getAwardedContractViewBuilder(awardedContract, displayNumber).build();
  }

  private AwardedContractView getAwardedContractView(AwardedContract  awardedContract,
                                                     int displayNumber,
                                                     boolean isValid) {
    return getAwardedContractViewBuilder(awardedContract, displayNumber)
        .isValid(isValid)
        .build();
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

  private List<AwardedContractView> constructAwardedContractViews(ProjectDetail projectDetail,
                                                                  ValidationType validationType) {
    var awardedContracts = awardedContractService.getAwardedContracts(projectDetail);
    return IntStream.range(0, awardedContracts.size())
        .mapToObj(index -> {

          AwardedContractView awardedContractView;
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
