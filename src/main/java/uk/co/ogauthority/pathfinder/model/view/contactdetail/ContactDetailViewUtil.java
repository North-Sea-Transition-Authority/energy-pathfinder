package uk.co.ogauthority.pathfinder.model.view.contactdetail;

import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailCapture;

public class ContactDetailViewUtil {

  private ContactDetailViewUtil() {
    throw new IllegalStateException("ContactDetailUtil is a util class and should not be instantiated");
  }

  public static ContactDetailView from(ContactDetailCapture contactDetailCapture) {
    var contactDetailView = new ContactDetailView();
    contactDetailView.setName(contactDetailCapture.getName());
    contactDetailView.setPhoneNumber(contactDetailCapture.getPhoneNumber());
    contactDetailView.setJobTitle(contactDetailCapture.getJobTitle());
    contactDetailView.setEmailAddress(contactDetailCapture.getEmailAddress());
    return contactDetailView;
  }
}
