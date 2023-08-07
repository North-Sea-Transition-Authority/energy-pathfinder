package uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContract;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetup;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSummaryForm;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractView;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractViewUtil;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupRepository;
import uk.co.ogauthority.pathfinder.service.project.OrganisationGroupIdWrapper;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractSummaryService;

@Service
public class ForwardWorkPlanAwardedContractSummaryService extends AwardedContractSummaryService {

  private final ForwardWorkPlanAwardedContractSetupRepository repository;
  private final ForwardWorkPlanAwardedContractService awardedContractService;

  @Autowired
  public ForwardWorkPlanAwardedContractSummaryService(ProjectSectionItemOwnershipService projectSectionItemOwnershipService,
                                                      PortalOrganisationAccessor portalOrganisationAccessor,
                                                      ForwardWorkPlanAwardedContractSetupRepository repository,
                                                      ForwardWorkPlanAwardedContractService awardedContractService) {
    super(projectSectionItemOwnershipService, portalOrganisationAccessor);
    this.repository = repository;
    this.awardedContractService = awardedContractService;
  }

  public ForwardWorkPlanAwardedContractSummaryForm getForm(ProjectDetail projectDetail) {
    var awardedContractSetup = getForwardWorkPlanAwardedContractSetup(projectDetail);
    var form = new ForwardWorkPlanAwardedContractSummaryForm();

    awardedContractSetup.ifPresent(
        forwardWorkPlanAwardedContractSetup ->
            form.setHasOtherContractsToAdd(forwardWorkPlanAwardedContractSetup.getHasOtherContractToAdd())
    );
    return form;
  }

  @Transactional
  public void saveAwardedContractSummary(ForwardWorkPlanAwardedContractSummaryForm form, ProjectDetail projectDetail) {
    var awardedContractSetup = getForwardWorkPlanAwardedContractSetup(projectDetail)
        .orElse(new ForwardWorkPlanAwardedContractSetup(projectDetail));

    awardedContractSetup.setHasOtherContractToAdd(form.getHasOtherContractsToAdd());
    repository.save(awardedContractSetup);
  }

  private Optional<ForwardWorkPlanAwardedContractSetup> getForwardWorkPlanAwardedContractSetup(ProjectDetail projectDetail) {
    return repository.findByProjectDetail(projectDetail);
  }

  @Override
  public List<ForwardWorkPlanAwardedContractView> getAwardedContractViews(ProjectDetail projectDetail) {
    return constructAwardedContractViews(projectDetail, ValidationType.NO_VALIDATION);
  }

  @Override
  public List<ForwardWorkPlanAwardedContractView> getValidatedAwardedContractViews(ProjectDetail projectDetail) {
    return constructAwardedContractViews(projectDetail, ValidationType.FULL);
  }

  public ForwardWorkPlanAwardedContractView getAwardedContractView(Integer awardedContractId,
                                                                   ProjectDetail projectDetail,
                                                                   Integer displayOrder) {
    var awardedContract = awardedContractService.getAwardedContract(awardedContractId, projectDetail);
    return getAwardedContractView(awardedContract, displayOrder);
  }

  private ForwardWorkPlanAwardedContractView getAwardedContractView(ForwardWorkPlanAwardedContract awardedContract, int displayNumber) {
    return getAwardedContractViewBuilder(awardedContract, displayNumber).build();
  }


  private ForwardWorkPlanAwardedContractView getAwardedContractView(ForwardWorkPlanAwardedContract awardedContract,
                                                                    int displayNumber,
                                                                    boolean isValid) {
    return getAwardedContractViewBuilder(awardedContract, displayNumber)
        .isValid(isValid)
        .build();
  }

  private ForwardWorkPlanAwardedContractViewUtil.ForwardWorkPlanAwardedContractViewBuilder getAwardedContractViewBuilder(
      ForwardWorkPlanAwardedContract awardedContract,
      int displayNumber) {
    var includeSummaryLinks = projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        awardedContract.getProjectDetail(),
        new OrganisationGroupIdWrapper(awardedContract.getAddedByOrganisationGroup())
    );
    var addedByPortalOrganisationGroup =
        portalOrganisationAccessor.getOrganisationGroupById(awardedContract.getAddedByOrganisationGroup())
            .orElse(new PortalOrganisationGroup());
    return new ForwardWorkPlanAwardedContractViewUtil.ForwardWorkPlanAwardedContractViewBuilder(
        awardedContract,
        displayNumber,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(includeSummaryLinks);
  }

  private List<ForwardWorkPlanAwardedContractView> constructAwardedContractViews(ProjectDetail projectDetail,
                                                                                 ValidationType validationType) {
    var awardedContracts = awardedContractService.getAwardedContracts(projectDetail);
    return IntStream.range(0, awardedContracts.size())
        .mapToObj(index -> {

          ForwardWorkPlanAwardedContractView awardedContractView;
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
