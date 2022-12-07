package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.campaigninformation.CampaignInformationController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.campaigninformation.CampaignInformationView;
import uk.co.ogauthority.pathfinder.model.view.campaigninformation.CampaignInformationViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class CampaignInformationSectionSummaryService implements ProjectSectionSummaryService {

  protected static final String TEMPLATE_PATH = "project/campaigninformation/campaignInformationSectionSummary.ftl";
  protected static final String PAGE_NAME = CampaignInformationController.PAGE_NAME;
  protected static final String SECTION_ID = "campaignInformation";
  protected static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  protected static final int DISPLAY_ORDER = ProjectTask.CAMPAIGN_INFORMATION.getDisplayOrder();

  private final CampaignInformationService campaignInformationService;
  private final CampaignProjectService campaignProjectService;
  private final ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;
  private final DifferenceService differenceService;

  @Autowired
  public CampaignInformationSectionSummaryService(
      CampaignInformationService campaignInformationService,
      CampaignProjectService campaignProjectService,
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService,
      DifferenceService differenceService
  ) {
    this.campaignInformationService = campaignInformationService;
    this.campaignProjectService = campaignProjectService;
    this.projectSectionSummaryCommonModelService = projectSectionSummaryCommonModelService;
    this.differenceService = differenceService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return campaignInformationService.isTaskValidForProjectDetail(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {

    final var summaryModel = projectSectionSummaryCommonModelService.getCommonSummaryModelMap(
        detail,
        PAGE_NAME,
        SECTION_ID
    );

    final var campaignInformationOptional = campaignInformationService.getCampaignInformationByProjectDetail(detail);

    final var campaignInformationView = convertToCampaignInformationView(campaignInformationOptional);

    var isProjectIncludedInCampaign = campaignInformationOptional.isPresent()
        && BooleanUtils.isTrue(campaignInformationOptional.get().isPartOfCampaign());

    summaryModel.put("isProjectIncludedInCampaign", isProjectIncludedInCampaign);
    summaryModel.put("campaignInformationDiffModel", getCampaignInformationDiffModel(detail, campaignInformationView));

    ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(summaryModel, detail);

    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private Map<String, Object> getCampaignInformationDiffModel(ProjectDetail projectDetail,
                                                              CampaignInformationView currentCampaignInformationView) {
    final var previousCampaignInformationOptional = campaignInformationService.getCampaignInformationByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    );

    final var previousCampaignInformationView = convertToCampaignInformationView(previousCampaignInformationOptional);

    return differenceService.differentiate(
        currentCampaignInformationView,
        previousCampaignInformationView
    );
  }

  private CampaignInformationView convertToCampaignInformationView(Optional<CampaignInformation> campaignInformationOptional) {
    return campaignInformationOptional
        .map(campaignInformation -> {
          final var campaignProjects = campaignProjectService.getCampaignProjects(campaignInformation.getProjectDetail());
          return CampaignInformationViewUtil.from(campaignInformation, campaignProjects);
        })
        .orElse(new CampaignInformationView());
  }
}
