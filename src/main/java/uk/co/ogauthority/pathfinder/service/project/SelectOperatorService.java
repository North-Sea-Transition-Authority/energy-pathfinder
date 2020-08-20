package uk.co.ogauthority.pathfinder.service.project;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationGroupRestController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.enums.TopNavigationType;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class SelectOperatorService {

  private final PortalOrganisationAccessor portalOrganisationAccessor;
  private final ValidationService validationService;
  private final SearchSelectorService searchSelectorService;
  private final ProjectOperatorService projectOperatorService;

  @Autowired
  public SelectOperatorService(
      PortalOrganisationAccessor portalOrganisationAccessor,
      ValidationService validationService,
      SearchSelectorService searchSelectorService,
      ProjectOperatorService projectOperatorService) {
    this.portalOrganisationAccessor = portalOrganisationAccessor;
    this.validationService = validationService;
    this.searchSelectorService = searchSelectorService;
    this.projectOperatorService = projectOperatorService;
  }

  /**
   * Get the PortalOrganisationGroup for the given id.
   * If the user cannot access this group then error.
   * @param user user trying to access the group
   * @param orgGrpId portalOrganisationUnit orgGrpId
   * @return the PortalOrganisationGroup with the given id
   */
  public PortalOrganisationGroup getOrganisationGroupOrError(AuthenticatedUserAccount user, Integer orgGrpId) {
    var orgGroup =  getOrganisationGroupOrError(orgGrpId);

    if (!projectOperatorService.canUserAccessOrgGroup(user, orgGroup)) {
      throw new AccessDeniedException(
          String.format(
              "User with wua: %d does not have access to organisation group with id: %d",
              user.getWuaId(),
              orgGroup.getOrgGrpId())
      );
    }
    return orgGroup;
  }

  /**
   * Get the PortalOrganisationGroup for the specified id, error if it does not exist.
   * @param orgGrpId id of the PortalOrganisationGroup
   * @return the PortalOrganisationGroup with the specified id
   */
  public PortalOrganisationGroup getOrganisationGroupOrError(Integer orgGrpId) {
    return portalOrganisationAccessor.getOrganisationGroupById(orgGrpId)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("unable to find organisation group with id %d", orgGrpId))
        );
  }

  public ProjectOperator updateProjectOperator(ProjectDetail detail, PortalOrganisationGroup organisationGroup) {
    return projectOperatorService.createOrUpdateProjectOperator(detail, organisationGroup);
  }

  public BindingResult validate(ProjectOperatorForm form,
                                BindingResult bindingResult) {
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  /**
   * Get the projectOperator and build the form. Error if projectOperator not found.
   * @param detail detail to build the form for.
   * @return form with detail's associated organisationGroup.
   */
  public ProjectOperatorForm getForm(ProjectDetail detail) {
    var projectOperator = getProjectOperatorOrError(detail);
    return new ProjectOperatorForm(projectOperator.getOrganisationGroup().getOrgGrpId().toString());
  }

  public boolean isComplete(ProjectDetail detail) {
    var form = getForm(detail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult);
    return !bindingResult.hasErrors();
  }

  /**
   * If there's data in the form turn it back into a format the searchSelector can parse.
   * @param form valid or invalid ProjectOperatorForm
   * @return id and display name of the search selector items empty map if there's no form data.
   */
  public Map<String, String> getPreSelectedOrgGroup(ProjectOperatorForm form) {
    if (form.getOrganisationGroup() != null) {
      return  searchSelectorService.buildPrePopulatedSelections(
          Collections.singletonList(form.getOrganisationGroup()),
          Map.of(
              form.getOrganisationGroup(),
              getOrganisationGroupOrError(Integer.parseInt(form.getOrganisationGroup())).getName()
          )
      );

    }
    return Map.of();
  }

  /**
   * Build the model and view for the selectOperator template.
   * Add breadcrumbs in the controller if they're required.
   * @param form a ProjectOperator form (with operator if updating)
   * @param cancelUrl the url to go back to if user clicks cancel
   * @param primaryButtonText primary button text
   * @return the completed model and view
   */
  public ModelAndView getSelectOperatorModelAndView(ProjectOperatorForm form,
                                                    String cancelUrl,
                                                    String primaryButtonText,
                                                    TopNavigationType topNavigationType) {
    return new ModelAndView("project/selectoperator/selectOperator")
        .addObject("form", form)
        .addObject("preselectedOperator", getPreSelectedOrgGroup(form))
        .addObject("primaryButtonText", primaryButtonText)
        .addObject("cancelUrl", cancelUrl)
        .addObject("backLink", topNavigationType.equals(TopNavigationType.BACKLINK))
        .addObject("breadCrumbs", topNavigationType.equals(TopNavigationType.BREADCRUMBS))
        .addObject("operatorsRestUrl", SearchSelectorService.route(on(OrganisationGroupRestController.class)
            .searchFields(null, null))
        );
  }

  private ProjectOperator getProjectOperatorOrError(ProjectDetail detail) {
    return projectOperatorService.getProjectOperatorByProjectDetail(detail)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format(
                "No ProjectOperator found for detail id: %d", detail.getId()
            )
        ));
  }
}
