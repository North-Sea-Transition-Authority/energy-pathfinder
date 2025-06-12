package uk.co.ogauthority.pathfinder.service.project.projectinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.EnergyType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStageSubCategory;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationForm;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.projectinformation.ProjectInformationRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectFormSectionServiceTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@ExtendWith(MockitoExtension.class)
class ProjectInformationServiceTest {

  @Mock
  private ProjectInformationRepository projectInformationRepository;

  @Mock
  private ValidationService validationService;

  @Mock
  private ProjectInformationFormValidator projectInformationFormValidator;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @InjectMocks
  private ProjectInformationService projectInformationService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  private ProjectInformation projectInformation;

  @Test
  void createOrUpdate_newDetail() {
    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    when(projectInformationRepository.save(any(ProjectInformation.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

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
  void createOrUpdate_existingDetail() {
    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(ProjectInformationUtil.getProjectInformation_withCompleteDetails(details)));
    when(projectInformationRepository.save(any(ProjectInformation.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

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
  void createOrUpdate_whenDiscoveryFieldStageSubCategory_thenNoHiddenQuestionsSaved() {
    when(projectInformationRepository.save(any(ProjectInformation.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    var form = ProjectInformationUtil.getCompleteForm();
    form.setOilAndGasSubCategory(FieldStageSubCategory.DISCOVERY);

    var notPersistedQuarterYearInput = new QuarterYearInput(Quarter.Q2, "2021");

    // the following should not be persisted
    form.setDevelopmentFirstProductionDate(notPersistedQuarterYearInput);
    form.setCarbonCaptureSubCategory(FieldStageSubCategory.CAPTURE_AND_ONSHORE);

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(ProjectInformationUtil.getProjectInformation_withCompleteDetails(details)));

    projectInformation = projectInformationService.createOrUpdate(details, form);

    assertThat(projectInformation.getFieldStage()).isEqualTo(FieldStage.OIL_AND_GAS);
    assertThat(projectInformation.getFirstProductionDateQuarter()).isNull();
    assertThat(projectInformation.getFirstProductionDateYear()).isNull();
    assertThat(projectInformation.getFieldStageSubCategory()).isEqualTo(FieldStageSubCategory.DISCOVERY);
  }

  @Test
  void createOrUpdate_whenDevelopmentFieldStageSubCategory_thenFirstProductionSaved() {
    when(projectInformationRepository.save(any(ProjectInformation.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    var form = ProjectInformationUtil.getCompleteForm();
    form.setOilAndGasSubCategory(FieldStageSubCategory.DEVELOPMENT);

    var persistedQuarterYearInput = new QuarterYearInput(Quarter.Q1, "2020");
    form.setDevelopmentFirstProductionDate(persistedQuarterYearInput);

    // the following should not be persisted
    form.setCarbonCaptureSubCategory(FieldStageSubCategory.CAPTURE_AND_ONSHORE);

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(ProjectInformationUtil.getProjectInformation_withCompleteDetails(details)));

    projectInformation = projectInformationService.createOrUpdate(details, form);

    assertThat(projectInformation.getFieldStage()).isEqualTo(FieldStage.OIL_AND_GAS);
    assertThat(projectInformation.getFirstProductionDateQuarter()).isEqualTo(persistedQuarterYearInput.getQuarter());
    assertThat(projectInformation.getFirstProductionDateYear()).isEqualTo(Integer.parseInt(persistedQuarterYearInput.getYear()));
    assertThat(projectInformation.getFieldStageSubCategory()).isEqualTo(FieldStageSubCategory.DEVELOPMENT);
  }

  @Test
  void createOrUpdate_whenCarbonCaptureAndStorageFieldCategory_thenHiddenFieldsSaved() {
    when(projectInformationRepository.save(any(ProjectInformation.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.CARBON_CAPTURE_AND_STORAGE);

    form.setCarbonCaptureSubCategory(FieldStageSubCategory.TRANSPORTATION_AND_STORAGE);

    var notPersistedQuarterYearInput = new QuarterYearInput(Quarter.Q2, "2021");

    // the following should not be persisted
    form.setDevelopmentFirstProductionDate(notPersistedQuarterYearInput);

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(ProjectInformationUtil.getProjectInformation_withCompleteDetails(details)));

    projectInformation = projectInformationService.createOrUpdate(details, form);

    assertThat(projectInformation.getFieldStage()).isEqualTo(FieldStage.CARBON_CAPTURE_AND_STORAGE);
    assertThat(projectInformation.getFirstProductionDateQuarter()).isNull();
    assertThat(projectInformation.getFirstProductionDateQuarter()).isNull();
    assertThat(projectInformation.getFieldStageSubCategory()).isEqualTo(FieldStageSubCategory.TRANSPORTATION_AND_STORAGE);
  }

  @Test
  void createOrUpdate_whenHydrogenFieldStage_thenHiddenFieldSaved() {
    when(projectInformationRepository.save(any(ProjectInformation.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.HYDROGEN);

    form.setHydrogenSubCategory(FieldStageSubCategory.OFFSHORE_HYDROGEN);

    var notPersistedQuarterYearInput = new QuarterYearInput(Quarter.Q2, "2021");

    // the following should not be persisted
    form.setDevelopmentFirstProductionDate(notPersistedQuarterYearInput);

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(ProjectInformationUtil.getProjectInformation_withCompleteDetails(details)));

    projectInformation = projectInformationService.createOrUpdate(details, form);

    assertThat(projectInformation.getFieldStage()).isEqualTo(FieldStage.HYDROGEN);
    assertThat(projectInformation.getFirstProductionDateQuarter()).isNull();
    assertThat(projectInformation.getFirstProductionDateQuarter()).isNull();
    assertThat(projectInformation.getFieldStageSubCategory()).isEqualTo(FieldStageSubCategory.OFFSHORE_HYDROGEN);
  }

  @Test
  void createOrUpdate_whenElectrificationFieldStage_thenHiddenFieldSaved() {
    when(projectInformationRepository.save(any(ProjectInformation.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.ELECTRIFICATION);

    form.setElectrificationSubCategory(FieldStageSubCategory.ONSHORE_ELECTRIFICATION);

    var notPersistedQuarterYearInput = new QuarterYearInput(Quarter.Q2, "2021");

    // the following should not be persisted
    form.setDevelopmentFirstProductionDate(notPersistedQuarterYearInput);

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(ProjectInformationUtil.getProjectInformation_withCompleteDetails(details)));

    projectInformation = projectInformationService.createOrUpdate(details, form);

    assertThat(projectInformation.getFieldStage()).isEqualTo(FieldStage.ELECTRIFICATION);
    assertThat(projectInformation.getFirstProductionDateQuarter()).isNull();
    assertThat(projectInformation.getFirstProductionDateQuarter()).isNull();
    assertThat(projectInformation.getFieldStageSubCategory()).isEqualTo(FieldStageSubCategory.ONSHORE_ELECTRIFICATION);
  }

  @Test
  void createOrUpdate_whenWindEnergyFieldStage_thenHiddenFieldSaved() {
    when(projectInformationRepository.save(any(ProjectInformation.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.WIND_ENERGY);

    form.setWindEnergySubCategory(FieldStageSubCategory.FLOATING_OFFSHORE_WIND);

    var notPersistedQuarterYearInput = new QuarterYearInput(Quarter.Q2, "2021");

    // the following should not be persisted
    form.setDevelopmentFirstProductionDate(notPersistedQuarterYearInput);

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(ProjectInformationUtil.getProjectInformation_withCompleteDetails(details)));

    projectInformation = projectInformationService.createOrUpdate(details, form);

    assertThat(projectInformation.getFieldStage()).isEqualTo(FieldStage.WIND_ENERGY);
    assertThat(projectInformation.getFirstProductionDateQuarter()).isNull();
    assertThat(projectInformation.getFirstProductionDateQuarter()).isNull();
    assertThat(projectInformation.getFieldStageSubCategory()).isEqualTo(FieldStageSubCategory.FLOATING_OFFSHORE_WIND);
  }

  @Test
  void createOrUpdate_whenNoFieldStage_thenAllHiddenFieldsEmpty() {
    when(projectInformationRepository.save(any(ProjectInformation.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(null);

    var notPersistedQuarterYearInput = new QuarterYearInput(Quarter.Q2, "2021");

    // the following should not be persisted
    form.setDevelopmentFirstProductionDate(notPersistedQuarterYearInput);
    form.setWindEnergySubCategory(FieldStageSubCategory.FIXED_BOTTOM_OFFSHORE_WIND);

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(ProjectInformationUtil.getProjectInformation_withCompleteDetails(details)));

    projectInformation = projectInformationService.createOrUpdate(details, form);

    assertThat(projectInformation.getFieldStage()).isNull();
    assertThat(projectInformation.getFirstProductionDateQuarter()).isNull();
    assertThat(projectInformation.getFirstProductionDateQuarter()).isNull();
    assertThat(projectInformation.getFieldStageSubCategory()).isNull();
  }

  @Test
  void getProjectInformation_whenExists_thenReturn() {
    var projectDetail = ProjectUtil.getProjectDetails();
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    when(projectInformationRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(projectInformation)
    );

    var result = projectInformationService.getProjectInformation(projectDetail);

    assertThat(result).contains(projectInformation);
  }

  @Test
  void getProjectInformation_whenNotFound_thenReturnEmpty() {
    var projectDetail = ProjectUtil.getProjectDetails();

    when(projectInformationRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.empty()
    );

    var result = projectInformationService.getProjectInformation(projectDetail);

    assertThat(result).isEmpty();
  }

  @Test
  void getProjectInformationOrError_whenExists_thenReturn() {
    var projectDetail = ProjectUtil.getProjectDetails();
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    when(projectInformationRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(projectInformation)
    );

    var result = projectInformationService.getProjectInformationOrError(projectDetail);

    assertThat(result).isEqualTo(projectInformation);
  }

  @Test
  void getProjectInformationOrError_whenNotFound_thenException() {
    var projectDetail = ProjectUtil.getProjectDetails();

    when(projectInformationRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.empty()
    );

    assertThrows(
        PathfinderEntityNotFoundException.class,
        () -> projectInformationService.getProjectInformationOrError(projectDetail)
    );
  }

  @Test
  void getProjectTitle_whenTitleExists_thenReturnTitle() {
      var expectedTitle = "Test Project Title";
      when(projectInformationRepository.findTitleByProjectDetail(details)).thenReturn(expectedTitle);

      var result = projectInformationService.getProjectTitle(details);

      assertThat(result).isEqualTo(expectedTitle);
  }

  @Test
  void getProjectTitle_whenTitleDoesNotExist_thenReturnEmptyString() {
      when(projectInformationRepository.findTitleByProjectDetail(details)).thenReturn(null);

      var result = projectInformationService.getProjectTitle(details);

      assertThat(result).isEmpty();
  }

  @Test
  void getForm_noExistingDetail() {
    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    ProjectInformationForm form = projectInformationService.getForm(details);

    assertThat(form.getFieldStage()).isNull();
    assertThat(form.getProjectTitle()).isNull();
    assertThat(form.getProjectSummary()).isNull();
    assertThat(form.getContactDetail()).isNull();
  }

  @Test
  void getForm_existingDetail() {
    projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
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
  void getForm_whenDiscoveryFieldStageSubCategory_assertExpectedProperties() {

    projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    projectInformation.setFieldStageSubCategory(FieldStageSubCategory.DISCOVERY);

    // The following should not be populated to the forms
    var persistedFirstProductionDate = new QuarterYearInput(Quarter.Q1, "2020");
    projectInformation.setFirstProductionDateQuarter(persistedFirstProductionDate.getQuarter());
    projectInformation.setFirstProductionDateYear(Integer.parseInt(persistedFirstProductionDate.getYear()));

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(projectInformation));

    ProjectInformationForm form = projectInformationService.getForm(details);

    assertThat(form.getFieldStage()).isEqualTo(FieldStage.OIL_AND_GAS);

    assertThat(form.getDevelopmentFirstProductionDate()).isNull();
    assertThat(form.getCarbonCaptureSubCategory()).isNull();
    assertThat(form.getHydrogenSubCategory()).isNull();
    assertThat(form.getElectrificationSubCategory()).isNull();
    assertThat(form.getOilAndGasSubCategory()).isEqualTo(FieldStageSubCategory.DISCOVERY);
    assertThat(form.getWindEnergySubCategory()).isNull();
  }

  @Test
  void getForm_whenDevelopmentFieldStageSubCategory_thenFirstProductionDatePopulated() {

    projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    projectInformation.setFieldStageSubCategory(FieldStageSubCategory.DEVELOPMENT);

    var persistedFirstProductionDate = new QuarterYearInput(Quarter.Q1, "2020");
    projectInformation.setFirstProductionDateQuarter(persistedFirstProductionDate.getQuarter());
    projectInformation.setFirstProductionDateYear(Integer.parseInt(persistedFirstProductionDate.getYear()));

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(projectInformation));

    ProjectInformationForm form = projectInformationService.getForm(details);

    assertThat(form.getFieldStage()).isEqualTo(FieldStage.OIL_AND_GAS);
    assertThat(form.getDevelopmentFirstProductionDate()).isEqualTo(persistedFirstProductionDate);

    assertThat(form.getCarbonCaptureSubCategory()).isNull();
    assertThat(form.getHydrogenSubCategory()).isNull();
    assertThat(form.getElectrificationSubCategory()).isNull();
    assertThat(form.getOilAndGasSubCategory()).isEqualTo(FieldStageSubCategory.DEVELOPMENT);
    assertThat(form.getWindEnergySubCategory()).isNull();
  }

  @Test
  void getForm_whenFieldStageWithSubCategory_thenHiddenFieldsPopulated() {

    projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    projectInformation.setFieldStage(FieldStage.WIND_ENERGY);

    projectInformation.setFieldStageSubCategory(FieldStageSubCategory.FLOATING_OFFSHORE_WIND);

    // The following should not be populated to the forms
    var invalidPersistedFirstProductionDate = new QuarterYearInput(Quarter.Q2, "2021");
    projectInformation.setFirstProductionDateQuarter(invalidPersistedFirstProductionDate.getQuarter());
    projectInformation.setFirstProductionDateYear(Integer.parseInt(invalidPersistedFirstProductionDate.getYear()));

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(projectInformation));

    ProjectInformationForm form = projectInformationService.getForm(details);

    assertThat(form.getFieldStage()).isEqualTo(FieldStage.WIND_ENERGY);
    assertThat(form.getWindEnergySubCategory()).isEqualTo(FieldStageSubCategory.FLOATING_OFFSHORE_WIND);

    assertThat(form.getDevelopmentFirstProductionDate()).isNull();
    assertThat(form.getCarbonCaptureSubCategory()).isNull();
    assertThat(form.getHydrogenSubCategory()).isNull();
    assertThat(form.getElectrificationSubCategory()).isNull();
  }

  @Test
  void getForm_whenNoFieldStage_thenNoHiddenFieldsPopulated() {

    projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    projectInformation.setFieldStage(null);

    // The following should not be populated to the forms
    projectInformation.setFieldStageSubCategory(FieldStageSubCategory.FLOATING_OFFSHORE_WIND);

    var invalidPersistedFirstProductionDate = new QuarterYearInput(Quarter.Q2, "2021");
    projectInformation.setFirstProductionDateQuarter(invalidPersistedFirstProductionDate.getQuarter());
    projectInformation.setFirstProductionDateYear(Integer.parseInt(invalidPersistedFirstProductionDate.getYear()));

    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(projectInformation));

    ProjectInformationForm form = projectInformationService.getForm(details);

    assertThat(form.getFieldStage()).isNull();
    assertThat(form.getDevelopmentFirstProductionDate()).isNull();
    assertThat(form.getCarbonCaptureSubCategory()).isNull();
    assertThat(form.getHydrogenSubCategory()).isNull();
    assertThat(form.getElectrificationSubCategory()).isNull();
    assertThat(form.getWindEnergySubCategory()).isNull();
  }

  @Test
  void getForm_whenCarbonCaptureAndStorageFieldStage_thenCorrectSubCategoryPopulated() {
      // Setup
      projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
      projectInformation.setFieldStage(FieldStage.CARBON_CAPTURE_AND_STORAGE);
      projectInformation.setFieldStageSubCategory(FieldStageSubCategory.CAPTURE_AND_ONSHORE);

      when(projectInformationRepository.findByProjectDetail(details))
          .thenReturn(Optional.of(projectInformation));

      // Execute
      ProjectInformationForm form = projectInformationService.getForm(details);

      // Verify
      assertThat(form.getFieldStage()).isEqualTo(FieldStage.CARBON_CAPTURE_AND_STORAGE);
      assertThat(form.getCarbonCaptureSubCategory()).isEqualTo(FieldStageSubCategory.CAPTURE_AND_ONSHORE);
      assertThat(form.getDevelopmentFirstProductionDate()).isNull();
      assertThat(form.getOilAndGasSubCategory()).isNull();
      assertThat(form.getHydrogenSubCategory()).isNull();
      assertThat(form.getElectrificationSubCategory()).isNull();
      assertThat(form.getWindEnergySubCategory()).isNull();
  }

  @Test
  void getForm_whenHydrogenFieldStage_thenCorrectSubCategoryPopulated() {
      // Setup
      projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
      projectInformation.setFieldStage(FieldStage.HYDROGEN);
      projectInformation.setFieldStageSubCategory(FieldStageSubCategory.OFFSHORE_HYDROGEN);

      when(projectInformationRepository.findByProjectDetail(details))
          .thenReturn(Optional.of(projectInformation));

      // Execute
      ProjectInformationForm form = projectInformationService.getForm(details);

      // Verify
      assertThat(form.getFieldStage()).isEqualTo(FieldStage.HYDROGEN);
      assertThat(form.getHydrogenSubCategory()).isEqualTo(FieldStageSubCategory.OFFSHORE_HYDROGEN);
      assertThat(form.getDevelopmentFirstProductionDate()).isNull();
      assertThat(form.getOilAndGasSubCategory()).isNull();
      assertThat(form.getCarbonCaptureSubCategory()).isNull();
      assertThat(form.getElectrificationSubCategory()).isNull();
      assertThat(form.getWindEnergySubCategory()).isNull();
  }

  @Test
  void getForm_whenElectrificationFieldStage_thenCorrectSubCategoryPopulated() {
      // Setup
      projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
      projectInformation.setFieldStage(FieldStage.ELECTRIFICATION);
      projectInformation.setFieldStageSubCategory(FieldStageSubCategory.OFFSHORE_ELECTRIFICATION);

      when(projectInformationRepository.findByProjectDetail(details))
          .thenReturn(Optional.of(projectInformation));

      // Execute
      ProjectInformationForm form = projectInformationService.getForm(details);

      // Verify
      assertThat(form.getFieldStage()).isEqualTo(FieldStage.ELECTRIFICATION);
      assertThat(form.getElectrificationSubCategory()).isEqualTo(FieldStageSubCategory.OFFSHORE_ELECTRIFICATION);
      assertThat(form.getDevelopmentFirstProductionDate()).isNull();
      assertThat(form.getOilAndGasSubCategory()).isNull();
      assertThat(form.getCarbonCaptureSubCategory()).isNull();
      assertThat(form.getHydrogenSubCategory()).isNull();
      assertThat(form.getWindEnergySubCategory()).isNull();
  }

  @Test
  void getForm_whenOilAndGasFieldStageWithFirstProductionDate_thenCorrectValuesPopulated() {
      // Setup
      projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
      projectInformation.setFieldStage(FieldStage.OIL_AND_GAS);
      projectInformation.setFieldStageSubCategory(FieldStageSubCategory.DEVELOPMENT);
      projectInformation.setFirstProductionDateQuarter(Quarter.Q3);
      projectInformation.setFirstProductionDateYear(2023);

      when(projectInformationRepository.findByProjectDetail(details))
          .thenReturn(Optional.of(projectInformation));

      // Execute
      ProjectInformationForm form = projectInformationService.getForm(details);

      // Verify
      assertThat(form.getFieldStage()).isEqualTo(FieldStage.OIL_AND_GAS);
      assertThat(form.getOilAndGasSubCategory()).isEqualTo(FieldStageSubCategory.DEVELOPMENT);
      assertThat(form.getDevelopmentFirstProductionDate()).isNotNull();
      assertThat(form.getDevelopmentFirstProductionDate().getQuarter()).isEqualTo(Quarter.Q3);
      assertThat(form.getDevelopmentFirstProductionDate().getYear()).isEqualTo("2023");
      assertThat(form.getCarbonCaptureSubCategory()).isNull();
      assertThat(form.getHydrogenSubCategory()).isNull();
      assertThat(form.getElectrificationSubCategory()).isNull();
      assertThat(form.getWindEnergySubCategory()).isNull();
  }

  @Test
  void getForm_whenOilAndGasFieldStageWithNonDevelopmentSubCategory_thenNoProductionDate() {
      // Setup
      projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
      projectInformation.setFieldStage(FieldStage.OIL_AND_GAS);
      projectInformation.setFieldStageSubCategory(FieldStageSubCategory.DECOMMISSIONING);
      // Set these values which should be ignored for non-development subcategory
      projectInformation.setFirstProductionDateQuarter(Quarter.Q4);
      projectInformation.setFirstProductionDateYear(2024);

      when(projectInformationRepository.findByProjectDetail(details))
          .thenReturn(Optional.of(projectInformation));

      // Execute
      ProjectInformationForm form = projectInformationService.getForm(details);

      // Verify
      assertThat(form.getFieldStage()).isEqualTo(FieldStage.OIL_AND_GAS);
      assertThat(form.getOilAndGasSubCategory()).isEqualTo(FieldStageSubCategory.DECOMMISSIONING);
      assertThat(form.getDevelopmentFirstProductionDate()).isNull();
      assertThat(form.getCarbonCaptureSubCategory()).isNull();
      assertThat(form.getHydrogenSubCategory()).isNull();
      assertThat(form.getElectrificationSubCategory()).isNull();
      assertThat(form.getWindEnergySubCategory()).isNull();
  }

  @Test
  void validate_partial() {
    var form = new ProjectInformationForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectInformationService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
    verify(projectInformationFormValidator).validate(eq(form), eq(bindingResult), any(ProjectInformationValidationHint.class));
  }

  @Test
  void validate_full() {
    var form = ProjectInformationUtil.getCompleteForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectInformationService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
    verify(projectInformationFormValidator).validate(eq(form), eq(bindingResult), any(ProjectInformationValidationHint.class));
  }

  @Test
  void removeSectionData() {
    projectInformationService.removeSectionData(details);

    verify(projectInformationRepository, times(1)).deleteByProjectDetail(details);
  }

  @Test
  void copySectionData_verifyDuplicationServiceInteraction() {

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
  void getProjectInformationByProjectAndVersion_whenFoundThenReturn() {
    final var project = details.getProject();
    final var version = details.getVersion() - 1;

    final var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);

    when(projectInformationRepository.findByProjectDetail_ProjectAndProjectDetail_Version(project, version))
        .thenReturn(Optional.of(projectInformation));

    var result = projectInformationService.getProjectInformationByProjectAndVersion(project, version);

    assertThat(result).contains(projectInformation);
  }

  @Test
  void getProjectInformationByProjectAndVersion_whenNotFoundThenReturnEmpty() {
    final var project = details.getProject();
    final var version = details.getVersion() - 1;

    when(projectInformationRepository.findByProjectDetail_ProjectAndProjectDetail_Version(project, version))
        .thenReturn(Optional.empty());

    var result = projectInformationService.getProjectInformationByProjectAndVersion(project, version);

    assertThat(result).isEmpty();
  }

  @Test
  void canShowInTaskList_whenInfrastructureProject_thenTrue() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);
    assertThat(projectInformationService.canShowInTaskList(projectDetail, Set.of(UserToProjectRelationship.OPERATOR)))
        .isTrue();
  }

  @Test
  void canShowInTaskList_whenNotInfrastructureProject_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    assertThat(projectInformationService.canShowInTaskList(projectDetail, Set.of(UserToProjectRelationship.OPERATOR)))
        .isFalse();
  }

  @Test
  void canShowInTaskList_whenNullProjectType_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails();
    projectDetail.setProjectType(null);
    assertThat(projectInformationService.canShowInTaskList(projectDetail, Set.of(UserToProjectRelationship.OPERATOR)))
        .isFalse();
  }

  @Test
  void canShowInTaskList_userToProjectRelationshipSmokeTest() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);
    ProjectFormSectionServiceTestUtil.canShowInTaskList_userToProjectRelationshipSmokeTest(
        projectInformationService,
        projectDetail,
        Set.of(UserToProjectRelationship.OPERATOR)
    );
  }

  @Test
  void isTaskValidForProjectDetail_whenInfrastructureProject_thenTrue() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);
    assertThat(projectInformationService.isTaskValidForProjectDetail(projectDetail)).isTrue();
  }

  @Test
  void isTaskValidForProjectDetail_whenNotInfrastructureProject_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    assertThat(projectInformationService.isTaskValidForProjectDetail(projectDetail)).isFalse();
  }

  @Test
  void isDecomRelated_whenDecommissioningFieldStageSubCategory_thenTrue() {
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    projectInformation.setFieldStageSubCategory(FieldStageSubCategory.DECOMMISSIONING);

    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.of(projectInformation));

    assertThat(projectInformationService.isDecomRelated(details)).isTrue();
  }

  @Test
  void isDecomRelated_whenNotDecommissioningFieldStageSubCategory_thenFalse() {
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    projectInformation.setFieldStageSubCategory(FieldStageSubCategory.DISCOVERY);

    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.of(projectInformation));

    assertThat(projectInformationService.isDecomRelated(details)).isFalse();
  }

  @Test
  void isDecomRelated_whenNoProjectInformation_thenFalse() {
    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());

    assertThat(projectInformationService.isDecomRelated(details)).isFalse();
  }

  @Test
  void isEnergyTransitionProject_whenProjectDetailAndEnergyTransitionFieldStage_thenTrue() {
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    projectInformation.setFieldStage(FieldStage.HYDROGEN);

    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.of(projectInformation));

    assertThat(projectInformationService.isEnergyTransitionProject(details)).isTrue();
  }

  @Test
  void isEnergyTransitionProject_whenProjectDetailAndNotEnergyTransitionFieldStage_thenFalse() {
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    projectInformation.setFieldStageSubCategory(FieldStageSubCategory.DISCOVERY);

    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.of(projectInformation));

    assertThat(projectInformationService.isEnergyTransitionProject(details)).isFalse();
  }

