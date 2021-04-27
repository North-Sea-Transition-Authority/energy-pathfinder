package uk.co.ogauthority.pathfinder.service.project.location;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import javax.validation.Validation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFieldService;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectLocationServiceValidationTest {

  @Mock
  private ProjectLocationRepository projectLocationRepository;

  @Mock
  private DevUkFieldService fieldService;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private ProjectLocationFormValidator projectLocationFormValidator;

  @Mock
  private ProjectLocationBlocksService projectLocationBlocksService;

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private ProjectLocationService projectLocationService;

  @Before
  public void setup() {
    final var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    final var validationService = new ValidationService(validator);

    projectLocationService = new ProjectLocationService(
        projectLocationRepository,
        fieldService,
        searchSelectorService,
        validationService,
        projectLocationFormValidator,
        projectLocationBlocksService,
        projectInformationService,
        entityDuplicationService
    );
  }

  @Test
  public void validate_whenNoLicenceBlocksAddedAndFullValidation_thenNoErrors() {
    final var form = ProjectLocationTestUtil.getCompletedForm();
    form.setLicenceBlocks(Collections.emptyList());

    final var validationErrors = validateProjectLocationForm(form, ValidationType.FULL);
    assertThat(validationErrors.hasErrors()).isFalse();
  }

  @Test
  public void validate_whenNoLicenceBlocksAddedAndPartialValidation_thenNoErrors() {
    final var form = ProjectLocationTestUtil.getCompletedForm();
    form.setLicenceBlocks(Collections.emptyList());

    final var validationErrors = validateProjectLocationForm(form, ValidationType.PARTIAL);
    assertThat(validationErrors.hasErrors()).isFalse();
  }

  @Test
  public void validate_whenLicenceBlocksAddedAndFullValidation_thenNoErrors() {
    final var form = ProjectLocationTestUtil.getCompletedForm();
    form.setLicenceBlocks(ProjectLocationTestUtil.LICENCE_BLOCKS);

    final var validationErrors = validateProjectLocationForm(form, ValidationType.FULL);
    assertThat(validationErrors.hasErrors()).isFalse();
  }

  @Test
  public void validate_whenLicenceBlocksAddedAndPartialValidation_thenNoErrors() {
    final var form = ProjectLocationTestUtil.getCompletedForm();
    form.setLicenceBlocks(ProjectLocationTestUtil.LICENCE_BLOCKS);

    final var validationErrors = validateProjectLocationForm(form, ValidationType.PARTIAL);
    assertThat(validationErrors.hasErrors()).isFalse();
  }

  private BindingResult validateProjectLocationForm(ProjectLocationForm form,
                                                    ValidationType validationType) {
    final var bindingResult = new BeanPropertyBindingResult(form, "form");
    return projectLocationService.validate(form, bindingResult, validationType);
  }

}