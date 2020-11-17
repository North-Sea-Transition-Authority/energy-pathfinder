package uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.form.forminput.file.UploadFileWithDescriptionForm;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.testutil.CollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class CollaborationOpportunityFormValidatorTest {

  private final CollaborationOpportunityValidationHint hint = new CollaborationOpportunityValidationHint();

  private CollaborationOpportunityFormValidator collaborationOpportunityFormValidator;

  @Before
  public void setup() {
    collaborationOpportunityFormValidator = new CollaborationOpportunityFormValidator();
  }

  @Test
  public void validate_whenValidFrom_thenNoErrors() {
    final var form = CollaborationOpportunityTestUtil.getCompleteForm();
    final var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(collaborationOpportunityFormValidator, form, errors, hint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenTooManyFiles_thenError() {

    var form = CollaborationOpportunityTestUtil.getCompleteForm();

    final var file = new UploadFileWithDescriptionForm("1", "description", Instant.now());
    form.setUploadedFileWithDescriptionForms(List.of(file, file));

    final var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(collaborationOpportunityFormValidator, form, errors, hint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).containsExactly(
        entry(
            "uploadedFileWithDescriptionForms",
            Set.of("uploadedFileWithDescriptionForms" + FieldValidationErrorCodes.EXCEEDED_MAXIMUM_FILE_UPLOAD_LIMIT.getCode())
        )
    );
  }

  @Test
  public void validate_whenExactFileLimit_thenNoError() {

    var form = CollaborationOpportunityTestUtil.getCompleteForm();

    final var file = new UploadFileWithDescriptionForm("1", "description", Instant.now());
    form.setUploadedFileWithDescriptionForms(List.of(file));

    final var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(collaborationOpportunityFormValidator, form, errors, hint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test(expected = ActionNotAllowedException.class)
  public void validate_whenNoValidationHint_thenException() {
    final var form = CollaborationOpportunityTestUtil.getCompleteForm();
    final var errors = new BeanPropertyBindingResult(form, "form");
    ValidationUtils.invokeValidator(collaborationOpportunityFormValidator, form, errors);
  }

}