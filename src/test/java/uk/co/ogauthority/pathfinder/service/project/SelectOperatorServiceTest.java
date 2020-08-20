package uk.co.ogauthority.pathfinder.service.project;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorUtil;
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

  private static final Person person = UserTestingUtil.getPerson(authenticatedUser);

  private static final PortalOrganisationGroup organisationGroup = TeamTestingUtil.generateOrganisationGroup(
      1,
      "Org Grp",
      "Org Grp"
  );

  private static final ProjectOperator projectOperator = ProjectOperatorUtil.getOperator(detail, organisationGroup);

  @Before
  public void setUp() throws Exception {
    selectOperatorService = new SelectOperatorService(
        portalOrganisationAccessor,
        validationService,
        searchSelectorService,
        projectOperatorService
    );
  }

  @Test
  public void getOrganisationGroupOrError_userCanAccessGroup() {
    when(portalOrganisationAccessor.getOrganisationGroupById(organisationGroup.getOrgGrpId())).thenReturn(Optional.of(organisationGroup));
    when(projectOperatorService.canUserAccessOrgGroup(authenticatedUser, organisationGroup)).thenReturn(true);
    assertThat(selectOperatorService.getOrganisationGroupOrError(authenticatedUser, organisationGroup.getOrgGrpId())).isEqualTo(organisationGroup);
  }

  @Test(expected = AccessDeniedException.class)
  public void getOrganisationGroupOrError_userCannotAccessGroup() {
    when(portalOrganisationAccessor.getOrganisationGroupById(organisationGroup.getOrgGrpId())).thenReturn(Optional.of(organisationGroup));
    when(projectOperatorService.canUserAccessOrgGroup(authenticatedUser, organisationGroup)).thenReturn(false);
    selectOperatorService.getOrganisationGroupOrError(authenticatedUser, organisationGroup.getOrgGrpId());
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getOrganisationGroupOrError_noMatchingGroup() {
    when(portalOrganisationAccessor.getOrganisationGroupById(organisationGroup.getOrgGrpId())).thenReturn(Optional.empty());
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
}
