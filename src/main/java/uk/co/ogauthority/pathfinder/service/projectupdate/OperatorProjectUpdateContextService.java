package uk.co.ogauthority.pathfinder.service.projectupdate;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;

@Service
public class OperatorProjectUpdateContextService extends ProjectUpdateContextService {

  @Autowired
  public OperatorProjectUpdateContextService(ProjectContextService projectContextService,
                                             ProjectUpdateService projectUpdateService) {
    super(projectContextService, projectUpdateService);
  }

  @Override
  public OperatorProjectUpdateContext buildProjectUpdateContext(ProjectDetail detail,
                                                                AuthenticatedUserAccount user,
                                                                Set<ProjectStatus> statusCheck,
                                                                Set<ProjectPermission> permissionCheck) {
    var projectUpdateContext = super.buildProjectUpdateContext(
        detail,
        user,
        statusCheck,
        permissionCheck
    );

    return new OperatorProjectUpdateContext(projectUpdateContext);
  }
}
