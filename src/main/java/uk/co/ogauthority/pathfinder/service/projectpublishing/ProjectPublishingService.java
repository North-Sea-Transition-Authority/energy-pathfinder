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

@Service
public class ProjectPublishingService {

  private final ProjectPublishingDetailRepository projectPublishingDetailRepository;

  @Autowired
  public ProjectPublishingService(ProjectPublishingDetailRepository projectPublishingDetailRepository) {
    this.projectPublishingDetailRepository = projectPublishingDetailRepository;
  }

  @Transactional
  public ProjectPublishingDetail publishProject(ProjectDetail projectDetail, AuthenticatedUserAccount publisher) {
    var projectPublishingDetail = new ProjectPublishingDetail();
    projectPublishingDetail.setProjectDetail(projectDetail);
    projectPublishingDetail.setPublishedInstant(Instant.now());
    projectPublishingDetail.setPublisherWuaId(publisher.getWuaId());
    projectPublishingDetailRepository.save(projectPublishingDetail);
    projectDetail.setStatus(ProjectStatus.PUBLISHED);
    return projectPublishingDetail;
  }
}
