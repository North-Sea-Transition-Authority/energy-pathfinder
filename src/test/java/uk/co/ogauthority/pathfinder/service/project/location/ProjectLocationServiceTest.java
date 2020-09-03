package uk.co.ogauthority.pathfinder.service.project.location;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFieldService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectLocationServiceTest {


  @Mock
  private ProjectLocationRepository projectLocationRepository;

  @Mock
  private DevUkFieldService fieldService;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private ValidationService validationService;

  @Mock
  ProjectLocationFormValidator projectLocationFormValidator;

  private ProjectLocationService projectLocationService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  private ProjectLocation projectLocation;

  @Before
  public void setUp() {
    projectLocationService = new ProjectLocationService(
        projectLocationRepository,
        fieldService,
        searchSelectorService,
        validationService,
        projectLocationFormValidator);

    when(projectLocationRepository.save(any(ProjectLocation.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createOrUpdate_newLocation_manualEntry() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    projectLocation = projectLocationService.createOrUpdate(details, ProjectLocationUtil.getCompletedForm_manualField());
    assertThat(projectLocation.getProjectDetail()).isEqualTo(details);
    assertThat(projectLocation.getManualFieldName()).isEqualTo(ProjectLocationUtil.MANUAL_FIELD_NAME);
    checkCommonFieldsMatch(projectLocation);
  }

  @Test
  public void createOrUpdate_newLocation() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    when(fieldService.findById(ProjectLocationUtil.FIELD_ID)).thenReturn(ProjectLocationUtil.FIELD);
    projectLocation = projectLocationService.createOrUpdate(details, ProjectLocationUtil.getCompletedForm_withField());
    assertThat(projectLocation.getProjectDetail()).isEqualTo(details);
    assertThat(projectLocation.getField()).isEqualTo(ProjectLocationUtil.FIELD);
    checkCommonFieldsMatch(projectLocation);
  }

  @Test
  public void createOrUpdate_existingLocation_fieldToManual() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(
        Optional.of(
            ProjectLocationUtil.getProjectLocation_withField(details)
        ));
    projectLocation = projectLocationService.createOrUpdate(details, ProjectLocationUtil.getCompletedForm_manualField());
    assertThat(projectLocation.getProjectDetail()).isEqualTo(details);
    assertThat(projectLocation.getManualFieldName()).isEqualTo(ProjectLocationUtil.MANUAL_FIELD_NAME);
    checkCommonFieldsMatch(projectLocation);
  }

  @Test
  public void createOrUpdate_existingLocation_dateNotSetWhenLinkedQuestionIsFalse() {
    var form = ProjectLocationUtil.getCompletedForm_manualField();
    form.setApprovedFieldDevelopmentPlan(false);
    projectLocation = ProjectLocationUtil.getProjectLocation_withField(details);
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(
        Optional.of(
            projectLocation
        ));

    //before call fdp date set
    assertThat(projectLocation.getApprovedFieldDevelopmentPlan()).isTrue();
    assertThat(projectLocation.getApprovedFdpDate()).isNotNull();

    //update with new form details
    projectLocation = projectLocationService.createOrUpdate(details, form);

    //after call fdp date null
    assertThat(projectLocation.getProjectDetail()).isEqualTo(details);
    assertThat(projectLocation.getApprovedFieldDevelopmentPlan()).isFalse();
    assertThat(projectLocation.getApprovedFdpDate()).isNull();
  }

  @Test
  public void createOrUpdate_existingLocation_manualToField() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(
        Optional.of(
            ProjectLocationUtil.getProjectLocation_withManualField(details)
        ));
    when(fieldService.findById(ProjectLocationUtil.FIELD_ID)).thenReturn(ProjectLocationUtil.FIELD);
    projectLocation = projectLocationService.createOrUpdate(details, ProjectLocationUtil.getCompletedForm_withField());
    assertThat(projectLocation.getProjectDetail()).isEqualTo(details);
    assertThat(projectLocation.getField()).isEqualTo(ProjectLocationUtil.FIELD);
    checkCommonFieldsMatch(projectLocation);
  }

  @Test
  public void createOrUpdate_existingLocation_blankForm() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(
        Optional.of(
            ProjectLocationUtil.getProjectLocation_withManualField(details)
        ));
    projectLocation = projectLocationService.createOrUpdate(details, ProjectLocationUtil.getBlankForm());
    assertThat(projectLocation.getProjectDetail()).isEqualTo(details);
    assertThat(projectLocation.getField()).isNull();
    assertThat(projectLocation.getManualFieldName()).isNull();
    assertThat(projectLocation.getFieldType()).isNull();
    assertThat(projectLocation.getWaterDepth()).isNull();
    assertThat(projectLocation.getApprovedFieldDevelopmentPlan()).isNull();
    assertThat(projectLocation.getApprovedFdpDate()).isNull();
    assertThat(projectLocation.getApprovedDecomProgram()).isNull();
  }

  @Test
  public void getForm_existingLocation_manualEntry() {
    projectLocation = ProjectLocationUtil.getProjectLocation_withManualField(details);
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(
        Optional.of(projectLocation)
    );
    var form = projectLocationService.getForm(details);
    assertThat(form.getField()).isEqualTo(ProjectLocationUtil.MANUAL_FIELD_NAME);
    checkCommonFormFieldsMatch(projectLocation, form);
  }

  @Test
  public void getForm_existingLocation_withField() {
    projectLocation = ProjectLocationUtil.getProjectLocation_withField(details);
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(
        Optional.of(projectLocation)
    );
    var form = projectLocationService.getForm(details);
    assertThat(form.getField()).isEqualTo(ProjectLocationUtil.FIELD_ID.toString());
    checkCommonFormFieldsMatch(projectLocation, form);
  }

  @Test
  public void getForm_noExistingLocation() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    var form = projectLocationService.getForm(details);
    assertThat(form.getField()).isNull();
    assertThat(form.getFieldType()).isNull();
    assertThat(form.getWaterDepth()).isNull();
    assertThat(form.getApprovedFieldDevelopmentPlan()).isNull();
    assertThat(form.getApprovedFdpDate()).isNull();
    assertThat(form.getApprovedDecomProgram()).isNull();
  }

  @Test
  public void validate_partial() {
    var form = new ProjectLocationForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectLocationService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void validate_full() {
    var form = ProjectLocationUtil.getCompletedForm_withField();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectLocationService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void getPreSelectedLocation_manualEntry() {
    when(searchSelectorService.buildPrePopulatedSelections(any(), any())).thenCallRealMethod();
    when(searchSelectorService.removePrefix(any())).thenCallRealMethod();
    var form = ProjectLocationUtil.getCompletedForm_manualField();
    var preSelectedLocation = projectLocationService.getPreSelectedField(form);
    assertThat(preSelectedLocation).containsOnly(
      entry(ProjectLocationUtil.MANUAL_FIELD_NAME, ProjectLocationUtil.MANUAL_FIELD_NAME_NO_PREFIX)
    );
  }

  @Test
  public void getPreSelectedLocation_withField() {
    when(searchSelectorService.buildPrePopulatedSelections(any(), any())).thenCallRealMethod();
    when(fieldService.findById(ProjectLocationUtil.FIELD_ID)).thenReturn(ProjectLocationUtil.FIELD);
    var form = ProjectLocationUtil.getCompletedForm_withField();
    var preSelectedLocation = projectLocationService.getPreSelectedField(form);
    assertThat(preSelectedLocation).containsOnly(
        entry(ProjectLocationUtil.FIELD_ID.toString(), ProjectLocationUtil.FIELD_NAME)
    );
  }

  @Test
  public void getPreSelectedLocation_emptyForm() {
    var preSelectedLocation = projectLocationService.getPreSelectedField(new ProjectLocationForm());
    assertThat(preSelectedLocation).isEmpty();
  }

  private void checkCommonFieldsMatch(ProjectLocation projectLocation) {
    assertThat(projectLocation.getFieldType()).isEqualTo(ProjectLocationUtil.FIELD_TYPE);
    assertThat(projectLocation.getWaterDepth()).isEqualTo(ProjectLocationUtil.WATER_DEPTH);
    assertThat(projectLocation.getApprovedFieldDevelopmentPlan()).isEqualTo(ProjectLocationUtil.APPROVED_FDP_PLAN);
    assertThat(projectLocation.getApprovedFdpDate()).isEqualTo(ProjectLocationUtil.APPROVED_FDP_DATE);
    assertThat(projectLocation.getApprovedDecomProgram()).isEqualTo(ProjectLocationUtil.APPROVED_DECOM_PROGRAM);
  }

  private void checkCommonFormFieldsMatch(ProjectLocation projectLocation, ProjectLocationForm form) {
    assertThat(form.getFieldType()).isEqualTo(projectLocation.getFieldType());
    assertThat(form.getWaterDepth()).isEqualTo(projectLocation.getWaterDepth());
    assertThat(form.getApprovedFieldDevelopmentPlan()).isEqualTo(projectLocation.getApprovedFieldDevelopmentPlan());
    assertThat(form.getApprovedFdpDate()).isEqualTo(new ThreeFieldDateInput(projectLocation.getApprovedFdpDate()));
    assertThat(form.getApprovedDecomProgram()).isEqualTo(projectLocation.getApprovedDecomProgram());
    assertThat(form.getApprovedDecomProgramDate()).isEqualTo(new ThreeFieldDateInput(projectLocation.getApprovedDecomProgramDate()));
  }
}
