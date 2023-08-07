package uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContract;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetup;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectFormSectionServiceTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@ExtendWith(MockitoExtension.class)
class ForwardWorkPlanAwardedContractFormSectionServiceTest {

  @Mock
  private ForwardWorkPlanAwardedContractSetupService setupService;

  @Mock
  private ForwardWorkPlanAwardedContractSummaryService summaryService;

  @Mock
  private ForwardWorkPlanAwardedContractService awardedContractService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @InjectMocks
  private ForwardWorkPlanAwardedContractFormSectionService formSectionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  @Test
  void isComplete_hasContractsToAdd_hasFinishedAddingContracts() {
    var awardedContractSetup = AwardedContractTestUtil.createForwardWorkPlanAwardedContractSetup(projectDetail);
    awardedContractSetup.setHasOtherContractToAdd(false);
    when(setupService.getForwardWorkPlanAwardedContractSetup(projectDetail))
        .thenReturn(Optional.of(awardedContractSetup));

    var awardedContractView = AwardedContractTestUtil.createForwardWorkPlanAwardedContractView(1);
    when(summaryService.getValidatedAwardedContractViews(projectDetail))
        .thenReturn(List.of(awardedContractView));

    when(summaryService.validateViews(List.of(awardedContractView))).thenReturn(ValidationResult.VALID);

    var result = formSectionService.isComplete(projectDetail);
    assertThat(result).isTrue();
  }

  @Test
  void isComplete_hasNoContractsToAdd() {
    var awardedContractSetup = AwardedContractTestUtil.createForwardWorkPlanAwardedContractSetup(projectDetail);
    awardedContractSetup.setHasContractToAdd(false);
    when(setupService.getForwardWorkPlanAwardedContractSetup(projectDetail))
        .thenReturn(Optional.of(awardedContractSetup));

    var result = formSectionService.isComplete(projectDetail);
    assertThat(result).isTrue();
  }

  @Test
  void isComplete_hasContractsToAdd_hasInvalidContracts() {
    var awardedContractSetup = AwardedContractTestUtil.createForwardWorkPlanAwardedContractSetup(projectDetail);
    awardedContractSetup.setHasOtherContractToAdd(false);
    when(setupService.getForwardWorkPlanAwardedContractSetup(projectDetail))
        .thenReturn(Optional.of(awardedContractSetup));

    var awardedContractView = AwardedContractTestUtil.createForwardWorkPlanAwardedContractView(1);
    when(summaryService.getValidatedAwardedContractViews(projectDetail))
        .thenReturn(List.of(awardedContractView));

    when(summaryService.validateViews(List.of(awardedContractView))).thenReturn(ValidationResult.INVALID);

    var result = formSectionService.isComplete(projectDetail);
    assertThat(result).isFalse();
  }

  @Test
  void isComplete_hasContractsToAdd_hasMoreContractsToAdd() {
    var awardedContractSetup = AwardedContractTestUtil.createForwardWorkPlanAwardedContractSetup(projectDetail);
    awardedContractSetup.setHasOtherContractToAdd(true);
    when(setupService.getForwardWorkPlanAwardedContractSetup(projectDetail))
        .thenReturn(Optional.of(awardedContractSetup));

    var awardedContractView = AwardedContractTestUtil.createForwardWorkPlanAwardedContractView(1);
    when(summaryService.getValidatedAwardedContractViews(projectDetail))
        .thenReturn(List.of(awardedContractView));

    when(summaryService.validateViews(List.of(awardedContractView))).thenReturn(ValidationResult.VALID);

    var result = formSectionService.isComplete(projectDetail);
    assertThat(result).isFalse();
  }

  @Test
  void isComplete_hasContractsToAdd_hasNotAddedContracts() {
    var awardedContractSetup = AwardedContractTestUtil.createForwardWorkPlanAwardedContractSetup(projectDetail);
    awardedContractSetup.setHasOtherContractToAdd(false);
    when(setupService.getForwardWorkPlanAwardedContractSetup(projectDetail))
        .thenReturn(Optional.of(awardedContractSetup));

    when(summaryService.getValidatedAwardedContractViews(projectDetail))
        .thenReturn(Collections.emptyList());

    when(summaryService.validateViews(Collections.emptyList())).thenReturn(ValidationResult.INVALID);

    var result = formSectionService.isComplete(projectDetail);
    assertThat(result).isFalse();
  }

