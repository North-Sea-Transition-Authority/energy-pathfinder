package uk.co.ogauthority.pathfinder.controller.project.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;

/**
 * Annotation to be used in conjunction with
 * {@link uk.co.ogauthority.pathfinder.mvc.argumentresolver.ProjectContextArgumentResolver} on controller
 * methods to restrict processing of the method to applications at a specific status.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ProjectStatusCheck {

  ProjectStatus status();

}
