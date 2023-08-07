package uk.co.ogauthority.pathfinder.service.newsletters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.model.entity.quarterlystatistics.ReportableProject;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.service.quarterlystatistics.ReportableProjectService;
import uk.co.ogauthority.pathfinder.testutil.ReportableProjectTestUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@ExtendWith(MockitoExtension.class)
class NewsletterProjectServiceTest {

  @Mock
  private ReportableProjectService reportableProjectService;

  @InjectMocks
  private NewsletterProjectService newsletterProjectService;

  @Test
  void getProjectsUpdatedInTheLastMonth_whenNoProjects_thenEmptyList() {
    when(reportableProjectService.getReportableProjectsUpdatedBetween(any(), any()))
        .thenReturn(Collections.emptyList());

    final var reportableProjects = newsletterProjectService.getProjectsUpdatedInTheLastMonth();
    assertThat(reportableProjects).isEmpty();
  }

  @Test
  void getProjectsUpdatedInTheLastMonth_whenProjectsExist_verifyReturnFormatAndInteractions() {

    final var reportableProject = ReportableProjectTestUtil.createReportableProject(FieldStage.DECOMMISSIONING);
    when(reportableProjectService.getReportableProjectsUpdatedBetween(any(), any())).thenReturn(List.of(reportableProject));

    final var newsletterProjectViews = newsletterProjectService.getProjectsUpdatedInTheLastMonth();

    assertThat(newsletterProjectViews).hasSize(1);
    var resultNewsletterProjectView = newsletterProjectViews.get(0);
    assertThat(resultNewsletterProjectView.getProject()).contains(
        getReportableProjectStringInExpectedFormat(reportableProject)
    );
    assertThat(resultNewsletterProjectView.getFieldStage()).isEqualTo(FieldStage.DECOMMISSIONING);

    final var dateOneMonthPriorToToday = LocalDate.now().minusMonths(1);
    final var minDate = DateUtil.getStartOfMonth(dateOneMonthPriorToToday);
    final var maxDate = DateUtil.getEndOfMonth(dateOneMonthPriorToToday);

    verify(reportableProjectService).getReportableProjectsUpdatedBetween(minDate, maxDate);
  }

  @Test
  void getProjectsUpdatedInTheLastMonth_whenProjectsExist_verifyOrdering() {
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

    final var newsletterProjectViews = newsletterProjectService.getProjectsUpdatedInTheLastMonth();
    assertThat(newsletterProjectViews).hasSize(3);
    assertThat(newsletterProjectViews.get(0).getProject())
        .isEqualTo(getReportableProjectStringInExpectedFormat(expectedFirstReportableProject));
    assertThat(newsletterProjectViews.get(1).getProject())
        .isEqualTo(getReportableProjectStringInExpectedFormat(expectedSecondReportableProject));
    assertThat(newsletterProjectViews.get(2).getProject())
        .isEqualTo(getReportableProjectStringInExpectedFormat(expectedThirdReportableProject));
  }

  private String getReportableProjectStringInExpectedFormat(ReportableProject reportableProject) {
    return String.format(
        "%s - %s",
        reportableProject.getOperatorName(),
        reportableProject.getProjectDisplayName()
    );
  }
}
