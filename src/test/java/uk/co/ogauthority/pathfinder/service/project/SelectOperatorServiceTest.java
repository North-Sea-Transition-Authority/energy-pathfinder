package uk.co.ogauthority.pathfinder.service.project;


import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationGroupRestController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.enums.TopNavigationType;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.service.project.selectoperator.SelectOperatorService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class SelectOperatorServiceTest {

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  @Mock
  private ValidationService validationService;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private ProjectOperatorService projectOperatorService;

  private SelectOperatorService selectOperatorService;

  private static final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final PortalOrganisationGroup organisationGroup = TeamTestingUtil.generateOrganisationGroup(
      1,
      "Org Grp",
      "Org Grp"
  );

  private static final ProjectOperator projectOperator = ProjectOperatorTestUtil.getOperator(detail, organisationGroup);

  @Before
  public void setUp() {
    selectOperatorService = new SelectOperatorService(
        portalOrganisationAccessor,
        validationService,
        searchSelectorService,
        projectOperatorService
    );
  }

  @Test
  public void getOrganisationGroupOrError_userCanAccessGroup() {
    when(portalOrganisationAccessor.getOrganisationGroupOrError(organisationGroup.getOrgGrpId())).thenReturn(organisationGroup);
    when(projectOperatorService.canUserAccessOrgGroup(authenticatedUser, organisationGroup)).thenReturn(true);
    assertThat(selectOperatorService.getOrganisationGroupOrError(authenticatedUser, organisationGroup.getOrgGrpId())).isEqualTo(organisationGroup);
  }

  @Test(expected = AccessDeniedException.class)
  public void getOrganisationGroupOrError_userCannotAccessGroup() {
    when(portalOrganisationAccessor.getOrganisationGroupOrError(organisationGroup.getOrgGrpId())).thenReturn(organisationGroup);
    when(projectOperatorService.canUserAccessOrgGroup(authenticatedUser, organisationGroup)).thenReturn(false);
    selectOperatorService.getOrganisationGroupOrError(authenticatedUser, organisationGroup.getOrgGrpId());
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getOrganisationGroupOrError_noMatchingGroup() {
    doThrow(new PathfinderEntityNotFoundException("test")).when(portalOrganisationAccessor).getOrganisationGroupOrError(organisationGroup.getOrgGrpId());
    selectOperatorService.getOrganisationGroupOrError(authenticatedUser, organisationGroup.getOrgGrpId());
  }

  @Test
  public void getForm() {
    when(projectOperatorService.getProjectOperatorByProjectDetail(detail)).thenReturn(Optional.of(projectOperator));
    var form = selectOperatorService.getForm(detail);
    assertThat(form.getOrganisationGroup()).isEqualTo(organisationGroup.getOrgGrpId().toString());
  }

  @Test
  public void validate() {
    var form = new ProjectOperatorForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    selectOperatorService.validate(
        form,
        bindingResult
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void updateProjectOperator_assertProjectOperatorServiceInteraction() {
    selectOperatorService.updateProjectOperator(detail, organisationGroup);
    verify(projectOperatorService, times(1)).createOrUpdateProjectOperator(detail, organisationGroup);
  }

  @Test
  public void getPreSelectedOrgGroup_whenFormValueIsNull_thenEmptyMap() {
    final var form = new ProjectOperatorForm();
    final var result = selectOperatorService.getPreSelectedOrgGroup(form);
    assertThat(result).isEmpty();
  }

  @Test
  public void getPreSelectedOrgGroup_whenFormValueFromList_thenEmptyMap() {

    final var organisationGroupId = String.valueOf(organisationGroup.getOrgGrpId());

    var form = new ProjectOperatorForm();
    form.setOrganisationGroup(organisationGroupId);

    when(searchSelectorService.buildPrePopulatedSelections(any(), any())).thenCallRealMethod();
    when(portalOrganisationAccessor.getOrganisationGroupOrError(organisationGroup.getOrgGrpId())).thenReturn(organisationGroup);

    final var result = selectOperatorService.getPreSelectedOrgGroup(form);
    assertThat(result).containsExactly(
        entry(organisationGroupId, organisationGroup.getName())
    );
  }

  @Test
  public void getSelectOperatorModelAndView() {

    final var form = new ProjectOperatorForm();
    final var primaryButtonText = "primaryButtonText";
    final var cancelUrl = "/cancel";
    final var topNavigationType = TopNavigationType.BACKLINK;

    final var model = selectOperatorService.getSelectOperatorModelAndView(
        form,
        cancelUrl,
        primaryButtonText,
        topNavigationType
    );

    assertThat(model.getModel()).containsExactly(
        entry("form", form),
        entry("preselectedOperator", Map.of()),
        entry("primaryButtonText", primaryButtonText),
        entry("cancelUrl", cancelUrl),
        entry("backLink", true),
        entry("breadCrumbs", false),
        entry("operatorsRestUrl",
            SearchSelectorService.route(on(OrganisationGroupRestController.class)
                .searchUserOrganisations(null, null))
        )
    );
  }
}
