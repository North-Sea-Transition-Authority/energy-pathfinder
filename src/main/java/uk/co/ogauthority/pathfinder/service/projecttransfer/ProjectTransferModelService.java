package uk.co.ogauthority.pathfinder.service.projecttransfer;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.controller.projecttransfer.ProjectTransferController;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationGroupRestController;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationUnitRestController;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.projecttransfer.ProjectTransferForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectHeaderSummaryService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Service
public class ProjectTransferModelService {

  protected static final String TRANSFER_PROJECT_TEMPLATE_PATH = "projecttransfer/transfer";

  private final SearchSelectorService searchSelectorService;
  private final PortalOrganisationAccessor portalOrganisationAccessor;
  private final ProjectOperatorService projectOperatorService;
  private final ProjectHeaderSummaryService projectHeaderSummaryService;
  private final BreadcrumbService breadcrumbService;

  @Autowired
  public ProjectTransferModelService(
      SearchSelectorService searchSelectorService,
      PortalOrganisationAccessor portalOrganisationAccessor,
      ProjectOperatorService projectOperatorService,
      ProjectHeaderSummaryService projectHeaderSummaryService,
      BreadcrumbService breadcrumbService
  ) {
    this.searchSelectorService = searchSelectorService;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
    this.projectOperatorService = projectOperatorService;
    this.projectHeaderSummaryService = projectHeaderSummaryService;
    this.breadcrumbService = breadcrumbService;
  }

  protected Map<String, String> getPreSelectedOrgGroup(ProjectTransferForm form) {
    if (form.getNewOrganisationGroup() != null) {
      return searchSelectorService.buildPrePopulatedSelections(
          Collections.singletonList(form.getNewOrganisationGroup()),
          Map.of(
              form.getNewOrganisationGroup(),
              portalOrganisationAccessor.getOrganisationGroupOrError(Integer.parseInt(form.getNewOrganisationGroup())).getName()
          )
      );

    }
    return Map.of();
  }

  protected Map<String, String> getPreSelectedPublishableOrganisation(ProjectTransferForm form) {

    final var publishableOrganisation = form.getPublishableOrganisation();

    if (publishableOrganisation != null) {
      return searchSelectorService.buildPrePopulatedSelections(
          Collections.singletonList(publishableOrganisation),
          Map.of(
              publishableOrganisation,
              getPublishableOrganisationName(publishableOrganisation)
          )
      );
    }

    return Map.of();
  }

  private String getPublishableOrganisationName(String selectedPublishableOrganisation) {
    return portalOrganisationAccessor.getOrganisationUnitOrError(
        Integer.parseInt(selectedPublishableOrganisation)
    ).getSelectionText();
  }

  public ModelAndView getTransferProjectModelAndView(ProjectDetail projectDetail,
                                                     AuthenticatedUserAccount user,
                                                     ProjectTransferForm form) {
    final var projectId = projectDetail.getProject().getId();

    final var organisationGroup = projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)
        .getOrganisationGroup();

    final var modelAndView = new ModelAndView(TRANSFER_PROJECT_TEMPLATE_PATH)
        .addObject("projectHeaderHtml", projectHeaderSummaryService.getProjectHeaderHtml(projectDetail, user))
        .addObject("currentOperator", organisationGroup.getName())
        .addObject("form", form)
        .addObject("preselectedOperator", getPreSelectedOrgGroup(form))
        .addObject("operatorsRestUrl", SearchSelectorService.route(on(OrganisationGroupRestController.class)
            .searchPathfinderOrganisations(null))
        )
        .addObject("cancelUrl", ReverseRouter.route(on(ManageProjectController.class)
            .getProject(projectId, null, null, null))
        )
        .addObject("organisationUnitRestUrl", SearchSelectorService.route(on(OrganisationUnitRestController.class)
            .searchOrganisationUnits(null))
        )
        .addObject("preselectedPublishableOrganisation", getPreSelectedPublishableOrganisation(form));

    breadcrumbService.fromManageProject(projectId, modelAndView, ProjectTransferController.PAGE_NAME);

    return modelAndView;
  }
}
