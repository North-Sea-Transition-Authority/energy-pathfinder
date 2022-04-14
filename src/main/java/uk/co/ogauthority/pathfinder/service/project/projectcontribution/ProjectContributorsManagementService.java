package uk.co.ogauthority.pathfinder.service.project.projectcontribution;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.projectcontributor.ProjectContributorsController;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationGroupRestController;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorValidationHint;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsForm;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsFormValidator;
import uk.co.ogauthority.pathfinder.model.view.organisationgroup.OrganisationGroupView;
import uk.co.ogauthority.pathfinder.repository.project.projectcontributor.ProjectContributorRepository;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Service
public class ProjectContributorsManagementService {

  private final BreadcrumbService breadcrumbService;
  private final PortalOrganisationAccessor portalOrganisationAccessor;
  private final ProjectContributorRepository projectContributorRepository;
  private final ProjectOperatorService projectOperatorService;
  private final ValidationService validationService;
  private final String regulatorSharedEmail;
  private final ProjectContributorsFormValidator projectContributorsFormValidator;

  @Autowired
  public ProjectContributorsManagementService(
      BreadcrumbService breadcrumbService,
      PortalOrganisationAccessor portalOrganisationAccessor,
      ProjectContributorRepository projectContributorRepository,
      ProjectOperatorService projectOperatorService,
      ValidationService validationService,
      @Value("${regulator.shared.email}") String regulatorSharedEmail,
      ProjectContributorsFormValidator projectContributorsFormValidator) {
    this.breadcrumbService = breadcrumbService;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
    this.projectContributorRepository = projectContributorRepository;
    this.projectOperatorService = projectOperatorService;
    this.validationService = validationService;
    this.regulatorSharedEmail = regulatorSharedEmail;
    this.projectContributorsFormValidator = projectContributorsFormValidator;
  }

  public ModelAndView getProjectContributorsFormModelAndView(ProjectContributorsForm form,
                                                             ProjectDetail projectDetail,
                                                             List<FieldError> errorList) {
    var modelAndView = new ModelAndView("project/projectcontributors/projectContributors")
        .addObject("form", form)
        .addObject("pageName", ProjectContributorsController.FORM_PAGE_NAME)
        .addObject("alreadyAddedContributors", getOrganisationGroupViews(form))
        .addObject("contributorsRestUrl",
            SearchSelectorService.route(on(OrganisationGroupRestController.class)
                .searchPathfinderOrganisations(null)))
        .addObject("backToTaskListUrl",
            ControllerUtils.getBackToTaskListUrl(projectDetail.getProject().getId())
        )
        .addObject("projectSetupUrl", ControllerUtils.getProjectSetupUrl(projectDetail.getProject().getId()))
        .addObject("regulatorEmailAddress", regulatorSharedEmail)
        .addObject("errorList", errorList);

    breadcrumbService.fromTaskList(
        projectDetail.getProject().getId(),
        modelAndView,
        ProjectContributorsController.TASK_LIST_NAME
    );

    return modelAndView;

  }

  @Transactional
  public void saveProjectContributors(ProjectContributorsForm form, ProjectDetail projectDetail) {
    var projectOperator = projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail);
    List<Integer> uniqueGroupOrganisationIds = form.getContributors()
        .stream()
        .distinct()
        .filter(integer -> !projectOperator.getOrganisationGroup().getOrgGrpId().equals(integer))
        .collect(Collectors.toList());
    List<ProjectContributor> projectContributors =
        portalOrganisationAccessor.getOrganisationGroupsWhereIdIn(uniqueGroupOrganisationIds)
            .stream()
            .map(portalOrganisationGroup -> new ProjectContributor(projectDetail, portalOrganisationGroup))
            .collect(Collectors.toList());

    projectContributorRepository.deleteAllByProjectDetail(projectDetail);
    projectContributorRepository.saveAll(projectContributors);
  }

  public boolean isValid(ProjectDetail projectDetail, ValidationType validationType) {
    var form = getForm(projectDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  public BindingResult validate(ProjectContributorsForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    final var projectContributorValidationHint = new ProjectContributorValidationHint(validationType);
    projectContributorsFormValidator.validate(form, bindingResult, projectContributorValidationHint);
    return validationService.validate(form, bindingResult, validationType);
  }

  public ProjectContributorsForm getForm(ProjectDetail projectDetail) {
    var form = new ProjectContributorsForm();
    var listOfOrganisationIds = projectContributorRepository.findAllByProjectDetail(projectDetail)
        .stream()
        .map(projectContributor -> projectContributor.getContributionOrganisationGroup().getOrgGrpId())
        .collect(Collectors.toList());
    form.setContributors(listOfOrganisationIds);
    return form;
  }

  List<ProjectContributor> getProjectContributorsForDetail(ProjectDetail detail) {
    return projectContributorRepository.findAllByProjectDetail(detail);
  }

  void removeProjectContributorsForDetail(ProjectDetail detail) {
    projectContributorRepository.deleteAllByProjectDetail(detail);
  }

  private List<OrganisationGroupView> getOrganisationGroupViews(ProjectContributorsForm form) {
    return portalOrganisationAccessor.getOrganisationGroupsWhereIdIn(form.getContributors())
        .stream()
        .map(portalOrganisationGroup -> new OrganisationGroupView(
            portalOrganisationGroup.getOrgGrpId(),
            portalOrganisationGroup.getName(),
            true)
        )
        .collect(Collectors.toList());
  }
}
