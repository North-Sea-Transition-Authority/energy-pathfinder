package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class ContactJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var contactJson = ContactJson.from(projectInformation);

    var expectedContactJson = new ContactJson(
        projectInformation.getContactName(),
        projectInformation.getPhoneNumber(),
        projectInformation.getJobTitle(),
        projectInformation.getEmailAddress()
    );

    assertThat(contactJson).isEqualTo(expectedContactJson);
  }
}
