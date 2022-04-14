package uk.co.ogauthority.pathfinder.service.project.projectcontribution;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.projectcontributor.ProjectContributorsController;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationGroupRestController;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsForm;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.projectcontributor.ProjectContributorRepository;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectContextUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectContributorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@RunWith(MockitoJUnitRunner.class)
public class ProjectContributorsManagementServiceTest {

  private final String regulatorSharedEmail = "dummy@email.com";
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);
  private final ProjectDetail detail = ProjectUtil.getProjectDetails();
  private final ProjectContext projectContext = ProjectContextUtil.getContext_withAllPermissions(detail,
      authenticatedUser);

  @Mock
  private BreadcrumbService breadcrumbService;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  @Mock
  private ProjectContributorRepository projectContributorRepository;

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private ValidationService validationService;

  @Mock
  private ProjectContributorsFormValidator projectContributorsFormValidator;

  private ProjectContributorsManagementService projectContributorsManagementService;

  @Before
  public void setup() {
    projectContributorsManagementService = new ProjectContributorsManagementService(breadcrumbService,
        portalOrganisationAccessor,
        projectContributorRepository,
        projectOperatorService,
        validationService,
        regulatorSharedEmail,
        projectContributorsFormValidator);
  }

  @Test
  public void getProjectContributorsFormModelAndView_assertModelObjects() {
    var form = new ProjectContributorsForm();
    var modelAndView = projectContributorsManagementService.getProjectContributorsFormModelAndView(
        form,
        projectContext.getProjectDetails(),
        List.of()
    );

    assertThat(modelAndView.getModel()).containsExactly(
        entry("form", form),
        entry("pageName", ProjectContributorsController.FORM_PAGE_NAME),
        entry("alreadyAddedContributors", List.of()),
        entry("contributorsRestUrl",
            SearchSelectorService.route(on(OrganisationGroupRestController.class)
                .searchPathfinderOrganisations(null))),
        entry("backToTaskListUrl",
            ControllerUtils.getBackToTaskListUrl(detail.getProject().getId())),
        entry("projectSetupUrl",
            ControllerUtils.getProjectSetupUrl(detail.getProject().getId())),
        entry("regulatorEmailAddress", regulatorSharedEmail),
        entry("errorList", List.of())
    );
  }

  @Test
  public void saveProjectContributors_verifyMethodCalls() {
    var form = new ProjectContributorsForm();
    form.setContributors(List.of(1, 2, 3));
    var portalOrg1 = TeamTestingUtil.generateOrganisationGroup(1, "org1", "org1");
    var portalOrg2 = TeamTestingUtil.generateOrganisationGroup(2, "org2", "org2");
    var myPortalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(3, "org3", "org3");
    var projectOperator = ProjectOperatorTestUtil.getOperator(detail, myPortalOrganisationGroup);
    ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(detail)).thenReturn(projectOperator);
    when(portalOrganisationAccessor.getOrganisationGroupsWhereIdIn(List.of(1, 2))).thenReturn(
        List.of(portalOrg1, portalOrg2)
    );

    projectContributorsManagementService.saveProjectContributors(form, detail);

    verify(projectContributorRepository, times(1)).deleteAllByProjectDetail(detail);
    verify(projectContributorRepository, times(1)).saveAll(argumentCaptor.capture());
    List<ProjectContributor> savedProjectContributors = (List<ProjectContributor>) argumentCaptor.getValue();
    assertThat(savedProjectContributors).hasSize(2);
    assertThat(savedProjectContributors.get(0))
        .extracting(ProjectContributor::getContributionOrganisationGroup, ProjectContributor::getProjectDetail)
        .containsExactly(portalOrg1, detail);
    assertThat(savedProjectContributors.get(1))
        .extracting(ProjectContributor::getContributionOrganisationGroup, ProjectContributor::getProjectDetail)
        .containsExactly(portalOrg2, detail);
  }

  @Test
  public void validate_full() {
    var form = new ProjectContributorsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectContributorsManagementService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );
    verify(projectContributorsFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), any());
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void validate_partial() {
    var form = new ProjectContributorsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectContributorsManagementService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void isValid_noError_assertTrue() {
    var form = new ProjectContributorsForm();
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    when(validationService.validate(any(), any(), (ValidationType) any())).thenReturn(bindingResult);

    assertThat(projectContributorsManagementService.isValid(detail, ValidationType.FULL)).isTrue();
  }

  @Test
  public void isValid_error_assertFalse() {
    var form = new ProjectContributorsForm();
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(validationService.validate(any(), any(), (ValidationType) any())).thenReturn(bindingResult);

    assertThat(projectContributorsManagementService.isValid(detail, ValidationType.FULL)).isFalse();
  }

  @Test
  public void getForm_noPreviousContributors_assertFormFields() {
    when(projectContributorRepository.findAllByProjectDetail(detail)).thenReturn(List.of());

    var form = projectContributorsManagementService.getForm(detail);

    assertThat(form.getContributors()).isEmpty();
  }

  @Test
  public void getForm_previousContributors_assertFormFields() {
    var projectContributor1 = new ProjectContributor(
        detail,
        TeamTestingUtil.generateOrganisationGroup(1, "org", "org")
    );
    var projectContributor2 = new ProjectContributor(
        detail,
        TeamTestingUtil.generateOrganisationGroup(2, "org", "org")
    );
    when(projectContributorRepository.findAllByProjectDetail(detail)).thenReturn(List.of(
        projectContributor1,
        projectContributor2
    ));

    var form = projectContributorsManagementService.getForm(detail);

    assertThat(form.getContributors())
        .containsExactly(
            projectContributor1.getContributionOrganisationGroup().getOrgGrpId(),
            projectContributor2.getContributionOrganisationGroup().getOrgGrpId());
  }

  @Test
  public void removeProjectContributorsForDetail_removeSavedContributors() {
    projectContributorsManagementService.removeProjectContributorsForDetail(detail);

    verify(projectContributorRepository, times(1)).deleteAllByProjectDetail(detail);
  }

  @Test
  public void getProjectContributorsForDetail_assertContributors() {
    var projectContributor1 = ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 1);
    var projectContributor2 = ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 2);
    when(projectContributorRepository.findAllByProjectDetail(detail))
        .thenReturn(List.of(projectContributor1, projectContributor2));

    var projectContributors = projectContributorsManagementService.getProjectContributorsForDetail(detail);

    assertThat(projectContributors).containsExactlyInAnyOrder(
        projectContributor1,
        projectContributor2
    );
  }
}