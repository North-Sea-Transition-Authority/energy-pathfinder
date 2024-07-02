package uk.co.ogauthority.pathfinder.externalapi;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup_;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationUnit_;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail_;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator_;
import uk.co.ogauthority.pathfinder.model.entity.project.Project_;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation_;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

@Repository
class ProjectDtoRepository {
  private final EntityManager entityManager;

  @Autowired
  ProjectDtoRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public List<ProjectDto> searchProjectDtos(List<Integer> projectIds,
                                            List<ProjectStatus> projectStatuses,
                                            String projectTitle,
                                            Integer operatorOrganisationGroupId,
                                            ProjectType projectType) {
    var criteriaBuilder = entityManager.getCriteriaBuilder();
    var criteriaQuery = criteriaBuilder.createQuery(ProjectDto.class);
    var projectRoot = criteriaQuery.from(Project.class);
    var projectDetailJoin = projectRoot.join(Project_.projectDetails);
    var projectInformationJoin = projectDetailJoin.join(ProjectDetail_.projectInformation, JoinType.LEFT);
    var projectOperatorJoin = projectDetailJoin.join(ProjectDetail_.projectOperators);
    var organisationGroupJoin = projectOperatorJoin.join(ProjectOperator_.organisationGroup, JoinType.LEFT);
    var organisationUnitJoin = projectOperatorJoin.join(ProjectOperator_.publishableOrganisationUnit, JoinType.LEFT);

    var predicates = new ArrayList<Predicate>();

    // Only get the latest version of a project in either any of the specified statuses, except draft
    // This is using the same logic as in ProjectDetailsRepository.findByProjectIdAndIsLatestSubmittedVersion()
    var versionSubQuery = criteriaQuery.subquery(Integer.class);
    var subRoot = versionSubQuery.from(ProjectDetail.class);
    versionSubQuery.select(criteriaBuilder.max(subRoot.get(ProjectDetail_.version)));
    var projectIdPredicate = criteriaBuilder.equal(subRoot.get(ProjectDetail_.project), projectRoot);
    var projectStatusesPredicate =
        criteriaBuilder.in(subRoot.get(ProjectDetail_.STATUS)).value(ProjectStatus.getPostSubmissionProjectStatuses());
    versionSubQuery.where(projectIdPredicate, projectStatusesPredicate);
    var versionPredicate = criteriaBuilder.equal(projectDetailJoin.get(ProjectDetail_.version), versionSubQuery);
    predicates.add(versionPredicate);

    // Add optional filters

    if (Objects.nonNull(projectIds)) {
      var projectIdsPredicates = OraclePartitionUtil.partitionedList(projectIds)
          .stream()
          .map(projectIdsSubList -> criteriaBuilder.in(projectRoot.get(Project_.ID)).value(projectIdsSubList))
          .toArray(Predicate[]::new);
      predicates.add(criteriaBuilder.or(projectIdsPredicates));
    }

    if (Objects.nonNull(projectStatuses)) {
      predicates.add(criteriaBuilder.in(projectDetailJoin.get(ProjectDetail_.STATUS)).value(projectStatuses));
    }

    if (Objects.nonNull(projectTitle)) {
      predicates.add(criteriaBuilder.like(
          criteriaBuilder.lower(projectInformationJoin.get(ProjectInformation_.projectTitle)),
          getSqlLikeString(projectTitle)
      ));
    }

    if (Objects.nonNull(operatorOrganisationGroupId)) {
      predicates.add(criteriaBuilder.equal(
          organisationGroupJoin.get(PortalOrganisationGroup_.orgGrpId), operatorOrganisationGroupId
      ));
    }

    if (Objects.nonNull(projectType)) {
      predicates.add(criteriaBuilder.equal(projectDetailJoin.get(ProjectDetail_.projectType), projectType));
    }

    // Getting query results
    criteriaQuery.multiselect(
        projectRoot.get(Project_.id),
        projectDetailJoin.get(ProjectDetail_.status),
        projectDetailJoin.get(ProjectDetail_.version),
        projectInformationJoin.get(ProjectInformation_.projectTitle),
        organisationGroupJoin.get(PortalOrganisationGroup_.orgGrpId),
        organisationUnitJoin.get(PortalOrganisationUnit_.ouId),
        projectDetailJoin.get(ProjectDetail_.projectType)
        )
        .where(predicates.toArray(new Predicate[0]))
        .orderBy(criteriaBuilder.asc(projectRoot.get(Project_.id)));

    var query = entityManager.createQuery(criteriaQuery);
    return query.getResultList();
  }

  private String getSqlLikeString(String target) {
    return Optional.ofNullable(target)
        .map(string -> "%" + string.toLowerCase() + "%")
        .orElse(null);
  }
}
