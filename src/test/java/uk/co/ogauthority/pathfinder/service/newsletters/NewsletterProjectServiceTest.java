package uk.co.ogauthority.pathfinder.service.newsletters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.quarterlystatistics.ReportableProject;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.service.quarterlystatistics.ReportableProjectService;
import uk.co.ogauthority.pathfinder.testutil.ReportableProjectTestUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class NewsletterProjectServiceTest {

  @Mock
  private ReportableProjectService reportableProjectService;

  private NewsletterProjectService newsletterProjectService;

  @Before
  public void setup() {
    newsletterProjectService = new NewsletterProjectService(reportableProjectService);
  }

  @Test
  public void getProjectsUpdatedInTheLastMonth_whenNoProjects_thenEmptyList() {
    when(reportableProjectService.getReportableProjectsUpdatedBetween(any(), any()))
        .thenReturn(Collections.emptyList());

    final var reportableProjects = newsletterProjectService.getProjectsUpdatedInTheLastMonth();
    assertThat(reportableProjects).isEmpty();
  }

  @Test
  public void getProjectsUpdatedInTheLastMonth_whenProjectsExist_verifyReturnFormatAndInteractions() {

    final var reportableProject = ReportableProjectTestUtil.createReportableProject(FieldStage.DECOMMISSIONING);
    when(reportableProjectService.getReportableProjectsUpdatedBetween(any(), any())).thenReturn(List.of(reportableProject));

    final var reportableProjectStrings = newsletterProjectService.getProjectsUpdatedInTheLastMonth();

    assertThat(reportableProjectStrings).containsExactly(
        getReportableProjectStringInExpectedFormat(reportableProject)
    );

    final var dateOneMonthPriorToToday = LocalDate.now().minusMonths(1);
    final var minDate = DateUtil.getStartOfMonth(dateOneMonthPriorToToday);
    final var maxDate = DateUtil.getEndOfMonth(dateOneMonthPriorToToday);

    verify(reportableProjectService, times(1)).getReportableProjectsUpdatedBetween(minDate, maxDate);
  }

  @Test
  public void getProjectsUpdatedInTheLastMonth_whenProjectsExist_verifyOrdering() {

    final var operatorNameFirstAlphabetically = "a company";
    final var operatorNameLastAlphabetically = "z company";
    final var projectTitleFirstAlphabetically = "a project";
    final var projectTitleLastAlphabetically = "z project";

    final var expectedFirstReportableProject = ReportableProjectTestUtil.createReportableProject(
        operatorNameFirstAlphabetically,
        projectTitleFirstAlphabetically
    );

    final var expectedSecondReportableProject = ReportableProjectTestUtil.createReportableProject(
        operatorNameFirstAlphabetically,
        projectTitleLastAlphabetically
    );

    final var expectedThirdReportableProject = ReportableProjectTestUtil.createReportableProject(
        operatorNameLastAlphabetically,
        projectTitleFirstAlphabetically
    );
    when(reportableProjectService.getReportableProjectsUpdatedBetween(any(), any())).thenReturn(
        List.of(expectedThirdReportableProject, expectedSecondReportableProject, expectedFirstReportableProject)
    );

    final var reportableProjectStrings = newsletterProjectService.getProjectsUpdatedInTheLastMonth();

    assertThat(reportableProjectStrings).containsExactly(
        getReportableProjectStringInExpectedFormat(expectedFirstReportableProject),
        getReportableProjectStringInExpectedFormat(expectedSecondReportableProject),
        getReportableProjectStringInExpectedFormat(expectedThirdReportableProject)
    );
  }

  private String getReportableProjectStringInExpectedFormat(ReportableProject reportableProject) {
    return String.format(
        "%s - %s",
        reportableProject.getOperatorName(),
        reportableProject.getProjectTitle()
    );
  }
}