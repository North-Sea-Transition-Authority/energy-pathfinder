package uk.co.ogauthority.pathfinder.service.project.awardedcontract.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractSummaryService;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@ExtendWith(MockitoExtension.class)
class InfrastructureAwardedContractSummaryServiceTest {

  @Mock
  private InfrastructureAwardedContractService awardedContractService;

  @Mock
  private ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  private InfrastructureAwardedContractSummaryService awardedContractSummaryService;

  @BeforeEach
  void setUp() {
    awardedContractSummaryService = new InfrastructureAwardedContractSummaryService(
        projectSectionItemOwnershipService,
        portalOrganisationAccessor,
        awardedContractService
    );
  }

  @Test
  void getAwardedContractViews() {
    var awardedContract1 = AwardedContractTestUtil.createInfrastructureAwardedContract();
    var awardedContract2 = AwardedContractTestUtil.createInfrastructureAwardedContract();
    var projectDetail = awardedContract1.getProjectDetail();

    when(awardedContractService.getAwardedContracts(projectDetail))
        .thenReturn(List.of(awardedContract1, awardedContract2));

    var awardedContractViews = awardedContractSummaryService.getAwardedContractViews(projectDetail);
    assertThat(awardedContractViews).hasSize(2);

    var awardedContractView1 = awardedContractViews.get(0);
    assertThat(awardedContractView1.getDisplayOrder()).isEqualTo(1);
    assertThat(awardedContractView1.isValid()).isTrue();

    var awardedContractView2 = awardedContractViews.get(1);
    assertThat(awardedContractView2.getDisplayOrder()).isEqualTo(2);
    assertThat(awardedContractView2.isValid()).isTrue();
  }

  @Test
  void getAwardedContractViews_whenNoAwardedContracts_thenEmpty() {
    var projectDetail = ProjectUtil.getProjectDetails();

    when(awardedContractService.getAwardedContracts(projectDetail))
        .thenReturn(List.of());

    var awardedContractViews = awardedContractSummaryService.getAwardedContractViews(projectDetail);
    assertThat(awardedContractViews).isEmpty();
  }

  @Test
  void getValidatedAwardedContractViews() {
    var awardedContract1 = AwardedContractTestUtil.createInfrastructureAwardedContract();
    var awardedContract2 = AwardedContractTestUtil.createInfrastructureAwardedContract_withManualEntryFunction("function");

    var projectDetail = awardedContract1.getProjectDetail();

    when(awardedContractService.getAwardedContracts(projectDetail))
        .thenReturn(List.of(awardedContract1, awardedContract2));

    when(awardedContractService.isValid(awardedContract1, ValidationType.FULL)).thenReturn(true);
    when(awardedContractService.isValid(awardedContract2, ValidationType.FULL)).thenReturn(false);

    var awardedContractViews = awardedContractSummaryService.getValidatedAwardedContractViews(projectDetail);
    assertThat(awardedContractViews).hasSize(2);

    var awardedContractView1 = awardedContractViews.get(0);
    assertThat(awardedContractView1.getDisplayOrder()).isEqualTo(1);
    assertThat(awardedContractView1.isValid()).isTrue();

    var awardedContractView2 = awardedContractViews.get(1);
    assertThat(awardedContractView2.getDisplayOrder()).isEqualTo(2);
    assertThat(awardedContractView2.isValid()).isFalse();
  }

  @Test
  void getAwardedContractView() {
    final var awardedContract = AwardedContractTestUtil.createInfrastructureAwardedContract();
    final var displayOrder = 10;

    when(awardedContractService.getAwardedContract(awardedContract.getId(), awardedContract.getProjectDetail()))
        .thenReturn(awardedContract);

    var awardedContractView = awardedContractSummaryService.getAwardedContractView(
        awardedContract.getId(),
        awardedContract.getProjectDetail(),
        displayOrder
    );

    assertThat(awardedContractView.getDisplayOrder()).isEqualTo(displayOrder);
  }

  @Test
  void getAwardedContractViewErrors_whenErrors() {
    var awardedContractView1 = AwardedContractTestUtil.createInfrastructureAwardedContractView(1);
    awardedContractView1.setIsValid(true);

    final var displayOrderOfInvalidView = 2;
    var awardedContractView2 = AwardedContractTestUtil.createInfrastructureAwardedContractView(displayOrderOfInvalidView);
    awardedContractView2.setIsValid(false);

    var errorItems = awardedContractSummaryService.getAwardedContractViewErrors(
        List.of(awardedContractView1, awardedContractView2)
    );

    assertThat(errorItems).hasSize(1);

    var errorItem = errorItems.get(0);
    assertThat(errorItem.getFieldName()).isEqualTo(
        String.format(AwardedContractSummaryService.ERROR_FIELD_NAME, displayOrderOfInvalidView)
    );
    assertThat(errorItem.getErrorMessage()).isEqualTo(
        String.format(AwardedContractSummaryService.ERROR_MESSAGE, displayOrderOfInvalidView)
    );
    assertThat(errorItem.getDisplayOrder()).isEqualTo(displayOrderOfInvalidView);
  }

  @Test
  void getAwardedContractViewErrors_whenNoErrors() {

    var awardedContractView1 = AwardedContractTestUtil.createInfrastructureAwardedContractView(1);
    awardedContractView1.setIsValid(true);

    var awardedContractView2 = AwardedContractTestUtil.createInfrastructureAwardedContractView(2);
    awardedContractView2.setIsValid(true);

    var errorItems = awardedContractSummaryService.getAwardedContractViewErrors(
        List.of(awardedContractView1, awardedContractView2)
    );

    assertThat(errorItems).isEmpty();
  }

  @Test
  void areAllAwardedContractsValid_whenValid_thenTrue() {
    var awardedContractView1 = AwardedContractTestUtil.createInfrastructureAwardedContractView(1);
    awardedContractView1.setIsValid(true);

    var awardedContractView2 = AwardedContractTestUtil.createInfrastructureAwardedContractView(2);
    awardedContractView2.setIsValid(true);

    var allValid = awardedContractSummaryService.validateViews(
        List.of(awardedContractView1, awardedContractView2)
    );
    assertThat(allValid).isEqualTo(ValidationResult.VALID);
  }

  @Test
  void areAllAwardedContractsValid_whenInvalid_thenFalse() {
    var awardedContractView1 = AwardedContractTestUtil.createInfrastructureAwardedContractView(1);
    awardedContractView1.setIsValid(true);

    var awardedContractView2 = AwardedContractTestUtil.createInfrastructureAwardedContractView(2);
    awardedContractView2.setIsValid(false);

    var allValid = awardedContractSummaryService.validateViews(
        List.of(awardedContractView1, awardedContractView2)
    );
    assertThat(allValid).isEqualTo(ValidationResult.INVALID);
  }

  @Test
  void getErrors_emptyList() {
    var errors = awardedContractSummaryService.getAwardedContractViewErrors(Collections.emptyList());
    assertThat(errors).hasSize(1);
    assertThat(errors.get(0).getDisplayOrder()).isEqualTo(1);
    assertThat(errors.get(0).getFieldName()).isEqualTo(AwardedContractSummaryService.EMPTY_LIST_ERROR);
    assertThat(errors.get(0).getErrorMessage()).isEqualTo(AwardedContractSummaryService.EMPTY_LIST_ERROR);
  }
}
