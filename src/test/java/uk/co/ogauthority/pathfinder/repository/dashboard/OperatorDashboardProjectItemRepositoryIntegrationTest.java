package uk.co.ogauthority.pathfinder.repository.dashboard;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.OperatorDashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.testutil.ProjectContributorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("integration-test")
@DirtiesContext
public class OperatorDashboardProjectItemRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private OperatorDashboardProjectItemRepository operatorDashboardProjectItemRepository;

  @Test
  public void findAllByOrganisationGroupOrContributorIn_whenImOperator_thenOnlyItemsInMyOrgAreReturned() {
    var myPortalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "myOrg", "org");
    var myOperatorDashboardItem1 = createOperatorDashboardProjectItem(11, myPortalOrganisationGroup);
    var myOperatorDashboardItem2 = createOperatorDashboardProjectItem(22, myPortalOrganisationGroup);

    var myPortalOrganisationGroupB = TeamTestingUtil.generateOrganisationGroup(2, "orgs", "orgs");
    var myOperatorDashboardItem3 = createOperatorDashboardProjectItem(33, myPortalOrganisationGroupB);

    var otherPortalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(3, "otherOrg", "org");
    var otherOperatorDashboardItem = createOperatorDashboardProjectItem(44, otherPortalOrganisationGroup);

    persistEntity(
        myPortalOrganisationGroup,
        myPortalOrganisationGroupB,
        otherPortalOrganisationGroup
    );
    persistEntity(
        myOperatorDashboardItem1,
        myOperatorDashboardItem2,
        myOperatorDashboardItem3,
        otherOperatorDashboardItem
    );
    flushDatabase();

    var myOperatorDashboardItems = operatorDashboardProjectItemRepository.findAllByOrganisationGroupOrContributorIn(
        List.of(myPortalOrganisationGroup, myPortalOrganisationGroupB)
    );

    assertThat(myOperatorDashboardItems).containsExactlyInAnyOrder(
        myOperatorDashboardItem1,
        myOperatorDashboardItem2,
        myOperatorDashboardItem3
    );
  }

  @Test
  public void findAllByOrganisationGroupOrContributorIn_whenImOperatorAndContributor_thenOnlyItemsInMyOrgAreReturnedAndMyContributingItems() {
    var myPortalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "myOrg", "org");
    var otherPortalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(3, "otherOrg", "org");

    var project1 = createProject();
    var project2 = createProject();
    var project3 = createProject();

    var projectDetail1 = createProjectDetail(ProjectStatus.DRAFT, project1);
    var projectDetail2 = createProjectDetail(ProjectStatus.DRAFT, project2);
    var projectDetail3 = createProjectDetail(ProjectStatus.DRAFT, project3);

    persistEntity(
        myPortalOrganisationGroup,
        otherPortalOrganisationGroup
    );
    persistEntity(
        project1,
        project2,
        project3
    );
    persistEntity(
        projectDetail1,
        projectDetail2,
        projectDetail3
    );
    flushDatabase();

    var myOperatorDashboardItem1 = createOperatorDashboardProjectItem(11, myPortalOrganisationGroup);
    var contributingDashboardProjectItem1 = createOperatorDashboardProjectItem(22, projectDetail1, otherPortalOrganisationGroup);
    var contributingDashboardProjectItem2 = createOperatorDashboardProjectItem(33, projectDetail2, otherPortalOrganisationGroup);
    var otherOperatorDashboardItem = createOperatorDashboardProjectItem(44, projectDetail3, otherPortalOrganisationGroup);

    var projectContributor1 = ProjectContributorTestUtil.contributorWithGroupOrg(projectDetail1, myPortalOrganisationGroup);
    var projectContributor2 = ProjectContributorTestUtil.contributorWithGroupOrg(projectDetail2, myPortalOrganisationGroup);

    persistEntity(
        projectContributor1,
        projectContributor2
    );
    persistEntity(
        myOperatorDashboardItem1,
        contributingDashboardProjectItem1,
        contributingDashboardProjectItem2,
        otherOperatorDashboardItem
    );
    flushDatabase();

    var myOperatorDashboardItems = operatorDashboardProjectItemRepository.findAllByOrganisationGroupOrContributorIn(
        List.of(myPortalOrganisationGroup)
    );

    assertThat(myOperatorDashboardItems).containsExactlyInAnyOrder(
        myOperatorDashboardItem1,
        contributingDashboardProjectItem1,
        contributingDashboardProjectItem2
    );
  }

  @Test
  public void findAllByOrganisationGroupOrContributorIn_whenImNotOperatorNorContributor_thenReturnNothing() {
    var myPortalOrganisationGroupA = TeamTestingUtil.generateOrganisationGroup(1, "myOrg", "org");
    var otherPortalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(3, "otherOrg", "org");

    var project1 = createProject();
    var project2 = createProject();

    var projectDetail1 = createProjectDetail(ProjectStatus.DRAFT, project1);
    var projectDetail2 = createProjectDetail(ProjectStatus.DRAFT, project2);

    persistEntity(
        myPortalOrganisationGroupA,
        otherPortalOrganisationGroup
    );
    persistEntity(
        project1,
        project2
    );
    persistEntity(
        projectDetail1,
        projectDetail2
    );
    flushDatabase();

    var otherOperatorDashboardItem1 = createOperatorDashboardProjectItem(11, otherPortalOrganisationGroup);
    var otherOperatorDashboardItem2 = createOperatorDashboardProjectItem(44, otherPortalOrganisationGroup);
    var otherContributingDashboardProjectItem1 = createOperatorDashboardProjectItem(22, projectDetail1, otherPortalOrganisationGroup);
    var otherContributingDashboardProjectItem2 = createOperatorDashboardProjectItem(33, projectDetail2, otherPortalOrganisationGroup);

    var projectContributor1 = ProjectContributorTestUtil.contributorWithGroupOrg(projectDetail1, otherPortalOrganisationGroup);
    var projectContributor2 = ProjectContributorTestUtil.contributorWithGroupOrg(projectDetail2, otherPortalOrganisationGroup);

    persistEntity(
        projectContributor1,
        projectContributor2
    );
    persistEntity(
        otherOperatorDashboardItem1,
        otherContributingDashboardProjectItem1,
        otherContributingDashboardProjectItem2,
        otherOperatorDashboardItem2
    );
    flushDatabase();

    var myOperatorDashboardItems = operatorDashboardProjectItemRepository.findAllByOrganisationGroupOrContributorIn(
        List.of(myPortalOrganisationGroupA)
    );

    assertThat(myOperatorDashboardItems).isEmpty();
  }

  private void persistEntity(Object... entities) {
    for (Object o : entities) {
      entityManager.persist(o);
    }
  }

  private void flushDatabase() {
    entityManager.flush();
  }

  private OperatorDashboardProjectItem createOperatorDashboardProjectItem(int projectId,
                                                                          PortalOrganisationGroup portalOrganisationGroup) {
    return createOperatorDashboardProjectItem(
        projectId,
        ProjectUtil.getProjectDetails(),
        portalOrganisationGroup
    );
  }

  private OperatorDashboardProjectItem createOperatorDashboardProjectItem(int projectId,
                                                                          ProjectDetail projectDetail,
                                                                          PortalOrganisationGroup portalOrganisationGroup) {
    var operatorDashboardProjectItem = new OperatorDashboardProjectItem();
    operatorDashboardProjectItem.setProjectId(projectId);
    operatorDashboardProjectItem.setProjectDetailId(projectDetail.getId());
    operatorDashboardProjectItem.setOrganisationGroup(portalOrganisationGroup);
    return operatorDashboardProjectItem;
  }

  private Project createProject() {
    return new Project();
  }

  private ProjectDetail createProjectDetail(ProjectStatus projectStatus, Project project) {
    var projectDetail = new ProjectDetail();
    projectDetail.setProject(project);
    projectDetail.setStatus(projectStatus);
    projectDetail.setVersion(1);
    return projectDetail;
  }
}