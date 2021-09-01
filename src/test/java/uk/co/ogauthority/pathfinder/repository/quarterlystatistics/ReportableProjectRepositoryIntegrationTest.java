package uk.co.ogauthority.pathfinder.repository.quarterlystatistics;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.model.entity.quarterlystatistics.ReportableProject;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("integration-test")
@DirtiesContext
public class ReportableProjectRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ReportableProjectRepository reportableProjectRepository;

  @Test
  public void findByLastUpdatedDatetimeNotBetween_assertCorrectFilteringApplied() {

    var dayTemporalUnit = ChronoUnit.DAYS;

    var earliestUpdateWindow = Instant.now().minus(10, dayTemporalUnit);
    var latestUpdateWindow = Instant.now();

    var timeWithinUpdateWindow = earliestUpdateWindow.plus(1, dayTemporalUnit);
    var timeNotInUpdateWindow = earliestUpdateWindow.minus(1, dayTemporalUnit);

    var updatedInWindowReportableProject = new ReportableProject();
    updatedInWindowReportableProject.setProjectDetailId(1);
    updatedInWindowReportableProject.setLastUpdatedDatetime(timeWithinUpdateWindow);

    var notUpdatedInWindowReportableProject = new ReportableProject();
    notUpdatedInWindowReportableProject.setProjectDetailId(2);
    notUpdatedInWindowReportableProject.setLastUpdatedDatetime(timeNotInUpdateWindow);

    entityManager.persist(updatedInWindowReportableProject);
    entityManager.persist(notUpdatedInWindowReportableProject);
    entityManager.flush();

    var resultingReportableProjects = reportableProjectRepository.findByLastUpdatedDatetimeNotBetween(
        earliestUpdateWindow,
        latestUpdateWindow
    );

    assertThat(resultingReportableProjects).containsExactly(notUpdatedInWindowReportableProject);
  }
}