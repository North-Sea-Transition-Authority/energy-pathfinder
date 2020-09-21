package uk.co.ogauthority.pathfinder.model.view.contactdetail;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ContactDetailViewUtilTest {

  private TestContactDetailCapture testContactDetailCapture;

  @Before
  public void setup() {
    testContactDetailCapture = new TestContactDetailCapture();
    testContactDetailCapture.setName("NAME");
    testContactDetailCapture.setPhoneNumber("123");
    testContactDetailCapture.setEmailAddress("someone@example.com");
    testContactDetailCapture.setJobTitle("Job title");
  }

  @Test
  public void from() {
    var contactDetailView = ContactDetailViewUtil.from(testContactDetailCapture);
    assertThat(contactDetailView.getName()).isEqualTo(testContactDetailCapture.getName());
    assertThat(contactDetailView.getPhoneNumber()).isEqualTo(testContactDetailCapture.getPhoneNumber());
    assertThat(contactDetailView.getEmailAddress()).isEqualTo(testContactDetailCapture.getEmailAddress());
    assertThat(contactDetailView.getJobTitle()).isEqualTo(testContactDetailCapture.getJobTitle());
  }
}