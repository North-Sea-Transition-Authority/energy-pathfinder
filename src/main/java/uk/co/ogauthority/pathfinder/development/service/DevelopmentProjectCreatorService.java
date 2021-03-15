package uk.co.ogauthority.pathfinder.development.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.development.DevelopmentProjectCreatorForm;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionAnswer;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationForm;
import uk.co.ogauthority.pathfinder.model.form.project.setup.ProjectSetupForm;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.service.project.StartProjectService;
import uk.co.ogauthority.pathfinder.service.project.location.ProjectLocationService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;

@Service
public class DevelopmentProjectCreatorService {
  private static final Logger LOGGER = LoggerFactory.getLogger(DevelopmentProjectCreatorService.class);

  //Project Information
  public static final String PROJECT_TITLE = "AUTO PROJECT TITLE";
  public static final String PROJECT_SUMMARY = "SUMMARY";
  public static final FieldStage FIELD_STAGE = FieldStage.ENERGY_TRANSITION;
  public static final String CONTACT_NAME = "Jane Doe";
  public static final String PHONE_NUMBER = "01303 123 456";
  public static final String JOB_TITLE = "Big Boss";
  public static final String EMAIL = "a@b.co";

  //Project Location
  public static final Integer FIELD_ID = 997;//CLAIR
  public static final FieldType FIELD_TYPE = FieldType.CARBON_STORAGE;
  public static final Integer WATER_DEPTH = 90;
  public static final Boolean APPROVED_FDP_PLAN = true;
  public static final LocalDate APPROVED_FDP_DATE = LocalDate.now().withDayOfMonth(1);
  public static final Boolean APPROVED_DECOM_PROGRAM = false;
  public static final UkcsArea UKCS_AREA =  UkcsArea.WOS;
  public static final List<String> LICENCE_BLOCKS = Collections.singletonList("16/29b1629b666"); //16/29b

  private final StartProjectService startProjectService;
  private final ProjectInformationService projectInformationService;
  private final ProjectLocationService projectLocationService;
  private final ProjectSetupService projectSetupService;
  private final ProjectDetailsRepository projectDetailsRepository;


  @Autowired
  public DevelopmentProjectCreatorService(StartProjectService startProjectService,
                                          ProjectInformationService projectInformationService,
                                          ProjectLocationService projectLocationService,
                                          ProjectSetupService projectSetupService,
                                          ProjectDetailsRepository projectDetailsRepository) {
    this.startProjectService = startProjectService;
    this.projectInformationService = projectInformationService;
    this.projectLocationService = projectLocationService;
    this.projectSetupService = projectSetupService;
    this.projectDetailsRepository = projectDetailsRepository;
  }

  public void createProjects(DevelopmentProjectCreatorForm form, AuthenticatedUserAccount user, PortalOrganisationGroup organisationGroup) {
    IntStream.range(0, form.getNumberOfProjects()).forEach(i -> {
      var index = i + 1;
      LOGGER.info(String.format("Starting create project %d of %d", index, form.getNumberOfProjects()));
      buildProject(user, organisationGroup, index, form.getProjectStatus());
      LOGGER.info(String.format("Finished creating project %d of %d", index, form.getNumberOfProjects()));
    });
  }

  public ProjectDetail buildProject(AuthenticatedUserAccount user,
                                    PortalOrganisationGroup organisationGroup,
                                    int projectIndex,
                                    ProjectStatus status
  ) {
    var detail = createProject(user, organisationGroup, status);
    addProjectInformation(detail, projectIndex);
    addProjectLocation(detail);
    addProjectSetup(detail);
    return detail;
  }

  private ProjectDetail createProject(AuthenticatedUserAccount user, PortalOrganisationGroup organisationGroup, ProjectStatus status) {
    var projectDetail = startProjectService.startProject(user, organisationGroup);
    projectDetail.setStatus(status);

    if (!status.equals(ProjectStatus.DRAFT)) {
      projectDetail.setSubmittedByWua(user.getWuaId());
      projectDetail.setSubmittedInstant(Instant.now());
    }

    return projectDetailsRepository.save(projectDetail);
  }

  private void addProjectInformation(ProjectDetail detail, int projectIndex) {
    projectInformationService.createOrUpdate(detail, getProjectInformationForm(projectIndex));
  }

  private void addProjectLocation(ProjectDetail detail) {
    var form = getCompletedLocationForm();
    var location = projectLocationService.createOrUpdate(detail, form);
    projectLocationService.createOrUpdateBlocks(form.getLicenceBlocks(), location);
  }

  private void addProjectSetup(ProjectDetail detail) {
    projectSetupService.createOrUpdateProjectTaskListSetup(detail, getProjectSetupForm());
  }

  public static ProjectInformationForm getProjectInformationForm(int projectIndex) {
    var form = new ProjectInformationForm();
    form.setProjectSummary(PROJECT_SUMMARY);
    form.setProjectTitle(String.format("%s %d", PROJECT_TITLE, projectIndex));
    form.setFieldStage(FIELD_STAGE);


    form.setContactDetail(createContactDetailForm());

    return form;
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

  public static ProjectLocationForm getCompletedLocationForm() {
    var form = new ProjectLocationForm(FIELD_ID.toString());
    form.setFieldType(FIELD_TYPE);
    form.setMaximumWaterDepth(WATER_DEPTH);
    form.setApprovedFieldDevelopmentPlan(APPROVED_FDP_PLAN);
    form.setApprovedFdpDate(new ThreeFieldDateInput(APPROVED_FDP_DATE));
    form.setApprovedDecomProgram(APPROVED_DECOM_PROGRAM);
    form.setApprovedDecomProgramDate(new ThreeFieldDateInput(null, null, null));
    form.setLicenceBlocks(LICENCE_BLOCKS);
    return form;
  }

  public static ProjectSetupForm getProjectSetupForm() {
    var form = new ProjectSetupForm();
    setCommonProjectSetupFields(form);
    return form;
  }

  private static void setCommonProjectSetupFields(ProjectSetupForm form) {
    form.setUpcomingTendersIncluded(TaskListSectionAnswer.UPCOMING_TENDERS_NO);
    form.setAwardedContractsIncluded(TaskListSectionAnswer.AWARDED_CONTRACTS_NO);
    form.setCollaborationOpportunitiesIncluded(TaskListSectionAnswer.COLLABORATION_OPPORTUNITIES_NO);
  }
}
