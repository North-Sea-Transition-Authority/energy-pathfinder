package uk.co.ogauthority.pathfinder.testutil;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractForm;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractView;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractViewUtil;

public class AwardedContractTestUtil {

  public static final String CONTRACTOR_NAME = "My first contractor";
  public static final Function CONTRACT_FUNCTION = Function.LOGISTICS;
  public static final String DESCRIPTION_OF_WORK = "Description of work";
  public static final LocalDate DATE_AWARDED = LocalDate.now();
  public static final ContractBand CONTRACT_BAND = ContractBand.GREATER_THAN_OR_EQUAL_TO_25M;
  public static final String CONTACT_NAME = ContactDetailsTestUtil.CONTACT_NAME;
  public static final String PHONE_NUMBER = ContactDetailsTestUtil.PHONE_NUMBER;
  public static final String JOB_TITLE = ContactDetailsTestUtil.JOB_TITLE;
  public static final String EMAIL_ADDRESS = ContactDetailsTestUtil.EMAIL;
  public static final int ADDED_BY_ORGANISATION_GROUP = 1;
  private static final PortalOrganisationGroup ADDED_BY_PORTAL_ORGANISATION_GROUP =
      TeamTestingUtil.generateOrganisationGroup(1, "org", "org");

  private AwardedContractTestUtil() {
    throw new IllegalStateException("AwardedContractTestUtil is a utility class and should not be instantiated");
  }

  public static AwardedContract createAwardedContract(
      ProjectDetail projectDetail,
      String contractorName,
      Function contractFunction,
      String manualEntryContractFunction,
      String descriptionOfWork,
      LocalDate dateAwarded,
      ContractBand contractBand,
      String contactName,
      String phoneNumber,
      String emailAddress,
      String jobTitle,
      int addedByOrganisationGroup
  ) {
    var awardedContract = new AwardedContract();
    awardedContract.setProjectDetail(projectDetail);
    awardedContract.setContractorName(contractorName);
    awardedContract.setContractFunction(contractFunction);
    awardedContract.setManualContractFunction(manualEntryContractFunction);
    awardedContract.setDescriptionOfWork(descriptionOfWork);
    awardedContract.setDateAwarded(dateAwarded);
    awardedContract.setContractBand(contractBand);
    awardedContract.setContactName(contactName);
    awardedContract.setPhoneNumber(phoneNumber);
    awardedContract.setEmailAddress(emailAddress);
    awardedContract.setJobTitle(jobTitle);
    awardedContract.setAddedByOrganisationGroup(addedByOrganisationGroup);
    return awardedContract;
  }

  public static AwardedContract createAwardedContract() {
    return createAwardedContract(
        ProjectUtil.getProjectDetails(),
        CONTRACTOR_NAME,
        CONTRACT_FUNCTION,
        null,
        DESCRIPTION_OF_WORK,
        DATE_AWARDED,
        CONTRACT_BAND,
        CONTACT_NAME,
        PHONE_NUMBER,
        EMAIL_ADDRESS,
        JOB_TITLE,
        ADDED_BY_ORGANISATION_GROUP
    );
  }

  public static AwardedContract createAwardedContract_withManualEntryFunction(String function) {
    return createAwardedContract(
        ProjectUtil.getProjectDetails(),
        CONTRACTOR_NAME,
        null,
        function,
        DESCRIPTION_OF_WORK,
        DATE_AWARDED,
        CONTRACT_BAND,
        CONTACT_NAME,
        PHONE_NUMBER,
        EMAIL_ADDRESS,
        JOB_TITLE,
        ADDED_BY_ORGANISATION_GROUP
    );
  }

  public static AwardedContractForm createAwardedContractForm(
      String contractorName,
      String contractFunction,
      String descriptionOfWork,
      ThreeFieldDateInput dateAwarded,
      ContractBand contractBand,
      String contactName,
      String phoneNumber,
      String emailAddress,
      String jobTitle
  ) {
    var form = new AwardedContractForm();
    form.setContractorName(contractorName);
    form.setContractFunction(contractFunction);
    form.setDescriptionOfWork(descriptionOfWork);
    form.setDateAwarded(dateAwarded);
    form.setContractBand(contractBand);

    var contactDetailForm = new ContactDetailForm();
    contactDetailForm.setName(contactName);
    contactDetailForm.setPhoneNumber(phoneNumber);
    contactDetailForm.setJobTitle(jobTitle);
    contactDetailForm.setEmailAddress(emailAddress);
    form.setContactDetail(contactDetailForm);

    return form;
  }

  public static AwardedContractForm createAwardedContractForm() {
    return createAwardedContractForm(
        CONTRACTOR_NAME,
        CONTRACT_FUNCTION.getSelectionId(),
        DESCRIPTION_OF_WORK,
        new ThreeFieldDateInput(DATE_AWARDED),
        CONTRACT_BAND,
        CONTACT_NAME,
        PHONE_NUMBER,
        EMAIL_ADDRESS,
        JOB_TITLE
    );
  }

  public static AwardedContractView createAwardedContractView(Integer displayOrder) {
    return new AwardedContractViewUtil.AwardedContractViewBuilder(
        AwardedContractTestUtil.createAwardedContract(),
        displayOrder,
        ADDED_BY_PORTAL_ORGANISATION_GROUP
    )
        .build() ;
  }
}
