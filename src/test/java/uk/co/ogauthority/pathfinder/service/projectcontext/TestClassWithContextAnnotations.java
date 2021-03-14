package uk.co.ogauthority.pathfinder.service.projectcontext;

import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;

@ProjectStatusCheck(status = ProjectStatus.QA)
@ProjectFormPagePermissionCheck(permissions = {ProjectPermission.PROVIDE_ASSESSMENT})
@ProjectTypeCheck(types = { ProjectType.INFRASTRUCTURE, ProjectType.FORWARD_WORK_PLAN })
public class TestClassWithContextAnnotations {
}
