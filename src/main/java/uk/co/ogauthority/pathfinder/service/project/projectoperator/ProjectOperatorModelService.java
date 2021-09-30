package uk.co.ogauthority.pathfinder.service.project.projectoperator;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationGroupRestController;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationUnitRestController;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.enums.TopNavigationType;
import uk.co.ogauthority.pathfinder.model.enums.contact.ServiceContactDetail;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Service
public class ProjectOperatorModelService {

  private final SearchSelectorService searchSelectorService;
  private final PortalOrganisationAccessor portalOrganisationAccessor;

  @Autowired
  public ProjectOperatorModelService(SearchSelectorService searchSelectorService,
                                     PortalOrganisationAccessor portalOrganisationAccessor) {
    this.searchSelectorService = searchSelectorService;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
  }

  public ModelAndView getProjectOperatorModelAndView(ProjectOperatorForm form,
                                                     String cancelUrl,
                                                     String primaryButtonText,
                                                     TopNavigationType topNavigationType,
                                                     String pageTitle) {
    return new ModelAndView("project/projectoperator/selectOperator")
        .addObject("form", form)
        .addObject("preselectedOperator", getPreSelectedOrgGroup(form))
        .addObject("preselectedPublishableOrganisation", getPreSelectedPublishableOrganisation(form))
        .addObject("primaryButtonText", primaryButtonText)
        .addObject("cancelUrl", cancelUrl)
        .addObject("backLink", topNavigationType.equals(TopNavigationType.BACKLINK))
        .addObject("breadCrumbs", topNavigationType.equals(TopNavigationType.BREADCRUMBS))
        .addObject("operatorsRestUrl", SearchSelectorService.route(on(OrganisationGroupRestController.class)
            .searchUserOrganisations(null, null))
        )
        .addObject("regulatorEmailAddress", ServiceContactDetail.BUSINESS_SUPPORT.getEmailAddress())
        .addObject(
            "organisationUnitRestUrl",
            SearchSelectorService.route(on(OrganisationUnitRestController.class)
                .searchUserInvolvementOrganisationUnits(null, null))
        )
        .addObject("pageTitle", pageTitle);
  }

  /**
   * If there's data in the form turn it back into a format the searchSelector can parse.
   * @param form valid or invalid ProjectOperatorForm
   * @return id and display name of the search selector items empty map if there's no form data.
   */
  protected Map<String, String> getPreSelectedOrgGroup(ProjectOperatorForm form) {

    final var operator = form.getOperator();

    if (operator != null) {
      return  searchSelectorService.buildPrePopulatedSelections(
          Collections.singletonList(operator),
          Map.of(
              operator,
              getOperatorName(operator)
          )
      );

    }
    return Map.of();
  }

  protected Map<String, String> getPreSelectedPublishableOrganisation(ProjectOperatorForm form) {

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

  private String getOperatorName(String selectedProjectOperator) {
    return portalOrganisationAccessor.getOrganisationGroupOrError(Integer.parseInt(selectedProjectOperator)).getName();
  }

  private String getPublishableOrganisationName(String selectedPublishableOrganisation) {
    return portalOrganisationAccessor.getOrganisationUnitOrError(
        Integer.parseInt(selectedPublishableOrganisation)
    ).getSelectionText();
  }
}
