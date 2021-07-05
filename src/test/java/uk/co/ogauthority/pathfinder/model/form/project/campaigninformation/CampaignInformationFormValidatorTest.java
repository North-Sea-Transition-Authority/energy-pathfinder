package uk.co.ogauthority.pathfinder.model.form.project.campaigninformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class CampaignInformationFormValidatorTest {

  private final CampaignInformationForm form = new CampaignInformationForm();

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private CampaignInformationFormValidator campaignInformationFormValidator;

  @Before
  public void setup() {
    campaignInformationFormValidator = new CampaignInformationFormValidator();
  }

  @Test
  public void supports_whenCampaignInformationFormClass_thenTrue() {

    final var supportedClass = CampaignInformationForm.class;

    final var isSupported = campaignInformationFormValidator.supports(supportedClass);

    assertThat(isSupported).isTrue();
  }

  @Test
  public void supports_whenNotCampaignInformationFormClass_thenFalse() {

    final var nonSupportedClass = NotSupportedFormClass.class;

    final var isSupported = campaignInformationFormValidator.supports(nonSupportedClass);

    assertThat(isSupported).isFalse();
  }

  @Test(expected = ActionNotAllowedException.class)
  public void validate_whenNoCampaignInformationValidationHint_thenException() {
    final var errors = new BeanPropertyBindingResult(form, "form");
    campaignInformationFormValidator.validate(form, errors);
  }

  @Test
  public void validate_whenFullValidationAndPartOfCampaignAndNoProjectsAdded_thenErrors() {

    form.setIsPartOfCampaign(true);
    form.setProjectsIncludedInCampaign(List.of());

    final var validationResult = validateCampaignInformationForm(form, ValidationType.FULL);

    final var resultingErrors = ValidatorTestingUtil.extractErrors(validationResult);

    String PROJECT_SELECTOR_FIELD_NAME = CampaignInformationValidationHint.PROJECT_SELECTOR_FIELD_NAME;
    assertThat(resultingErrors).containsExactly(
        entry(PROJECT_SELECTOR_FIELD_NAME, Set.of(String.format("%s%s", PROJECT_SELECTOR_FIELD_NAME, FieldValidationErrorCodes.MIN_LENGTH_NOT_MET)))
    );
  }

  @Test
  public void validate_whenFullValidationAndPartOfCampaignAndProjectsAdded_thenNoErrors() {

    form.setIsPartOfCampaign(true);
    form.setProjectsIncludedInCampaign(List.of(1));

    final var validationResult = validateCampaignInformationForm(form, ValidationType.FULL);

    final var resultingErrors = ValidatorTestingUtil.extractErrors(validationResult);

    assertThat(resultingErrors).isEmpty();
  }

  @Test
  public void validate_whenFullValidationAndNotPartOfCampaign_thenNoErrors() {

    form.setIsPartOfCampaign(false);

    final var validationResult = validateCampaignInformationForm(form, ValidationType.FULL);

    final var resultingErrors = ValidatorTestingUtil.extractErrors(validationResult);

    assertThat(resultingErrors).isEmpty();
  }

  @Test
  public void validate_whenPartialValidationAndNotPartOfCampaign_thenNoErrors() {

    form.setIsPartOfCampaign(false);

    final var validationResult = validateCampaignInformationForm(form, ValidationType.PARTIAL);

    final var resultingErrors = ValidatorTestingUtil.extractErrors(validationResult);

    assertThat(resultingErrors).isEmpty();
  }

  @Test
  public void validate_whenPartialValidationAndPartOfCampaignAndProjectsAdded_thenNoErrors() {

    form.setIsPartOfCampaign(true);
    form.setProjectsIncludedInCampaign(List.of(1));

    final var validationResult = validateCampaignInformationForm(form, ValidationType.PARTIAL);

    final var resultingErrors = ValidatorTestingUtil.extractErrors(validationResult);

    assertThat(resultingErrors).isEmpty();

  }

  @Test
  public void validate_whenPartialValidationAndPartOfCampaignAndNoProjectsAdded_thenNoErrors() {

    form.setIsPartOfCampaign(false);
    form.setProjectsIncludedInCampaign(List.of());

    final var validationResult = validateCampaignInformationForm(form, ValidationType.PARTIAL);

    final var resultingErrors = ValidatorTestingUtil.extractErrors(validationResult);

    assertThat(resultingErrors).isEmpty();

  }

  // the check for isPartOfCampaign being mandatory is done by the standard validation service
  // and not this custom validator. This test is simply to assert that the validation doesn't crash
  // with a null input for this Boolean field
  @Test
  public void validate_whenFullValidationAndPartOfCampaignNull_thenNoCrashOrErrors() {

    form.setIsPartOfCampaign(null);

    final var validationResult = validateCampaignInformationForm(form, ValidationType.FULL);

    final var resultingErrors = ValidatorTestingUtil.extractErrors(validationResult);

    assertThat(resultingErrors).isEmpty();
  }

  private BindingResult validateCampaignInformationForm(CampaignInformationForm form, ValidationType validationType) {

    CampaignInformationValidationHint campaignInformationValidationHint = new CampaignInformationValidationHint(
        validationType, projectDetail);

    final var errors = new BeanPropertyBindingResult(form, "form");

    campaignInformationFormValidator.validate(form, errors, campaignInformationValidationHint);

    return errors;
  }

  static class NotSupportedFormClass {}

}