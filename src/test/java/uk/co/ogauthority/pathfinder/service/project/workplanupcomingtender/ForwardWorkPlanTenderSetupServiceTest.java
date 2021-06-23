package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

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
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanTenderSetup;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanTenderSetupForm;
import uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender.ForwardWorkPlanTenderSetupRepository;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanTenderSetupServiceTest {

  @Mock
  private ForwardWorkPlanTenderSetupRepository forwardWorkPlanTenderSetupRepository;

  @Mock
  private ValidationService validationService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService;

  @Before
  public void setup() {
    forwardWorkPlanTenderSetupService = new ForwardWorkPlanTenderSetupService(
        forwardWorkPlanTenderSetupRepository,
        validationService
    );
  }

  @Test
  public void getForwardWorkPlanTenderSetupForDetail_whenFound_thenReturnPopulatedOptional() {

    final var workPlanTenderSetup = new ForwardWorkPlanTenderSetup();

    when(forwardWorkPlanTenderSetupRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.of(workPlanTenderSetup));

    final var resultingWorkPlanTenderSetup = forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupForDetail(
        projectDetail
    );

    assertThat(resultingWorkPlanTenderSetup).contains(workPlanTenderSetup);
  }

  @Test
  public void getForwardWorkPlanTenderSetupForDetail_whenNotFound_thenReturnEmptyOptional() {

    when(forwardWorkPlanTenderSetupRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.empty());

    final var resultingWorkPlanTenderSetup = forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupForDetail(
        projectDetail
    );

    assertThat(resultingWorkPlanTenderSetup).isEmpty();
  }

  @Test
  public void getForwardWorkPlanTenderSetupFormFromDetail_whenFound_thenPopulatedFormReturned() {

    final var workPlanTenderSetup = new ForwardWorkPlanTenderSetup();
    workPlanTenderSetup.setHasTendersToAdd(true);

    when(forwardWorkPlanTenderSetupRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.of(workPlanTenderSetup));

    final var resultingWorkPlanTenderSetupForm = forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupFormFromDetail(
        projectDetail
    );

    assertThat(resultingWorkPlanTenderSetupForm.getHasTendersToAdd()).isEqualTo(workPlanTenderSetup.getHasTendersToAdd());
  }

  @Test
  public void getForwardWorkPlanTenderSetupFormFromDetail_whenNotFound_thenEmptyFormReturned() {

    final var workPlanTenderSetup = new ForwardWorkPlanTenderSetup();
    workPlanTenderSetup.setHasTendersToAdd(null);

    when(forwardWorkPlanTenderSetupRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.of(workPlanTenderSetup));

    final var resultingWorkPlanTenderSetupForm = forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupFormFromDetail(
        projectDetail
    );

    assertThat(resultingWorkPlanTenderSetupForm.getHasTendersToAdd()).isNull();
  }

  @Test
  public void validate_verifyInteractions() {

    final var form = new ForwardWorkPlanTenderSetupForm();
    final var bindingResult = new BeanPropertyBindingResult(form, "form");
    final var validationType = ValidationType.FULL;

    forwardWorkPlanTenderSetupService.validate(form, bindingResult, validationType);

    verify(validationService, times(1)).validate(
        form,
        bindingResult,
        validationType
    );
  }

  @Test
  public void saveForwardWorkPlanTenderSetup_verifyInteractions() {

    final var forwardWorkPlanTenderSetup = new ForwardWorkPlanTenderSetup();
    forwardWorkPlanTenderSetup.setProjectDetail(projectDetail);

    when(forwardWorkPlanTenderSetupRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(forwardWorkPlanTenderSetup));

    final var forwardWorkPlanTenderSetupForm = new ForwardWorkPlanTenderSetupForm();
    forwardWorkPlanTenderSetupForm.setHasTendersToAdd(true);

    when(forwardWorkPlanTenderSetupRepository.save(forwardWorkPlanTenderSetup))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    final var persistedEntity = forwardWorkPlanTenderSetupService.saveForwardWorkPlanTenderSetup(
        forwardWorkPlanTenderSetupForm,
        projectDetail
    );

    verify(forwardWorkPlanTenderSetupRepository, times(1)).save(forwardWorkPlanTenderSetup);

    assertThat(persistedEntity.getHasTendersToAdd()).isEqualTo(forwardWorkPlanTenderSetupForm.getHasTendersToAdd());
    assertThat(persistedEntity.getProjectDetail()).isEqualTo(projectDetail);
  }

  @Test
  public void getForwardWorkPlanTenderSetupForProjectAndVersion_whenFound_thenPopulatedOptional() {

    final var expectedWorkPlanTenderSetup = new ForwardWorkPlanTenderSetup();
    final var project = projectDetail.getProject();
    final var projectVersion = projectDetail.getVersion();

    when(forwardWorkPlanTenderSetupRepository.findByProjectDetail_ProjectAndProjectDetail_Version(
        project,
        projectVersion
    )).thenReturn(Optional.of(expectedWorkPlanTenderSetup));

    final var resultingWorkPlanTenderSetup = forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupForProjectAndVersion(
        project,
        projectVersion
    );

    assertThat(resultingWorkPlanTenderSetup).contains(expectedWorkPlanTenderSetup);

  }

  @Test
  public void getForwardWorkPlanTenderSetupForProjectAndVersion_whenNotFound_thenEmptyOptional() {

    final var project = projectDetail.getProject();
    final var projectVersion = projectDetail.getVersion();

    when(forwardWorkPlanTenderSetupRepository.findByProjectDetail_ProjectAndProjectDetail_Version(
        project,
        projectVersion
    )).thenReturn(Optional.empty());

    final var resultingWorkPlanTenderSetup = forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupForProjectAndVersion(
        project,
        projectVersion
    );

    assertThat(resultingWorkPlanTenderSetup).isEmpty();
  }

  @Test
  public void getTenderSetupView_projectDetailVariant_whenSetupFound_thenPopulatedView() {

    final var workPlanTenderSetup = new ForwardWorkPlanTenderSetup();
    workPlanTenderSetup.setHasTendersToAdd(true);

    when(forwardWorkPlanTenderSetupRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(workPlanTenderSetup));

    final var workPlanTenderSetupView = forwardWorkPlanTenderSetupService.getTenderSetupView(projectDetail);

    assertThat(workPlanTenderSetupView.getHasTendersToAdd()).isEqualTo(StringDisplayUtil.YES);

  }

  @Test
  public void getTenderSetupView_projectDetailVariant_whenSetupNotFound_thenEmptyView() {

    when(forwardWorkPlanTenderSetupRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    final var workPlanTenderSetupView = forwardWorkPlanTenderSetupService.getTenderSetupView(projectDetail);

    assertThat(workPlanTenderSetupView.getHasTendersToAdd()).isEmpty();

  }

  @Test
  public void getTenderSetupView_projectAndVersionVariant_whenSetupFound_thenPopulatedView() {

    final var project = projectDetail.getProject();
    final var projectVersion = projectDetail.getVersion();

    final var workPlanTenderSetup = new ForwardWorkPlanTenderSetup();
    workPlanTenderSetup.setHasTendersToAdd(true);

    when(forwardWorkPlanTenderSetupRepository.findByProjectDetail_ProjectAndProjectDetail_Version(
        project,
        projectVersion
    )).thenReturn(Optional.of(workPlanTenderSetup));

    final var workPlanTenderSetupView = forwardWorkPlanTenderSetupService.getTenderSetupView(
        project,
        projectVersion
    );

    assertThat(workPlanTenderSetupView.getHasTendersToAdd()).isEqualTo(StringDisplayUtil.YES);

  }

  @Test
  public void getTenderSetupView_projectAndVersionVariant_whenSetupNotFound_thenEmptyView() {

    final var project = projectDetail.getProject();
    final var projectVersion = projectDetail.getVersion();

    when(forwardWorkPlanTenderSetupRepository.findByProjectDetail_ProjectAndProjectDetail_Version(
        project,
        projectVersion
    )).thenReturn(Optional.empty());

    final var workPlanTenderSetupView = forwardWorkPlanTenderSetupService.getTenderSetupView(
        project,
        projectVersion
    );


    assertThat(workPlanTenderSetupView.getHasTendersToAdd()).isEmpty();

  }

}