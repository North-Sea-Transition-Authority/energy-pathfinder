package uk.co.ogauthority.pathfinder.testutil;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContractCommon;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContract;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetup;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.InfrastructureAwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormCommon;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractForm;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSummaryForm;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.infrastructure.InfrastructureAwardedContractForm;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractViewCommon;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupView;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractView;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractViewUtil;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.infrastructure.InfrastructureAwardedContractView;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.infrastructure.InfrastructureAwardedContractViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractSectionSummaryService;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class AwardedContractTestUtil {

  public static final String CONTRACTOR_NAME = "My first contractor";
  public static final Function CONTRACT_FUNCTION = Function.LOGISTICS;
  public static final String DESCRIPTION_OF_WORK = "Description of work";
  public static final LocalDate DATE_AWARDED = LocalDate.now();
  public static final ContractBand INFRASTRUCTURE_CONTRACT_BAND = ContractBand.GREATER_THAN_OR_EQUAL_TO_25M;
  public static final ContractBand WORK_PLAN_CONTRACT_BAND = ContractBand.LESS_THAN_5M;
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

  public static InfrastructureAwardedContract createInfrastructureAwardedContract(
      Integer id,
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
    var awardedContract = new InfrastructureAwardedContract(id, projectDetail);
    setAwardedContractCommonFields(
        awardedContract,
        contractorName,
        contractFunction,
        manualEntryContractFunction,
        descriptionOfWork,
        dateAwarded,
        contractBand,
        contactName,
        phoneNumber,
        emailAddress,
        jobTitle,
        addedByOrganisationGroup
    );
    return awardedContract;
  }

  public static ForwardWorkPlanAwardedContract createForwardWorkPlanAwardedContract(
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
    var awardedContract = new ForwardWorkPlanAwardedContract(projectDetail);
    setAwardedContractCommonFields(
        awardedContract,
        contractorName,
        contractFunction,
        manualEntryContractFunction,
        descriptionOfWork,
        dateAwarded,
        contractBand,
        contactName,
        phoneNumber,
        emailAddress,
        jobTitle,
        addedByOrganisationGroup
    );
    return awardedContract;
  }

  private static void setAwardedContractCommonFields(
      AwardedContractCommon awardedContract,
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
  }

  public static InfrastructureAwardedContract createInfrastructureAwardedContract() {
    return createInfrastructureAwardedContract(ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE));
  }

  public static InfrastructureAwardedContract createInfrastructureAwardedContract(ProjectDetail projectDetail) {
    return createInfrastructureAwardedContract(null, projectDetail);
  }

  public static InfrastructureAwardedContract createInfrastructureAwardedContract(Integer id, ProjectDetail projectDetail) {
    return createInfrastructureAwardedContract(
        id,
        projectDetail,
        CONTRACTOR_NAME,
        CONTRACT_FUNCTION,
        null,
        DESCRIPTION_OF_WORK,
        DATE_AWARDED,
        INFRASTRUCTURE_CONTRACT_BAND,
        CONTACT_NAME,
        PHONE_NUMBER,
        EMAIL_ADDRESS,
        JOB_TITLE,
        ADDED_BY_ORGANISATION_GROUP
    );
  }

  public static ForwardWorkPlanAwardedContract createForwardWorkPlanAwardedContract() {
    return createForwardWorkPlanAwardedContract(
        ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN),
        CONTRACTOR_NAME,
        CONTRACT_FUNCTION,
        null,
        DESCRIPTION_OF_WORK,
        DATE_AWARDED,
        WORK_PLAN_CONTRACT_BAND,
        CONTACT_NAME,
        PHONE_NUMBER,
        EMAIL_ADDRESS,
        JOB_TITLE,
        ADDED_BY_ORGANISATION_GROUP
    );
  }

  public static InfrastructureAwardedContract createInfrastructureAwardedContract_withManualEntryFunction(String function) {
    return createInfrastructureAwardedContract(
        null,
        ProjectUtil.getProjectDetails(),
        CONTRACTOR_NAME,
        null,
        function,
        DESCRIPTION_OF_WORK,
        DATE_AWARDED,
        INFRASTRUCTURE_CONTRACT_BAND,
        CONTACT_NAME,
        PHONE_NUMBER,
        EMAIL_ADDRESS,
        JOB_TITLE,
        ADDED_BY_ORGANISATION_GROUP
    );
  }

  public static ForwardWorkPlanAwardedContract createForwardWorkPlanAwardedContract_withManualEntryFunction(String function) {
    return createForwardWorkPlanAwardedContract(
        ProjectUtil.getProjectDetails(),
        CONTRACTOR_NAME,
        null,
        function,
        DESCRIPTION_OF_WORK,
        DATE_AWARDED,
        WORK_PLAN_CONTRACT_BAND,
        CONTACT_NAME,
        PHONE_NUMBER,
        EMAIL_ADDRESS,
        JOB_TITLE,
        ADDED_BY_ORGANISATION_GROUP
    );
  }

  public static InfrastructureAwardedContractForm createInfrastructureAwardedContractForm(
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
    var form = new InfrastructureAwardedContractForm();
    populateAwardedContractForm(
        form,
        contractorName,
        contractFunction,
        descriptionOfWork,
        dateAwarded,
        contractBand,
        contactName,
        phoneNumber,
        emailAddress,
        jobTitle
    );

    return form;
  }

  public static ForwardWorkPlanAwardedContractForm createForwardWorkPlanAwardedContractForm(
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
    var form = new ForwardWorkPlanAwardedContractForm();
    populateAwardedContractForm(
        form,
        contractorName,
        contractFunction,
        descriptionOfWork,
        dateAwarded,
        contractBand,
        contactName,
        phoneNumber,
        emailAddress,
        jobTitle
    );

    return form;
  }

  private static void populateAwardedContractForm(
      AwardedContractFormCommon form,
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
  }


  public static InfrastructureAwardedContractForm createInfrastructureAwardedContractForm() {
    return createInfrastructureAwardedContractForm(
        CONTRACTOR_NAME,
        CONTRACT_FUNCTION.getSelectionId(),
        DESCRIPTION_OF_WORK,
        new ThreeFieldDateInput(DATE_AWARDED),
        INFRASTRUCTURE_CONTRACT_BAND,
        CONTACT_NAME,
        PHONE_NUMBER,
        EMAIL_ADDRESS,
        JOB_TITLE
    );
  }

  public static ForwardWorkPlanAwardedContractForm createForwardWorkPlanAwardedContractForm() {
    return createForwardWorkPlanAwardedContractForm(
        CONTRACTOR_NAME,
        CONTRACT_FUNCTION.getSelectionId(),
        DESCRIPTION_OF_WORK,
        new ThreeFieldDateInput(DATE_AWARDED),
        WORK_PLAN_CONTRACT_BAND,
        CONTACT_NAME,
        PHONE_NUMBER,
        EMAIL_ADDRESS,
        JOB_TITLE
    );
  }

  public static InfrastructureAwardedContractView createInfrastructureAwardedContractView(Integer displayOrder) {
    return new InfrastructureAwardedContractViewUtil.InfrastructureAwardedContractViewBuilder(
        createInfrastructureAwardedContract(),
        displayOrder,
        ADDED_BY_PORTAL_ORGANISATION_GROUP
    )
        .build() ;
  }

  public static ForwardWorkPlanAwardedContractView createForwardWorkPlanAwardedContractView(Integer displayOrder) {
    return new ForwardWorkPlanAwardedContractViewUtil.ForwardWorkPlanAwardedContractViewBuilder(
        createForwardWorkPlanAwardedContract(),
        displayOrder,
        ADDED_BY_PORTAL_ORGANISATION_GROUP
    )
        .build() ;
  }

  private static void assertModelPropertiesCommon(ProjectSectionSummary projectSectionSummary,
                                           String templatePath,
                                           int displayOrder) {
    assertThat(projectSectionSummary.getDisplayOrder()).isEqualTo(displayOrder);
    assertThat(projectSectionSummary.getSidebarSectionLinks())
        .isEqualTo(List.of(AwardedContractSectionSummaryService.SECTION_LINK));
    assertThat(projectSectionSummary.getTemplatePath())
        .isEqualTo(templatePath);
  }

  public static void assertInfrastructureModelProperties(ProjectSectionSummary projectSectionSummary,
                                                         String templatePath,
                                                         int displayOrder) {
    assertModelPropertiesCommon(projectSectionSummary, templatePath, displayOrder);

    var model = projectSectionSummary.getTemplateModel();
    assertThat(model).containsOnlyKeys("awardedContractDiffModel");
  }

  public static void assertForwardWorkPlanModelProperties(ProjectSectionSummary projectSectionSummary,
                                                         String templatePath,
                                                         int displayOrder) {
    assertModelPropertiesCommon(projectSectionSummary, templatePath, displayOrder);

    var model = projectSectionSummary.getTemplateModel();
    assertThat(model).containsOnlyKeys("awardedContractDiffModel", "awardedContractSetupDiffModel");
  }



  public static ForwardWorkPlanAwardedContractSetup createForwardWorkPlanAwardedContractSetup(ProjectDetail projectDetail) {
    var contractSetup = new ForwardWorkPlanAwardedContractSetup(projectDetail);
    contractSetup.setHasContractToAdd(true);
    return contractSetup;
  }

  public static ForwardWorkPlanAwardedContractSetupView createForwardWorkPlanAwardedContractSetupView() {
    var setUpView = new ForwardWorkPlanAwardedContractSetupView();
    setUpView.setHasContractsToAdd("Yes");
    return setUpView;
  }

  public static ForwardWorkPlanAwardedContractSummaryForm createForwardWorkPlanAwardedContractSummaryForm() {
    var form = new ForwardWorkPlanAwardedContractSummaryForm();
    form.setHasOtherContractsToAdd(true);
    return form;
  }

  public static void checkCommonViewFields(AwardedContractCommon source,
                                           AwardedContractViewCommon destination,
                                           Integer displayOrder,
                                           PortalOrganisationGroup addedByPortalOrganisationGroup) {
    assertThat(destination.getDisplayOrder()).isEqualTo(displayOrder);
    assertThat(destination.getContractorName()).isEqualTo(source.getContractorName());
    assertThat(destination.getDescriptionOfWork()).isEqualTo(source.getDescriptionOfWork());
    assertThat(destination.getDateAwarded()).isEqualTo(DateUtil.formatDate(source.getDateAwarded()));

    assertThat(destination.getContactName()).isEqualTo(source.getContactName());
    assertThat(destination.getContactPhoneNumber()).isEqualTo(source.getPhoneNumber());
    assertThat(destination.getContactEmailAddress()).isEqualTo(source.getEmailAddress());
    assertThat(destination.getContactJobTitle()).isEqualTo(source.getJobTitle());
    assertThat(destination.getAddedByPortalOrganisationGroup()).isEqualTo(addedByPortalOrganisationGroup.getName());
  }
}
