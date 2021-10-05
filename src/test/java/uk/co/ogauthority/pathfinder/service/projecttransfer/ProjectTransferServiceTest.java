package uk.co.ogauthority.pathfinder.service.projecttransfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.entity.projecttransfer.ProjectTransfer;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.model.form.projecttransfer.ProjectTransferForm;
import uk.co.ogauthority.pathfinder.model.form.projecttransfer.ProjectTransferFormValidator;
import uk.co.ogauthority.pathfinder.model.form.projecttransfer.ProjectTransferValidationHint;
import uk.co.ogauthority.pathfinder.repository.projecttransfer.ProjectTransferRepository;
import uk.co.ogauthority.pathfinder.service.email.OperatorEmailService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.cancellation.CancelDraftProjectVersionService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
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
  private CancelDraftProjectVersionService cancelDraftProjectVersionService;

  @Mock
  private ValidationService validationService;

  @Mock
  private ProjectTransferFormValidator projectTransferFormValidator;

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
        cancelDraftProjectVersionService,
        validationService,
        projectTransferFormValidator,
        operatorEmailService
    );

    when(projectTransferRepository.save(any(ProjectTransfer.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void transferProject() {

    final var fromOrganisationGroupId = 1;
    final var publishableOrganisationUnitId = 100;

    final var projectTransferForm = new ProjectTransferForm();
    projectTransferForm.setNewOrganisationGroup(Integer.toString(fromOrganisationGroupId));
    projectTransferForm.setTransferReason("Test transfer reason");
    projectTransferForm.setIsPublishedAsOperator(false);
    projectTransferForm.setPublishableOrganisation(String.valueOf(publishableOrganisationUnitId));

    final var newProjectDetail = ProjectUtil.getProjectDetails();

    final var fromOrganisationGroup = ProjectOperatorTestUtil.getOrgGroup("Old operator");
    final var toOrganisationGroup = ProjectOperatorTestUtil.getOrgGroup("New operator");

    final var toPublishableOrganisation = TeamTestingUtil.generateOrganisationUnit(
        publishableOrganisationUnitId,
        "name",
        toOrganisationGroup
    );

    when(projectUpdateService.createNewProjectVersion(projectDetail, authenticatedUser)).thenReturn(newProjectDetail);
    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)).thenReturn(
        ProjectOperatorTestUtil.getOperator(fromOrganisationGroup)
    );

    final var projectOperatorForm = new ProjectOperatorForm();
    projectOperatorForm.setOperator(projectTransferForm.getNewOrganisationGroup());
    projectOperatorForm.setIsPublishedAsOperator(projectTransferForm.isPublishedAsOperator());
    projectOperatorForm.setPublishableOrganisation(projectTransferForm.getPublishableOrganisation());

    final var projectOperator = new ProjectOperator();
    projectOperator.setOrganisationGroup(toOrganisationGroup);
    projectOperator.setIsPublishedAsOperator(projectTransferForm.isPublishedAsOperator());
    projectOperator.setPublishableOrganisationUnit(toPublishableOrganisation);

    when(projectOperatorService.createOrUpdateProjectOperator(
        newProjectDetail,
        projectOperatorForm
    )).thenReturn(projectOperator);

    var projectTransfer = projectTransferService.transferProject(projectDetail, authenticatedUser, projectTransferForm);

    verify(cancelDraftProjectVersionService, times(1)).cancelDraftIfExists(projectDetail.getProject().getId());


    verify(projectOperatorService, times(1)).createOrUpdateProjectOperator(newProjectDetail, projectOperatorForm);

    assertThat(projectTransfer.getProjectDetail()).isEqualTo(newProjectDetail);
    assertThat(projectTransfer.getFromOrganisationGroup()).isEqualTo(fromOrganisationGroup);
    assertThat(projectTransfer.getToOrganisationGroup()).isEqualTo(toOrganisationGroup);
    assertThat(projectTransfer.getTransferReason()).isEqualTo(projectTransferForm.getTransferReason());
    assertThat(projectTransfer.getTransferredInstant()).isNotNull();
    assertThat(projectTransfer.getTransferredByWuaId()).isEqualTo(authenticatedUser.getWuaId());
    assertThat(projectTransfer.isPublishedAsOperator()).isEqualTo(projectOperator.isPublishedAsOperator());
    assertThat(projectTransfer.getPublishableOrganisationUnit()).isEqualTo(projectOperator.getPublishableOrganisationUnit());

    verify(projectTransferRepository, times(1)).save(projectTransfer);

    verify(operatorEmailService, times(1)).sendProjectTransferEmails(
        newProjectDetail,
        fromOrganisationGroup,
        toOrganisationGroup,
        projectTransferForm.getTransferReason()
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
}
