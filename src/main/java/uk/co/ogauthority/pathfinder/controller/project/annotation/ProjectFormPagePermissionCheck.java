package uk.co.ogauthority.pathfinder.controller.project.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;

/**
 * Annotation to be used in conjunction with
 *    {@link uk.co.ogauthority.pathfinder.mvc.argumentresolver.ProjectContextArgumentResolver} on controller.
 * methods to restrict processing of the method to a User who has access to
 *     the EDIT and SUBMIT {@link uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ProjectFormPagePermissionCheck {
  //TODO PAT-328 separate annotations for RegulatorPagePermissionCheck
  ProjectPermission[] permissions() default {ProjectPermission.SUBMIT, ProjectPermission.EDIT};
}
