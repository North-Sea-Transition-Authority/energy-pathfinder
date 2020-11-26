package uk.co.ogauthority.pathfinder.service.projectcontext;

import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;

@ProjectStatusCheck(status = ProjectStatus.QA)
@ProjectFormPagePermissionCheck(permissions = {ProjectPermission.PROVIDE_ASSESSMENT})
public class TestClassWithContextAnnotations {
}
