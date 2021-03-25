package uk.co.ogauthority.pathfinder.model.form.forminput.contact;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Map;
import java.util.Set;
import javax.validation.Validation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ContactDetailsTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

public class ContactDetailFormTest {

  private ValidationService validationService;

  @Before
  public void setup() {
    var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    validationService = new ValidationService(validator);
  }

  private Map<String, Set<String>> validateAndReturnErrors(ContactDetailForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    validationService.validate(form, bindingResult, ValidationType.FULL);
    return ValidatorTestingUtil.extractErrors(bindingResult);
  }

  @Test
  public void validate_whenValidForm_noErrors() {
    var contactDetailForm = ContactDetailsTestUtil.createContactDetailForm();
    var fieldErrors = validateAndReturnErrors(contactDetailForm);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void objectConstruction_whenContactDetailCapture_thenCorrectPopulation() {
    var contactDetailEntity = new ContactDetailTestEntity();
    contactDetailEntity.setName(ContactDetailsTestUtil.CONTACT_NAME);
    contactDetailEntity.setJobTitle(ContactDetailsTestUtil.JOB_TITLE);
    contactDetailEntity.setPhoneNumber(ContactDetailsTestUtil.PHONE_NUMBER);
    contactDetailEntity.setEmailAddress(ContactDetailsTestUtil.EMAIL);

    var contactDetailForm = new ContactDetailForm(contactDetailEntity);

    assertThat(contactDetailForm.getName()).isEqualTo(contactDetailEntity.getName());
    assertThat(contactDetailForm.getJobTitle()).isEqualTo(contactDetailEntity.getJobTitle());
    assertThat(contactDetailForm.getPhoneNumber()).isEqualTo(contactDetailEntity.getPhoneNumber());
    assertThat(contactDetailForm.getEmailAddress()).isEqualTo(contactDetailEntity.getEmailAddress());
  }

  @Test
  public void validateNotEmptyAnnotatedFields() {
    var contactDetailForm = new ContactDetailForm();
    var fieldErrors = validateAndReturnErrors(contactDetailForm);

    assertThat(fieldErrors).containsOnly(
        entry("name", Set.of("NotEmpty")),
        entry("phoneNumber", Set.of("NotEmpty")),
        entry("jobTitle", Set.of("NotEmpty")),
        entry("emailAddress", Set.of("NotEmpty"))
    );
  }

  @Test
  public void validateName_whenGreaterThan4000Chars_thenError() {
    var contactDetailForm = ContactDetailsTestUtil.createContactDetailForm();
    contactDetailForm.setName(ValidatorTestingUtil.over4000Chars());

    var fieldErrors = validateAndReturnErrors(contactDetailForm);

    assertThat(fieldErrors).containsOnly(
        entry("name", Set.of("LengthRestrictedString"))
    );
  }

  @Test
  public void validateJobTitle_whenGreaterThan4000Chars_thenError() {
    var contactDetailForm = ContactDetailsTestUtil.createContactDetailForm();
    contactDetailForm.setJobTitle(ValidatorTestingUtil.over4000Chars());

    var fieldErrors = validateAndReturnErrors(contactDetailForm);

    assertThat(fieldErrors).containsOnly(
        entry("jobTitle", Set.of("LengthRestrictedString"))
    );
  }

  @Test
  public void validatePhoneNumber_whenInvalidFormat_thenError() {
    var contactDetailForm = ContactDetailsTestUtil.createContactDetailForm();
    contactDetailForm.setPhoneNumber("NOT_A_PHONE_NUMBER");

    var fieldErrors = validateAndReturnErrors(contactDetailForm);

    assertThat(fieldErrors).containsOnly(
        entry("phoneNumber", Set.of("ValidPhoneNumber"))
    );
  }

  @Test
  public void validateEmailAddress_whenInvalidFormat_thenError() {
    var contactDetailForm = ContactDetailsTestUtil.createContactDetailForm();
    contactDetailForm.setEmailAddress("NOT_AN_EMAIL_ADDRESS");

    var fieldErrors = validateAndReturnErrors(contactDetailForm);

    assertThat(fieldErrors).containsOnly(
        entry("emailAddress", Set.of("ValidEmail"))
    );
  }
}