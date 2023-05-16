package uk.co.ogauthority.pathfinder.externalapi;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

@DataJpaTest
@ActiveProfiles("integration-test")
@DirtiesContext
@Transactional
class ProjectDtoRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  private ProjectDtoRepository projectDtoRepository;

  private Project project;
  private ProjectDetail projectDetail1;
  private ProjectDetail projectDetail2;
  private PortalOrganisationGroup portalOrganisationGroup1;

  @BeforeEach
  void setup() throws IllegalAccessException {
    projectDtoRepository = new ProjectDtoRepository(entityManager.getEntityManager());
    project = new Project();
    var project2 = new Project();

    entityManager.persist(project);
    entityManager.persist(project2);

    projectDetail1 = new ProjectDetail(project, ProjectStatus.PUBLISHED, null, 1, true, null);
    projectDetail2 = new ProjectDetail(project2, ProjectStatus.PUBLISHED, null, 1, true, null);

    entityManager.persist(projectDetail1);
    entityManager.persist(projectDetail2);

    portalOrganisationGroup1 = new PortalOrganisationGroup();
    FieldUtils.writeField(portalOrganisationGroup1, "orgGrpId", 55, true);
    var portalOrganisationGroup2 = new PortalOrganisationGroup();
    FieldUtils.writeField(portalOrganisationGroup2, "orgGrpId", 116, true);

    entityManager.persist(portalOrganisationGroup1);
    entityManager.persist(portalOrganisationGroup2);

    var projectOperator1 = new ProjectOperator(projectDetail1, portalOrganisationGroup1);
    var projectOperator2 = new ProjectOperator(projectDetail2, portalOrganisationGroup2);

    entityManager.persist(projectOperator1);
    entityManager.persist(projectOperator2);
  }

  @Test
  void searchPathfinderProjects_SearchByProjectIds() {
    var searchedIds = Collections.singletonList(project.getId());
    var returnedProjectDtoList = projectDtoRepository
        .searchProjectDtos(searchedIds, null, null, null, null);

    assertThat(returnedProjectDtoList)
        .extracting(ProjectDto::getProjectId)
        .containsExactly(project.getId());
  }

  @Test
  void searchPathfinderProjects_SearchByProjectStatuses() {
    var publishedProject = projectDetail1;
    var archivedProject = projectDetail2;

    publishedProject.setStatus(ProjectStatus.PUBLISHED);
    archivedProject.setStatus(ProjectStatus.ARCHIVED);

    entityManager.persist(publishedProject);
    entityManager.persist(archivedProject);

    var searchedStatus = publishedProject.getStatus();
    var returnedProjectDtoList = projectDtoRepository
        .searchProjectDtos(null, Collections.singletonList(searchedStatus), null, null, null);

    assertThat(returnedProjectDtoList)
        .extracting(ProjectDto::getProjectStatus)
        .containsExactly(searchedStatus);
  }

  @Test
  void searchPathfinderProjects_SearchByProjectTitle() {
    var matchingTitleProjectInformation = new ProjectInformation();
    matchingTitleProjectInformation.setProjectDetail(projectDetail1);
    matchingTitleProjectInformation.setProjectTitle("my project name");
    var nonMatchingTitleProjectInformation = new ProjectInformation();
    nonMatchingTitleProjectInformation.setProjectDetail(projectDetail2);
    nonMatchingTitleProjectInformation.setProjectTitle("other project name");

    entityManager.persist(matchingTitleProjectInformation);
    entityManager.persist(nonMatchingTitleProjectInformation);

    var searchedProjectTitle = "my pROJect";
    var returnedProjectDtoList = projectDtoRepository
        .searchProjectDtos(null, null, searchedProjectTitle, null, null);

    assertThat(returnedProjectDtoList)
        .extracting(ProjectDto::getProjectTitle)
        .containsExactly(matchingTitleProjectInformation.getProjectTitle());
  }

  @Test
  void searchPathfinderProjects_SearchByOperator() {
    var searchedOrgGroupId = portalOrganisationGroup1.getOrgGrpId();
    var returnedProjectDtoList = projectDtoRepository
        .searchProjectDtos(null, null, null, searchedOrgGroupId, null);

    assertThat(returnedProjectDtoList)
        .extracting(ProjectDto::getOperatorOrganisationGroupId)
        .containsExactly(portalOrganisationGroup1.getOrgGrpId());
  }

  @Test
  void searchPathfinderProjects_SearchByProjectTypes() {
    var infrastructureProject = projectDetail1;
    var forwardWorkPlanProject = projectDetail2;

    infrastructureProject.setProjectType(ProjectType.INFRASTRUCTURE);
    forwardWorkPlanProject.setProjectType(ProjectType.FORWARD_WORK_PLAN);

    entityManager.persist(infrastructureProject);
    entityManager.persist(forwardWorkPlanProject);

    var searchedProjectType = infrastructureProject.getProjectType();
    var returnedProjectDtoList = projectDtoRepository
        .searchProjectDtos(null, null, null, null, infrastructureProject.getProjectType());

    assertThat(returnedProjectDtoList)
        .extracting(ProjectDto::getProjectType)
        .containsExactly(searchedProjectType);
  }

  @ParameterizedTest
  @EnumSource(value = ProjectStatus.class, names = "DRAFT", mode = EnumSource.Mode.EXCLUDE)
  void searchPathfinderProjects_NewUpdateInProgress_AssertReturnsNonDraftVersion(ProjectStatus nonDraftStatus) {
    var previousProjectDetail = new ProjectDetail(
        project, ProjectStatus.PUBLISHED, null, 1, false, ProjectType.INFRASTRUCTURE
    );
    var currentNonDraftProjectDetail = projectDetail1;
    currentNonDraftProjectDetail.setProject(project);
    currentNonDraftProjectDetail.setStatus(nonDraftStatus);
    currentNonDraftProjectDetail.setVersion(2);
    var draftProjectDetail = projectDetail2;
    draftProjectDetail.setProject(project);
    draftProjectDetail.setVersion(3);
    draftProjectDetail.setStatus(ProjectStatus.DRAFT);

    entityManager.persist(previousProjectDetail);
    entityManager.merge(currentNonDraftProjectDetail);
    entityManager.merge(draftProjectDetail);

    var projectDtos = projectDtoRepository.searchProjectDtos(
        null,
        List.of(ProjectStatus.PUBLISHED, ProjectStatus.ARCHIVED, ProjectStatus.QA),
        null,
        null,
        null
    );

    assertThat(projectDtos).extracting(
        ProjectDto::getProjectId,
        ProjectDto::getProjectStatus,
        ProjectDto::getProjectVersion
    ).containsExactly(
        tuple(
            currentNonDraftProjectDetail.getProject().getId(),
            currentNonDraftProjectDetail.getStatus(),
            currentNonDraftProjectDetail.getVersion()
        )
    );
  }

  @Test
  void searchPathfinderProjects_DoesNotReturnDrafts() {
    var draftProject = projectDetail2;
    draftProject.setStatus(ProjectStatus.DRAFT);
    entityManager.merge(draftProject);

    var projectDtos = projectDtoRepository.searchProjectDtos(
        null,
        Collections.singletonList(ProjectStatus.DRAFT),
        null,
        null,
        null
    );

    assertThat(projectDtos).isEmpty();
  }

  @Test
  void searchPathfinderProjects_WhenAllNullParams_AssertAllProjectsReturned() {
    var returnedProjectDtoList = projectDtoRepository
        .searchProjectDtos(null, null, null, null, null);

    assertThat(returnedProjectDtoList)
        .extracting(ProjectDto::getProjectId)
        .containsExactlyInAnyOrder(project.getId(), projectDetail2.getProject().getId());
  }

  @Test
  void searchPathfinderProjects_AreInOrder() {
    var searchedProjectIds = List.of(projectDetail2.getProject().getId(), projectDetail1.getProject().getId());
    var returnedProjectDtoList = projectDtoRepository
        .searchProjectDtos(searchedProjectIds, null, null, null, null);

    var returnedProjectIds = returnedProjectDtoList.stream()
        .map(ProjectDto::getProjectId)
        .collect(Collectors.toList());
    var sortedProjectIds = List.copyOf(returnedProjectIds).stream()
        .sorted()
        .collect(Collectors.toList());

    assertThat(returnedProjectIds).isEqualTo(sortedProjectIds);
  }
}
