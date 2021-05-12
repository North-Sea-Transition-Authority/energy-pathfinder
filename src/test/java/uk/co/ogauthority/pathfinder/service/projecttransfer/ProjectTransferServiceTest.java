package uk.co.ogauthority.pathfinder.service.projecttransfer;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
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
import uk.co.ogauthority.pathfinder.service.email.OperatorEmailService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.CancelDraftProjectVersionService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectHeaderSummaryService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTransferServiceTest {

  @Mock
  private ProjectTransferRepository projectTransferRepository;

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private ProjectUpdateService projectUpdateService;

  @Mock
  private ProjectHeaderSummaryService projectHeaderSummaryService;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private CancelDraftProjectVersionService cancelDraftProjectVersionService;

  @Mock
  private ValidationService validationService;

  @Mock
  private ProjectTransferFormValidator projectTransferFormValidator;

  @Mock
  private BreadcrumbService breadcrumbService;

  @Mock
  private OperatorEmailService operatorEmailService;

  @Captor
  private ArgumentCaptor<ProjectTransferValidationHint> projectTransferValidationHintArgumentCaptor;

  private ProjectTransferService projectTransferService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectTransferService = new ProjectTransferService(
        projectTransferRepository,
        projectOperatorService,
        projectUpdateService,
        projectHeaderSummaryService,
        portalOrganisationAccessor,
        searchSelectorService,
        cancelDraftProjectVersionService,
        validationService,
        projectTransferFormValidator,
        breadcrumbService,
        operatorEmailService
    );

    when(projectTransferRepository.save(any(ProjectTransfer.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void transferProject() {
    var fromOrganisationGroupId = 1;

    var form = new ProjectTransferForm();
    form.setNewOrganisationGroup(Integer.toString(fromOrganisationGroupId));
    form.setTransferReason("Test transfer reason");

    var newProjectDetail = ProjectUtil.getProjectDetails();

    var fromOrganisationGroup = ProjectOperatorTestUtil.getOrgGroup("Old operator");
    var toOrganisationGroup = ProjectOperatorTestUtil.getOrgGroup("New operator");

    when(projectUpdateService.createNewProjectVersion(projectDetail, authenticatedUser)).thenReturn(newProjectDetail);
    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)).thenReturn(
        ProjectOperatorTestUtil.getOperator(fromOrganisationGroup)
    );
    when(portalOrganisationAccessor.getOrganisationGroupOrError(fromOrganisationGroupId)).thenReturn(
        toOrganisationGroup
    );

    var projectTransfer = projectTransferService.transferProject(projectDetail, authenticatedUser, form);

    verify(cancelDraftProjectVersionService, times(1)).cancelDraftIfExists(projectDetail.getProject().getId());
    verify(projectOperatorService, times(1)).createOrUpdateProjectOperator(newProjectDetail, toOrganisationGroup);

    assertThat(projectTransfer.getProjectDetail()).isEqualTo(newProjectDetail);
    assertThat(projectTransfer.getFromOrganisationGroup()).isEqualTo(fromOrganisationGroup);
    assertThat(projectTransfer.getToOrganisationGroup()).isEqualTo(toOrganisationGroup);
    assertThat(projectTransfer.getTransferReason()).isEqualTo(form.getTransferReason());
    assertThat(projectTransfer.getTransferredInstant()).isNotNull();
    assertThat(projectTransfer.getTransferredByWuaId()).isEqualTo(authenticatedUser.getWuaId());

    verify(projectTransferRepository, times(1)).save(projectTransfer);

    verify(operatorEmailService, times(1)).sendProjectTransferEmails(
        newProjectDetail,
        fromOrganisationGroup,
        toOrganisationGroup,
        form.getTransferReason()
    );
  }

  @Test
  public void getProjectTransfer_whenFound_thenReturn() {
    var projectTransfer = new ProjectTransfer();

    when(projectTransferRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(projectTransfer));

    assertThat(projectTransferService.getProjectTransfer(projectDetail)).contains(projectTransfer);
  }

  @Test
  public void getProjectTransfer_whenNotFound_thenEmptyOptionalReturned() {
    when(projectTransferRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    assertThat(projectTransferService.getProjectTransfer(projectDetail)).isEmpty();
  }

  @Test
  public void validate() {
    var form = new ProjectTransferForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var operator = ProjectOperatorTestUtil.getOperator();

    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)).thenReturn(
        operator
    );

    projectTransferService.validate(form, bindingResult, projectDetail);

    verify(projectTransferFormValidator, times(1)).validate(eq(form), eq(bindingResult), projectTransferValidationHintArgumentCaptor.capture());
    var projectTransferValidationHint = projectTransferValidationHintArgumentCaptor.getValue();
    assertThat(projectTransferValidationHint.getCurrentOrganisationGroup()).isEqualTo(operator.getOrganisationGroup());

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void getPreSelectedOrgGroup_whenFormValueIsNull_thenEmptyMap() {
    final var form = new ProjectTransferForm();
    final var result = projectTransferService.getPreSelectedOrgGroup(form);
    assertThat(result).isEmpty();
  }

  @Test
  public void getPreSelectedOrgGroup_whenFormValueFromList_thenEmptyMap() {
    var organisationGroup = ProjectOperatorTestUtil.ORG_GROUP;

    final var organisationGroupId = String.valueOf(organisationGroup.getOrgGrpId());

    var form = new ProjectTransferForm();
    form.setNewOrganisationGroup(organisationGroupId);

    when(searchSelectorService.buildPrePopulatedSelections(any(), any())).thenCallRealMethod();
    when(portalOrganisationAccessor.getOrganisationGroupOrError(organisationGroup.getOrgGrpId())).thenReturn(organisationGroup);

    final var result = projectTransferService.getPreSelectedOrgGroup(form);
    assertThat(result).containsExactly(
        entry(organisationGroupId, organisationGroup.getName())
    );
  }

  @Test
  public void getTransferProjectModelAndView() {
    var form = new ProjectTransferForm();
    var projectId = projectDetail.getProject().getId();
    var projectHeaderHtml = "html";
    var projectOperator = ProjectOperatorTestUtil.getOperator();

    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)).thenReturn(projectOperator);
    when(projectHeaderSummaryService.getProjectHeaderHtml(projectDetail, authenticatedUser)).thenReturn(projectHeaderHtml);

    var modelAndView = projectTransferService.getTransferProjectModelAndView(projectDetail, authenticatedUser, form);

    final var pageHeading = String.format(
        "Change %s operator",
        projectDetail.getProjectType().getLowercaseDisplayName()
    );

    assertThat(modelAndView.getViewName()).isEqualTo(ProjectTransferService.TRANSFER_PROJECT_TEMPLATE_PATH);
    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("projectHeaderHtml", projectHeaderHtml),
        entry("currentOperator", projectOperator.getOrganisationGroup().getName()),
        entry("form", form),
        entry("preselectedOperator", projectTransferService.getPreSelectedOrgGroup(form)),
        entry("operatorsRestUrl", SearchSelectorService.route(on(OrganisationGroupRestController.class)
            .searchPathfinderOrganisations(null))),
        entry("cancelUrl", ReverseRouter.route(on(ManageProjectController.class)
            .getProject(projectId, null, null, null))
        ),
        entry("pageHeading", pageHeading)
    );

    verify(breadcrumbService, times(1)).fromManageProject(
        projectDetail,
        modelAndView,
        pageHeading
    );
  }
}
