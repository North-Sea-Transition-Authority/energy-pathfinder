package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;

public class ContactDetailsTestUtil {

  public static final String CONTACT_NAME = "Jane Doe";
  public static final String PHONE_NUMBER = "01303 123 456";
  public static final String JOB_TITLE = "Big Boss";
  public static final String EMAIL = "a@b.co";

  private ContactDetailsTestUtil() {
    throw new IllegalStateException("ContactDetailsTestUtil is a utility class and should not be instantiated");
  }

  public static ContactDetailForm createContactDetailForm() {
    return createContactDetailForm(CONTACT_NAME, PHONE_NUMBER, JOB_TITLE, EMAIL);
  }

  public static ContactDetailForm createContactDetailForm(String name,
                                                          String phoneNumber,
                                                          String jobTitle,
                                                          String email) {
    var contactDetailForm = new ContactDetailForm();
    contactDetailForm.setName(name);
    contactDetailForm.setPhoneNumber(phoneNumber);
    contactDetailForm.setJobTitle(jobTitle);
    contactDetailForm.setEmailAddress(email);
    return contactDetailForm;
  }

}