  @Test
  void isComplete_noAwardedContractSetupFound() {
    when(setupService.getForwardWorkPlanAwardedContractSetup(projectDetail))
        .thenReturn(Optional.empty());

    var result = formSectionService.isComplete(projectDetail);
    assertThat(result).isFalse();
  }

  @Test
  void copySectionData_withAwardedContracts_successful() {
    var fromProjectDetail = projectDetail;
    fromProjectDetail.setVersion(1);

    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setProjectDetail(fromProjectDetail);
    when(awardedContractService.getAwardedContracts(fromProjectDetail)).thenReturn(List.of(awardedContract));

    var toProjectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    toProjectDetail.setVersion(2);

    var awardedContractSetup = AwardedContractTestUtil.createForwardWorkPlanAwardedContractSetup(fromProjectDetail);
    when(setupService.getForwardWorkPlanAwardedContractSetup(projectDetail))
        .thenReturn(Optional.of(awardedContractSetup));

    formSectionService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService).duplicateEntityAndSetNewParent(
        awardedContractSetup,
        toProjectDetail,
        ForwardWorkPlanAwardedContractSetup.class
    );

    verify(entityDuplicationService).duplicateEntitiesAndSetNewParent(
        List.of(awardedContract),
        toProjectDetail,
        ForwardWorkPlanAwardedContract.class
    );
  }

  @Test
  void copySectionData_withoutAwardedContracts_successful() {
    var fromProjectDetail = projectDetail;
    fromProjectDetail.setVersion(1);

    when(awardedContractService.getAwardedContracts(fromProjectDetail)).thenReturn(Collections.emptyList());

    var toProjectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    toProjectDetail.setVersion(2);

    var awardedContractSetup = AwardedContractTestUtil.createForwardWorkPlanAwardedContractSetup(fromProjectDetail);
    awardedContractSetup.setHasContractToAdd(false);
    when(setupService.getForwardWorkPlanAwardedContractSetup(projectDetail))
        .thenReturn(Optional.of(awardedContractSetup));

    formSectionService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService).duplicateEntityAndSetNewParent(
        awardedContractSetup,
        toProjectDetail,
        ForwardWorkPlanAwardedContractSetup.class
    );

    verify(entityDuplicationService, never()).duplicateEntitiesAndSetNewParent(any(), any(), any());
  }

  @Test
  void copySectionData_whenNoAwardedContractSetupFound_thenDoNothing() {
    var fromProjectDetail = projectDetail;
    fromProjectDetail.setVersion(1);
    var toProjectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    toProjectDetail.setVersion(2);

    when(setupService.getForwardWorkPlanAwardedContractSetup(projectDetail))
        .thenReturn(Optional.empty());

    formSectionService.copySectionData(fromProjectDetail, toProjectDetail);
    verify(entityDuplicationService, never()).duplicateEntityAndSetNewParent(any(), any(), any());
    verify(entityDuplicationService, never()).duplicateEntitiesAndSetNewParent(any(), any(), any());
  }

  @Test
  void getSupportedProjectTypes() {
    var result = formSectionService.getSupportedProjectTypes();
    assertThat(result).containsExactly(ProjectType.FORWARD_WORK_PLAN);
  }

  @Test
  void canShowInTaskList_whenValidProjectTypeAndUserRelationship_thenTrue() {
    var result = formSectionService.canShowInTaskList(
        projectDetail,
        Set.of(UserToProjectRelationship.OPERATOR)
    );
    assertThat(result).isTrue();
  }

  @Test
  void canShowInTaskList_userToProjectRelationshipSmokeTest() {
    ProjectFormSectionServiceTestUtil.canShowInTaskList_userToProjectRelationshipSmokeTest(
        formSectionService,
        projectDetail,
        Set.of(UserToProjectRelationship.OPERATOR, UserToProjectRelationship.CONTRIBUTOR)
    );
  }

  @Test
  void isTaskValidForProjectDetail_true() {
    assertThat(formSectionService.isTaskValidForProjectDetail(projectDetail)).isTrue();
  }

  @Test
  void isTaskValidForProjectDetail_false() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);
    assertThat(formSectionService.isTaskValidForProjectDetail(projectDetail)).isFalse();
  }

  @Test
  void removeSectionData() {
    formSectionService.removeSectionData(projectDetail);
    verify(setupService).deleteAllByProjectDetail(projectDetail);
    verify(awardedContractService).deleteAllByProjectDetail(projectDetail);
  }
}
