package uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.form.forminput.file.UploadFileWithDescriptionForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityValidationHintCommon;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanCollaborationOpportunityFormValidatorTest {

  private ForwardWorkPlanCollaborationOpportunityFormValidator forwardWorkPlanCollaborationOpportunityFormValidator;

  @Before
  public void setup() {
    forwardWorkPlanCollaborationOpportunityFormValidator = new ForwardWorkPlanCollaborationOpportunityFormValidator();
  }

  @Test
  public void validate_whenValidForm_thenNoErrors() {

    final var form = ForwardWorkPlanCollaborationOpportunityTestUtil.getCompleteForm();

    final var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(
        forwardWorkPlanCollaborationOpportunityFormValidator,
        form,
        errors,
        new ForwardWorkPlanCollaborationOpportunityValidationHint()
    );

    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenNoFiles_thenNoErrors() {

    final var form = ForwardWorkPlanCollaborationOpportunityTestUtil.getCompleteForm();
    form.setUploadedFileWithDescriptionForms(List.of());

    final var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(
        forwardWorkPlanCollaborationOpportunityFormValidator,
        form,
        errors,
        new ForwardWorkPlanCollaborationOpportunityValidationHint()
    );

    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenTooManyFiles_thenError() {

    final var form = ForwardWorkPlanCollaborationOpportunityTestUtil.getCompleteForm();
    final var validationHint = new ForwardWorkPlanCollaborationOpportunityValidationHint();

    final var uploadedFileList = new ArrayList<UploadFileWithDescriptionForm>();

    // Populate the uploaded file list with one more file that the allowed limit.
    // This should result in an error in the validator
    IntStream.range(0, validationHint.getFileUploadLimit() + 1).forEach(value ->
      uploadedFileList.add(new UploadFileWithDescriptionForm())
    );

    form.setUploadedFileWithDescriptionForms(uploadedFileList);

    final var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(
        forwardWorkPlanCollaborationOpportunityFormValidator,
        form,
        errors,
        validationHint
    );

    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    final var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    final var uploadedFileFormFieldName = "uploadedFileWithDescriptionForms";

    assertThat(fieldErrors).contains(
        entry(
            uploadedFileFormFieldName,
            Set.of(String.format(
                "%s%s",
                uploadedFileFormFieldName,
                FieldValidationErrorCodes.EXCEEDED_MAXIMUM_FILE_UPLOAD_LIMIT.getCode())
            )
        )
    );

    assertThat(fieldErrorMessages).contains(
        entry(uploadedFileFormFieldName, Set.of(CollaborationOpportunityValidationHintCommon.TOO_MANY_FILES_ERROR_MESSAGE))
    );
  }
}