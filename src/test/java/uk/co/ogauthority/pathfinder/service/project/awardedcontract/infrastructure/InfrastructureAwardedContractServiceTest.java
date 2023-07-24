package uk.co.ogauthority.pathfinder.service.project.awardedcontract.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.AwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.infrastructure.AwardedContractRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractServiceCommon;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectFormSectionServiceTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@ExtendWith(MockitoExtension.class)
public class InfrastructureAwardedContractServiceTest {

  @Mock
  private AwardedContractRepository awardedContractRepository;

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @Mock
  private AwardedContractServiceCommon awardedContractServiceCommon;

  private InfrastructureAwardedContractService awardedContractService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @BeforeEach
  void setup() {
    awardedContractService = new InfrastructureAwardedContractService(
        projectSetupService,
        entityDuplicationService,
        awardedContractServiceCommon,
        awardedContractRepository
    );
  }

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
    final var awardedContracts = List.of(AwardedContractTestUtil.createAwardedContract());

    when(awardedContractServiceCommon.getAwardedContracts(fromProjectDetail))
        .thenReturn(awardedContracts);

    awardedContractService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        awardedContracts,
        toProjectDetail,
        AwardedContract.class
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
    var awardedContract1 = AwardedContractTestUtil.createAwardedContract();
    var awardedContract2 = AwardedContractTestUtil.createAwardedContract();
    var projectDetail = awardedContract1.getProjectDetail();

    when(awardedContractServiceCommon.getAwardedContracts(projectDetail)).thenReturn(
        List.of(awardedContract1, awardedContract2)
    );

    when(awardedContractServiceCommon.isValid(awardedContract1, ValidationType.FULL)).thenReturn(true);
    when(awardedContractServiceCommon.isValid(awardedContract2, ValidationType.FULL)).thenReturn(true);

    var isComplete = awardedContractService.isComplete(projectDetail);
    assertThat(isComplete).isTrue();
  }

  @Test
  void isComplete_whenInvalid_thenFalse() {
    var awardedContract1 = AwardedContractTestUtil.createAwardedContract();
    var awardedContract2 = AwardedContractTestUtil.createAwardedContract();
    awardedContract2.setContractorName(null);

    var projectDetail = awardedContract1.getProjectDetail();

    when(awardedContractServiceCommon.getAwardedContracts(projectDetail)).thenReturn(
        List.of(awardedContract1, awardedContract2)
    );

    var isComplete = awardedContractService.isComplete(projectDetail);
    assertThat(isComplete).isFalse();
  }

  @Test
  void isComplete_whenNoAwardedContracts_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails();

    when(awardedContractServiceCommon.getAwardedContracts(projectDetail)).thenReturn(
        Collections.emptyList()
    );

    var isComplete = awardedContractService.isComplete(projectDetail);
    assertThat(isComplete).isFalse();
  }
}
