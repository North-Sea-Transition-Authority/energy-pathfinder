package uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormCommon;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractForm;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractRepository;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@ExtendWith(MockitoExtension.class)
class ForwardWorkPlanAwardedContractServiceTest {

  @Mock
  private ForwardWorkPlanAwardedContractRepository awardedContractRepository;

  @Mock
  private ValidationService validationService;

  @Mock
  private AwardedContractFormValidator validator;

  @Mock
  private TeamService teamService;

  private ForwardWorkPlanAwardedContractService awardedContractService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
  private final AuthenticatedUserAccount userAccount = UserTestingUtil.getAuthenticatedUserAccount();
  private final PortalOrganisationGroup portalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "org", "org");

  @BeforeEach
  void setUp() {
    var searchSelectorService = new SearchSelectorService();
    awardedContractService = new ForwardWorkPlanAwardedContractService(
        teamService,
        searchSelectorService,
        validationService,
        validator,
        awardedContractRepository
    );
  }

  @Test
  void getAwardedContracts() {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    when(awardedContractRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(List.of(awardedContract));

    var result = awardedContractService.getAwardedContracts(projectDetail);
    assertThat(result).containsExactly(awardedContract);
  }

  @Test
  void getAwardedContractsByProjectAndVersion() {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    var project = mock(Project.class);
    when(awardedContractRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, 1))
        .thenReturn(List.of(awardedContract));

    var result = awardedContractService.getAwardedContractsByProjectAndVersion(project,1);
    assertThat(result).containsExactly(awardedContract);
  }

  @Test
  void getAwardedContract() {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setId(10);
    when(awardedContractRepository.findByIdAndProjectDetail(10, projectDetail))
        .thenReturn(Optional.of(awardedContract));

    var result = awardedContractService.getAwardedContract(10, projectDetail);
    assertThat(result).isEqualTo(awardedContract);
  }

  @Test
  void getAwardedContract_noAwardedContractFound() {
    when(awardedContractRepository.findByIdAndProjectDetail(10, projectDetail))
        .thenReturn(Optional.empty());

    assertThrows(
        PathfinderEntityNotFoundException.class,
        () -> awardedContractService.getAwardedContract(10, projectDetail)
    );
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void hasAwardedContracts(boolean hasAwardedContracts) {
    when(awardedContractRepository.existsByProjectDetail(projectDetail)).thenReturn(hasAwardedContracts);
    var result = awardedContractService.hasAwardedContracts(projectDetail);
    assertThat(result).isEqualTo(hasAwardedContracts);
  }

  @Test
  void validate_partial() {
    var form = new ForwardWorkPlanAwardedContractForm();
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
    var form = new ForwardWorkPlanAwardedContractForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    awardedContractService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void createAwardedContract() {
    when(teamService.getContributorPortalOrganisationGroup(userAccount)).thenReturn(portalOrganisationGroup);
    when(awardedContractRepository.save(any(ForwardWorkPlanAwardedContract.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    var form = AwardedContractTestUtil.createForwardWorkPlanAwardedContractForm();
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
    when(awardedContractRepository.save(any(ForwardWorkPlanAwardedContract.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    var form = AwardedContractTestUtil.createForwardWorkPlanAwardedContractForm();
    var manualEntryFunction = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my new function";
    form.setContractFunction(manualEntryFunction);

    var awardedContract = awardedContractService.createAwardedContract(projectDetail, form, userAccount);

    checkCommonFields(form, awardedContract);
    assertThat(awardedContract.getManualContractFunction()).isEqualTo(SearchSelectorService.removePrefix(manualEntryFunction));
    assertThat(awardedContract.getContractFunction()).isNull();
    assertThat(awardedContract.getAddedByOrganisationGroup()).isEqualTo(portalOrganisationGroup.getOrgGrpId());
  }

  @Test
  void updateAwardedContract_whenEntityNotFound_exception() {
    when(awardedContractRepository.findByIdAndProjectDetail(any(), any())).thenReturn(Optional.empty());
    assertThrows(
        PathfinderEntityNotFoundException.class,
        () -> awardedContractService.updateAwardedContract(null, null, null));
  }

  @Test
  void updateAwardedContract() {
    var form = AwardedContractTestUtil.createForwardWorkPlanAwardedContractForm();
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setProjectDetail(projectDetail);

    when(awardedContractRepository.findByIdAndProjectDetail(any(), any())).thenReturn(Optional.of(awardedContract));
    when(awardedContractRepository.save(any(ForwardWorkPlanAwardedContract.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
    var savedAwardedContract = awardedContractService.updateAwardedContract(1, projectDetail, form);

    checkCommonFields(form, savedAwardedContract);
    assertThat(savedAwardedContract.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(savedAwardedContract.getContractFunction()).isEqualTo(Function.valueOf(form.getContractFunction()));
    assertThat(savedAwardedContract.getManualContractFunction()).isNull();
  }

  @Test
  void updateAwardedContract_withManualContractFunction() {
    var form = AwardedContractTestUtil.createForwardWorkPlanAwardedContractForm();
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setProjectDetail(projectDetail);

    var manualEntryFunction = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my new function";
    form.setContractFunction(manualEntryFunction);

    when(awardedContractRepository.findByIdAndProjectDetail(any(), any())).thenReturn(Optional.of(awardedContract));
    when(awardedContractRepository.save(any(ForwardWorkPlanAwardedContract.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
    var savedAwardedContract = awardedContractService.updateAwardedContract(1, projectDetail, form);

    checkCommonFields(form, savedAwardedContract);
    assertThat(savedAwardedContract.getManualContractFunction()).isEqualTo(SearchSelectorService.removePrefix(manualEntryFunction));
    assertThat(savedAwardedContract.getContractFunction()).isNull();
  }

  @Test
  void deleteAwardedContract() {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContractService.deleteAwardedContract(awardedContract);
    verify(awardedContractRepository).delete(awardedContract);
  }

  @Test
  void deleteAllByProjectDetail() {
    awardedContractService.deleteAllByProjectDetail(projectDetail);
    verify(awardedContractRepository).deleteAllByProjectDetail(projectDetail);
  }


  @Test
  void isValid_whenValid_thenTrue() {
    BindingResult bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanAwardedContractForm.class, "form");
    when(validationService.validate(any(), any(), any(ValidationType.class))).thenReturn(bindingResult);

    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();

    var isValid = awardedContractService.isValid(awardedContract, ValidationType.FULL);

    assertThat(isValid).isTrue();
  }

  @Test
  void isValid_whenInvalid_thenFalse() {
    BindingResult bindingResult = new BeanPropertyBindingResult(ForwardWorkPlanAwardedContractForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(validationService.validate(any(), any(), any(ValidationType.class))).thenReturn(bindingResult);

    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setContractorName(null);

    var isValid = awardedContractService.isValid(awardedContract, ValidationType.FULL);

    assertThat(isValid).isFalse();
  }

  @Test
  void getForm() {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    var form = awardedContractService.getForm(awardedContract);

    checkCommonFields(form, awardedContract);
    assertThat(form.getContractFunction()).isEqualTo(awardedContract.getContractFunction().name());
  }

  private void checkCommonFields(AwardedContractFormCommon form, ForwardWorkPlanAwardedContract awardedContract) {
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
