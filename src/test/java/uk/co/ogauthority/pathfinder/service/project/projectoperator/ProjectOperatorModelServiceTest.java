package uk.co.ogauthority.pathfinder.service.project.projectoperator;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationGroupRestController;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationUnitRestController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.enums.TopNavigationType;
import uk.co.ogauthority.pathfinder.model.enums.contact.ServiceContactDetail;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectOperatorModelServiceTest {

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  private ProjectOperatorModelService projectOperatorModelService;

  @Before
  public void setup() {
    projectOperatorModelService = new ProjectOperatorModelService(
        searchSelectorService,
        portalOrganisationAccessor
    );
  }

  @Test
  public void getProjectOperatorModelAndView_assertModelProperties() {

    final var form = new ProjectOperatorForm();
    final var primaryButtonText = "primaryButtonText";
    final var cancelUrl = "/cancel";
    final var topNavigationType = TopNavigationType.BACKLINK;
    final var pageTitle = "page title";

    final var resultingModel = projectOperatorModelService.getProjectOperatorModelAndView(
        form,
        cancelUrl,
        primaryButtonText,
        topNavigationType,
        pageTitle
    );

    assertThat(resultingModel.getModel()).containsExactly(
        entry("form", form),
        entry("preselectedOperator", Map.of()),
        entry("preselectedPublishableOrganisation", Map.of()),
        entry("primaryButtonText", primaryButtonText),
        entry("cancelUrl", cancelUrl),
        entry("backLink", true),
        entry("breadCrumbs", false),
        entry("operatorsRestUrl",
            SearchSelectorService.route(on(OrganisationGroupRestController.class)
                .searchUserOrganisations(null, null))
        ),
        entry("regulatorEmailAddress", ServiceContactDetail.BUSINESS_SUPPORT.getEmailAddress()),
        entry("organisationUnitRestUrl", SearchSelectorService.route(on(OrganisationUnitRestController.class)
            .searchUserInvolvementOrganisationUnits(null, null))
        ),
        entry("pageTitle", pageTitle)
    );
  }

  @Test
  public void getPreSelectedOrgGroup_whenFormValueIsNull_thenEmptyMap() {
    final var form = new ProjectOperatorForm();
    final var result = projectOperatorModelService.getPreSelectedOrgGroup(form);
    assertThat(result).isEmpty();
  }

  @Test
  public void getPreSelectedOrgGroup_whenFormValueFromList_thenEmptyMap() {

    final var organisationGroup = TeamTestingUtil.generateOrganisationGroup(100, "name", "short name");

    final var organisationGroupId = String.valueOf(organisationGroup.getOrgGrpId());

    final var form = new ProjectOperatorForm();
    form.setOperator(organisationGroupId);

    when(searchSelectorService.buildPrePopulatedSelections(any(), any())).thenCallRealMethod();
    when(portalOrganisationAccessor.getOrganisationGroupOrError(organisationGroup.getOrgGrpId())).thenReturn(organisationGroup);

    final var result = projectOperatorModelService.getPreSelectedOrgGroup(form);

    assertThat(result).containsExactly(
        entry(organisationGroupId, organisationGroup.getName())
    );
  }

  @Test
  public void getPreSelectedPublishableOrganisation_whenFormValueIsNull_thenEmptyMap() {
    final var form = new ProjectOperatorForm();
    final var result = projectOperatorModelService.getPreSelectedPublishableOrganisation(form);
    assertThat(result).isEmpty();
  }

  @Test
  public void getPreSelectedPublishableOrganisation_whenFormValueFromList_thenEmptyMap() {

    final var organisationUnit = TeamTestingUtil.generateOrganisationUnit(100, "name", new PortalOrganisationGroup());

    final var organisationUnitId = String.valueOf(organisationUnit.getOuId());

    final var form = new ProjectOperatorForm();
    form.setPublishableOrganisation(organisationUnitId);

    when(searchSelectorService.buildPrePopulatedSelections(any(), any())).thenCallRealMethod();
    when(portalOrganisationAccessor.getOrganisationUnitOrError(organisationUnit.getOuId())).thenReturn(organisationUnit);

    final var result = projectOperatorModelService.getPreSelectedPublishableOrganisation(form);

    assertThat(result).containsExactly(
        entry(organisationUnitId, organisationUnit.getName())
    );
  }

}