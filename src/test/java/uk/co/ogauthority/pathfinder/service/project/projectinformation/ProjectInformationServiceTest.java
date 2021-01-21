package uk.co.ogauthority.pathfinder.service.project.projectinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationForm;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.projectinformation.ProjectInformationRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectInformationServiceTest {

  @Mock
  private ProjectInformationRepository projectInformationRepository;

  @Mock
  private ValidationService validationService;

  @Mock
  private ProjectInformationFormValidator projectInformationFormValidator;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private ProjectInformationService projectInformationService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  private ProjectInformation projectInformation;

  @Before
  public void setUp() {
    projectInformationService = new ProjectInformationService(
        projectInformationRepository,
        validationService,
        projectInformationFormValidator,
        entityDuplicationService
    );

    when(projectInformationRepository.save(any(ProjectInformation.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createOrUpdate_newDetail() {
    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    projectInformation = projectInformationService.createOrUpdate(details, ProjectInformationUtil.getCompleteForm());

    assertThat(projectInformation.getProjectDetail()).isEqualTo(details);
    assertThat(projectInformation.getFieldStage()).isEqualTo(ProjectInformationUtil.FIELD_STAGE);
    assertThat(projectInformation.getProjectTitle()).isEqualTo(ProjectInformationUtil.PROJECT_TITLE);
    assertThat(projectInformation.getProjectSummary()).isEqualTo(ProjectInformationUtil.PROJECT_SUMMARY);
    assertThat(projectInformation.getContactName()).isEqualTo(ProjectInformationUtil.CONTACT_NAME);
    assertThat(projectInformation.getPhoneNumber()).isEqualTo(ProjectInformationUtil.PHONE_NUMBER);
    assertThat(projectInformation.getJobTitle()).isEqualTo(ProjectInformationUtil.JOB_TITLE);
    assertThat(projectInformation.getEmailAddress()).isEqualTo(ProjectInformationUtil.EMAIL);
  }

  @Test
  public void createOrUpdate_existingDetail() {
    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(ProjectInformationUtil.getProjectInformation_withCompleteDetails(details)));
    var form = ProjectInformationUtil.getCompleteForm();
    form.getContactDetail().setName("New name");
    projectInformation = projectInformationService.createOrUpdate(details, form);

    assertThat(projectInformation.getProjectDetail()).isEqualTo(details);
    assertThat(projectInformation.getFieldStage()).isEqualTo(ProjectInformationUtil.FIELD_STAGE);
    assertThat(projectInformation.getProjectTitle()).isEqualTo(ProjectInformationUtil.PROJECT_TITLE);
    assertThat(projectInformation.getProjectSummary()).isEqualTo(ProjectInformationUtil.PROJECT_SUMMARY);
    assertThat(projectInformation.getContactName()).isEqualTo("New name");
    assertThat(projectInformation.getPhoneNumber()).isEqualTo(ProjectInformationUtil.PHONE_NUMBER);
    assertThat(projectInformation.getJobTitle()).isEqualTo(ProjectInformationUtil.JOB_TITLE);
    assertThat(projectInformation.getEmailAddress()).isEqualTo(ProjectInformationUtil.EMAIL);
  }

  @Test
  public void createOrUpdate_whenDiscoveryFieldStage_thenFirstProductionSaved() {

    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.DISCOVERY);

    var persistedQuarterYearInput = new QuarterYearInput(Quarter.Q1, "2020");
    var notPersistedQuarterYearInput = new QuarterYearInput(Quarter.Q2, "2021");
    form.setDiscoveryFirstProductionDate(persistedQuarterYearInput);

    // the following should not be persisted
    form.setDevelopmentFirstProductionDate(notPersistedQuarterYearInput);
    form.setDecomWorkStartDate(notPersistedQuarterYearInput);
    form.setProductionCessationDate(new ThreeFieldDateInput(LocalDate.now()));

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(ProjectInformationUtil.getProjectInformation_withCompleteDetails(details)));

    projectInformation = projectInformationService.createOrUpdate(details, form);

    assertThat(projectInformation.getFieldStage()).isEqualTo(FieldStage.DISCOVERY);
    assertThat(projectInformation.getFirstProductionDateQuarter()).isEqualTo(persistedQuarterYearInput.getQuarter());
    assertThat(projectInformation.getFirstProductionDateYear()).isEqualTo(Integer.parseInt(persistedQuarterYearInput.getYear()));
    assertThat(projectInformation.getDecomWorkStartDateQuarter()).isNull();
    assertThat(projectInformation.getDecomWorkStartDateYear()).isNull();
    assertThat(projectInformation.getProductionCessationDate()).isNull();
  }

  @Test
  public void createOrUpdate_whenDevelopmentFieldStage_thenFirstProductionSaved() {

    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.DEVELOPMENT);

    var persistedQuarterYearInput = new QuarterYearInput(Quarter.Q1, "2020");
    var notPersistedQuarterYearInput = new QuarterYearInput(Quarter.Q2, "2021");
    form.setDevelopmentFirstProductionDate(persistedQuarterYearInput);

    // the following should not be persisted
    form.setDiscoveryFirstProductionDate(notPersistedQuarterYearInput);
    form.setDecomWorkStartDate(notPersistedQuarterYearInput);
    form.setProductionCessationDate(new ThreeFieldDateInput(LocalDate.now()));

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(ProjectInformationUtil.getProjectInformation_withCompleteDetails(details)));

    projectInformation = projectInformationService.createOrUpdate(details, form);

    assertThat(projectInformation.getFieldStage()).isEqualTo(FieldStage.DEVELOPMENT);
    assertThat(projectInformation.getFirstProductionDateQuarter()).isEqualTo(persistedQuarterYearInput.getQuarter());
    assertThat(projectInformation.getFirstProductionDateYear()).isEqualTo(Integer.parseInt(persistedQuarterYearInput.getYear()));
    assertThat(projectInformation.getDecomWorkStartDateQuarter()).isNull();
    assertThat(projectInformation.getDecomWorkStartDateYear()).isNull();
    assertThat(projectInformation.getProductionCessationDate()).isNull();
  }

  @Test
  public void createOrUpdate_whenDecommissioningFieldStage_thenHiddenFieldsSaved() {

    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.DECOMMISSIONING);

    var persistedQuarterYearInput = new QuarterYearInput(Quarter.Q1, "2020");
    var notPersistedQuarterYearInput = new QuarterYearInput(Quarter.Q2, "2021");
    var persistedThreeFieldDateInput = new ThreeFieldDateInput(LocalDate.now());

    form.setDecomWorkStartDate(persistedQuarterYearInput);
    form.setProductionCessationDate(persistedThreeFieldDateInput);

    // the following should not be persisted
    form.setDiscoveryFirstProductionDate(notPersistedQuarterYearInput);
    form.setDevelopmentFirstProductionDate(notPersistedQuarterYearInput);

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(ProjectInformationUtil.getProjectInformation_withCompleteDetails(details)));

    projectInformation = projectInformationService.createOrUpdate(details, form);

    assertThat(projectInformation.getFieldStage()).isEqualTo(FieldStage.DECOMMISSIONING);
    assertThat(projectInformation.getDecomWorkStartDateQuarter()).isEqualTo(persistedQuarterYearInput.getQuarter());
    assertThat(projectInformation.getDecomWorkStartDateYear()).isEqualTo(Integer.parseInt(persistedQuarterYearInput.getYear()));
    assertThat(projectInformation.getFirstProductionDateQuarter()).isNull();
    assertThat(projectInformation.getFirstProductionDateQuarter()).isNull();
    assertThat(projectInformation.getProductionCessationDate()).isEqualTo(persistedThreeFieldDateInput.createDateOrNull());
  }

  @Test
  public void createOrUpdate_whenNoFieldStage_thenAllHiddenFieldsEmpty() {

    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(null);

    var notPersistedQuarterYearInput = new QuarterYearInput(Quarter.Q2, "2021");
    var notPersistedThreeFieldDateInput = new ThreeFieldDateInput(LocalDate.now());

    // the following should not be persisted
    form.setDiscoveryFirstProductionDate(notPersistedQuarterYearInput);
    form.setDevelopmentFirstProductionDate(notPersistedQuarterYearInput);
    form.setDecomWorkStartDate(notPersistedQuarterYearInput);
    form.setProductionCessationDate(notPersistedThreeFieldDateInput);

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(ProjectInformationUtil.getProjectInformation_withCompleteDetails(details)));

    projectInformation = projectInformationService.createOrUpdate(details, form);

    assertThat(projectInformation.getFieldStage()).isNull();
    assertThat(projectInformation.getDecomWorkStartDateQuarter()).isNull();
    assertThat(projectInformation.getDecomWorkStartDateYear()).isNull();
    assertThat(projectInformation.getFirstProductionDateQuarter()).isNull();
    assertThat(projectInformation.getFirstProductionDateQuarter()).isNull();
    assertThat(projectInformation.getProductionCessationDate()).isNull();
  }

  @Test
  public void getProjectInformation_whenExists_thenReturn() {
    var projectDetail = ProjectUtil.getProjectDetails();
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    when(projectInformationRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(projectInformation)
    );

    var result = projectInformationService.getProjectInformation(projectDetail);

    assertThat(result).contains(projectInformation);
  }

  @Test
  public void getProjectInformation_whenNotFound_thenReturnEmpty() {
    var projectDetail = ProjectUtil.getProjectDetails();

    when(projectInformationRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.empty()
    );

    var result = projectInformationService.getProjectInformation(projectDetail);

    assertThat(result).isEmpty();
  }

  @Test
  public void getProjectInformationOrError_whenExists_thenReturn() {
    var projectDetail = ProjectUtil.getProjectDetails();
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    when(projectInformationRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(projectInformation)
    );

    var result = projectInformationService.getProjectInformationOrError(projectDetail);

    assertThat(result).isEqualTo(projectInformation);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getProjectInformationOrError_whenNotFound_thenException() {
    var projectDetail = ProjectUtil.getProjectDetails();

    when(projectInformationRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.empty()
    );

    projectInformationService.getProjectInformationOrError(projectDetail);
  }

  @Test
  public void getForm_noExistingDetail() {
    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    ProjectInformationForm form = projectInformationService.getForm(details);

    assertThat(form.getFieldStage()).isNull();
    assertThat(form.getProjectTitle()).isNull();
    assertThat(form.getProjectSummary()).isNull();
    assertThat(form.getContactDetail()).isNull();
  }

  @Test
  public void getForm_existingDetail() {
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(projectInformation));
    ProjectInformationForm form = projectInformationService.getForm(details);

    assertThat(form.getFieldStage()).isEqualTo(projectInformation.getFieldStage());
    assertThat(form.getProjectTitle()).isEqualTo(projectInformation.getProjectTitle());
    assertThat(form.getProjectSummary()).isEqualTo(projectInformation.getProjectSummary());

    var contactDetailForm = form.getContactDetail();
    assertThat(contactDetailForm.getName()).isEqualTo(projectInformation.getContactName());
    assertThat(contactDetailForm.getPhoneNumber()).isEqualTo(projectInformation.getPhoneNumber());
    assertThat(contactDetailForm.getJobTitle()).isEqualTo(projectInformation.getJobTitle());
    assertThat(contactDetailForm.getEmailAddress()).isEqualTo(projectInformation.getEmailAddress());
  }

  @Test
  public void getForm_whenDiscoveryFieldStage_thenFirstProductionDatePopulated() {

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    projectInformation.setFieldStage(FieldStage.DISCOVERY);

    var persistedFirstProductionDate = new QuarterYearInput(Quarter.Q1, "2020");
    projectInformation.setFirstProductionDateQuarter(persistedFirstProductionDate.getQuarter());
    projectInformation.setFirstProductionDateYear(Integer.parseInt(persistedFirstProductionDate.getYear()));

    // The following should not be populated to the forms
    var invalidPersistedDecomWorkStartDate = new QuarterYearInput(Quarter.Q2, "2021");
    projectInformation.setDecomWorkStartDateQuarter(invalidPersistedDecomWorkStartDate.getQuarter());
    projectInformation.setDecomWorkStartDateYear(Integer.parseInt(invalidPersistedDecomWorkStartDate.getYear()));
    projectInformation.setProductionCessationDate(LocalDate.now());

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(projectInformation));

    ProjectInformationForm form = projectInformationService.getForm(details);

    assertThat(form.getFieldStage()).isEqualTo(FieldStage.DISCOVERY);
    assertThat(form.getDiscoveryFirstProductionDate()).isEqualTo(persistedFirstProductionDate);

    assertThat(form.getDevelopmentFirstProductionDate()).isNull();
    assertThat(form.getDecomWorkStartDate()).isNull();
    assertThat(form.getProductionCessationDate()).isNull();
  }

  @Test
  public void getForm_whenDevelopmentFieldStage_thenFirstProductionDatePopulated() {

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    projectInformation.setFieldStage(FieldStage.DEVELOPMENT);

    var persistedFirstProductionDate = new QuarterYearInput(Quarter.Q1, "2020");
    projectInformation.setFirstProductionDateQuarter(persistedFirstProductionDate.getQuarter());
    projectInformation.setFirstProductionDateYear(Integer.parseInt(persistedFirstProductionDate.getYear()));

    // The following should not be populated to the forms
    var invalidPersistedDecomWorkStartDate = new QuarterYearInput(Quarter.Q2, "2021");
    projectInformation.setDecomWorkStartDateQuarter(invalidPersistedDecomWorkStartDate.getQuarter());
    projectInformation.setDecomWorkStartDateYear(Integer.parseInt(invalidPersistedDecomWorkStartDate.getYear()));
    projectInformation.setProductionCessationDate(LocalDate.now());

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(projectInformation));

    ProjectInformationForm form = projectInformationService.getForm(details);

    assertThat(form.getFieldStage()).isEqualTo(FieldStage.DEVELOPMENT);
    assertThat(form.getDevelopmentFirstProductionDate()).isEqualTo(persistedFirstProductionDate);

    assertThat(form.getDiscoveryFirstProductionDate()).isNull();
    assertThat(form.getDecomWorkStartDate()).isNull();
    assertThat(form.getProductionCessationDate()).isNull();
  }

  @Test
  public void getForm_whenDecommissioningFieldStage_thenHiddenFieldsPopulated() {

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    projectInformation.setFieldStage(FieldStage.DECOMMISSIONING);

    var persistedDecomWorkStartDate = new QuarterYearInput(Quarter.Q1, "2020");
    projectInformation.setDecomWorkStartDateQuarter(persistedDecomWorkStartDate.getQuarter());
    projectInformation.setDecomWorkStartDateYear(Integer.parseInt(persistedDecomWorkStartDate.getYear()));

    var persistedProductionCessationDate = LocalDate.now();
    projectInformation.setProductionCessationDate(persistedProductionCessationDate);

    // The following should not be populated to the forms
    var invalidPersistedFirstProductionDate = new QuarterYearInput(Quarter.Q2, "2021");
    projectInformation.setFirstProductionDateQuarter(invalidPersistedFirstProductionDate.getQuarter());
    projectInformation.setFirstProductionDateYear(Integer.parseInt(invalidPersistedFirstProductionDate.getYear()));

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(projectInformation));

    ProjectInformationForm form = projectInformationService.getForm(details);

    assertThat(form.getFieldStage()).isEqualTo(FieldStage.DECOMMISSIONING);
    assertThat(form.getDecomWorkStartDate()).isEqualTo(persistedDecomWorkStartDate);
    assertThat(form.getProductionCessationDate()).isEqualTo(new ThreeFieldDateInput(persistedProductionCessationDate));

    assertThat(form.getDevelopmentFirstProductionDate()).isNull();
    assertThat(form.getDiscoveryFirstProductionDate()).isNull();
  }

  @Test
  public void getForm_whenNoFieldStage_thenNoHiddenFieldsPopulated() {

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    projectInformation.setFieldStage(null);

    // The following should not be populated to the forms
    var invalidPersistedDecomWorkStartDate = new QuarterYearInput(Quarter.Q1, "2020");
    projectInformation.setDecomWorkStartDateQuarter(invalidPersistedDecomWorkStartDate.getQuarter());
    projectInformation.setDecomWorkStartDateYear(Integer.parseInt(invalidPersistedDecomWorkStartDate.getYear()));

    var invalidPersistedProductionCessationDate = LocalDate.now();
    projectInformation.setProductionCessationDate(invalidPersistedProductionCessationDate);

    var invalidPersistedFirstProductionDate = new QuarterYearInput(Quarter.Q2, "2021");
    projectInformation.setFirstProductionDateQuarter(invalidPersistedFirstProductionDate.getQuarter());
    projectInformation.setFirstProductionDateYear(Integer.parseInt(invalidPersistedFirstProductionDate.getYear()));

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(projectInformation));

    ProjectInformationForm form = projectInformationService.getForm(details);

    assertThat(form.getFieldStage()).isNull();
    assertThat(form.getDecomWorkStartDate()).isNull();
    assertThat(form.getProductionCessationDate()).isNull();
    assertThat(form.getDevelopmentFirstProductionDate()).isNull();
    assertThat(form.getDiscoveryFirstProductionDate()).isNull();
  }

  @Test
  public void validate_partial() {
    var form = new ProjectInformationForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectInformationService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void validate_full() {
    var form = ProjectInformationUtil.getCompleteForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectInformationService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void removeSectionData() {
    projectInformationService.removeSectionData(details);

    verify(projectInformationRepository, times(1)).deleteByProjectDetail(details);
  }

  @Test
  public void copySectionData_verifyDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    final var fromProjectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(fromProjectDetail);
    when(projectInformationRepository.findByProjectDetail(fromProjectDetail))
        .thenReturn(Optional.of(fromProjectInformation));

    projectInformationService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntityAndSetNewParent(
        fromProjectInformation,
        toProjectDetail,
        ProjectInformation.class
    );
  }

  @Test
  public void getProjectInformationByProjectAndVersion_whenFoundThenReturn() {

    final var project = details.getProject();
    final var version = details.getVersion() - 1;

    final var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);

    when(projectInformationRepository.findByProjectDetail_ProjectAndProjectDetail_Version(project, version))
        .thenReturn(Optional.of(projectInformation));

    var result = projectInformationService.getProjectInformationByProjectAndVersion(project, version);

    assertThat(result).contains(projectInformation);
  }

  @Test
  public void getProjectInformationByProjectAndVersion_whenNotFoundThenReturnEmpty() {

    final var project = details.getProject();
    final var version = details.getVersion() - 1;

    when(projectInformationRepository.findByProjectDetail_ProjectAndProjectDetail_Version(project, version))
        .thenReturn(Optional.empty());

    var result = projectInformationService.getProjectInformationByProjectAndVersion(project, version);

    assertThat(result).isEmpty();
  }
}
