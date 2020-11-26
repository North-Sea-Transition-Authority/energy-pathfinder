package uk.co.ogauthority.pathfinder.service.project;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;

@Service
public class SubmitProjectService {

  private final ProjectDetailsRepository projectDetailsRepository;

  private final ProjectCleanUpService projectCleanUpService;

  @Autowired
  public SubmitProjectService(ProjectDetailsRepository projectDetailsRepository,
                              ProjectCleanUpService projectCleanUpService) {
    this.projectDetailsRepository = projectDetailsRepository;
    this.projectCleanUpService = projectCleanUpService;
  }

  @Transactional
  public void submitProject(ProjectDetail projectDetail, AuthenticatedUserAccount user) {

    projectCleanUpService.removeProjectSectionDataIfNotRelevant(projectDetail);

    projectDetail.setStatus(ProjectStatus.QA);
    projectDetail.setSubmittedByWua(user.getWuaId());
    projectDetail.setSubmittedInstant(Instant.now());
    projectDetailsRepository.save(projectDetail);
  }
}
