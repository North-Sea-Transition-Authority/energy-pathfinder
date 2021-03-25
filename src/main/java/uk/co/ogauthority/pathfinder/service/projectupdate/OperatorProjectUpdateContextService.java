package uk.co.ogauthority.pathfinder.service.projectupdate;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
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
                                                                Set<ProjectPermission> permissionCheck,
                                                                Set<ProjectType> allowedProjectTypes) {
    var projectUpdateContext = super.buildProjectUpdateContext(
        detail,
        user,
        statusCheck,
        permissionCheck,
        allowedProjectTypes
    );

    return new OperatorProjectUpdateContext(projectUpdateContext);
  }
}
