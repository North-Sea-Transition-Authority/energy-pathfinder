package uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure;

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
import uk.co.ogauthority.pathfinder.testutil.InfrastructureCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class CollaborationOpportunityFormValidatorTest {

  private final InfrastructureCollaborationOpportunityValidationHint hint = new InfrastructureCollaborationOpportunityValidationHint();

  private InfrastructureCollaborationOpportunityFormValidator infrastructureCollaborationOpportunityFormValidator;

  @Before
  public void setup() {
    infrastructureCollaborationOpportunityFormValidator = new InfrastructureCollaborationOpportunityFormValidator();
  }

  @Test
  public void validate_whenValidFrom_thenNoErrors() {
    final var form = InfrastructureCollaborationOpportunityTestUtil.getCompleteForm();
    final var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(infrastructureCollaborationOpportunityFormValidator, form, errors, hint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenTooManyFiles_thenError() {

    var form = InfrastructureCollaborationOpportunityTestUtil.getCompleteForm();

    final var file = new UploadFileWithDescriptionForm("1", "description", Instant.now());
    form.setUploadedFileWithDescriptionForms(List.of(file, file));

    final var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(infrastructureCollaborationOpportunityFormValidator, form, errors, hint);

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

    var form = InfrastructureCollaborationOpportunityTestUtil.getCompleteForm();

    final var file = new UploadFileWithDescriptionForm("1", "description", Instant.now());
    form.setUploadedFileWithDescriptionForms(List.of(file));

    final var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(infrastructureCollaborationOpportunityFormValidator, form, errors, hint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test(expected = ActionNotAllowedException.class)
  public void validate_whenNoValidationHint_thenException() {
    final var form = InfrastructureCollaborationOpportunityTestUtil.getCompleteForm();
    final var errors = new BeanPropertyBindingResult(form, "form");
    ValidationUtils.invokeValidator(infrastructureCollaborationOpportunityFormValidator, form, errors);
  }

}