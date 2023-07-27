package uk.co.ogauthority.pathfinder.service.project.awardedcontract.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.InfrastructureAwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormCommon;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.infrastructure.InfrastructureAwardedContractForm;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.infrastructure.InfrastructureAwardedContractRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectFormSectionServiceTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@ExtendWith(MockitoExtension.class)
class InfrastructureAwardedContractServiceTest {

  @Mock
  private InfrastructureAwardedContractRepository awardedContractRepository;

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @Mock
  private TeamService teamService;

  @Mock
  private ValidationService validationService;

  @Mock
  private AwardedContractFormValidator formValidator;

  private InfrastructureAwardedContractService awardedContractService;

  @BeforeEach
  void setUp() {
    SearchSelectorService searchSelectorService = new SearchSelectorService();
    awardedContractService = new InfrastructureAwardedContractService(
        teamService,
        searchSelectorService,
        validationService,
        formValidator,
        projectSetupService,
        entityDuplicationService,
        awardedContractRepository
    );
  }

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount userAccount = UserTestingUtil.getAuthenticatedUserAccount();
  private final PortalOrganisationGroup portalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "org", "org");

  @Test
  void canShowInTaskList_true() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.AWARDED_CONTRACTS)).thenReturn(true);
    assertThat(awardedContractService.canShowInTaskList(detail, Set.of(UserToProjectRelationship.OPERATOR))).isTrue();
  }

  @Test
  void canShowInTaskList_false() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.AWARDED_CONTRACTS)).thenReturn(false);
    assertThat(awardedContractService.canShowInTaskList(detail, Set.of(UserToProjectRelationship.OPERATOR))).isFalse();
  }

  @Test
  void canShowInTaskList_userToProjectRelationshipSmokeTest() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.AWARDED_CONTRACTS)).thenReturn(true);
    ProjectFormSectionServiceTestUtil.canShowInTaskList_userToProjectRelationshipSmokeTest(
        awardedContractService,
        detail,
        Set.of(UserToProjectRelationship.OPERATOR, UserToProjectRelationship.CONTRIBUTOR)
    );
  }

  @Test
  void isTaskValidForProjectDetail_true() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.AWARDED_CONTRACTS)).thenReturn(true);
    assertThat(awardedContractService.isTaskValidForProjectDetail(detail)).isTrue();
  }

  @Test
  void isTaskValidForProjectDetail_false() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.AWARDED_CONTRACTS)).thenReturn(false);
    assertThat(awardedContractService.isTaskValidForProjectDetail(detail)).isFalse();
  }

  @Test
  void removeSectionData_verifyInteractions() {
    awardedContractService.removeSectionData(detail);

    verify(awardedContractRepository, times(1)).deleteAllByProjectDetail(detail);
  }

  @Test
  void copySectionData_verifyDuplicationServiceInteraction() {
    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);
    final var awardedContracts = List.of(AwardedContractTestUtil.createInfrastructureAwardedContract());

    when(awardedContractService.getAwardedContracts(fromProjectDetail))
        .thenReturn(awardedContracts);

    awardedContractService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        awardedContracts,
        toProjectDetail,
        InfrastructureAwardedContract.class
    );
  }

  @Test
  void getSupportedProjectTypes_verifyInfrastructure() {
    assertThat(awardedContractService.getSupportedProjectTypes()).containsExactly(ProjectType.INFRASTRUCTURE);
  }

  @Test
  void alwaysCopySectionData_verifyFalse() {
    assertThat(awardedContractService.alwaysCopySectionData(detail)).isFalse();
  }

  @Test
  void allowSectionDataCleanUp_verifyIsTrue() {
    final var allowSectionDateCleanUp = awardedContractService.allowSectionDataCleanUp(detail);
    assertThat(allowSectionDateCleanUp).isTrue();
  }

  @Test
  void isComplete_whenValid_thenTrue() {
    BindingResult bindingResult = new BeanPropertyBindingResult(InfrastructureAwardedContractForm.class, "form");
    when(validationService.validate(any(), any(), any(ValidationType.class))).thenReturn(bindingResult);

    var awardedContract1 = AwardedContractTestUtil.createInfrastructureAwardedContract();
    var awardedContract2 = AwardedContractTestUtil.createInfrastructureAwardedContract();
    var projectDetail = awardedContract1.getProjectDetail();
    awardedContract2.setProjectDetail(projectDetail);
    awardedContract1.setId(1);
    awardedContract2.setId(2);

    when(awardedContractService.getAwardedContracts(projectDetail)).thenReturn(
        List.of(awardedContract1, awardedContract2)
    );

    var isComplete = awardedContractService.isComplete(projectDetail);
    assertThat(isComplete).isTrue();
  }

  @Test
  void isComplete_whenInvalid_thenFalse() {
    BindingResult bindingResult = new BeanPropertyBindingResult(InfrastructureAwardedContractForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(validationService.validate(any(), any(), any(ValidationType.class))).thenReturn(bindingResult);

    var awardedContract1 = AwardedContractTestUtil.createInfrastructureAwardedContract();
    var awardedContract2 = AwardedContractTestUtil.createInfrastructureAwardedContract();
    awardedContract2.setContractorName(null);

    var projectDetail = awardedContract1.getProjectDetail();

    when(awardedContractService.getAwardedContracts(projectDetail)).thenReturn(
        List.of(awardedContract1, awardedContract2)
    );

    var isComplete = awardedContractService.isComplete(projectDetail);
    assertThat(isComplete).isFalse();
  }

  @Test
  void isComplete_whenNoAwardedContracts_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails();

    when(awardedContractService.getAwardedContracts(projectDetail)).thenReturn(
        Collections.emptyList()
    );

    var isComplete = awardedContractService.isComplete(projectDetail);
    assertThat(isComplete).isFalse();
  }

  @Test
  void getForm() {
    var awardedContract = AwardedContractTestUtil.createInfrastructureAwardedContract();

    var form = awardedContractService.getForm(awardedContract);
    checkCommonFields(form, awardedContract);
    assertThat(form.getContractFunction()).isEqualTo(awardedContract.getContractFunction().name());
  }

  @Test
  void getForm_whenEntityProvided() {
    var awardedContract = AwardedContractTestUtil.createInfrastructureAwardedContract();
    var form = awardedContractService.getForm(awardedContract);
    checkCommonFields(form, awardedContract);
  }

  @Test
  void getForm_withManualEntryFunction() {
    final String manualEntryFunction = "My new function";
    final String manualEntryFunctionWithPrefix = SearchSelectorService.getValueWithManualEntryPrefix(manualEntryFunction);

    var awardedContract = AwardedContractTestUtil.createInfrastructureAwardedContract_withManualEntryFunction(manualEntryFunction);

    var form = awardedContractService.getForm(awardedContract);
    checkCommonFields(form, awardedContract);
    assertThat(form.getContractFunction()).isEqualTo(manualEntryFunctionWithPrefix);
  }

  @Test
  void getAwardedContracts() {
    var awardedContracts = List.of(
        AwardedContractTestUtil.createInfrastructureAwardedContract(),
        AwardedContractTestUtil.createInfrastructureAwardedContract()
    );

    when(awardedContractRepository.findByProjectDetailOrderByIdAsc(detail)).thenReturn(awardedContracts);

    assertThat(awardedContractService.getAwardedContracts(detail)).isEqualTo(awardedContracts);
  }

  @Test
  void getAwardedContractsByProjectAndVersion() {
    var project = detail.getProject();
    var version = detail.getVersion();
    var awardedContracts = List.of(
        AwardedContractTestUtil.createInfrastructureAwardedContract(),
        AwardedContractTestUtil.createInfrastructureAwardedContract()
    );

    when(awardedContractRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version))
        .thenReturn(awardedContracts);

    assertThat(awardedContractService.getAwardedContractsByProjectAndVersion(project, version)).isEqualTo(awardedContracts);
  }

  @Test
  void createAwardedContract() {
    when(teamService.getContributorPortalOrganisationGroup(userAccount)).thenReturn(portalOrganisationGroup);
    when(awardedContractRepository.save(any(InfrastructureAwardedContract.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

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
    when(awardedContractRepository.save(any(InfrastructureAwardedContract.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
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
    var awardedContract = new InfrastructureAwardedContract(projectDetail);

    when(awardedContractRepository.findByIdAndProjectDetail(any(), any())).thenReturn(Optional.of(awardedContract));
    when(awardedContractRepository.save(any(InfrastructureAwardedContract.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
    var savedAwardedContract = awardedContractService.updateAwardedContract(1, projectDetail, form);

    checkCommonFields(form, savedAwardedContract);
    assertThat(savedAwardedContract.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(savedAwardedContract.getContractFunction()).isEqualTo(Function.valueOf(form.getContractFunction()));
    assertThat(savedAwardedContract.getManualContractFunction()).isNull();
  }

  @Test
  void updateAwardedContract_withManualContractFunction() {
    when(awardedContractRepository.save(any(InfrastructureAwardedContract.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
    var form = AwardedContractTestUtil.createInfrastructureAwardedContractForm();

    var manualEntryFunction = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my new function";
    form.setContractFunction(manualEntryFunction);

    var projectDetail = ProjectUtil.getProjectDetails();
    var awardedContract = new InfrastructureAwardedContract(projectDetail);

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
    var awardedContract = AwardedContractTestUtil.createInfrastructureAwardedContract();
    awardedContractService.deleteAwardedContract(awardedContract);
    verify(awardedContractRepository, times(1)).delete(awardedContract);
  }

  @Test
  void isValid_whenValid_thenTrue() {
    BindingResult bindingResult = new BeanPropertyBindingResult(AwardedContractFormCommon.class, "form");
    when(validationService.validate(any(), any(), any(ValidationType.class))).thenReturn(bindingResult);

    var awardedContract = AwardedContractTestUtil.createInfrastructureAwardedContract();
    var isValid = awardedContractService.isValid(awardedContract, ValidationType.FULL);

    assertThat(isValid).isTrue();
  }

  @Test
  void isValid_whenInvalid_thenFalse() {
    BindingResult bindingResult = new BeanPropertyBindingResult(AwardedContractFormCommon.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(validationService.validate(any(), any(), any(ValidationType.class))).thenReturn(bindingResult);

    var awardedContract = AwardedContractTestUtil.createInfrastructureAwardedContract();
    awardedContract.setContractorName(null);

    var isValid = awardedContractService.isValid(awardedContract, ValidationType.FULL);

    assertThat(isValid).isFalse();
  }

  private void checkCommonFields(AwardedContractFormCommon form, InfrastructureAwardedContract awardedContract) {
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
