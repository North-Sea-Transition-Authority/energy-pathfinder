package uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetup;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupForm;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupRepository;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanAwardedContractSetupServiceTest {

  @Mock
  private ForwardWorkPlanAwardedContractSetupRepository repository;

  @InjectMocks
  private ForwardWorkPlanAwardedContractSetupService setupService;

  private final ProjectDetail  projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  @Captor
  private ArgumentCaptor<ForwardWorkPlanAwardedContractSetup> awardedContractSetupCaptor;

  @Test
  public void getForwardWorkPlanAwardedContractSetupFormFromDetail_noAwardedContractFound() {
    when(repository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    var form = setupService.getForwardWorkPlanAwardedContractSetupFormFromDetail(projectDetail);
    assertThat(form.getHasContractToAdd()).isNull();
  }

  @Test
  public void getForwardWorkPlanAwardedContractSetupFormFromDetail_awardedContractFound() {
    var awardedContractSetup = new ForwardWorkPlanAwardedContractSetup();
    awardedContractSetup.setHasContractToAdd(true);
    when(repository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(awardedContractSetup));

    var form = setupService.getForwardWorkPlanAwardedContractSetupFormFromDetail(projectDetail);
    assertThat(form.getHasContractToAdd()).isTrue();
  }

  @Test
  public void saveAwardedContractSetup_firstTimeSaving() {
    var form = new ForwardWorkPlanAwardedContractSetupForm();
    form.setHasContractToAdd(false);

    when(repository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    setupService.saveAwardedContractSetup(form, projectDetail);

    verify(repository).save(awardedContractSetupCaptor.capture());
    var awardedContractSetup = awardedContractSetupCaptor.getValue();
    assertThat(awardedContractSetup.getHasContractToAdd()).isFalse();
  }

  @Test
  public void saveAwardedContractSetup_updateExisting() {
    var form = new ForwardWorkPlanAwardedContractSetupForm();
    form.setHasContractToAdd(false);

    var awardedContractSetup = new ForwardWorkPlanAwardedContractSetup();
    awardedContractSetup.setHasContractToAdd(true);
    when(repository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(awardedContractSetup));

    setupService.saveAwardedContractSetup(form, projectDetail);

    verify(repository).save(awardedContractSetupCaptor.capture());
    var result = awardedContractSetupCaptor.getValue();
    assertThat(result.getHasContractToAdd()).isFalse();
  }

  @Test
  public void getForwardWorkPlanAwardedContractSetup_foundAwardedContractSetup() {
    var awardedContractSetup = new ForwardWorkPlanAwardedContractSetup();
    when(repository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(awardedContractSetup));

    var resultOptional = setupService.getForwardWorkPlanAwardedContractSetup(projectDetail);
    assertThat(resultOptional).isPresent();
    var result = resultOptional.get();
    assertThat(result).isEqualTo(awardedContractSetup);
  }

  @Test
  public void getForwardWorkPlanAwardedContractSetup_notFound() {
    when(repository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    var resultOptional = setupService.getForwardWorkPlanAwardedContractSetup(projectDetail);
    assertThat(resultOptional).isEmpty();
  }
}
