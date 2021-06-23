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
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanTenderCompletionForm;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanTenderCompletionServiceTest {

  @Mock
  private ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService;

  @Mock
  private ValidationService validationService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private ForwardWorkPlanTenderCompletionService forwardWorkPlanTenderCompletionService;

  @Before
  public void setup() {
    forwardWorkPlanTenderCompletionService = new ForwardWorkPlanTenderCompletionService(
        forwardWorkPlanTenderSetupService,
        validationService
    );
  }

  @Test
  public void getForwardWorkPlanTenderCompletionFormFromDetail_whenSetupFound_thenPropertiesPopulated() {

    final var forwardWorkPlanTenderSetup = new ForwardWorkPlanTenderSetup();
    forwardWorkPlanTenderSetup.setHasOtherTendersToAdd(true);

    when(forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupForDetail(projectDetail)).thenReturn(
        Optional.of(forwardWorkPlanTenderSetup)
    );

    final var form = forwardWorkPlanTenderCompletionService.getForwardWorkPlanTenderCompletionFormFromDetail(
        projectDetail
    );

    assertThat(form.getHasOtherTendersToAdd()).isEqualTo(forwardWorkPlanTenderSetup.getHasOtherTendersToAdd());
  }

  @Test
  public void getForwardWorkPlanTenderCompletionFormFromDetail_whenSetupNotFound_thenEmptyForm() {

    when(forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupForDetail(projectDetail)).thenReturn(
        Optional.empty()
    );

    final var form = forwardWorkPlanTenderCompletionService.getForwardWorkPlanTenderCompletionFormFromDetail(
        projectDetail
    );

    assertThat(form.getHasOtherTendersToAdd()).isNull();
  }

  @Test
  public void validate_verifyInteractions() {

    final var form = new ForwardWorkPlanTenderCompletionForm();
    final var bindingResult = new BeanPropertyBindingResult(form, "form");
    final var validationType = ValidationType.FULL;

    forwardWorkPlanTenderCompletionService.validate(form, bindingResult, validationType);

    verify(validationService, times(1)).validate(
        form,
        bindingResult,
        validationType
    );
  }
}