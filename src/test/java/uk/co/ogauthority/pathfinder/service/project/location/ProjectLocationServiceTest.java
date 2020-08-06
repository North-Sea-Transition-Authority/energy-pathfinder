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
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFieldService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
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
  private SpringValidatorAdapter validator;

  private ProjectLocationService projectLocationService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  private ProjectLocation projectLocation;

  @Before
  public void setUp() throws Exception {
    projectLocationService = new ProjectLocationService(
        projectLocationRepository,
        fieldService,
        searchSelectorService,
        validator
    );

    when(projectLocationRepository.save(any(ProjectLocation.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createOrUpdate_newLocation_manualEntry() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    projectLocation = projectLocationService.createOrUpdate(details, ProjectLocationUtil.getCompletedForm_manualField());
    assertThat(projectLocation.getProjectDetail()).isEqualTo(details);
    assertThat(projectLocation.getManualFieldName()).isEqualTo(ProjectLocationUtil.MANUAL_FIELD_NAME);
  }

  @Test
  public void createOrUpdate_newLocation() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    when(fieldService.findById(ProjectLocationUtil.FIELD_ID)).thenReturn(ProjectLocationUtil.FIELD);
    projectLocation = projectLocationService.createOrUpdate(details, ProjectLocationUtil.getCompletedForm_withField());
    assertThat(projectLocation.getProjectDetail()).isEqualTo(details);
    assertThat(projectLocation.getField()).isEqualTo(ProjectLocationUtil.FIELD);
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
  }

  @Test
  public void createOrUpdate_existingLocation_blankForm() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(
        Optional.of(
            ProjectLocationUtil.getProjectLocation_withManualField(details)
        ));
    projectLocation = projectLocationService.createOrUpdate(details, new ProjectLocationForm());
    assertThat(projectLocation.getProjectDetail()).isEqualTo(details);
    assertThat(projectLocation.getField()).isNull();
    assertThat(projectLocation.getManualFieldName()).isNull();
  }

  @Test
  public void getForm_existingLocation_manualEntry() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(
        Optional.of(
            ProjectLocationUtil.getProjectLocation_withManualField(details)
        ));
    var form = projectLocationService.getForm(details);
    assertThat(form.getField()).isEqualTo(ProjectLocationUtil.MANUAL_FIELD_NAME);
  }

  @Test
  public void getForm_existingLocation_withField() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(
        Optional.of(
            ProjectLocationUtil.getProjectLocation_withField(details)
        ));
    var form = projectLocationService.getForm(details);
    assertThat(form.getField()).isEqualTo(ProjectLocationUtil.FIELD_ID.toString());
  }

  @Test
  public void getForm_noExistingLocation() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    var form = projectLocationService.getForm(details);
    assertThat(form.getField()).isNull();
  }

  @Test
  public void validate_partial() {
    projectLocationService.validate(
        new ProjectLocationForm(),
        null,
        ValidationType.PARTIAL
    );
    verify(validator, times(0)).validate(any(), any(), any());
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

    verify(validator, times(1)).validate(form, bindingResult, FullValidation.class);
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
}
