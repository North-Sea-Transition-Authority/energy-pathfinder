package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

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
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetup;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationCompletionForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanCollaborationCompletionServiceTest {

  @Mock
  private ForwardWorkPlanCollaborationSetupService forwardWorkPlanCollaborationSetupService;

  @Mock
  private ValidationService validationService;

  private ProjectDetail projectDetail;

  private ForwardWorkPlanCollaborationCompletionService forwardWorkPlanCollaborationCompletionService;

  @Before
  public void setup() {
    forwardWorkPlanCollaborationCompletionService = new ForwardWorkPlanCollaborationCompletionService(
        forwardWorkPlanCollaborationSetupService,
        validationService
    );

    projectDetail = ProjectUtil.getProjectDetails();
  }

  @Test
  public void getForwardWorkPlanCollaborationCompletionFormFromDetail_whenEntityFound_thenPopulatedForm() {

    final var collaborationSetupEntity = new ForwardWorkPlanCollaborationSetup();
    collaborationSetupEntity.setHasOtherCollaborationToAdd(false);

    when(forwardWorkPlanCollaborationSetupService.getCollaborationSetupFromDetail(projectDetail))
        .thenReturn(Optional.of(collaborationSetupEntity));

    final var completionForm = forwardWorkPlanCollaborationCompletionService
        .getForwardWorkPlanCollaborationCompletionFormFromDetail(projectDetail);

    assertThat(completionForm.getHasOtherCollaborationsToAdd()).isEqualTo(collaborationSetupEntity.getHasOtherCollaborationToAdd());

  }

  @Test
  public void getForwardWorkPlanCollaborationCompletionFormFromDetail_whenEntityNotFound_thenEmptyForm() {

    when(forwardWorkPlanCollaborationSetupService.getCollaborationSetupFromDetail(projectDetail))
        .thenReturn(Optional.empty());

    final var completionForm = forwardWorkPlanCollaborationCompletionService
        .getForwardWorkPlanCollaborationCompletionFormFromDetail(projectDetail);

    assertThat(completionForm.getHasOtherCollaborationsToAdd()).isNull();
  }

  @Test
  public void validate_verifyInteractions() {

    final var form = new ForwardWorkPlanCollaborationCompletionForm();
    final var bindingResult = ReverseRouter.emptyBindingResult();
    final var validationType = ValidationType.FULL;

    forwardWorkPlanCollaborationCompletionService.validate(
        form,
        bindingResult,
        validationType
    );

    verify(validationService, times(1)).validate(
        form,
        bindingResult,
        validationType
    );
  }

  @Test
  public void saveCollaborationCompletionForm_verifyInteractions() {

    final var forwardWorkPlanCollaborationSetup = new ForwardWorkPlanCollaborationSetup();

    when(forwardWorkPlanCollaborationSetupService.getCollaborationSetupFromDetail(projectDetail))
        .thenReturn(Optional.of(forwardWorkPlanCollaborationSetup));

    final var completionForm = new ForwardWorkPlanCollaborationCompletionForm();

    forwardWorkPlanCollaborationCompletionService.saveCollaborationCompletionForm(completionForm, projectDetail);

    verify(forwardWorkPlanCollaborationSetupService, times(1)).persistForwardWorkPlanCollaborationSetup(
        forwardWorkPlanCollaborationSetup
    );
  }
}