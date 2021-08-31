package uk.co.ogauthority.pathfinder.service.quarterlystatistics;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.model.entity.quarterlystatistics.ReportableProject;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.quarterlystatistics.ReportableProjectRepository;
import uk.co.ogauthority.pathfinder.testutil.ReportableProjectTestUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ReportableProjectServiceTest {

  @Mock
  private ReportableProjectRepository reportableProjectRepository;

  private ReportableProjectService reportableProjectService;

  @Before
  public void setup() {
    reportableProjectService = new ReportableProjectService(reportableProjectRepository);
  }

  @Test
  public void getReportableProjectViews_whenNoResults_thenEmptyList() {
    when(reportableProjectRepository.findAll()).thenReturn(Collections.emptyList());
    final var reportableProjectViews = reportableProjectService.getReportableProjectViews();
    assertThat(reportableProjectViews).isEmpty();
  }

  @Test
  public void getReportableProjectViews_whenResults_thenPopulatedList() {
    final var reportableProject1 = ReportableProjectTestUtil.createReportableProject(FieldStage.DEVELOPMENT);
    final var reportableProject2 = ReportableProjectTestUtil.createReportableProject(FieldStage.DECOMMISSIONING);
    final var reportableProject3 = ReportableProjectTestUtil.createReportableProject(FieldStage.DEVELOPMENT);

    when(reportableProjectRepository.findAll()).thenReturn(List.of(reportableProject1, reportableProject2, reportableProject3));

    final var reportableProjectViews = reportableProjectService.getReportableProjectViews();

    assertThat(reportableProjectViews).containsExactly(
        new ReportableProjectView(reportableProject1),
        new ReportableProjectView(reportableProject2),
        new ReportableProjectView(reportableProject3)
    );

  }

  @Test
  public void getReportableProjectViews_confirmDerivedField_whenUpdatedInQuarter() {

    var reportableProject = ReportableProjectTestUtil.createReportableProject(FieldStage.DEVELOPMENT);

    final var timeInCurrentQuarter = DateUtil.getQuarterFromLocalDate(LocalDate.now()).getEndDateAsInstant();
    reportableProject.setLastUpdatedDatetime(timeInCurrentQuarter);

    when(reportableProjectRepository.findAll()).thenReturn(List.of(reportableProject));

    final var reportableProjectViews = reportableProjectService.getReportableProjectViews();
    assertThat(reportableProjectViews).hasSize(1);

    final var reportableProjectView = reportableProjectViews.get(0);

    assertCommonDerivedFields(reportableProjectView, reportableProject);
    assertThat(reportableProjectView.hasUpdateInQuarter()).isTrue();
  }

  @Test
  public void getReportableProjectViews_confirmDerivedField_whenNoUpdateInQuarter() {

    var reportableProject = ReportableProjectTestUtil.createReportableProject(FieldStage.DEVELOPMENT);
    reportableProject.setLastUpdatedDatetime(Instant.now().minus(200, ChronoUnit.DAYS));

    when(reportableProjectRepository.findAll()).thenReturn(List.of(reportableProject));

    final var reportableProjectViews = reportableProjectService.getReportableProjectViews();
    assertThat(reportableProjectViews).hasSize(1);

    final var reportableProjectView = reportableProjectViews.get(0);

    assertCommonDerivedFields(reportableProjectView, reportableProject);
    assertThat(reportableProjectView.hasUpdateInQuarter()).isFalse();
  }

  @Test
  public void getReportableProjectsUpdatedBetween_whenNoResults_thenEmptyListReturned() {
    when(reportableProjectRepository.findByLastUpdatedDatetimeBetween(any(), any())).thenReturn(Collections.emptyList());
    final var reportableProjects = reportableProjectService.getReportableProjectsUpdatedBetween(
        Instant.now(),
        Instant.now()
    );
    assertThat(reportableProjects).isEmpty();
  }

  @Test
  public void getReportableProjectsUpdatedBetween_whenResults_thenEmptyListReturned() {

    final var reportableProject = ReportableProjectTestUtil.createReportableProject(FieldStage.DISCOVERY);
    when(reportableProjectRepository.findByLastUpdatedDatetimeBetween(any(), any())).thenReturn(List.of(reportableProject));

    final var reportableProjects = reportableProjectService.getReportableProjectsUpdatedBetween(
        Instant.now(),
        Instant.now()
    );
    assertThat(reportableProjects).containsExactly(reportableProject);
  }

  @Test
  public void getReportableProjects_whenNoReportableProjectsFound_thenReturnEmptyList() {
    when(reportableProjectRepository.findAll()).thenReturn(Collections.emptyList());
    var resultingReportableProjects = reportableProjectService.getReportableProjects();
    assertThat(resultingReportableProjects).isEmpty();
  }

  @Test
  public void getReportableProjects_whenReportableProjectsFound_thenReturnPopulatedList() {

    var expectedReportableProjects = List.of(
        ReportableProjectTestUtil.createReportableProject(FieldStage.DEVELOPMENT)
    );

    when(reportableProjectRepository.findAll()).thenReturn(expectedReportableProjects);

    var resultingReportableProjects = reportableProjectService.getReportableProjects();

    assertThat(resultingReportableProjects).isEqualTo(expectedReportableProjects);
  }

  private void assertCommonDerivedFields(ReportableProjectView reportableProjectView, ReportableProject reportableProject) {
    assertThat(reportableProjectView.getViewProjectUrl()).isEqualTo(
        ReverseRouter.route(on(ManageProjectController.class).getProject(
            reportableProject.getProjectId(),
            null,
            null,
            null
        ))
    );
    assertThat(reportableProjectView.getLastUpdatedDatetimeFormatted()).isEqualTo(
        DateUtil.formatInstant(reportableProject.getLastUpdatedDatetime())
    );
  }

}