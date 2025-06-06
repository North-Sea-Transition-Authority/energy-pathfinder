package uk.co.ogauthority.pathfinder.service.project.awardedcontract;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.AwardContractController;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;

public abstract class AwardedContractSectionSummaryService {

  public static final String PAGE_NAME = AwardContractController.PAGE_NAME;
  public static final String SECTION_ID = "awardedContract";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );

  protected final DifferenceService differenceService;
  protected final ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;
  protected final ProjectSectionItemOwnershipService projectSectionItemOwnershipService;
  protected final PortalOrganisationAccessor portalOrganisationAccessor;

  @Autowired
  public AwardedContractSectionSummaryService(
      DifferenceService differenceService,
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService,
      ProjectSectionItemOwnershipService projectSectionItemOwnershipService,
      PortalOrganisationAccessor portalOrganisationAccessor) {
    this.differenceService = differenceService;
    this.projectSectionSummaryCommonModelService = projectSectionSummaryCommonModelService;
    this.projectSectionItemOwnershipService = projectSectionItemOwnershipService;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
  }

  public abstract boolean canShowSection(ProjectDetail detail);

  protected Map<String, Object> getSummaryModel(ProjectDetail detail,
                                                List<Map<String, ?>> awardedContractViewDifferenceModel) {
    var summaryModel = projectSectionSummaryCommonModelService.getCommonSummaryModelMap(
        detail,
        PAGE_NAME,
        SECTION_ID
    );

    summaryModel.put("awardedContractDiffModel", awardedContractViewDifferenceModel);

    return summaryModel;
  }

  protected ProjectSectionSummary getSummary(Map<String, Object> summaryModel,
                                             String templatePath,
                                             int displayOrder) {
    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        templatePath,
        summaryModel,
        displayOrder
    );
  }

}
