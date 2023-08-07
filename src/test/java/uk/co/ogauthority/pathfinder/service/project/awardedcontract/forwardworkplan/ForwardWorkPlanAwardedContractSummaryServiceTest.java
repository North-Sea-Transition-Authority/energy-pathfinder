package uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetup;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupRepository;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractSummaryService;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@ExtendWith(MockitoExtension.class)
class ForwardWorkPlanAwardedContractSummaryServiceTest {

  @Mock
  private ForwardWorkPlanAwardedContractService awardedContractService;

  @Mock
  private ForwardWorkPlanAwardedContractSetupRepository repository;

  @Mock
  private ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  @InjectMocks
  private ForwardWorkPlanAwardedContractSummaryService summaryService;

  @Captor
  private ArgumentCaptor<ForwardWorkPlanAwardedContractSetup> formCaptor;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  @Test
  void saveAwardedContractSummary_existingSetupFound() {
    var awardedContractSetup = AwardedContractTestUtil.createForwardWorkPlanAwardedContractSetup(detail);
    when(repository.findByProjectDetail(detail)).thenReturn(Optional.of(awardedContractSetup));

    var form = AwardedContractTestUtil.createForwardWorkPlanAwardedContractSummaryForm();

    summaryService.saveAwardedContractSummary(form, detail);
    verify(repository).save(formCaptor.capture());

    var result = formCaptor.getValue();
    assertThat(result.getHasOtherContractToAdd()).isTrue();
  }

  @Test
  void saveAwardedContractSummary_noExistingSetupFound() {
    when(repository.findByProjectDetail(detail)).thenReturn(Optional.empty());

    var form = AwardedContractTestUtil.createForwardWorkPlanAwardedContractSummaryForm();

    summaryService.saveAwardedContractSummary(form, detail);
    verify(repository).save(formCaptor.capture());

    var result = formCaptor.getValue();
    assertThat(result.getHasOtherContractToAdd()).isTrue();
  }

  @Test
  void getAwardedContractViews() {
    var awardedContract1 = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    var awardedContract2 = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();

    when(awardedContractService.getAwardedContracts(detail))
        .thenReturn(List.of(awardedContract1, awardedContract2));

    var awardedContractViews = summaryService.getAwardedContractViews(detail);
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
    when(awardedContractService.getAwardedContracts(detail))
        .thenReturn(List.of());

    var awardedContractViews = summaryService.getAwardedContractViews(detail);
    assertThat(awardedContractViews).isEmpty();
  }

  @Test
  void getValidatedAwardedContractViews() {
    var awardedContract1 = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    var awardedContract2 = AwardedContractTestUtil.createForwardWorkPlanAwardedContract_withManualEntryFunction("function");

    when(awardedContractService.getAwardedContracts(detail))
        .thenReturn(List.of(awardedContract1, awardedContract2));

    when(awardedContractService.isValid(awardedContract1, ValidationType.FULL)).thenReturn(true);
    when(awardedContractService.isValid(awardedContract2, ValidationType.FULL)).thenReturn(false);

    var awardedContractViews = summaryService.getValidatedAwardedContractViews(detail);
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
    final var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    final var displayOrder = 10;

    when(awardedContractService.getAwardedContract(awardedContract.getId(), awardedContract.getProjectDetail()))
        .thenReturn(awardedContract);

    var awardedContractView = summaryService.getAwardedContractView(
        awardedContract.getId(),
        awardedContract.getProjectDetail(),
        displayOrder
    );

    assertThat(awardedContractView.getDisplayOrder()).isEqualTo(displayOrder);
  }

  @Test
  void getAwardedContractViewErrors_whenErrors() {
    var awardedContractView1 = AwardedContractTestUtil.createForwardWorkPlanAwardedContractView(1);
    awardedContractView1.setIsValid(true);

    var displayOrderOfInvalidView = 2;
    var awardedContractView2 = AwardedContractTestUtil.createForwardWorkPlanAwardedContractView(displayOrderOfInvalidView);
    awardedContractView2.setIsValid(false);

    var errorItems = summaryService.getAwardedContractViewErrors(
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
}
