package uk.co.ogauthority.pathfinder.model.form.project.commissionedwell;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.MaxYearMustBeInFutureHint;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.CommissionedWellTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@ExtendWith(MockitoExtension.class)
class CommissionedWellFormValidatorTest {

  @Mock
  private MinMaxDateInputValidator minMaxDateInputValidator;

  private CommissionedWellFormValidator commissionedWellFormValidator;

  @BeforeEach
  void setup() {
    commissionedWellFormValidator = new CommissionedWellFormValidator(
        minMaxDateInputValidator
    );
    doCallRealMethod().when(minMaxDateInputValidator).validate(any(), any(), any(Object[].class));
    when(minMaxDateInputValidator.supports(MinMaxDateInput.class)).thenReturn(true);
  }

  @Test
  void validate_whenPartialValidationAndNoCommissioningScheduleDate_thenNoError() {

    var form = CommissionedWellTestUtil.getCompleteCommissionedWellForm();
    form.setCommissioningSchedule(new MinMaxDateInput(null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    var validationHint = new CommissionedWellValidatorHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(commissionedWellFormValidator, form, errors, validationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  void validate_whenFullValidationAndNoCommissioningScheduleDate_thenError() {

    var form = CommissionedWellTestUtil.getCompleteCommissionedWellForm();
    form.setCommissioningSchedule(new MinMaxDateInput(null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    var validationHint = new CommissionedWellValidatorHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(commissionedWellFormValidator, form, errors, validationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("commissioningSchedule.minYear", Set.of("minYear" + FieldValidationErrorCodes.INVALID.getCode()))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("commissioningSchedule.minYear",
            Set.of(String.format(
                MinMaxDateInputValidator.ENTER_BOTH_YEARS_ERROR,
                CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_LABEL.getInitCappedLabel(),
                StringDisplayUtil.getPrefixForVowelOrConsonant(CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_YEAR_LABELS.getMinYearLabel()),
                CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_YEAR_LABELS.getMinYearLabel(),
                CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_YEAR_LABELS.getMaxYearLabel()
            )))
    );
  }

  @Test
  void validate_whenNoCommissioningScheduleMinDate_thenError() {

    var form = CommissionedWellTestUtil.getCompleteCommissionedWellForm();
    form.setCommissioningSchedule(new MinMaxDateInput(null, String.valueOf(LocalDate.now().getYear())));

    var errors = new BeanPropertyBindingResult(form, "form");

    Arrays.asList(ValidationType.FULL, ValidationType.PARTIAL).forEach(validationType -> {

      var validationHint = new CommissionedWellValidatorHint(ValidationType.FULL);

      ValidationUtils.invokeValidator(commissionedWellFormValidator, form, errors, validationHint);

      var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
      var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

      assertThat(fieldErrors).containsExactly(
          entry("commissioningSchedule.minYear", Set.of("minYear" + FieldValidationErrorCodes.INVALID.getCode()))
      );

      assertThat(fieldErrorMessages).containsExactly(
          entry("commissioningSchedule.minYear",
              Set.of(String.format(
                  MinMaxDateInputValidator.ENTER_BOTH_YEARS_ERROR,
                  CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_LABEL.getInitCappedLabel(),
                  StringDisplayUtil.getPrefixForVowelOrConsonant(CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_YEAR_LABELS.getMinYearLabel()),
                  CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_YEAR_LABELS.getMinYearLabel(),
                  CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_YEAR_LABELS.getMaxYearLabel()
              )))
      );
    });
  }

  @Test
  void validate_whenNoCommissioningScheduleMaxDate_thenError() {

    var form = CommissionedWellTestUtil.getCompleteCommissionedWellForm();
    form.setCommissioningSchedule(new MinMaxDateInput("2021", null));

    var errors = new BeanPropertyBindingResult(form, "form");

    Arrays.asList(ValidationType.FULL, ValidationType.PARTIAL).forEach(validationType -> {

      var validationHint = new CommissionedWellValidatorHint(ValidationType.FULL);

      ValidationUtils.invokeValidator(commissionedWellFormValidator, form, errors, validationHint);

      var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
      var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

      assertThat(fieldErrors).containsExactly(
          entry("commissioningSchedule.maxYear", Set.of("maxYear" + FieldValidationErrorCodes.INVALID.getCode()))
      );

      assertThat(fieldErrorMessages).containsExactly(
          entry("commissioningSchedule.maxYear",
              Set.of(String.format(
                  MinMaxDateInputValidator.ENTER_BOTH_YEARS_ERROR,
                  CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_LABEL.getInitCappedLabel(),
                  StringDisplayUtil.getPrefixForVowelOrConsonant(CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_YEAR_LABELS.getMinYearLabel()),
                  CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_YEAR_LABELS.getMinYearLabel(),
                  CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_YEAR_LABELS.getMaxYearLabel()
              )))
      );
    });
  }

  @Test
  void validate_whenCommissioningScheduleMaxDateInPast_thenError() {

    var form = CommissionedWellTestUtil.getCompleteCommissionedWellForm();
    form.setCommissioningSchedule(new MinMaxDateInput("2019", "2020"));

    var errors = new BeanPropertyBindingResult(form, "form");

    Arrays.asList(ValidationType.FULL, ValidationType.PARTIAL).forEach(validationType -> {

      var validationHint = new CommissionedWellValidatorHint(ValidationType.FULL);

      ValidationUtils.invokeValidator(commissionedWellFormValidator, form, errors, validationHint);

      var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
      var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

      assertThat(fieldErrors).containsExactly(
          entry("commissioningSchedule.maxYear", Set.of("maxYear" + FieldValidationErrorCodes.INVALID.getCode()))
      );

      assertThat(fieldErrorMessages).containsExactly(
          entry("commissioningSchedule.maxYear",
              Set.of(String.format(
                  MaxYearMustBeInFutureHint.MAX_YEAR_IN_FUTURE_ERROR,
                  CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_LABEL.getInitCappedLabel(),
                  CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_YEAR_LABELS.getMaxYearLabel()
              )))
      );
    });
  }

  @Test
  void validate_whenCommissioningScheduleMaxDateInBeforeMinDate_thenError() {

    var form = CommissionedWellTestUtil.getCompleteCommissionedWellForm();
    form.setCommissioningSchedule(new MinMaxDateInput("2020", "2019"));

    var errors = new BeanPropertyBindingResult(form, "form");

    Arrays.asList(ValidationType.FULL, ValidationType.PARTIAL).forEach(validationType -> {

      var validationHint = new CommissionedWellValidatorHint(ValidationType.FULL);

      ValidationUtils.invokeValidator(commissionedWellFormValidator, form, errors, validationHint);

      var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
      var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

      assertThat(fieldErrors).containsExactly(
          entry("commissioningSchedule.minYear", Set.of("minYear" + FieldValidationErrorCodes.INVALID.getCode())),
          entry("commissioningSchedule.maxYear", Set.of("maxYear" + FieldValidationErrorCodes.INVALID.getCode()))
      );

      assertThat(fieldErrorMessages).containsExactly(
          entry("commissioningSchedule.minYear", Set.of(String.format(
              MinMaxDateInputValidator.MIN_BEFORE_MAX_YEAR_ERROR,
              CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_LABEL.getInitCappedLabel(),
              CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_YEAR_LABELS.getMinYearLabel(),
              CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_YEAR_LABELS.getMaxYearLabel())
          )),
          entry("commissioningSchedule.maxYear",
              Set.of(String.format(
                  MaxYearMustBeInFutureHint.MAX_YEAR_IN_FUTURE_ERROR,
                  CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_LABEL.getInitCappedLabel(),
                  CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_YEAR_LABELS.getMaxYearLabel()
              )))
      );
    });
  }

  @Test
  void validate_whenNoWellsSelectedAndFullValidation_thenErrorOnWellsSelectField() {

    var form = CommissionedWellTestUtil.getCompleteCommissionedWellForm();
    form.setWells(Collections.emptyList());

    var errors = new BeanPropertyBindingResult(form, "form");

    var validationHint = new CommissionedWellValidatorHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(commissionedWellFormValidator, form, errors, validationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("wellSelected", Set.of("wellSelected" + FieldValidationErrorCodes.INVALID.getCode()))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("wellSelected", Set.of(CommissionedWellFormValidator.NO_WELLS_SELECTED_ERROR_MESSAGE))
    );

  }

  @Test
  void validate_whenNoWellsSelectedAndPartialValidation_thenNoError() {

    var form = CommissionedWellTestUtil.getCompleteCommissionedWellForm();
    form.setWells(Collections.emptyList());

    var errors = new BeanPropertyBindingResult(form, "form");

    var validationHint = new CommissionedWellValidatorHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(commissionedWellFormValidator, form, errors, validationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).isEmpty();
    assertThat(fieldErrorMessages).isEmpty();
  }

  @Test
  void validate_whenEmptyFormAndPartialValidation_thenNoError() {

    var form = CommissionedWellTestUtil.getEmptyCommissionedWellForm();

    var errors = new BeanPropertyBindingResult(form, "form");

    var validationHint = new CommissionedWellValidatorHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(commissionedWellFormValidator, form, errors, validationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).isEmpty();
    assertThat(fieldErrorMessages).isEmpty();
  }

  @Test
  void validate_whenEmptyFormAndFullValidation_thenErrors() {

    var form = CommissionedWellTestUtil.getEmptyCommissionedWellForm();

    var errors = new BeanPropertyBindingResult(form, "form");

    var validationHint = new CommissionedWellValidatorHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(commissionedWellFormValidator, form, errors, validationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactlyInAnyOrderEntriesOf(
        Map.of(
            "commissioningSchedule.minYear", Set.of("minYear" + FieldValidationErrorCodes.INVALID.getCode()),
            "wellSelected", Set.of("wellSelected" + FieldValidationErrorCodes.INVALID.getCode())
        )
    );

    assertThat(fieldErrorMessages).containsExactlyInAnyOrderEntriesOf(
        Map.of(
            "commissioningSchedule.minYear",
            Set.of(String.format(
                MinMaxDateInputValidator.ENTER_BOTH_YEARS_ERROR,
                CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_LABEL.getInitCappedLabel(),
                StringDisplayUtil.getPrefixForVowelOrConsonant(
                    CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_YEAR_LABELS.getMinYearLabel()
                ),
                CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_YEAR_LABELS.getMinYearLabel(),
                CommissionedWellValidatorHint.COMMISSIONING_SCHEDULE_YEAR_LABELS.getMaxYearLabel()
            )),
            "wellSelected",
            Set.of(CommissionedWellFormValidator.NO_WELLS_SELECTED_ERROR_MESSAGE)
        )
    );
  }

  @Test
  void validate_whenValidForm_thenNoErrors() {

    var form = CommissionedWellTestUtil.getCompleteCommissionedWellForm();

    var errors = new BeanPropertyBindingResult(form, "form");

    Arrays.asList(ValidationType.FULL, ValidationType.PARTIAL).forEach(validationType -> {

      var validationHint = new CommissionedWellValidatorHint(validationType);

      ValidationUtils.invokeValidator(commissionedWellFormValidator, form, errors, validationHint);

      var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
      var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

      assertThat(fieldErrors).isEmpty();
      assertThat(fieldErrorMessages).isEmpty();
    });
  }

}