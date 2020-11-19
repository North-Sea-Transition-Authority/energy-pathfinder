package uk.co.ogauthority.pathfinder.service.project.subseainfrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import javax.validation.Validation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.SubseaInfrastructureFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.subseainfrastructure.SubseaInfrastructureRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFacilitiesService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.SubseaInfrastructureTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class SubseaInfrastructureValidationServiceTest {

  @Mock
  private DevUkFacilitiesService devUkFacilitiesService;

  @Mock
  private SubseaInfrastructureRepository subseaInfrastructureRepository;

  @Mock
  private SubseaInfrastructureFormValidator subseaInfrastructureFormValidator;

  private SubseaInfrastructureService subseaInfrastructureService;

  private ProjectDetail projectDetail;

  @Before
  public void setup() {
    var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    var validationService = new ValidationService(validator);

    var searchSelectorService = new SearchSelectorService();
    subseaInfrastructureService = new SubseaInfrastructureService(
        devUkFacilitiesService,
        subseaInfrastructureRepository,
        searchSelectorService,
        validationService,
        subseaInfrastructureFormValidator,
        projectSetupService);

    projectDetail = ProjectUtil.getProjectDetails();
  }

  @Test
  public void isValid_whenValidAndFullValidation_thenTrue() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();
    assertThat(subseaInfrastructureService.isValid(subseaInfrastructure, ValidationType.FULL)).isTrue();
  }

  @Test
  public void isValid_whenValidAndPartialValidation_thenTrue() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();
    assertThat(subseaInfrastructureService.isValid(subseaInfrastructure, ValidationType.PARTIAL)).isTrue();
  }

  @Test
  public void isValid_whenIncompleteAndPartialValidation_thenTrue() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();
    subseaInfrastructure.setDescription(null);
    assertThat(subseaInfrastructureService.isValid(subseaInfrastructure, ValidationType.PARTIAL)).isTrue();
  }

  @Test
  public void isValid_whenIncompleteAndFullValidation_thenFalse() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();
    subseaInfrastructure.setDescription(null);
    assertThat(subseaInfrastructureService.isValid(subseaInfrastructure, ValidationType.FULL)).isFalse();
  }

  @Test
  public void isComplete_whenComplete_thenTrue() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();

    when(subseaInfrastructureRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(List.of(subseaInfrastructure));

    assertThat(subseaInfrastructureService.isComplete(projectDetail)).isTrue();
  }

  @Test
  public void isComplete_whenIncomplete_thenFalse() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();
    subseaInfrastructure.setDescription(null);

    when(subseaInfrastructureRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(List.of(subseaInfrastructure));

    assertThat(subseaInfrastructureService.isComplete(projectDetail)).isFalse();
  }

  @Test
  public void isComplete_whenNotSubseaInfrastructure_thenFalse() {

    when(subseaInfrastructureRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(List.of());

    assertThat(subseaInfrastructureService.isComplete(projectDetail)).isFalse();
  }

  @Test
  public void isComplete_whenOneCompleteAndOneIncomplete_thenFalse() {

    var incompleteSubseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();
    incompleteSubseaInfrastructure.setDescription(null);

    var completeSubseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();

    when(subseaInfrastructureRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(List.of(incompleteSubseaInfrastructure, completeSubseaInfrastructure));

    assertThat(subseaInfrastructureService.isComplete(projectDetail)).isFalse();
  }
}
