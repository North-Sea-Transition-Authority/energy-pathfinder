package uk.co.ogauthority.pathfinder.service.project.decommissionedwell;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedwell.DecommissionedWell;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedwell.DecommissionedWellType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedwell.WellMechanicalStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedwell.WellOperationalStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedwell.DecommissionedWellForm;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedwell.DecommissionedWellFormValidator;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.decommissionedwell.DecommissionedWellRepository;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.DecommissionedWellTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class DecommissionedWellServiceTest {

  private static final String MANUAL_ENTRY_TYPE = SearchSelectablePrefix.FREE_TEXT_PREFIX + "type manual entry";
  private static final String MANUAL_ENTRY_OPERATIONAL_STATUS = SearchSelectablePrefix.FREE_TEXT_PREFIX + "operational status manual entry";
  private static final String MANUAL_ENTRY_MECHANICAL_STATUS = SearchSelectablePrefix.FREE_TEXT_PREFIX + "mechanical status manual entry";

  @Mock
  private ValidationService validationService;

  @Mock
  private DecommissionedWellFormValidator decommissionedWellFormValidator;

  @Mock
  private DecommissionedWellRepository decommissionedWellRepository;

  @Mock
  private ProjectSetupService projectSetupService;

  private DecommissionedWellService decommissionedWellService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    SearchSelectorService searchSelectorService = new SearchSelectorService();
    decommissionedWellService = new DecommissionedWellService(
        searchSelectorService,
        validationService,
        decommissionedWellFormValidator,
        decommissionedWellRepository,
        projectSetupService);

    when(decommissionedWellRepository.save(any(DecommissionedWell.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void findTypesLikeWithManualEntry_whenMatchFromList() {
    var type = DecommissionedWellType.OPEN_WATER;
    var results = decommissionedWellService.findTypesLikeWithManualEntry(type.getSelectionText());
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo(type.getSelectionId());
  }

  @Test
  public void findTypesLikeWithManualEntry_whenNoMatchFromList() {
    var type = "manual entry";
    var results = decommissionedWellService.findTypesLikeWithManualEntry(type);
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getText()).isEqualTo(type);
  }

  @Test
  public void findOperationalStatusesLikeWithManualEntry_whenMatchFromList() {
    var type = WellOperationalStatus.PLANNED;
    var results = decommissionedWellService.findOperationalStatusesLikeWithManualEntry(type.getSelectionText());
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo(type.getSelectionId());
  }

  @Test
  public void findOperationalStatusesLikeWithManualEntry_whenNoMatchFromList() {
    var type = "manual entry";
    var results = decommissionedWellService.findOperationalStatusesLikeWithManualEntry(type);
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getText()).isEqualTo(type);
  }

  @Test
  public void findMechanicalStatusesLikeWithManualEntry_whenMatchFromList() {
    var type = WellMechanicalStatus.ABANDONED_PHASE_1;
    var results = decommissionedWellService.findMechanicalStatusesLikeWithManualEntry(type.getSelectionText());
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo(type.getSelectionId());
  }

  @Test
  public void findMechanicalStatusesLikeWithManualEntry_whenNoMatchFromList() {
    var type = "manual entry";
    var results = decommissionedWellService.findMechanicalStatusesLikeWithManualEntry(type);
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getText()).isEqualTo(type);
  }

  @Test
  public void validate_whenPartial() {
    var form = new DecommissionedWellForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    decommissionedWellService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );

    verify(validationService).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void validate_whenFull() {
    var form = new DecommissionedWellForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    decommissionedWellService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );

    verify(validationService).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void createDecommissionedWell_noManualEntrySearchSelectorValues() {
    var form = DecommissionedWellTestUtil.getCompletedForm();
    var projectDetail = ProjectUtil.getProjectDetails();
    var decommissionedWell = decommissionedWellService.createDecommissionedWell(form, projectDetail);

    checkCommonEntityFields(form, decommissionedWell, projectDetail);
    assertThat(decommissionedWell.getType().getSelectionId()).isEqualTo(form.getType());
    assertThat(decommissionedWell.getManualType()).isNull();
    assertThat(decommissionedWell.getOperationalStatus().getSelectionId()).isEqualTo(form.getOperationalStatus());
    assertThat(decommissionedWell.getManualOperationalStatus()).isNull();
    assertThat(decommissionedWell.getMechanicalStatus().getSelectionId()).isEqualTo(form.getMechanicalStatus());
    assertThat(decommissionedWell.getManualMechanicalStatus()).isNull();
  }

  @Test
  public void createDecommissionedWell_onlyManualEntrySearchSelectorValues() {

    var form = DecommissionedWellTestUtil.getCompletedForm();
    form.setType(MANUAL_ENTRY_TYPE);
    form.setOperationalStatus(MANUAL_ENTRY_OPERATIONAL_STATUS);
    form.setMechanicalStatus(MANUAL_ENTRY_MECHANICAL_STATUS);

    var projectDetail = ProjectUtil.getProjectDetails();
    var decommissionedWell = decommissionedWellService.createDecommissionedWell(form, projectDetail);

    checkCommonEntityFields(form, decommissionedWell, projectDetail);
    assertThat(decommissionedWell.getType()).isNull();
    assertThat(decommissionedWell.getManualType()).isEqualTo(
        SearchSelectorService.removePrefix(MANUAL_ENTRY_TYPE)
    );
    assertThat(decommissionedWell.getOperationalStatus()).isNull();
    assertThat(decommissionedWell.getManualOperationalStatus()).isEqualTo(
        SearchSelectorService.removePrefix(MANUAL_ENTRY_OPERATIONAL_STATUS)
    );
    assertThat(decommissionedWell.getMechanicalStatus()).isNull();
    assertThat(decommissionedWell.getManualMechanicalStatus()).isEqualTo(
        SearchSelectorService.removePrefix(MANUAL_ENTRY_MECHANICAL_STATUS)
    );
  }

  @Test
  public void updateDecommissionedWell_whenDecommissionedWellEntityFoundAndNoManualEntry() {

    final Integer decommissionedWellId = 1;
    final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

    var form = DecommissionedWellTestUtil.getCompletedForm();

    var decommissionedWell = new DecommissionedWell();
    decommissionedWell.setProjectDetail(projectDetail);

    when(decommissionedWellRepository.findByIdAndProjectDetail(decommissionedWellId, projectDetail))
        .thenReturn(Optional.of(decommissionedWell));

    decommissionedWell = decommissionedWellService.updateDecommissionedWell(
        decommissionedWellId,
        projectDetail,
        form
    );

    checkCommonEntityFields(form, decommissionedWell, projectDetail);

    assertThat(decommissionedWell.getType().getSelectionId()).isEqualTo(form.getType());
    assertThat(decommissionedWell.getManualType()).isNull();
    assertThat(decommissionedWell.getOperationalStatus().getSelectionId()).isEqualTo(form.getOperationalStatus());
    assertThat(decommissionedWell.getManualOperationalStatus()).isNull();
    assertThat(decommissionedWell.getMechanicalStatus().getSelectionId()).isEqualTo(form.getMechanicalStatus());
    assertThat(decommissionedWell.getManualMechanicalStatus()).isNull();
  }

  @Test
  public void updateDecommissionedWell_whenDecommissionedWellEntityFoundAndOnlyManualEntry() {

    final Integer decommissionedWellId = 1;
    final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

    var form = DecommissionedWellTestUtil.getCompletedForm();
    form.setType(MANUAL_ENTRY_TYPE);
    form.setOperationalStatus(MANUAL_ENTRY_OPERATIONAL_STATUS);
    form.setMechanicalStatus(MANUAL_ENTRY_MECHANICAL_STATUS);

    var decommissionedWell = new DecommissionedWell();
    decommissionedWell.setProjectDetail(projectDetail);

    when(decommissionedWellRepository.findByIdAndProjectDetail(decommissionedWellId, projectDetail))
        .thenReturn(Optional.of(decommissionedWell));

    decommissionedWell = decommissionedWellService.updateDecommissionedWell(
        decommissionedWellId,
        projectDetail,
        form
    );

    checkCommonEntityFields(form, decommissionedWell, projectDetail);

    assertThat(decommissionedWell.getType()).isNull();
    assertThat(decommissionedWell.getManualType()).isEqualTo(
        SearchSelectorService.removePrefix(MANUAL_ENTRY_TYPE)
    );
    assertThat(decommissionedWell.getOperationalStatus()).isNull();
    assertThat(decommissionedWell.getManualOperationalStatus()).isEqualTo(
        SearchSelectorService.removePrefix(MANUAL_ENTRY_OPERATIONAL_STATUS)
    );
    assertThat(decommissionedWell.getMechanicalStatus()).isNull();
    assertThat(decommissionedWell.getManualMechanicalStatus()).isEqualTo(
        SearchSelectorService.removePrefix(MANUAL_ENTRY_MECHANICAL_STATUS)
    );
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void updateDecommissionedWell_whenDecommissionedWellEntityNotFound_thenException() {

    final Integer decommissionedWellId = 1;
    final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

    when(decommissionedWellRepository.findByIdAndProjectDetail(decommissionedWellId, projectDetail))
        .thenReturn(Optional.empty());

    decommissionedWellService.updateDecommissionedWell(decommissionedWellId, projectDetail, new DecommissionedWellForm());
    verify(decommissionedWellRepository, times(0)).save(any());
  }

  @Test
  public void updateDecommissionedWell_whenNoManualEntry() {

    final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

    var form = DecommissionedWellTestUtil.getCompletedForm();

    var decommissionedWell = new DecommissionedWell();
    decommissionedWell.setProjectDetail(projectDetail);

    decommissionedWell = decommissionedWellService.updateDecommissionedWell(form, decommissionedWell);

    checkCommonEntityFields(form, decommissionedWell, projectDetail);

    assertThat(decommissionedWell.getType().getSelectionId()).isEqualTo(form.getType());
    assertThat(decommissionedWell.getManualType()).isNull();
    assertThat(decommissionedWell.getOperationalStatus().getSelectionId()).isEqualTo(form.getOperationalStatus());
    assertThat(decommissionedWell.getManualOperationalStatus()).isNull();
    assertThat(decommissionedWell.getMechanicalStatus().getSelectionId()).isEqualTo(form.getMechanicalStatus());
    assertThat(decommissionedWell.getManualMechanicalStatus()).isNull();
  }

  @Test
  public void updateDecommissionedWell_whenOnlyManualEntry() {

    final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

    var form = DecommissionedWellTestUtil.getCompletedForm();
    form.setType(MANUAL_ENTRY_TYPE);
    form.setOperationalStatus(MANUAL_ENTRY_OPERATIONAL_STATUS);
    form.setMechanicalStatus(MANUAL_ENTRY_MECHANICAL_STATUS);

    var decommissionedWell = new DecommissionedWell();
    decommissionedWell.setProjectDetail(projectDetail);

    decommissionedWell = decommissionedWellService.updateDecommissionedWell(form, decommissionedWell);

    checkCommonEntityFields(form, decommissionedWell, projectDetail);

    assertThat(decommissionedWell.getType()).isNull();
    assertThat(decommissionedWell.getManualType()).isEqualTo(
        SearchSelectorService.removePrefix(MANUAL_ENTRY_TYPE)
    );
    assertThat(decommissionedWell.getOperationalStatus()).isNull();
    assertThat(decommissionedWell.getManualOperationalStatus()).isEqualTo(
        SearchSelectorService.removePrefix(MANUAL_ENTRY_OPERATIONAL_STATUS)
    );
    assertThat(decommissionedWell.getMechanicalStatus()).isNull();
    assertThat(decommissionedWell.getManualMechanicalStatus()).isEqualTo(
        SearchSelectorService.removePrefix(MANUAL_ENTRY_MECHANICAL_STATUS)
    );
  }

  @Test
  public void getPreSelectedType_whenFromList() {

    final DecommissionedWellType decommissionedWellType = DecommissionedWellType.OPEN_WATER;

    var form = DecommissionedWellTestUtil.getCompletedForm();
    form.setType(decommissionedWellType.getSelectionId());

    var result = decommissionedWellService.getPreSelectedType(form);

    assertThat(result).containsExactly(
        entry(decommissionedWellType.getSelectionId(), decommissionedWellType.getSelectionText())
    );
  }

  @Test
  public void getPreSelectedType_whenManualEntry() {

    var form = DecommissionedWellTestUtil.getCompletedForm();
    form.setType(MANUAL_ENTRY_TYPE);

    var result = decommissionedWellService.getPreSelectedType(form);

    assertThat(result).containsExactly(
        entry(
            MANUAL_ENTRY_TYPE,
            SearchSelectorService.removePrefix(MANUAL_ENTRY_TYPE)
        )
    );
  }

  @Test
  public void getPreSelectedOperationalStatus_whenFromList() {

    final WellOperationalStatus operationalStatus = WellOperationalStatus.PLANNED;

    var form = DecommissionedWellTestUtil.getCompletedForm();
    form.setOperationalStatus(operationalStatus.getSelectionId());

    var result = decommissionedWellService.getPreSelectedOperationalStatus(form);

    assertThat(result).containsExactly(
        entry(operationalStatus.getSelectionId(), operationalStatus.getSelectionText())
    );
  }

  @Test
  public void getPreSelectedOperationalStatus_whenManualEntry() {

    var form = DecommissionedWellTestUtil.getCompletedForm();
    form.setOperationalStatus(MANUAL_ENTRY_OPERATIONAL_STATUS);

    var result = decommissionedWellService.getPreSelectedOperationalStatus(form);

    assertThat(result).containsExactly(
        entry(
            MANUAL_ENTRY_OPERATIONAL_STATUS,
            SearchSelectorService.removePrefix(MANUAL_ENTRY_OPERATIONAL_STATUS)
        )
    );
  }

  @Test
  public void getPreSelectedMechanicalStatus_whenFromList() {

    final WellMechanicalStatus mechanicalStatus = WellMechanicalStatus.ABANDONED_PHASE_2;

    var form = DecommissionedWellTestUtil.getCompletedForm();
    form.setMechanicalStatus(mechanicalStatus.getSelectionId());

    var result = decommissionedWellService.getPreSelectedMechanicalStatus(form);

    assertThat(result).containsExactly(
        entry(mechanicalStatus.getSelectionId(), mechanicalStatus.getSelectionText())
    );
  }

  @Test
  public void getPreSelectedMechanicalStatus_whenManualEntry() {

    var form = DecommissionedWellTestUtil.getCompletedForm();
    form.setMechanicalStatus(MANUAL_ENTRY_MECHANICAL_STATUS);

    var result = decommissionedWellService.getPreSelectedMechanicalStatus(form);

    assertThat(result).containsExactly(
        entry(
            MANUAL_ENTRY_MECHANICAL_STATUS,
            SearchSelectorService.removePrefix(MANUAL_ENTRY_MECHANICAL_STATUS)
        )
    );
  }

  @Test
  public void getForm_whenEntityFoundAndNoManualEntry() {

    final Integer decommissionedWellId = 1;
    final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

    var decommissionedWell = DecommissionedWellTestUtil.createDecommissionedWell();

    when(decommissionedWellRepository.findByIdAndProjectDetail(decommissionedWellId, projectDetail))
        .thenReturn(Optional.of(decommissionedWell));

    var form = decommissionedWellService.getForm(decommissionedWellId, projectDetail);

    checkCommonFormFields(form, decommissionedWell);
    assertThat(form.getType()).isEqualTo(decommissionedWell.getType().getSelectionId());
    assertThat(form.getOperationalStatus()).isEqualTo(decommissionedWell.getOperationalStatus().getSelectionId());
    assertThat(form.getMechanicalStatus()).isEqualTo(decommissionedWell.getMechanicalStatus().getSelectionId());
  }

  @Test
  public void getForm_whenEntityFoundAndOnlyManualEntry() {

    final Integer decommissionedWellId = 1;
    final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

    var decommissionedWell = DecommissionedWellTestUtil.createDecommissionedWell();
    decommissionedWell.setManualType(SearchSelectorService.removePrefix(MANUAL_ENTRY_TYPE));
    decommissionedWell.setType(null);
    decommissionedWell.setManualOperationalStatus(SearchSelectorService.removePrefix(MANUAL_ENTRY_OPERATIONAL_STATUS));
    decommissionedWell.setOperationalStatus(null);
    decommissionedWell.setManualMechanicalStatus(SearchSelectorService.removePrefix(MANUAL_ENTRY_MECHANICAL_STATUS));
    decommissionedWell.setMechanicalStatus(null);

    when(decommissionedWellRepository.findByIdAndProjectDetail(decommissionedWellId, projectDetail))
        .thenReturn(Optional.of(decommissionedWell));

    var form = decommissionedWellService.getForm(decommissionedWellId, projectDetail);

    checkCommonFormFields(form, decommissionedWell);
    assertThat(form.getType()).isEqualTo(MANUAL_ENTRY_TYPE);
    assertThat(form.getOperationalStatus()).isEqualTo(MANUAL_ENTRY_OPERATIONAL_STATUS);
    assertThat(form.getMechanicalStatus()).isEqualTo(MANUAL_ENTRY_MECHANICAL_STATUS);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getForm_whenEntityNotFound_thenException() {

    final Integer decommissionedWellId = 1;
    final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

    when(decommissionedWellRepository.findByIdAndProjectDetail(decommissionedWellId, projectDetail))
        .thenReturn(Optional.empty());

    decommissionedWellService.getForm(decommissionedWellId, projectDetail);
  }

  private void checkCommonEntityFields(DecommissionedWellForm form,
                                       DecommissionedWell decommissionedWell,
                                       ProjectDetail projectDetail) {
    assertThat(decommissionedWell.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(decommissionedWell.getNumberToBeDecommissioned()).isEqualTo(form.getNumberToBeDecommissioned());
    assertThat(decommissionedWell.getPlugAbandonmentDateQuarter()).isEqualTo(form.getPlugAbandonmentDate().getQuarter());
    assertThat(decommissionedWell.getPlugAbandonmentDateYear()).isEqualTo(Integer.parseInt(form.getPlugAbandonmentDate().getYear()));
    assertThat(decommissionedWell.getPlugAbandonmentDateType()).isEqualTo(form.getPlugAbandonmentDateType());
  }

  private void checkCommonFormFields(DecommissionedWellForm form,
                                     DecommissionedWell decommissionedWell) {
    assertThat(form.getNumberToBeDecommissioned()).isEqualTo(decommissionedWell.getNumberToBeDecommissioned());
    assertThat(form.getPlugAbandonmentDate()).isEqualTo(new QuarterYearInput(
        decommissionedWell.getPlugAbandonmentDateQuarter(),
        String.valueOf(decommissionedWell.getPlugAbandonmentDateYear())
    ));
    assertThat(form.getPlugAbandonmentDateType()).isEqualTo(decommissionedWell.getPlugAbandonmentDateType());
  }

  @Test
  public void canShowInTaskList_true() {
    when(projectSetupService.taskSelectedForProjectDetail(detail, ProjectTask.WELLS)).thenReturn(true);
    assertThat(decommissionedWellService.canShowInTaskList(detail)).isTrue();
  }

  @Test
  public void canShowInTaskList_false() {
    when(projectSetupService.taskSelectedForProjectDetail(detail, ProjectTask.WELLS)).thenReturn(false);
    assertThat(decommissionedWellService.canShowInTaskList(detail)).isFalse();
  }

  @Test
  public void getDecommissionedWellsForProjectDetail_whenResults_thenReturnPopulatedList() {

    final var decommissionedWell1 = DecommissionedWellTestUtil.createDecommissionedWell();
    final var decommissionedWell2 = DecommissionedWellTestUtil.createDecommissionedWell();
    final var decommissionedWells = List.of(decommissionedWell1, decommissionedWell2);

    when(decommissionedWellRepository.findByProjectDetailOrderByIdAsc(detail)).thenReturn(decommissionedWells);

    final var result = decommissionedWellService.getDecommissionedWellsForProjectDetail(detail);

    assertThat(result).containsExactly(
        decommissionedWell1,
        decommissionedWell2
    );
  }

  @Test
  public void getDecommissionedWellsForProjectDetail_whenNoResults_thenReturnEmptyList() {

    when(decommissionedWellRepository.findByProjectDetailOrderByIdAsc(detail)).thenReturn(List.of());

    final var result = decommissionedWellService.getDecommissionedWellsForProjectDetail(detail);

    assertThat(result).isEmpty();
  }

  @Test
  public void removeSectionData_verifyInteractions() {

    final var decommissionedWell1 = DecommissionedWellTestUtil.createDecommissionedWell();
    final var decommissionedWell2 = DecommissionedWellTestUtil.createDecommissionedWell();
    final var decommissionedWells = List.of(decommissionedWell1, decommissionedWell2);

    when(decommissionedWellRepository.findByProjectDetailOrderByIdAsc(detail)).thenReturn(decommissionedWells);

    decommissionedWellService.removeSectionData(detail);

    verify(decommissionedWellRepository, times(1)).deleteAll(decommissionedWells);
  }

}