package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.ogauthority.pathfinder.testutil.ContactDetailsTestUtil.CONTACT_NAME;
import static uk.co.ogauthority.pathfinder.testutil.ContactDetailsTestUtil.EMAIL;
import static uk.co.ogauthority.pathfinder.testutil.ContactDetailsTestUtil.JOB_TITLE;
import static uk.co.ogauthority.pathfinder.testutil.ContactDetailsTestUtil.PHONE_NUMBER;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.model.view.contactdetail.TestContactDetailCapture;

class ContactJsonTest {

  @Test
  void from() {
    var contactDetailCapture = new TestContactDetailCapture();

    contactDetailCapture.setName(CONTACT_NAME);
    contactDetailCapture.setPhoneNumber(PHONE_NUMBER);
    contactDetailCapture.setJobTitle(JOB_TITLE);
    contactDetailCapture.setEmailAddress(EMAIL);

    var contactJson = ContactJson.from(contactDetailCapture);

    var expectedContactJson = new ContactJson(
        contactDetailCapture.getName(),
        contactDetailCapture.getPhoneNumber(),
        contactDetailCapture.getJobTitle(),
        contactDetailCapture.getEmailAddress()
    );

    assertThat(contactJson).isEqualTo(expectedContactJson);
  }
}
