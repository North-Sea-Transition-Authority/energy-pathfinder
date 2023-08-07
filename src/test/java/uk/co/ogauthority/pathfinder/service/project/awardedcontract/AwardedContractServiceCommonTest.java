package uk.co.ogauthority.pathfinder.service.project.awardedcontract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.AwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormCommon;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.infrastructure.InfrastructureAwardedContractForm;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.infrastructure.AwardedContractRepository;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@ExtendWith(MockitoExtension.class)
class AwardedContractServiceCommonTest {

  @Mock
  private AwardedContractRepository awardedContractRepository;

  @Mock
  private TeamService teamService;

  @Mock
  private ValidationService validationService;

  @Mock
  private AwardedContractFormValidator awardedContractFormValidator;

  private AwardedContractServiceCommon awardedContractService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount userAccount = UserTestingUtil.getAuthenticatedUserAccount();
  private final PortalOrganisationGroup portalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "org", "org");

  @BeforeEach
  void setup() {
    var searchSelectorService = new SearchSelectorService();
    var functionService = new FunctionService(searchSelectorService);

    awardedContractService = new AwardedContractServiceCommon(
        awardedContractRepository,
        functionService,
        teamService,
        searchSelectorService,
        validationService,
        awardedContractFormValidator);
  }


  @Test
  void getForm_whenNoAwardedContractFound_exception() {
    final Integer awardedContractId = 1;
    final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

    when(awardedContractRepository.findByIdAndProjectDetail(awardedContractId, projectDetail)).thenReturn(Optional.empty());
    assertThrows(PathfinderEntityNotFoundException.class, () -> awardedContractService.getForm(awardedContractId, projectDetail));
  }

  @Test
  void getForm() {
    var awardedContract = AwardedContractTestUtil.createAwardedContract();
    final Integer awardedContractId = 1;
    final ProjectDetail projectDetail = awardedContract.getProjectDetail();

    when(awardedContractRepository.findByIdAndProjectDetail(awardedContractId, projectDetail)).thenReturn(Optional.of(awardedContract));

    var form = awardedContractService.getForm(awardedContractId, projectDetail);
    checkCommonFields(form, awardedContract);
    assertThat(form.getContractFunction()).isEqualTo(awardedContract.getContractFunction().name());
  }

  @Test
  void getForm_whenEntityProvided() {
    var awardedContract = AwardedContractTestUtil.createAwardedContract();
    var form = awardedContractService.getForm(awardedContract);
    checkCommonFields(form, awardedContract);
  }

  @Test
  void getForm_withManualEntryFunction() {
    final String manualEntryFunction = "My new function";
    final String manualEntryFunctionWithPrefix = SearchSelectorService.getValueWithManualEntryPrefix(manualEntryFunction);

    var awardedContract = AwardedContractTestUtil.createAwardedContract_withManualEntryFunction(manualEntryFunction);

    when(awardedContractRepository.findByIdAndProjectDetail(any(), any())).thenReturn(Optional.of(awardedContract));

    var form = awardedContractService.getForm(1, ProjectUtil.getProjectDetails());
    checkCommonFields(form, awardedContract);
    assertThat(form.getContractFunction()).isEqualTo(manualEntryFunctionWithPrefix);
  }

  @Test
  void findContractFunctionsLikeWithManualEntry() {
    var selectedFunction= Function.FACILITIES_OFFSHORE;
    var results = awardedContractService.findContractFunctionsLikeWithManualEntry(selectedFunction.getDisplayName());

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo(selectedFunction.name());
  }

  @Test
  void findContractFunctionsLikeWithManualEntry_withManualEntry() {
    var manualEntry = "manual entry";
    var results = awardedContractService.findContractFunctionsLikeWithManualEntry(manualEntry);

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo(SearchSelectablePrefix.FREE_TEXT_PREFIX + manualEntry);
  }

  @Test
  void getAwardedContracts() {
    var awardedContracts = List.of(
        AwardedContractTestUtil.createAwardedContract(),
        AwardedContractTestUtil.createAwardedContract()
    );

    when(awardedContractRepository.findByProjectDetailOrderByIdAsc(detail)).thenReturn(awardedContracts);

    assertThat(awardedContractService.getAwardedContracts(detail)).isEqualTo(awardedContracts);
  }

  @Test
  void getAwardedContractsByProjectAndVersion() {
    var project = detail.getProject();
    var version = detail.getVersion();
    var awardedContracts = List.of(
        AwardedContractTestUtil.createAwardedContract(),
        AwardedContractTestUtil.createAwardedContract()
    );

    when(awardedContractRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version))
        .thenReturn(awardedContracts);

    assertThat(awardedContractService.getAwardedContractsByProjectAndVersion(project, version)).isEqualTo(awardedContracts);
  }

  @Test
  void createAwardedContract() {
    when(teamService.getContributorPortalOrganisationGroup(userAccount)).thenReturn(portalOrganisationGroup);
    when(awardedContractRepository.save(any(AwardedContract.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

    var form = AwardedContractTestUtil.createInfrastructureAwardedContractForm();
    var projectDetail = ProjectUtil.getProjectDetails();

    var awardedContract = awardedContractService.createAwardedContract(projectDetail, form, userAccount);

    checkCommonFields(form, awardedContract);
    assertThat(awardedContract.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(awardedContract.getContractFunction()).isEqualTo(Function.valueOf(form.getContractFunction()));
    assertThat(awardedContract.getManualContractFunction()).isNull();
    assertThat(awardedContract.getAddedByOrganisationGroup()).isEqualTo(portalOrganisationGroup.getOrgGrpId());
  }

  @Test
  void createAwardedContract_withManualContractFunction() {
    when(teamService.getContributorPortalOrganisationGroup(userAccount)).thenReturn(portalOrganisationGroup);
    when(awardedContractRepository.save(any(AwardedContract.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
    var form = AwardedContractTestUtil.createInfrastructureAwardedContractForm();

    var manualEntryFunction = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my new function";
    form.setContractFunction(manualEntryFunction);

    var projectDetail = ProjectUtil.getProjectDetails();

    var awardedContract = awardedContractService.createAwardedContract(projectDetail, form, userAccount);

    checkCommonFields(form, awardedContract);
    assertThat(awardedContract.getManualContractFunction()).isEqualTo(SearchSelectorService.removePrefix(manualEntryFunction));
    assertThat(awardedContract.getContractFunction()).isNull();
    assertThat(awardedContract.getAddedByOrganisationGroup()).isEqualTo(portalOrganisationGroup.getOrgGrpId());
  }

  @Test
  void updateAwardedContract_whenEntityNotFound_exception() {
    when(awardedContractRepository.findByIdAndProjectDetail(any(), any())).thenReturn(Optional.empty());
    assertThrows(PathfinderEntityNotFoundException.class, () -> awardedContractService.updateAwardedContract(null, null, null));
  }

  @Test
  void updateAwardedContract() {
    var form = AwardedContractTestUtil.createInfrastructureAwardedContractForm();
    var projectDetail = ProjectUtil.getProjectDetails();
    var awardedContract = new AwardedContract(projectDetail);

    when(awardedContractRepository.findByIdAndProjectDetail(any(), any())).thenReturn(Optional.of(awardedContract));
    when(awardedContractRepository.save(any(AwardedContract.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
    var savedAwardedContract = awardedContractService.updateAwardedContract(1, projectDetail, form);

    checkCommonFields(form, savedAwardedContract);
    assertThat(savedAwardedContract.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(savedAwardedContract.getContractFunction()).isEqualTo(Function.valueOf(form.getContractFunction()));
    assertThat(savedAwardedContract.getManualContractFunction()).isNull();
  }

  @Test
  void updateAwardedContract_withManualContractFunction() {
    when(awardedContractRepository.save(any(AwardedContract.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
    var form = AwardedContractTestUtil.createInfrastructureAwardedContractForm();

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
  void getPreSelectedContractFunction_noManualEntry() {
    var form = AwardedContractTestUtil.createInfrastructureAwardedContractForm();
    var function = Function.DRILLING;
    form.setContractFunction(function.name());

    var preSelectedMap = awardedContractService.getPreSelectedContractFunction(form);
    assertThat(preSelectedMap).containsExactly(
        entry(form.getContractFunction(), Function.valueOf(form.getContractFunction()).getDisplayName())
    );
  }

  @Test
  void getPreSelectedContractFunction_withManualEntry() {
    var form = AwardedContractTestUtil.createInfrastructureAwardedContractForm();
    var function = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my new function";
    form.setContractFunction(function);

    var preSelectedMap = awardedContractService.getPreSelectedContractFunction(form);
    assertThat(preSelectedMap).containsExactly(
        entry(form.getContractFunction(), SearchSelectorService.removePrefix(form.getContractFunction()))
    );
  }

  @Test
  void validate_partial() {
    var form = new InfrastructureAwardedContractForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    awardedContractService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  void validate_full() {
    var form = new InfrastructureAwardedContractForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    awardedContractService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void deleteAwardedContract() {
    var awardedContract = AwardedContractTestUtil.createAwardedContract();
    awardedContractService.deleteAwardedContract(awardedContract);
    verify(awardedContractRepository, times(1)).delete(awardedContract);
  }

  @Test
  void isValid_whenValid_thenTrue() {
    BindingResult bindingResult = new BeanPropertyBindingResult(AwardedContractFormCommon.class, "form");
    when(validationService.validate(any(), any(), any(ValidationType.class))).thenReturn(bindingResult);

    var awardedContract1 = AwardedContractTestUtil.createAwardedContract();
    var isValid = awardedContractService.isValid(awardedContract1, ValidationType.FULL);

    assertThat(isValid).isTrue();
  }

  @Test
  void isValid_whenInvalid_thenFalse() {
    BindingResult bindingResult = new BeanPropertyBindingResult(AwardedContractFormCommon.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(validationService.validate(any(), any(), any(ValidationType.class))).thenReturn(bindingResult);

    var awardedContract1 = AwardedContractTestUtil.createAwardedContract();
    awardedContract1.setContractorName(null);

    var isValid = awardedContractService.isValid(awardedContract1, ValidationType.FULL);

    assertThat(isValid).isFalse();
  }

  private void checkCommonFields(AwardedContractFormCommon form, AwardedContract awardedContract) {
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
}
