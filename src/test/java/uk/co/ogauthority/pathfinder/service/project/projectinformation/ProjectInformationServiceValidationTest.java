package uk.co.ogauthority.pathfinder.service.project.projectinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import javax.validation.Validation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetails;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.repository.project.projectinformation.ProjectInformationRepository;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

/**
 * Test the validation methods in ProjectInformationService.
 * Mocking spring validator methods doesn't seem to work and we need to verify calls in other tests,
 * so this has been separated for setup purposes.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectInformationServiceValidationTest {
  @Mock
  private ProjectInformationRepository projectInformationRepository;

  private SpringValidatorAdapter validator;

  private ProjectInformationService projectInformationService;

  private final ProjectDetails details = ProjectUtil.getProjectDetails();

  private ProjectInformation projectInformation;

  @Before
  public void setUp() throws Exception {
    validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    projectInformationService = new ProjectInformationService(
        projectInformationRepository,
        validator
    );
  }


  @Test
  public void isCompleted_fullForm() {
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(projectInformation));
    assertThat(projectInformationService.isComplete(details)).isTrue();
  }

  @Test
  public void isCompleted_incompleteForm() {
    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    assertThat(projectInformationService.isComplete(details)).isFalse();
  }
}
