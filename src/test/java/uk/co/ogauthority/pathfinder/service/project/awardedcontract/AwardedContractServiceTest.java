package uk.co.ogauthority.pathfinder.service.project.awardedcontract;

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
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractForm;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormValidator;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.AwardedContractRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class AwardedContractServiceTest {

  @Mock
  private ValidationService validationService;

  @Mock
  private AwardedContractRepository awardedContractRepository;

  @Mock
  private AwardedContractFormValidator awardedContractFormValidator;

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private AwardedContractService awardedContractService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {

    SearchSelectorService searchSelectorService = new SearchSelectorService();
    FunctionService functionService = new FunctionService(searchSelectorService);

    awardedContractService = new AwardedContractService(
        functionService,
        validationService,
        awardedContractRepository,
        awardedContractFormValidator,
        searchSelectorService,
        projectSetupService,
        entityDuplicationService
    );

    when(awardedContractRepository.save(any(AwardedContract.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getForm_whenNoAwardedContractFound_exception() {

    final Integer awardedContractId = 1;
    final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

    when(awardedContractRepository.findByIdAndProjectDetail(awardedContractId, projectDetail)).thenReturn(Optional.empty());
    awardedContractService.getForm(awardedContractId, projectDetail);
  }

  @Test
  public void getForm() {
    var awardedContract = AwardedContractTestUtil.createAwardedContract();
    final Integer awardedContractId = 1;
    final ProjectDetail projectDetail = awardedContract.getProjectDetail();

    when(awardedContractRepository.findByIdAndProjectDetail(awardedContractId, projectDetail)).thenReturn(Optional.of(awardedContract));

    var form = awardedContractService.getForm(awardedContractId, projectDetail);
    checkCommonFields(form, awardedContract);
    assertThat(form.getContractFunction()).isEqualTo(awardedContract.getContractFunction().name());
  }

  @Test
  public void getForm_whenEntityProvided() {
    var awardedContract = AwardedContractTestUtil.createAwardedContract();
    var form = awardedContractService.getForm(awardedContract);
    checkCommonFields(form, awardedContract);
  }

  @Test
  public void getForm_withManualEntryFunction() {
    final String manualEntryFunction = "My new function";
    final String manualEntryFunctionWithPrefix = SearchSelectorService.getValueWithManualEntryPrefix(manualEntryFunction);

    var awardedContract = AwardedContractTestUtil.createAwardedContract_withManualEntryFunction(manualEntryFunction);

    when(awardedContractRepository.findByIdAndProjectDetail(any(), any())).thenReturn(Optional.of(awardedContract));

    var form = awardedContractService.getForm(1, ProjectUtil.getProjectDetails());
    checkCommonFields(form, awardedContract);
    assertThat(form.getContractFunction()).isEqualTo(manualEntryFunctionWithPrefix);
  }

  private void checkCommonFields(AwardedContractForm form, AwardedContract awardedContract) {
    assertThat(form.getContractorName()).isEqualTo(awardedContract.getContractorName());
    assertThat(form.getDescriptionOfWork()).isEqualTo(awardedContract.getDescriptionOfWork());
    assertThat(form.getDateAwarded()).isEqualTo(new ThreeFieldDateInput(awardedContract.getDateAwarded()));
    assertThat(form.getContractBand()).isEqualTo(awardedContract.getContractBand());

    var contactDetailForm = form.getContactDetail();
    assertThat(contactDetailForm.getName()).isEqualTo(awardedContract.getContactName());
    assertThat(contactDetailForm.getPhoneNumber()).isEqualTo(awardedContract.getPhoneNumber());
    assertThat(contactDetailForm.getEmailAddress()).isEqualTo(awardedContract.getEmailAddress());
    assertThat(contactDetailForm.getJobTitle()).isEqualTo(awardedContract.getJobTitle());
  }

  @Test
  public void findContractFunctionsLikeWithManualEntry() {

    var selectedFunction= Function.FACILITIES_OFFSHORE;
    var results = awardedContractService.findContractFunctionsLikeWithManualEntry(selectedFunction.getDisplayName());

    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).getId()).isEqualTo(selectedFunction.name());
  }

  @Test
  public void findContractFunctionsLikeWithManualEntry_withManualEntry() {
    var manualEntry = "manual entry";
    var results = awardedContractService.findContractFunctionsLikeWithManualEntry(manualEntry);

    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).getId()).isEqualTo(SearchSelectablePrefix.FREE_TEXT_PREFIX + manualEntry);
  }

  @Test
  public void validate_partial() {
    var form = new AwardedContractForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    awardedContractService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void validate_full() {
    var form = new AwardedContractForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    awardedContractService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void createAwardedContract() {
    var form = AwardedContractTestUtil.createAwardedContractForm();
    var projectDetail = ProjectUtil.getProjectDetails();

    var awardedContract = awardedContractService.createAwardedContract(projectDetail, form);

    checkCommonFields(form, awardedContract);
    assertThat(awardedContract.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(awardedContract.getContractFunction()).isEqualTo(Function.valueOf(form.getContractFunction()));
    assertThat(awardedContract.getManualContractFunction()).isNull();
  }

  @Test
  public void createAwardedContract_withManualContractFunction() {
    var form = AwardedContractTestUtil.createAwardedContractForm();

    var manualEntryFunction = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my new function";
    form.setContractFunction(manualEntryFunction);

    var projectDetail = ProjectUtil.getProjectDetails();

    var awardedContract = awardedContractService.createAwardedContract(projectDetail, form);

    checkCommonFields(form, awardedContract);
    assertThat(awardedContract.getManualContractFunction()).isEqualTo(SearchSelectorService.removePrefix(manualEntryFunction));
    assertThat(awardedContract.getContractFunction()).isNull();
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void updateAwardedContract_whenEntityNotFound_exception() {
    when(awardedContractRepository.findByIdAndProjectDetail(any(), any())).thenReturn(Optional.empty());
    awardedContractService.updateAwardedContract(null, null, null);
  }

  @Test
  public void updateAwardedContract() {
    var form = AwardedContractTestUtil.createAwardedContractForm();
    var projectDetail = ProjectUtil.getProjectDetails();
    var awardedContract = new AwardedContract(projectDetail);

    when(awardedContractRepository.findByIdAndProjectDetail(any(), any())).thenReturn(Optional.of(awardedContract));
    var savedAwardedContract = awardedContractService.updateAwardedContract(1, projectDetail, form);

    checkCommonFields(form, savedAwardedContract);
    assertThat(savedAwardedContract.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(savedAwardedContract.getContractFunction()).isEqualTo(Function.valueOf(form.getContractFunction()));
    assertThat(savedAwardedContract.getManualContractFunction()).isNull();
  }

  @Test
  public void updateAwardedContract_withManualContractFunction() {
    var form = AwardedContractTestUtil.createAwardedContractForm();

    var manualEntryFunction = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my new function";
    form.setContractFunction(manualEntryFunction);

    var projectDetail = ProjectUtil.getProjectDetails();
    var awardedContract = new AwardedContract(projectDetail);

    when(awardedContractRepository.findByIdAndProjectDetail(any(), any())).thenReturn(Optional.of(awardedContract));
    var savedAwardedContract = awardedContractService.updateAwardedContract(1, projectDetail, form);

    checkCommonFields(form, savedAwardedContract);
    assertThat(savedAwardedContract.getManualContractFunction()).isEqualTo(SearchSelectorService.removePrefix(manualEntryFunction));
    assertThat(savedAwardedContract.getContractFunction()).isNull();
  }

  @Test
  public void getPreSelectedContractFunction_noManualEntry() {
    var form = AwardedContractTestUtil.createAwardedContractForm();
    var function = Function.DRILLING;
    form.setContractFunction(function.name());

    var preSelectedMap = awardedContractService.getPreSelectedContractFunction(form);
    assertThat(preSelectedMap).containsExactly(
        entry(form.getContractFunction(), Function.valueOf(form.getContractFunction()).getDisplayName())
    );
  }

  @Test
  public void getPreSelectedContractFunction_withManualEntry() {
    var form = AwardedContractTestUtil.createAwardedContractForm();
    var function = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my new function";
    form.setContractFunction(function);

    var preSelectedMap = awardedContractService.getPreSelectedContractFunction(form);
    assertThat(preSelectedMap).containsExactly(
        entry(form.getContractFunction(), SearchSelectorService.removePrefix(form.getContractFunction()))
    );
  }

  @Test
  public void deleteAwardedContract() {
    var awardedContract = AwardedContractTestUtil.createAwardedContract();
    awardedContractService.deleteAwardedContract(awardedContract);
    verify(awardedContractRepository, times(1)).delete(awardedContract);
  }

  @Test
  public void canShowInTaskList_true() {
    when(projectSetupService.taskSelectedForProjectDetail(detail, ProjectTask.AWARDED_CONTRACTS)).thenReturn(true);
    assertThat(awardedContractService.canShowInTaskList(detail)).isTrue();
  }

  @Test
  public void canShowInTaskList_false() {
    when(projectSetupService.taskSelectedForProjectDetail(detail, ProjectTask.AWARDED_CONTRACTS)).thenReturn(false);
    assertThat(awardedContractService.canShowInTaskList(detail)).isFalse();
  }

  @Test
  public void removeSectionData_verifyInteractions() {
    final var awardedContract1 = AwardedContractTestUtil.createAwardedContract();
    final var awardedContract2 = AwardedContractTestUtil.createAwardedContract();
    final var awardedContracts = List.of(awardedContract1, awardedContract2);

    when(awardedContractRepository.findByProjectDetailOrderByIdAsc(detail)).thenReturn(awardedContracts);

    awardedContractService.removeSectionData(detail);

    verify(awardedContractRepository, times(1)).deleteAll(awardedContracts);
  }

  @Test
  public void copySectionData_verifyDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);
    final var awardedContracts = List.of(AwardedContractTestUtil.createAwardedContract());

    when(awardedContractRepository.findByProjectDetailOrderByIdAsc(fromProjectDetail))
        .thenReturn(awardedContracts);

    awardedContractService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        awardedContracts,
        toProjectDetail,
        AwardedContract.class
    );
  }
}