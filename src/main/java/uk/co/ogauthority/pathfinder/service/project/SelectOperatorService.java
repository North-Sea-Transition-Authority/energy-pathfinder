package uk.co.ogauthority.pathfinder.service.project;

import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.SelectOperatorForm;
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

  public PortalOrganisationGroup getOrganisationGroupOrError(Integer orgGrpId) {
    //TODO security check of some kind that the user is in that team?
    return portalOrganisationAccessor.getOrganisationGroupById(orgGrpId)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("unable to find organisation group with id %d", orgGrpId))
        );
  }

  public BindingResult validate(SelectOperatorForm form,
                                BindingResult bindingResult) {
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  /**
   * Get the projectOperator and build the form. Error if projectOperator not found.
   * @param detail detail to build the form for.
   * @return form with detail's associated organisationGroup.
   */
  public SelectOperatorForm getForm(ProjectDetail detail) {
    var projectOperator = projectOperatorService.getProjectOperatorByProjectDetail(detail)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format(
                "Unable to create SelectOperatorForm no ProjectOperator found for detail id: %d", detail.getId()
            )
        ));
    return new SelectOperatorForm(projectOperator.getOrganisationGroup().getOrgGrpId().toString());
  }

  /**
   * If there's data in the form turn it back into a format the searchselector can parse.
   * @param form valid or invalid SelectOperatorForm
   * @return id and display name of the search selector items empty map if there's no form data.
   */
  public Map<String, String> getPreSelectedOrgGroup(SelectOperatorForm form) {
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
}
