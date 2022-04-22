package uk.co.ogauthority.pathfinder.service.project.projectcontribution;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationGroupRestController;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsForm;
import uk.co.ogauthority.pathfinder.model.view.organisationgroup.OrganisationGroupView;
import uk.co.ogauthority.pathfinder.repository.project.projectcontributor.ProjectContributorRepository;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Service
public class ProjectContributorsCommonService {

  private final ProjectContributorRepository projectContributorRepository;
  private final ProjectOperatorService projectOperatorService;
  private final PortalOrganisationAccessor portalOrganisationAccessor;
  private final String regulatorSharedEmail;

  @Autowired
  public ProjectContributorsCommonService(
      ProjectContributorRepository projectContributorRepository,
      ProjectOperatorService projectOperatorService,
      PortalOrganisationAccessor portalOrganisationAccessor,
      @Value("${regulator.shared.email}") String regulatorSharedEmail) {
    this.projectContributorRepository = projectContributorRepository;
    this.projectOperatorService = projectOperatorService;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
    this.regulatorSharedEmail = regulatorSharedEmail;
  }

  public void setContributorsInForm(ProjectContributorsForm form,
                                    ProjectDetail projectDetail) {
    var listOfOrganisationIds = projectContributorRepository.findAllByProjectDetail(projectDetail)
        .stream()
        .map(projectContributor -> projectContributor.getContributionOrganisationGroup().getOrgGrpId())
        .collect(Collectors.toList());
    form.setContributors(listOfOrganisationIds);
  }

  @Transactional
  public void saveProjectContributors(ProjectContributorsForm form, ProjectDetail projectDetail) {
    if (form.getContributors() != null) {
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
  }

  @Transactional
  public void deleteProjectContributors(ProjectDetail projectDetail) {
    projectContributorRepository.deleteAllByProjectDetail(projectDetail);
  }

  public void setModelAndViewCommonObjects(ModelAndView modelAndView,
                                           ProjectDetail detail,
                                           ProjectContributorsForm form,
                                           String pageName,
                                           List<FieldError> errorList) {
    modelAndView
        .addObject("form", form)
        .addObject("pageName", pageName)
        .addObject("alreadyAddedContributors", getOrganisationGroupViews(form))
        .addObject("contributorsRestUrl",
            SearchSelectorService.route(on(OrganisationGroupRestController.class)
                .searchPathfinderOrganisations(null)))
        .addObject("backToTaskListUrl",
            ControllerUtils.getBackToTaskListUrl(detail.getProject().getId())
        )
        .addObject("projectSetupUrl", ControllerUtils.getProjectSetupUrl(detail.getProject().getId()))
        .addObject("regulatorEmailAddress", regulatorSharedEmail)
        .addObject("errorList", errorList);

    ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(
        modelAndView,
        detail
    );
  }

  public List<ProjectContributor> getProjectContributorsForDetail(ProjectDetail detail) {
    return projectContributorRepository.findAllByProjectDetail(detail);
  }

  private List<OrganisationGroupView> getOrganisationGroupViews(ProjectContributorsForm form) {
    if (form.getContributors() == null) {
      return List.of();
    }
    return portalOrganisationAccessor.getOrganisationGroupsWhereIdIn(form.getContributors())
        .stream()
        .map(portalOrganisationGroup -> new OrganisationGroupView(
            portalOrganisationGroup.getOrgGrpId(),
            portalOrganisationGroup.getName(),
            true)
        )
        .sorted(Comparator.comparing(OrganisationGroupView::getName))
        .collect(Collectors.toList());
  }
}
