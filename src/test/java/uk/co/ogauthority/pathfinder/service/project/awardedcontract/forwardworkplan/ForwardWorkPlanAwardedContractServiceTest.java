package uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetup;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.testutil.ProjectFormSectionServiceTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanAwardedContractServiceTest {

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @Mock
  private ForwardWorkPlanAwardedContractSetupService setupService;

  @InjectMocks
  private ForwardWorkPlanAwardedContractService forwardWorkPlanAwardedContractService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  @Test
  public void isComplete_true() {
    when(setupService.isValid(projectDetail)).thenReturn(true);

    var result = forwardWorkPlanAwardedContractService.isComplete(projectDetail);
    assertThat(result).isTrue();
  }

  @Test
  public void isComplete_false() {
    when(setupService.isValid(projectDetail)).thenReturn(false);

    var result = forwardWorkPlanAwardedContractService.isComplete(projectDetail);
    assertThat(result).isFalse();
  }

  @Test
  public void isComplete_noAwardedContractSetupFound_returnFalse() {
    var result = forwardWorkPlanAwardedContractService.isComplete(projectDetail);
    assertThat(result).isFalse();
  }

  @Test
  public void copySectionData_successful() {
    var fromProjectDetail = projectDetail;
    fromProjectDetail.setVersion(1);

    var toProjectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    toProjectDetail.setVersion(2);

    var awardedContractSetup = new ForwardWorkPlanAwardedContractSetup();
    awardedContractSetup.setHasContractToAdd(true);
    when(setupService.getForwardWorkPlanAwardedContractSetup(projectDetail))
        .thenReturn(Optional.of(awardedContractSetup));

    forwardWorkPlanAwardedContractService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService).duplicateEntityAndSetNewParent(
        awardedContractSetup,
        toProjectDetail,
        ForwardWorkPlanAwardedContractSetup.class
    );
  }

  @Test
  public void copySectionData_whenNoAwardedContractSetupFound_thenDoNothing() {
    var fromProjectDetail = projectDetail;
    fromProjectDetail.setVersion(1);
    var toProjectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    toProjectDetail.setVersion(2);

    when(setupService.getForwardWorkPlanAwardedContractSetup(projectDetail))
        .thenReturn(Optional.empty());

    forwardWorkPlanAwardedContractService.copySectionData(fromProjectDetail, toProjectDetail);
    verify(entityDuplicationService, never()).duplicateEntityAndSetNewParent(any(), any(), any());
  }

  @Test
  public void getSupportedProjectTypes() {
    var result = forwardWorkPlanAwardedContractService.getSupportedProjectTypes();
    assertThat(result).containsExactly(ProjectType.FORWARD_WORK_PLAN);
  }

  @Test
  public void canShowInTaskList_whenValidProjectTypeAndUserRelationship_thenTrue() {
    var result = forwardWorkPlanAwardedContractService.canShowInTaskList(
        projectDetail,
        Set.of(UserToProjectRelationship.OPERATOR)
    );
    assertThat(result).isTrue();
  }

  @Test
  public void canShowInTaskList_userToProjectRelationshipSmokeTest() {
    ProjectFormSectionServiceTestUtil.canShowInTaskList_userToProjectRelationshipSmokeTest(
        forwardWorkPlanAwardedContractService,
        projectDetail,
        Set.of(UserToProjectRelationship.OPERATOR, UserToProjectRelationship.CONTRIBUTOR)
    );
  }

  @Test
  public void isTaskValidForProjectDetail_true() {
    assertThat(forwardWorkPlanAwardedContractService.isTaskValidForProjectDetail(projectDetail)).isTrue();
  }

  @Test
  public void isTaskValidForProjectDetail_false() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);
    assertThat(forwardWorkPlanAwardedContractService.isTaskValidForProjectDetail(projectDetail)).isFalse();
  }

}
