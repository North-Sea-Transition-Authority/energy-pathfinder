package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanTenderSetup;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanTenderCompletionForm;
import uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender.ForwardWorkPlanTenderSetupRepository;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanTenderCompletionServicePersistenceTest {

  @Mock
  private ValidationService validationService;

  @Mock
  private ForwardWorkPlanTenderSetupRepository forwardWorkPlanTenderSetupRepository;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private ForwardWorkPlanTenderCompletionService forwardWorkPlanTenderCompletionService;

  @Before
  public void setup() {

    final var forwardWorkPlanTenderSetupService = new ForwardWorkPlanTenderSetupService(
        forwardWorkPlanTenderSetupRepository,
        validationService
    );

    forwardWorkPlanTenderCompletionService = new ForwardWorkPlanTenderCompletionService(
        forwardWorkPlanTenderSetupService,
        validationService
    );
  }

  @Test
  public void saveForwardWorkPlanTenderCompletionForm_verifyInteractions() {

    final var forwardWorkPlanTenderSetup = new ForwardWorkPlanTenderSetup();

    when(forwardWorkPlanTenderSetupRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.of(forwardWorkPlanTenderSetup));

    final var forwardWorkPlanTenderCompletionForm = new ForwardWorkPlanTenderCompletionForm();
    forwardWorkPlanTenderCompletionForm.setHasOtherTendersToAdd(true);

    when(forwardWorkPlanTenderSetupRepository.save(any()))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    final var persistedEntity = forwardWorkPlanTenderCompletionService.saveForwardWorkPlanTenderCompletionForm(
        forwardWorkPlanTenderCompletionForm,
        projectDetail
    );

    assertThat(persistedEntity.getHasOtherTendersToAdd()).isEqualTo(forwardWorkPlanTenderCompletionForm.getHasOtherTendersToAdd());
  }

  @Test
  public void resetHasOtherTendersToAdd_verifyInteractions() {

    final var forwardWorkPlanTenderCompletionForm = new ForwardWorkPlanTenderCompletionForm();
    forwardWorkPlanTenderCompletionForm.setHasOtherTendersToAdd(true);

    when(forwardWorkPlanTenderSetupRepository.save(any()))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    final var resultingSetup = forwardWorkPlanTenderCompletionService.resetHasOtherTendersToAdd(
        forwardWorkPlanTenderCompletionForm,
        projectDetail
    );

    assertThat(resultingSetup.getHasTendersToAdd()).isNull();

  }
}
