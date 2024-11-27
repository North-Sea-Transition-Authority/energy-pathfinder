package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailCapture;

record ContactJson(
    String name,
    String phoneNumber,
    String jobTitle,
    String emailAddress
) {

  static ContactJson from(ContactDetailCapture contactDetailCapture) {
    var name = contactDetailCapture.getName();
    var phoneNumber = contactDetailCapture.getPhoneNumber();
    var jobTitle = contactDetailCapture.getJobTitle();
    var emailAddress = contactDetailCapture.getEmailAddress();

    return new ContactJson(name, phoneNumber, jobTitle, emailAddress);
  }
}
