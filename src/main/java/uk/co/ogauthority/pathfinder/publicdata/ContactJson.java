package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;

record ContactJson(
    String name,
    String phoneNumber,
    String jobTitle,
    String emailAddress
) {

  static ContactJson from(ProjectInformation projectInformation) {
    var name = projectInformation.getContactName();
    var phoneNumber = projectInformation.getPhoneNumber();
    var jobTitle = projectInformation.getJobTitle();
    var emailAddress = projectInformation.getEmailAddress();

    return new ContactJson(name, phoneNumber, jobTitle, emailAddress);
  }
}
