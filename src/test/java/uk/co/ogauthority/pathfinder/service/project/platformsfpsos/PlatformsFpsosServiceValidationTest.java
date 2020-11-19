package uk.co.ogauthority.pathfinder.service.project.platformsfpsos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import javax.validation.Validation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.platformsfpsos.PlatformFpsoRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFacilitiesService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class PlatformsFpsosServiceValidationTest {

  @Mock
  private PlatformFpsoRepository platformFpsoRepository;

  @Mock
  private DevUkFacilitiesService devUkFacilitiesService;

  @Mock
  private PlatformFpsoFormValidator platformFpsoFormValidator;

  @Mock
  private PlatformsFpsosService platformsFpsosService;

  @Mock
  private ProjectSetupService projectSetupService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  private final PlatformFpso platformFpso = PlatformFpsoTestUtil.getPlatformFpso_withSubstructuresRemoved(details);

  @Before
  public void setUp() throws Exception {
    var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    var validationService = new ValidationService(validator);
    platformsFpsosService = new PlatformsFpsosService(
        platformFpsoRepository,
        devUkFacilitiesService,
        new SearchSelectorService(),
        platformFpsoFormValidator,
        validationService,
        projectSetupService);
  }

  @Test
  public void isValid_fullForm() {
    assertThat(platformsFpsosService.isValid(platformFpso, ValidationType.FULL)).isTrue();
  }

  @Test
  public void isValid_incompleteForm() {
    var invalidPlatformFpso = PlatformFpsoTestUtil.getPlatformFpso_NoSubstructuresRemoved(details);
    invalidPlatformFpso.setStructure(null);
    assertThat(platformsFpsosService.isValid(invalidPlatformFpso, ValidationType.FULL)).isFalse();
  }

  @Test
  public void isCompleted_fullForm() {
    when(platformFpsoRepository.findAllByProjectDetailOrderByIdAsc(any()))
        .thenReturn(Collections.singletonList(platformFpso));
    assertThat(platformsFpsosService.isComplete(details)).isTrue();
  }

  @Test
  public void isCompleted_incompleteForm() {
    when(platformFpsoRepository.findAllByProjectDetailOrderByIdAsc(any())).thenReturn(Collections.emptyList());
    assertThat(platformsFpsosService.isComplete(details)).isFalse();
  }
}