  @Test
  void isEnergyTransitionProject_whenProjectDetailAndNoProjectInformation_thenFalse() {
    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());

    assertThat(projectInformationService.isEnergyTransitionProject(details)).isFalse();
  }

  @ParameterizedTest
  @MethodSource("energyTransitionProjects_arguments")
  void isEnergyTransitionProject_whenProjectInformationAndEnergyTransitionFieldStage_thenTrue(FieldStage fieldStage) {
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    projectInformation.setFieldStage(fieldStage);

    assertThat(projectInformationService.isEnergyTransitionProject(projectInformation)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("nonEnergyTransitionProjects_arguments")
  void isEnergyTransitionProject_whenProjectInformationAndNotEnergyTransitionFieldStage_thenFalse(FieldStage fieldStage) {
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    projectInformation.setFieldStage(fieldStage);

    assertThat(projectInformationService.isEnergyTransitionProject(projectInformation)).isFalse();
  }

  @Test
  void alwaysCopySectionData_verifyFalse() {
    assertThat(projectInformationService.alwaysCopySectionData(details)).isFalse();
  }

  @Test
  void allowSectionDataCleanUp_verifyIsTrue() {
    final var allowSectionDateCleanUp = projectInformationService.allowSectionDataCleanUp(details);
    assertThat(allowSectionDateCleanUp).isTrue();
  }

  @ParameterizedTest
  @MethodSource("oilAndGasProjects_arguments")
  void isOilAndGasProject_whenOilAndGasFieldStage_thenReturnTrue(FieldStage fieldStage) {
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    projectInformation.setFieldStage(fieldStage);

    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.of(projectInformation));

    var isOilAndGasProject = projectInformationService.isOilAndGasProject(details);

    assertThat(isOilAndGasProject).isTrue();
  }

  @ParameterizedTest
  @MethodSource("nonOilAndGasProjects_arguments")
  void isOilAndGasProject_whenNotOilAndGasFieldStage_thenReturnFalse(FieldStage fieldStage) {
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    projectInformation.setFieldStage(fieldStage);

    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.of(projectInformation));

    var isOilAndGasProject = projectInformationService.isOilAndGasProject(details);

    assertThat(isOilAndGasProject).isFalse();
  }

  @Test
  void getFieldStage_whenNoProjectInformationEntityFound_thenReturnEmptyOptional() {
    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());

    var resultingFieldStage = projectInformationService.getFieldStage(details);

    assertThat(resultingFieldStage).isEmpty();
  }

  @Test
  void getFieldStage_whenProjectInformationEntityFoundAndNoFieldStageProvided_thenReturnEmptyOptional() {
    var expectedProjectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    expectedProjectInformation.setFieldStage(null);

    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.of(expectedProjectInformation));

    var resultingFieldStage = projectInformationService.getFieldStage(details);

    assertThat(resultingFieldStage).isEmpty();
  }

  @Test
  void getFieldStage_whenFieldStageProvided_thenReturnPopulatedOptional() {
    var expectedFieldStage = FieldStage.HYDROGEN;

    var expectedProjectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    expectedProjectInformation.setFieldStage(expectedFieldStage);

    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.of(expectedProjectInformation));

    var resultingFieldStage = projectInformationService.getFieldStage(details);

    assertThat(resultingFieldStage).contains(expectedFieldStage);
  }

  @Test
  void getFieldStageSubCategory_whenNoProjectInformationEntityFound_thenReturnEmptyOptional() {
    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());

    var resultingFieldStageSubCategory = projectInformationService.getFieldStageSubCategory(details);

    assertThat(resultingFieldStageSubCategory).isEmpty();
  }

  @Test
  void getFieldStageSubCategory_whenProjectInformationEntityFoundAndNoFieldStageSubCategoryProvided_thenReturnEmptyOptional() {
    var expectedProjectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    expectedProjectInformation.setFieldStageSubCategory(null);

    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.of(expectedProjectInformation));

    var resultingFieldStageSubCategory = projectInformationService.getFieldStageSubCategory(details);

    assertThat(resultingFieldStageSubCategory).isEmpty();
  }

  @Test
  void getFieldStageSubCategory_whenFieldStageSubCategoryProvided_thenReturnPopulatedOptional() {
    var expectedFieldStageSubCategory = FieldStageSubCategory.DECOMMISSIONING;

    var expectedProjectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    expectedProjectInformation.setFieldStageSubCategory(expectedFieldStageSubCategory);

    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.of(expectedProjectInformation));

    var resultingFieldStageSubCategory = projectInformationService.getFieldStageSubCategory(details);

    assertThat(resultingFieldStageSubCategory).contains(expectedFieldStageSubCategory);
  }

  @Test
  void getSupportedProjectTypes_verifyInfrastructure() {
    assertThat(projectInformationService.getSupportedProjectTypes()).containsExactly(ProjectType.INFRASTRUCTURE);
  }

  @Test
  void isComplete_whenFormIsValid_thenReturnTrue() {
      // Setup - create a spy of the service
      ProjectInformationService spyService = spy(projectInformationService);

      var form = ProjectInformationUtil.getCompleteForm();

      // Mock form retrieval using spy
      doReturn(form).when(spyService).getForm(details);

      // Mock validation to return a binding result with no errors
      doAnswer(invocation -> {
          return invocation.getArgument(1); // Return binding result with no errors
      }).when(validationService).validate(
          any(ProjectInformationForm.class),
          any(BindingResult.class),
          eq(ValidationType.FULL)
      );

      // Execute method on the spy
      boolean result = spyService.isComplete(details);

      // Verify
      assertThat(result).isTrue();
  }

  @Test
  void isComplete_whenFormHasErrors_thenReturnFalse() {
      // Setup - create a spy of the service
      ProjectInformationService spyService = spy(projectInformationService);

      var form = new ProjectInformationForm(); // Incomplete form

      // Mock form retrieval using spy
      doReturn(form).when(spyService).getForm(details);

      // Mock validation to add errors
      doAnswer(invocation -> {
          BindingResult bindingResult = invocation.getArgument(1);
          bindingResult.rejectValue("projectTitle", "required", "Project title is required");
          return bindingResult;
      }).when(validationService).validate(
          any(ProjectInformationForm.class),
          any(BindingResult.class),
          eq(ValidationType.FULL)
      );

      // Execute method on the spy
      boolean result = spyService.isComplete(details);

      // Verify
      assertThat(result).isFalse();
  }

  private static Stream<Arguments> energyTransitionProjects_arguments() {
    return FieldStage.getEnergyTransitionProjectFieldStages().stream()
        .map(Arguments::of);
  }

  private static Stream<Arguments> nonEnergyTransitionProjects_arguments() {
    return Arrays.stream(FieldStage.values())
        .filter(fs -> !fs.getEnergyType().contains(EnergyType.TRANSITION))
        .map(Arguments::of);
  }

  private static Stream<Arguments> oilAndGasProjects_arguments() {
    return Arrays.stream(FieldStage.values())
        .filter(fs -> fs.getEnergyType().contains(EnergyType.OIL_AND_GAS))
        .map(Arguments::of);
  }

  private static Stream<Arguments> nonOilAndGasProjects_arguments() {
    return Arrays.stream(FieldStage.values())
        .filter(fs -> !fs.getEnergyType().contains(EnergyType.OIL_AND_GAS))
        .map(Arguments::of);
  }
}
