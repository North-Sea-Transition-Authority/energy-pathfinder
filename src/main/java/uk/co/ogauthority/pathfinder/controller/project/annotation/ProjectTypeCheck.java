package uk.co.ogauthority.pathfinder.controller.project.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

/**
 * Annotation to be used in conjunction with
 * {@link uk.co.ogauthority.pathfinder.mvc.argumentresolver.ProjectContextArgumentResolver} on controller
 * methods to restrict processing of the method to a project of a specific type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ProjectTypeCheck {

  ProjectType[] types() default {};

}
