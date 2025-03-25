package uk.co.ogauthority.pathfinder.model.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationValidationHint;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFieldService;
import uk.co.ogauthority.pathfinder.service.project.location.LicenceBlockValidatorService;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectLocationFormValidatorTest {

  public static final ThreeFieldDateInput BAD_THREE_FIELD_DATE = new ThreeFieldDateInput(-1, 22, -1);

  @Mock
  private DateInputValidator dateInputValidator;

  @Mock
  private LicenceBlockValidatorService licenceBlockValidatorService;

  @Mock
  private DevUkFieldService devUkFieldService;

  private ProjectLocationFormValidator validator;

  @Before
  public void setUp() {
    validator = new ProjectLocationFormValidator(dateInputValidator, licenceBlockValidatorService, devUkFieldService);
    doCallRealMethod().when(dateInputValidator).validate(any(), any(), any(Object[].class));
    when(dateInputValidator.supports(any())).thenReturn(true);
    var testField = new DevUkField();
    testField.setLandward(false);
    testField.setActive(true);
    when(devUkFieldService.findById(anyInt())).thenReturn(Optional.of(testField));
  }

  @Test
  public void validate_completeForm_isValid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_latitudeCoordinatesBlankAndValidationTypeFull_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLatitude().getDegreesInput().setInputValue("");
    form.getCentreOfInterestLatitude().getMinutesInput().setInputValue("");
    form.getCentreOfInterestLatitude().getSecondsInput().setInputValue("");
    form.getCentreOfInterestLatitude().getHemisphereInput().setInputValue("");

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("centreOfInterestLatitude.degreesInput.inputValue", Set.of("Enter a complete centre of interest latitude")),
        entry("centreOfInterestLatitude.minutesInput.inputValue", Set.of("")),
        entry("centreOfInterestLatitude.secondsInput.inputValue", Set.of("")),
        entry("centreOfInterestLatitude.hemisphereInput.inputValue", Set.of(""))
    );
  }

  @Test
  public void validate_latitudeCoordinatesBlankAndValidationTypePartial_isValid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLatitude().getDegreesInput().setInputValue("");
    form.getCentreOfInterestLatitude().getMinutesInput().setInputValue("");
    form.getCentreOfInterestLatitude().getSecondsInput().setInputValue("");
    form.getCentreOfInterestLatitude().getHemisphereInput().setInputValue("");

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).isEmpty();
  }

  @Test
  public void validate_latitudeCoordinatesAtMinimum_isValid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLatitude().getDegreesInput().setInputValue("45");
    form.getCentreOfInterestLatitude().getMinutesInput().setInputValue("0");
    form.getCentreOfInterestLatitude().getSecondsInput().setInputValue("0");

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).isEmpty();
  }

  @Test
  public void validate_latitudeCoordinatesAtMaximum_isValid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLatitude().getDegreesInput().setInputValue("64");
    form.getCentreOfInterestLatitude().getMinutesInput().setInputValue("59");
    form.getCentreOfInterestLatitude().getSecondsInput().setInputValue("59.99");

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).isEmpty();
  }

  @Test
  public void validate_latitudeDegreesBelowMinimum_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLatitude().getDegreesInput().setInputValue("44");
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("centreOfInterestLatitude.degreesInput.inputValue", Set.of("Centre of interest latitude must be within 45° 0' 0.0\" and 64° 59' 59.999\"")),
        entry("centreOfInterestLatitude.minutesInput.inputValue", Set.of("")),
        entry("centreOfInterestLatitude.secondsInput.inputValue", Set.of(""))
    );
  }

  @Test
  public void validate_latitudeDegreesAboveMaximum_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLatitude().getDegreesInput().setInputValue("65");
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("centreOfInterestLatitude.degreesInput.inputValue", Set.of("Centre of interest latitude must be within 45° 0' 0.0\" and 64° 59' 59.999\"")),
        entry("centreOfInterestLatitude.minutesInput.inputValue", Set.of("")),
        entry("centreOfInterestLatitude.secondsInput.inputValue", Set.of(""))
    );
  }

  @Test
  public void validate_latitudeMinutesBelowMinimum_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLatitude().getMinutesInput().setInputValue("-1");
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("centreOfInterestLatitude.minutesInput.inputValue", Set.of("Centre of interest latitude minutes must be 0 or more"))
    );
  }

  @Test
  public void validate_latitudeMinutesAboveMaximum_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLatitude().getMinutesInput().setInputValue("60");
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("centreOfInterestLatitude.minutesInput.inputValue", Set.of("Centre of interest latitude minutes must be 60 or fewer"))
    );
  }

  @Test
  public void validate_latitudeSecondsBelowMinimum_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLatitude().getSecondsInput().setInputValue("-0.001");
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("centreOfInterestLatitude.secondsInput.inputValue", Set.of("Centre of interest latitude seconds must be 0 or more"))
    );
  }

  @Test
  public void validate_latitudeSecondsAboveMaximum_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLatitude().getSecondsInput().setInputValue("60");
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("centreOfInterestLatitude.secondsInput.inputValue", Set.of("Centre of interest latitude seconds must be fewer than 60"))
    );
  }

  @Test
  public void validate_latitudeSecondsHasTooManyDecimalPlaces_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLatitude().getSecondsInput().setInputValue("59.9999");
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("centreOfInterestLatitude.secondsInput.inputValue", Set.of("Centre of interest latitude seconds must include no more than 3 decimal places"))
    );
  }

  @Test
  public void validate_longitudeCoordinatesBlankAndValidationTypeFull_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLongitude().getDegreesInput().setInputValue("");
    form.getCentreOfInterestLongitude().getMinutesInput().setInputValue("");
    form.getCentreOfInterestLongitude().getSecondsInput().setInputValue("");
    form.getCentreOfInterestLongitude().getHemisphereInput().setInputValue("");

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("centreOfInterestLongitude.degreesInput.inputValue", Set.of("Enter a complete centre of interest longitude")),
        entry("centreOfInterestLongitude.minutesInput.inputValue", Set.of("")),
        entry("centreOfInterestLongitude.secondsInput.inputValue", Set.of("")),
        entry("centreOfInterestLongitude.hemisphereInput.inputValue", Set.of(""))
    );
  }

  @Test
  public void validate_longitudeCoordinatesBlankAndValidationTypePartial_isValid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLongitude().getDegreesInput().setInputValue("");
    form.getCentreOfInterestLongitude().getMinutesInput().setInputValue("");
    form.getCentreOfInterestLongitude().getSecondsInput().setInputValue("");
    form.getCentreOfInterestLongitude().getHemisphereInput().setInputValue("");

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).isEmpty();
  }

  @Test
  public void validate_longitudeCoordinatesAtMinimum_isValid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLongitude().getDegreesInput().setInputValue("0");
    form.getCentreOfInterestLongitude().getMinutesInput().setInputValue("0");
    form.getCentreOfInterestLongitude().getSecondsInput().setInputValue("0");

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).isEmpty();
  }

  @Test
  public void validate_longitudeCoordinatesAtMaximum_isValid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLongitude().getDegreesInput().setInputValue("30");
    form.getCentreOfInterestLongitude().getMinutesInput().setInputValue("59");
    form.getCentreOfInterestLongitude().getSecondsInput().setInputValue("59.99");

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).isEmpty();
  }

  @Test
  public void validate_longitudeDegreesBelowMinimum_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLongitude().getDegreesInput().setInputValue("-1");
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("centreOfInterestLongitude.degreesInput.inputValue", Set.of("Centre of interest longitude degrees must be 0 or more"))
    );
  }

  @Test
  public void validate_longitudeDegreesAboveMaximum_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLongitude().getDegreesInput().setInputValue("31");
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("centreOfInterestLongitude.degreesInput.inputValue", Set.of("Centre of interest longitude must be within 0° 0' 0.0\" and 30° 59' 59.999\"")),
        entry("centreOfInterestLongitude.minutesInput.inputValue", Set.of("")),
        entry("centreOfInterestLongitude.secondsInput.inputValue", Set.of(""))
    );
  }

  @Test
  public void validate_longitudeMinutesBelowMinimum_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLongitude().getMinutesInput().setInputValue("-1");
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("centreOfInterestLongitude.minutesInput.inputValue", Set.of("Centre of interest longitude minutes must be 0 or more"))
    );
  }

  @Test
  public void validate_longitudeMinutesAboveMaximum_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLongitude().getMinutesInput().setInputValue("60");
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("centreOfInterestLongitude.minutesInput.inputValue", Set.of("Centre of interest longitude minutes must be 60 or fewer"))
    );
  }

  @Test
  public void validate_longitudeSecondsBelowMinimum_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLongitude().getSecondsInput().setInputValue("-0.001");
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("centreOfInterestLongitude.secondsInput.inputValue", Set.of("Centre of interest longitude seconds must be 0 or more"))
    );
  }

  @Test
  public void validate_longitudeSecondsAboveMaximum_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLongitude().getSecondsInput().setInputValue("60");
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("centreOfInterestLongitude.secondsInput.inputValue", Set.of("Centre of interest longitude seconds must be fewer than 60"))
    );
  }

  @Test
  public void validate_longitudeSecondsHasTooManyDecimalPlaces_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLongitude().getSecondsInput().setInputValue("59.9999");
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("centreOfInterestLongitude.secondsInput.inputValue", Set.of("Centre of interest longitude seconds must include no more than 3 decimal places"))
    );
  }

  @Test
  public void validate_answeredTrueButMissingDate_withEmptyDateAcceptableHint() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_answeredTrueButMissingDate() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.setApprovedFdpDate(new ThreeFieldDateInput(null, null, null));
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("approvedFdpDate.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("approvedFdpDate.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedFdpDate.year", Set.of(DateInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("approvedFdpDate.day", Set.of(
            String.format(DateInputValidator.EMPTY_DATE_ERROR, "an "+ ProjectLocationValidationHint.APPROVED_FDP_LABEL.getLabel()))
        ),
        entry("approvedFdpDate.month", Set.of("")),
        entry("approvedFdpDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_answeredTrueButMissingDate_isInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.setApprovedFdpDate(BAD_THREE_FIELD_DATE);
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("approvedFdpDate.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("approvedFdpDate.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedFdpDate.year", Set.of(DateInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("approvedFdpDate.day", Set.of(
            ProjectLocationValidationHint.APPROVED_FDP_LABEL.getInitCappedLabel() + DateInputValidator.VALID_DATE_ERROR)
        ),
        entry("approvedFdpDate.month", Set.of("")),
        entry("approvedFdpDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_answeredTrueToBoth_bothDatesMissing() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.setApprovedFdpDate(new ThreeFieldDateInput(null, null, null));
    form.setApprovedDecomProgram(true);
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).contains(
        entry("approvedFdpDate.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("approvedFdpDate.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedFdpDate.year", Set.of(DateInputValidator.YEAR_INVALID_CODE)),
        entry("approvedDecomProgramDate.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("approvedDecomProgramDate.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedDecomProgramDate.year", Set.of(DateInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).contains(
        entry("approvedFdpDate.day", Set.of(
            String.format(DateInputValidator.EMPTY_DATE_ERROR, "an "+ ProjectLocationValidationHint.APPROVED_FDP_LABEL.getLabel()))
        ),
        entry("approvedFdpDate.month", Set.of("")),
        entry("approvedFdpDate.year", Set.of("")),
        entry("approvedDecomProgramDate.day", Set.of(
            String.format(DateInputValidator.EMPTY_DATE_ERROR, "an " + ProjectLocationValidationHint.APPROVED_DECOM_LABEL.getLabel()))
        ),
        entry("approvedDecomProgramDate.month", Set.of("")),
        entry("approvedDecomProgramDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_answeredTrueToBoth_bothDatesMissing_areInvalid() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.setApprovedFdpDate(BAD_THREE_FIELD_DATE);
    form.setApprovedDecomProgram(true);
    form.setApprovedDecomProgramDate(BAD_THREE_FIELD_DATE);
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).contains(
        entry("approvedFdpDate.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("approvedFdpDate.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedFdpDate.year", Set.of(DateInputValidator.YEAR_INVALID_CODE)),
        entry("approvedDecomProgramDate.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("approvedDecomProgramDate.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedDecomProgramDate.year", Set.of(DateInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).contains(
        entry("approvedFdpDate.day", Set.of(
            ProjectLocationValidationHint.APPROVED_FDP_LABEL.getInitCappedLabel() + DateInputValidator.VALID_DATE_ERROR)),
        entry("approvedFdpDate.month", Set.of("")),
        entry("approvedFdpDate.year", Set.of("")),
        entry("approvedDecomProgramDate.day", Set.of(
            ProjectLocationValidationHint.APPROVED_DECOM_LABEL.getInitCappedLabel() + DateInputValidator.VALID_DATE_ERROR)
        ),
        entry("approvedDecomProgramDate.month", Set.of("")),
        entry("approvedDecomProgramDate.year", Set.of("")
        )
    );
  }

  @Test
  public void validate_fdpApprovalDateCannotBeInFuture_whenInFuture_thenFail() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.setApprovedFdpDate(new ThreeFieldDateInput(LocalDate.now().plusYears(1)));

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).contains(
        entry("approvedFdpDate.day", Set.of(DateInputValidator.DAY_BEFORE_DATE_CODE)),
        entry("approvedFdpDate.month", Set.of(DateInputValidator.MONTH_BEFORE_DATE_CODE)),
        entry("approvedFdpDate.year", Set.of(DateInputValidator.YEAR_BEFORE_DATE_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("approvedFdpDate.day", Set.of(
            ProjectLocationValidationHint.APPROVED_FDP_LABEL.getInitCappedLabel() + " must be the same as or before today's date")),
        entry("approvedFdpDate.month", Set.of("")),
        entry("approvedFdpDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_fdpApprovalDateCannotBeInFuture_whenTheSameAs_thenPass() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.setApprovedFdpDate(new ThreeFieldDateInput(LocalDate.now()));

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_fdpApprovalDateCannotBeInFuture_whenBefore_thenPass() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.setApprovedFieldDevelopmentPlan(true);
    form.setApprovedFdpDate(new ThreeFieldDateInput(LocalDate.now().minusYears(1)));

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_decomProgramApprovalCannotBeInFuture_whenInFuture_thenFail() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.setApprovedDecomProgram(true);
    form.setApprovedDecomProgramDate(new ThreeFieldDateInput(LocalDate.now().plusYears(1)));

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).contains(
        entry("approvedDecomProgramDate.day", Set.of(DateInputValidator.DAY_BEFORE_DATE_CODE)),
        entry("approvedDecomProgramDate.month", Set.of(DateInputValidator.MONTH_BEFORE_DATE_CODE)),
        entry("approvedDecomProgramDate.year", Set.of(DateInputValidator.YEAR_BEFORE_DATE_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("approvedDecomProgramDate.day", Set.of(
            ProjectLocationValidationHint.APPROVED_DECOM_LABEL.getInitCappedLabel() + " must be the same as or before today's date")),
        entry("approvedDecomProgramDate.month", Set.of("")),
        entry("approvedDecomProgramDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_decomProgramApprovalCannotBeInFuture_whenTheSameAs_thenPass() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.setApprovedDecomProgramDate(new ThreeFieldDateInput(LocalDate.now()));

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_decomProgramApprovalDateCannotBeInFuture_whenBefore_thenPass() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.setApprovedDecomProgramDate(new ThreeFieldDateInput(LocalDate.now().minusYears(1)));

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_fieldSelectedIsNotFound_thenFail() {
    var form = ProjectLocationTestUtil.getCompletedForm();

    when(devUkFieldService.findById(Integer.parseInt(form.getField()))).thenReturn(Optional.empty());

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).contains(
        entry("field", Set.of(ProjectLocationFormValidator.INVALID_FIELD_ERROR_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("field", Set.of(
            ProjectLocationFormValidator.INVALID_FIELD_ERROR_MSG
        ))
    );
  }

  @Test
  public void validate_fieldSelectedIsNotActive_thenFail() {
    var form = ProjectLocationTestUtil.getCompletedForm();

    var testField = new DevUkField();
    testField.setActive(false);
    testField.setLandward(false);

    when(devUkFieldService.findById(Integer.parseInt(form.getField()))).thenReturn(Optional.of(testField));

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).contains(
        entry("field", Set.of(ProjectLocationFormValidator.INVALID_FIELD_ERROR_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("field", Set.of(
            ProjectLocationFormValidator.INVALID_FIELD_ERROR_MSG
        ))
    );
  }

  @Test
  public void validate_fieldSelectedIsLandward_thenFail() {
    var form = ProjectLocationTestUtil.getCompletedForm();

    var testField = new DevUkField();
    testField.setActive(true);
    testField.setLandward(true);

    when(devUkFieldService.findById(Integer.parseInt(form.getField()))).thenReturn(Optional.of(testField));

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).contains(
        entry("field", Set.of(ProjectLocationFormValidator.INVALID_FIELD_ERROR_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("field", Set.of(
            ProjectLocationFormValidator.INVALID_FIELD_ERROR_MSG
        ))
    );
  }
}
