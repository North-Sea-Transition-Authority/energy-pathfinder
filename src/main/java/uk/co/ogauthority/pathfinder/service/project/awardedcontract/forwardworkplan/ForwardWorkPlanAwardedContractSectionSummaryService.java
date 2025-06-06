package uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan;

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
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractView;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.OrganisationGroupIdWrapper;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractSectionSummaryService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class ForwardWorkPlanAwardedContractSectionSummaryService
    extends AwardedContractSectionSummaryService
    implements ProjectSectionSummaryService {

  private final ForwardWorkPlanAwardedContractService awardedContractService;
  private final ForwardWorkPlanAwardedContractSetupService setupService;

  public static final String TEMPLATE_PATH = "project/awardedcontract/forwardworkplan/forwardWorkPlanAwardedContractSectionSummary.ftl";
  public static final int DISPLAY_ORDER = ProjectTask.WORK_PLAN_AWARDED_CONTRACTS.getDisplayOrder();

  @Autowired
  public ForwardWorkPlanAwardedContractSectionSummaryService(
      DifferenceService differenceService,
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService,
      ProjectSectionItemOwnershipService projectSectionItemOwnershipService,
      PortalOrganisationAccessor portalOrganisationAccessor,
      ForwardWorkPlanAwardedContractService awardedContractService,
      ForwardWorkPlanAwardedContractSetupService setupService) {
    super(differenceService, projectSectionSummaryCommonModelService, projectSectionItemOwnershipService,
        portalOrganisationAccessor);
    this.awardedContractService = awardedContractService;
    this.setupService = setupService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return ProjectService.isForwardWorkPlanProject(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    var awardedContracts = awardedContractService.getAwardedContracts(detail);
    var awardedContractViews = getAwardedContractViews(awardedContracts);
    var awardedContractViewDifferenceModel = getAwardedContractDifferenceModel(
        detail,
        awardedContractViews
    );

    var summaryModel =  super.getSummaryModel(detail, awardedContractViewDifferenceModel);
    summaryModel.put("awardedContractSetupDiffModel", getAwardedContractSetupDifferenceModel(detail));
    return super.getSummary(summaryModel, TEMPLATE_PATH, DISPLAY_ORDER);
  }

  private List<Map<String, ?>> getAwardedContractDifferenceModel(
      ProjectDetail projectDetail,
      List<ForwardWorkPlanAwardedContractView> currentAwardedContractViews
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
        ForwardWorkPlanAwardedContractView::getDisplayOrder,
        ForwardWorkPlanAwardedContractView::getDisplayOrder
    );
  }

  private Map<String, Object> getAwardedContractSetupDifferenceModel(ProjectDetail projectDetail) {
    var currentSetupView = setupService.getAwardedContractSetupView(projectDetail);
    var previousSetupView = setupService.getAwardedContractSetupView(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    );

    return differenceService.differentiate(
        currentSetupView,
        previousSetupView
    );
  }


  private List<ForwardWorkPlanAwardedContractView> getAwardedContractViews(List<ForwardWorkPlanAwardedContract> awardedContracts) {
    return IntStream.range(0, awardedContracts.size())
        .mapToObj(index -> {
          var awardedContract = awardedContracts.get(index);
          var displayIndex = index + 1;

          return getAwardedContractView(awardedContract, displayIndex);
        })
        .collect(Collectors.toList());
  }

  public ForwardWorkPlanAwardedContractView getAwardedContractView(ForwardWorkPlanAwardedContract awardedContract, int displayOrder) {
    return getAwardedContractViewBuilder(awardedContract, displayOrder).build();
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
}
