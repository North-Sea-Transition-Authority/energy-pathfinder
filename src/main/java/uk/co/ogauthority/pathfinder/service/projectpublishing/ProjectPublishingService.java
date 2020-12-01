package uk.co.ogauthority.pathfinder.service.projectpublishing;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectpublishing.ProjectPublishingDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.repository.projectpublishing.ProjectPublishingDetailRepository;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;

@Service
public class ProjectPublishingService {

  private final ProjectPublishingDetailRepository projectPublishingDetailRepository;
  private final ProjectService projectService;

  @Autowired
  public ProjectPublishingService(ProjectPublishingDetailRepository projectPublishingDetailRepository,
                                  ProjectService projectService) {
    this.projectPublishingDetailRepository = projectPublishingDetailRepository;
    this.projectService = projectService;
  }

  @Transactional
  public ProjectPublishingDetail publishProject(ProjectDetail projectDetail, AuthenticatedUserAccount publisher) {
    var projectPublishingDetail = new ProjectPublishingDetail();
    projectPublishingDetail.setProjectDetail(projectDetail);
    projectPublishingDetail.setPublishedInstant(Instant.now());
    projectPublishingDetail.setPublisherWuaId(publisher.getWuaId());
    projectPublishingDetailRepository.save(projectPublishingDetail);
    projectService.updateProjectDetailStatus(projectDetail, ProjectStatus.PUBLISHED);
    return projectPublishingDetail;
  }
}
