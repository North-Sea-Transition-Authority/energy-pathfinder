package uk.co.ogauthority.pathfinder.service.projecttransfer;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.controller.projecttransfer.ProjectTransferController;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationGroupRestController;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projecttransfer.ProjectTransfer;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.projecttransfer.ProjectTransferForm;
import uk.co.ogauthority.pathfinder.model.form.projecttransfer.ProjectTransferFormValidator;
import uk.co.ogauthority.pathfinder.model.form.projecttransfer.ProjectTransferValidationHint;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.projecttransfer.ProjectTransferRepository;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.CancelDraftProjectVersionService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectHeaderSummaryService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class ProjectTransferService {

  public static final String TRANSFER_PROJECT_TEMPLATE_PATH = "projecttransfer/transfer";

  private final ProjectTransferRepository projectTransferRepository;
  private final ProjectOperatorService projectOperatorService;
  private final ProjectUpdateService projectUpdateService;
  private final ProjectHeaderSummaryService projectHeaderSummaryService;
  private final PortalOrganisationAccessor portalOrganisationAccessor;
  private final SearchSelectorService searchSelectorService;
  private final CancelDraftProjectVersionService cancelDraftProjectVersionService;
  private final ValidationService validationService;
  private final ProjectTransferFormValidator projectTransferFormValidator;
  private final BreadcrumbService breadcrumbService;

  @Autowired
  public ProjectTransferService(
      ProjectTransferRepository projectTransferRepository,
      ProjectOperatorService projectOperatorService,
      ProjectUpdateService projectUpdateService,
      ProjectHeaderSummaryService projectHeaderSummaryService,
      PortalOrganisationAccessor portalOrganisationAccessor,
      SearchSelectorService searchSelectorService,
      CancelDraftProjectVersionService cancelDraftProjectVersionService,
      ValidationService validationService,
      ProjectTransferFormValidator projectTransferFormValidator,
      BreadcrumbService breadcrumbService) {
    this.projectTransferRepository = projectTransferRepository;
    this.projectOperatorService = projectOperatorService;
    this.projectUpdateService = projectUpdateService;
    this.projectHeaderSummaryService = projectHeaderSummaryService;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
    this.searchSelectorService = searchSelectorService;
    this.cancelDraftProjectVersionService = cancelDraftProjectVersionService;
    this.validationService = validationService;
    this.projectTransferFormValidator = projectTransferFormValidator;
    this.breadcrumbService = breadcrumbService;
  }

  @Transactional
  public ProjectTransfer transferProject(ProjectDetail latestSubmittedProjectDetail,
                                         AuthenticatedUserAccount user,
                                         ProjectTransferForm form) {
    cancelDraftProjectVersionService.cancelDraftIfExists(latestSubmittedProjectDetail.getProject().getId());

    var newProjectDetail = projectUpdateService.createNewProjectVersion(latestSubmittedProjectDetail, user);

    var fromOrganisationGroup = projectOperatorService.getProjectOperatorByProjectDetailOrError(latestSubmittedProjectDetail)
        .getOrganisationGroup();
    var toOrganisationGroup = portalOrganisationAccessor.getOrganisationGroupOrError(Integer.parseInt(form.getNewOrganisationGroup()));

    projectOperatorService.createOrUpdateProjectOperator(newProjectDetail, toOrganisationGroup);

    var projectTransfer = new ProjectTransfer();
    projectTransfer.setProjectDetail(newProjectDetail);
    projectTransfer.setFromOrganisationGroup(fromOrganisationGroup);
    projectTransfer.setToOrganisationGroup(toOrganisationGroup);
    projectTransfer.setTransferReason(form.getTransferReason());
    projectTransfer.setTransferredInstant(Instant.now());
    projectTransfer.setTransferredByWuaId(user.getWuaId());
    return projectTransferRepository.save(projectTransfer);
  }

  public Optional<ProjectTransfer> getProjectTransfer(ProjectDetail projectDetail) {
    return projectTransferRepository.findByProjectDetail(projectDetail);
  }

  public BindingResult validate(ProjectTransferForm form, BindingResult bindingResult, ProjectDetail projectDetail) {
    var organisationGroup = projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)
        .getOrganisationGroup();
    projectTransferFormValidator.validate(form, bindingResult, new ProjectTransferValidationHint(organisationGroup));
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  public Map<String, String> getPreSelectedOrgGroup(ProjectTransferForm form) {
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

  public ModelAndView getTransferProjectModelAndView(ProjectDetail projectDetail,
                                                     AuthenticatedUserAccount user,
                                                     ProjectTransferForm form) {
    var projectId = projectDetail.getProject().getId();
    var organisationGroup = projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)
        .getOrganisationGroup();
    var modelAndView = new ModelAndView(TRANSFER_PROJECT_TEMPLATE_PATH)
        .addObject("projectHeaderHtml", projectHeaderSummaryService.getProjectHeaderHtml(projectDetail, user))
        .addObject("currentOperator", organisationGroup.getName())
        .addObject("form", form)
        .addObject("preselectedOperator", getPreSelectedOrgGroup(form))
        .addObject("operatorsRestUrl", SearchSelectorService.route(on(OrganisationGroupRestController.class)
            .searchOrganisations(null)))
        .addObject("cancelUrl", ReverseRouter.route(on(ManageProjectController.class)
            .getProject(projectId, null, null, null)));
    breadcrumbService.fromManageProject(projectId, modelAndView, ProjectTransferController.PAGE_NAME);
    return modelAndView;
  }
}
