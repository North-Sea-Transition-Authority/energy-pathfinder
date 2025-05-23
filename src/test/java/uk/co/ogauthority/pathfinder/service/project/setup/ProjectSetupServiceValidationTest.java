package uk.co.ogauthority.pathfinder.service.project.setup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.validation.Validation;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.tasks.ProjectTaskListSetup;
import uk.co.ogauthority.pathfinder.model.form.project.setup.ProjectSetupFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.tasks.ProjectTaskListSetupRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectTaskListSetupTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSetupServiceValidationTest {

  @Mock
  private ProjectTaskListSetupRepository projectTaskListSetupRepository;

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private ProjectSetupFormValidator projectSetupFormValidator;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private ProjectSetupService projectSetupService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  private final ProjectTaskListSetup setup = ProjectTaskListSetupTestUtil.getProjectTaskListSetupWithFieldStageIndependentSectionsAnswered(details);

  @Before
  public void setUp() {
    var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    var validationService = new ValidationService(validator);
    projectSetupService = new ProjectSetupService(
        projectTaskListSetupRepository,
        projectInformationService,
        projectSetupFormValidator,
        validationService,
        entityDuplicationService
    );
  }

  @Test
  public void isCompleted_fullForm() {
    when(projectTaskListSetupRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(setup));
    assertThat(projectSetupService.isComplete(details)).isTrue();
  }

  @Test
  public void isCompleted_incompleteForm() {
    when(projectTaskListSetupRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    assertThat(projectSetupService.isComplete(details)).isFalse();
  }

  @Test
  public void isCompleted_partiallyCompleteForm() {
    var incompleteSetup = ProjectTaskListSetupTestUtil.getProjectTaskListSetupWithFieldStageIndependentSectionsAnswered(details);
    incompleteSetup.setTaskListSections(null);
    when(projectTaskListSetupRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    assertThat(projectSetupService.isComplete(details)).isFalse();
  }
}
