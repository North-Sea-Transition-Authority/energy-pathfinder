package uk.co.ogauthority.pathfinder.repository.projectupdate;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateRequestProjectDto;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("integration-test")
@DirtiesContext
public class RegulatorUpdateRequestRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private RegulatorUpdateRequestRepository regulatorUpdateRequestRepository;

  @Test
  public void getAllProjectsWithOutstandingRegulatorUpdateRequestsWithDeadlines_verifyCorrectFiltering() {

    var project1 = createProject();
    var draftProjectDetailWithUpdateRequest = createProjectDetail(ProjectStatus.DRAFT, project1);
    var projectOperator1 = createProjectOperator(draftProjectDetailWithUpdateRequest);

    // should not be returned as detail status is DRAFT
    var regulatorUpdateRequestForDraftProjectWithDeadline = createRegulatorUpdateRequest(draftProjectDetailWithUpdateRequest, LocalDate.now());

    var project2 = createProject();
    var qaProjectDetailWithUpdateRequest = createProjectDetail(ProjectStatus.QA, project2);
    var projectOperator2 = createProjectOperator(qaProjectDetailWithUpdateRequest);

    // should not be returned as update request has no deadline
    var regulatorUpdateRequestForQAProjectWithoutDeadline = createRegulatorUpdateRequest(qaProjectDetailWithUpdateRequest);

    var project3 = createProject();
    var publishedProjectDetailWitUpdateRequest = createProjectDetail(ProjectStatus.PUBLISHED, project3);
    var projectOperator3 = createProjectOperator(publishedProjectDetailWitUpdateRequest);

    // should be returned as detail status is not DRAFT and update request has a deadline
    var regulatorUpdateRequestForPublishedProjectWithDeadline = createRegulatorUpdateRequest(publishedProjectDetailWitUpdateRequest, LocalDate.now());

    // should not be returned as no update request
    var project4 = createProject();
    var archivedProjectDetailWithoutUpdateRequest = createProjectDetail(ProjectStatus.ARCHIVED, project4);
    var projectOperator4 = createProjectOperator(archivedProjectDetailWithoutUpdateRequest);

    persistEntity(
        project1,
        project2,
        project3,
        project4
    );

    persistEntity(
        draftProjectDetailWithUpdateRequest,
        qaProjectDetailWithUpdateRequest,
        publishedProjectDetailWitUpdateRequest,
        archivedProjectDetailWithoutUpdateRequest
    );

    persistEntity(
        projectOperator1,
        projectOperator2,
        projectOperator3,
        projectOperator4
    );

    persistEntity(
        regulatorUpdateRequestForDraftProjectWithDeadline,
        regulatorUpdateRequestForQAProjectWithoutDeadline,
        regulatorUpdateRequestForPublishedProjectWithDeadline
    );

    flushDatabase();

    var resultingDtos = regulatorUpdateRequestRepository.getAllProjectsWithOutstandingRegulatorUpdateRequestsWithDeadlines();

    assertThat(resultingDtos).extracting(RegulatorUpdateRequestProjectDto::getRegulatorUpdateRequest).containsExactly(
        regulatorUpdateRequestForPublishedProjectWithDeadline
    );
  }

  @Test
  public void getAllProjectsWithOutstandingRegulatorUpdateRequestsWithDeadlines_whenNewDetailSubmittedAfterUpdateRequest_thenProjectNotReturned() {

    var project = createProject();
    var submittedProjectDetailStatus = ProjectStatus.QA;

    var projectDetailWithUpdateRequest = createProjectDetail(submittedProjectDetailStatus, project);
    var projectOperatorForDetailWithUpdateRequest = createProjectOperator(projectDetailWithUpdateRequest);

    var regulatorUpdateRequestWithDeadline = createRegulatorUpdateRequest(projectDetailWithUpdateRequest, LocalDate.now());

    var projectDetailAfterUpdateRequest = createProjectDetail(submittedProjectDetailStatus, project);
    projectDetailAfterUpdateRequest.setVersion(projectDetailAfterUpdateRequest.getVersion() + 1);

    var projectOperatorForDetailAfterUpdateRequest = createProjectOperator(projectDetailAfterUpdateRequest);

    // entities related to the original project update request
    persistEntity(project);
    persistEntity(projectDetailWithUpdateRequest);
    persistEntity(projectOperatorForDetailWithUpdateRequest);
    persistEntity(regulatorUpdateRequestWithDeadline);

    // entities related to the submission after the original project update request
    persistEntity(projectDetailAfterUpdateRequest);
    persistEntity(projectOperatorForDetailAfterUpdateRequest);

    flushDatabase();

    var resultingDtos = regulatorUpdateRequestRepository.getAllProjectsWithOutstandingRegulatorUpdateRequestsWithDeadlines();

    assertThat(resultingDtos).isEmpty();
  }

  @Test
  public void getAllProjectsWithOutstandingRegulatorUpdateRequestsWithDeadlines_whenNewDetailCreatedAndNotSubmittedAfterUpdateRequest_thenProjectReturned() {

    var project = createProject();
    var submittedProjectDetailStatus = ProjectStatus.QA;
    var draftProjectDetailStatus = ProjectStatus.DRAFT;

    var projectDetailWithUpdateRequest = createProjectDetail(submittedProjectDetailStatus, project);
    var projectOperatorForDetailWithUpdateRequest = createProjectOperator(projectDetailWithUpdateRequest);

    var regulatorUpdateRequestWithDeadline = createRegulatorUpdateRequest(projectDetailWithUpdateRequest, LocalDate.now());

    var projectDetailAfterUpdateRequest = createProjectDetail(draftProjectDetailStatus, project);
    projectDetailAfterUpdateRequest.setVersion(projectDetailAfterUpdateRequest.getVersion() + 1);

    var projectOperatorForDetailAfterUpdateRequest = createProjectOperator(projectDetailAfterUpdateRequest);

    // entities related to the original project update request
    persistEntity(project);
    persistEntity(projectDetailWithUpdateRequest);
    persistEntity(projectOperatorForDetailWithUpdateRequest);
    persistEntity(regulatorUpdateRequestWithDeadline);

    // entities related to the submission after the original project update request
    persistEntity(projectDetailAfterUpdateRequest);
    persistEntity(projectOperatorForDetailAfterUpdateRequest);

    flushDatabase();

    var resultingDtos = regulatorUpdateRequestRepository.getAllProjectsWithOutstandingRegulatorUpdateRequestsWithDeadlines();

    assertThat(resultingDtos).extracting(RegulatorUpdateRequestProjectDto::getRegulatorUpdateRequest).containsExactly(
        regulatorUpdateRequestWithDeadline
    );
  }

  private void persistEntity(Object... entities) {
    for (Object entity : entities) {
      entityManager.persist(entity);
    }
  }

  private void flushDatabase() {
    entityManager.flush();
  }

  private Project createProject() {
    return new Project();
  }

  private ProjectOperator createProjectOperator(ProjectDetail projectDetail) {
    return new ProjectOperator(projectDetail);
  }

  private ProjectDetail createProjectDetail(ProjectStatus projectStatus, Project project) {
    var projectDetail = new ProjectDetail();
    projectDetail.setProject(project);
    projectDetail.setStatus(projectStatus);
    projectDetail.setVersion(1);
    return projectDetail;
  }

  private RegulatorUpdateRequest createRegulatorUpdateRequest(ProjectDetail projectDetail, LocalDate deadline) {
    var regulatorUpdateRequest = new RegulatorUpdateRequest();
    regulatorUpdateRequest.setProjectDetail(projectDetail);
    regulatorUpdateRequest.setDeadlineDate(deadline);
    return regulatorUpdateRequest;
  }

  private RegulatorUpdateRequest createRegulatorUpdateRequest(ProjectDetail projectDetail) {
    return createRegulatorUpdateRequest(projectDetail, null);
  }
}