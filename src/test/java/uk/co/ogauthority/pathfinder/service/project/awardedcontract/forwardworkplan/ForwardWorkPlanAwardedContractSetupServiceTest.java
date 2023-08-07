package uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetup;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupForm;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupRepository;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@ExtendWith(MockitoExtension.class)
class ForwardWorkPlanAwardedContractSetupServiceTest {

  @Mock
  private ForwardWorkPlanAwardedContractSetupRepository repository;

  @Mock
  private ValidationService validationService;

  @InjectMocks
  private ForwardWorkPlanAwardedContractSetupService setupService;

  private final ProjectDetail  projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  @Captor
  private ArgumentCaptor<ForwardWorkPlanAwardedContractSetup> awardedContractSetupCaptor;

  @Test
  void getAwardedContractSetupFormFromDetail_noAwardedContractFound() {
    when(repository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    var form = setupService.getAwardedContractSetupFormFromDetail(projectDetail);
    assertThat(form.getHasContractToAdd()).isNull();
  }

  @Test
  void getAwardedContractSetupFormFromDetail_awardedContractFound() {
    var awardedContractSetup = AwardedContractTestUtil.createForwardWorkPlanAwardedContractSetup(projectDetail);
    when(repository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(awardedContractSetup));

    var form = setupService.getAwardedContractSetupFormFromDetail(projectDetail);
    assertThat(form.getHasContractToAdd()).isTrue();
  }

  @Test
  void saveAwardedContractSetup_firstTimeSaving() {
    var form = new ForwardWorkPlanAwardedContractSetupForm();
    form.setHasContractToAdd(false);

    when(repository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    setupService.saveAwardedContractSetup(form, projectDetail);

    verify(repository).save(awardedContractSetupCaptor.capture());
    var awardedContractSetup = awardedContractSetupCaptor.getValue();
    assertThat(awardedContractSetup.getHasContractToAdd()).isFalse();
    assertThat(awardedContractSetup.getProjectDetail()).isEqualTo(projectDetail);
  }

  @Test
  void saveAwardedContractSetup_updateExisting() {
    var form = new ForwardWorkPlanAwardedContractSetupForm();
    form.setHasContractToAdd(false);

    var awardedContractSetup = new ForwardWorkPlanAwardedContractSetup();
    awardedContractSetup.setHasContractToAdd(true);
    awardedContractSetup.setProjectDetail(projectDetail);
    when(repository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(awardedContractSetup));

    setupService.saveAwardedContractSetup(form, projectDetail);

    verify(repository).save(awardedContractSetupCaptor.capture());
    var result = awardedContractSetupCaptor.getValue();
    assertThat(result.getHasContractToAdd()).isFalse();
    assertThat(result.getProjectDetail()).isEqualTo(projectDetail);
  }

  @Test
  void getForwardWorkPlanAwardedContractSetup_foundAwardedContractSetup() {
    var awardedContractSetup = AwardedContractTestUtil.createForwardWorkPlanAwardedContractSetup(projectDetail);
    when(repository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(awardedContractSetup));

    var resultOptional = setupService.getForwardWorkPlanAwardedContractSetup(projectDetail);
    assertThat(resultOptional).isPresent();
    var result = resultOptional.get();
    assertThat(result).isEqualTo(awardedContractSetup);
  }

  @Test
  void getForwardWorkPlanAwardedContractSetup_notFound() {
    when(repository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    var resultOptional = setupService.getForwardWorkPlanAwardedContractSetup(projectDetail);
    assertThat(resultOptional).isEmpty();
  }

  @Test
  void validate_withErrorsFound() {
    var validationMessage = "validationMessage";
    doAnswer(invocation -> {
      var bindingResult = invocation.getArgument(1, BindingResult.class);
      bindingResult.rejectValue("hasContractToAdd", "hasContractToAdd.required", validationMessage);
      return bindingResult;
    }).when(validationService).validate(
        any(ForwardWorkPlanAwardedContractSetupForm.class),
        any(BindingResult.class),
        eq(ValidationType.FULL)
    );

    var form = new ForwardWorkPlanAwardedContractSetupForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var result = setupService.validate(form, bindingResult);
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getAllErrors()).hasSize(1);
    assertThat(result.getAllErrors().get(0).getDefaultMessage()).isEqualTo(validationMessage);
  }

  @Test
  void validate_noErrorsFound() {
    var form = new ForwardWorkPlanAwardedContractSetupForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(validationService.validate(form, bindingResult, ValidationType.FULL))
        .thenReturn(bindingResult);

    var result = setupService.validate(form, bindingResult);
    assertThat(result.hasErrors()).isFalse();
  }

  @Test
  void isValid_true() {
    var awardedContractSetup = AwardedContractTestUtil.createForwardWorkPlanAwardedContractSetup(projectDetail);
    var bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanAwardedContractSetupForm.class, "form");

    when(repository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(awardedContractSetup));
    when(validationService.validate(
        any(ForwardWorkPlanAwardedContractSetupForm.class),
        any(BindingResult.class),
        eq(ValidationType.FULL))
    ).thenReturn(bindingResult);

    var result = setupService.isValid(projectDetail);
    assertThat(result).isTrue();
  }

  @Test
  void isValid_false() {
    var awardedContractSetup = AwardedContractTestUtil.createForwardWorkPlanAwardedContractSetup(projectDetail);
    awardedContractSetup.setHasContractToAdd(null);
    when(repository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(awardedContractSetup));

    var validationMessage = "validationMessage";
    doAnswer(invocation -> {
      var bindingResult = invocation.getArgument(1, BindingResult.class);
      bindingResult.rejectValue("hasContractToAdd", "hasContractToAdd.required", validationMessage);
      return bindingResult;
    }).when(validationService).validate(
        any(ForwardWorkPlanAwardedContractSetupForm.class),
        any(BindingResult.class),
        eq(ValidationType.FULL)
    );

    var result = setupService.isValid(projectDetail);
    assertThat(result).isFalse();
  }

  @Test
  void deleteAllByProjectDetail() {
    setupService.deleteAllByProjectDetail(projectDetail);
    verify(repository).deleteAllByProjectDetail(projectDetail);
  }
}
