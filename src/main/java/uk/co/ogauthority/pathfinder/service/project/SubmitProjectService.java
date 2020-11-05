package uk.co.ogauthority.pathfinder.service.project;

import java.time.Instant;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;

@Service
public class SubmitProjectService {

  private final ProjectDetailsRepository projectDetailsRepository;

  @Autowired
  public SubmitProjectService(ProjectDetailsRepository projectDetailsRepository) {
    this.projectDetailsRepository = projectDetailsRepository;
  }

  @Transactional
  public void submitProject(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    projectDetail.setStatus(ProjectStatus.QA);
    projectDetail.setSubmittedByWua(user.getWuaId());
    projectDetail.setSubmittedInstant(Instant.now());
    projectDetailsRepository.save(projectDetail);
  }
}
