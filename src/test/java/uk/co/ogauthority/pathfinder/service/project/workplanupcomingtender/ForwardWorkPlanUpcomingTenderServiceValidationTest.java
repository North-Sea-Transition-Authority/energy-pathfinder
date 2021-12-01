package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import javax.validation.Validation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanTenderSetup;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanUpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanUpcomingTenderServiceValidationTest {

  @Mock
  private FunctionService functionService;

  @Mock
  private ForwardWorkPlanUpcomingTenderFormValidator workPlanUpcomingTenderFormValidator;

  @Mock
  private ForwardWorkPlanUpcomingTenderRepository workPlanUpcomingTenderRepository;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @Mock
  private ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService;

  private ForwardWorkPlanUpcomingTenderService workPlanUpcomingTenderService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  private final ForwardWorkPlanUpcomingTender upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

  @Before
  public void setup() {
    var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    var validationService = new ValidationService(validator);

    workPlanUpcomingTenderService = new ForwardWorkPlanUpcomingTenderService(
        functionService,
        validationService,
        workPlanUpcomingTenderFormValidator,
        workPlanUpcomingTenderRepository,
        searchSelectorService,
        entityDuplicationService,
        forwardWorkPlanTenderSetupService
    );
  }

  @Test
  public void isValid_whenValidForm_returnsTrue() {
    assertThat(workPlanUpcomingTenderService.isValid(upcomingTender, ValidationType.FULL)).isTrue();
  }

  @Test
  public void isValid_whenIncompleteForm_returnsFalse() {
    upcomingTender.setJobTitle(null);
    assertThat(workPlanUpcomingTenderService.isValid(upcomingTender, ValidationType.FULL)).isFalse();
  }

  @Test
  public void isComplete_whenNoSetupAnswer_thenFalse() {

    when(forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupForDetail(projectDetail)).thenReturn(Optional.empty());

    final var isComplete = workPlanUpcomingTenderService.isComplete(projectDetail);

    assertThat(isComplete).isFalse();
  }

  @Test
  public void isComplete_whenSetupAnswerIsNo_thenTrue() {

    final var tenderSetup = new ForwardWorkPlanTenderSetup();
    tenderSetup.setHasTendersToAdd(false);

    when(forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupForDetail(projectDetail)).thenReturn(Optional.of(tenderSetup));

    final var isComplete = workPlanUpcomingTenderService.isComplete(projectDetail);

    assertThat(isComplete).isTrue();
  }

  @Test
  public void isComplete_whenSetupAnswerIsYesAndNoTendersAdded_thenFalse() {

    final var tenderSetup = new ForwardWorkPlanTenderSetup();
    tenderSetup.setHasTendersToAdd(true);
    tenderSetup.setHasOtherTendersToAdd(false);

    when(forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupForDetail(projectDetail)).thenReturn(Optional.of(tenderSetup));

    when(workPlanUpcomingTenderRepository.findByProjectDetailOrderByIdAsc(projectDetail)).thenReturn(List.of());

    final var isComplete = workPlanUpcomingTenderService.isComplete(projectDetail);

    assertThat(isComplete).isFalse();
  }

  @Test
  public void isComplete_whenSetupAnswerIsYesAndNotAllTendersValid_thenFalse() {

    final var tenderSetup = new ForwardWorkPlanTenderSetup();
    tenderSetup.setHasTendersToAdd(true);
    tenderSetup.setHasOtherTendersToAdd(false);

    when(forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupForDetail(projectDetail)).thenReturn(Optional.of(tenderSetup));

    final var validUpcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    final var invalidUpcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    invalidUpcomingTender.setJobTitle(null);

    when(workPlanUpcomingTenderRepository.findByProjectDetailOrderByIdAsc(projectDetail)).thenReturn(
        List.of(validUpcomingTender, invalidUpcomingTender)
    );

    final var isComplete = workPlanUpcomingTenderService.isComplete(projectDetail);

    assertThat(isComplete).isFalse();
  }

  @Test
  public void isComplete_whenSetupAnswerIsYesAndAllTendersValidAndNoMoreToAdd_thenTrue() {

    final var tenderSetup = new ForwardWorkPlanTenderSetup();
    tenderSetup.setHasTendersToAdd(true);
    tenderSetup.setHasOtherTendersToAdd(false);

    when(forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupForDetail(projectDetail)).thenReturn(Optional.of(tenderSetup));

    final var validUpcomingTender1 = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    final var validUpcomingTender2 = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

    when(workPlanUpcomingTenderRepository.findByProjectDetailOrderByIdAsc(projectDetail)).thenReturn(
        List.of(validUpcomingTender1, validUpcomingTender2)
    );

    final var isComplete = workPlanUpcomingTenderService.isComplete(projectDetail);

    assertThat(isComplete).isTrue();
  }

  @Test
  public void isComplete_whenSetupAnswerIsYesAndMoreToAddIsYes_thenFalse() {

    final var tenderSetup = new ForwardWorkPlanTenderSetup();
    tenderSetup.setHasTendersToAdd(true);
    tenderSetup.setHasOtherTendersToAdd(true);

    when(forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupForDetail(projectDetail)).thenReturn(Optional.of(tenderSetup));

    final var isComplete = workPlanUpcomingTenderService.isComplete(projectDetail);

    assertThat(isComplete).isFalse();
  }

  @Test
  public void isComplete_whenSetupAnswerIsYesAndMoreToAddNotSet_thenFalse() {

    final var tenderSetup = new ForwardWorkPlanTenderSetup();
    tenderSetup.setHasTendersToAdd(true);
    tenderSetup.setHasOtherTendersToAdd(null);

    when(forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupForDetail(projectDetail)).thenReturn(Optional.of(tenderSetup));

    final var isComplete = workPlanUpcomingTenderService.isComplete(projectDetail);

    assertThat(isComplete).isFalse();
  }
}
