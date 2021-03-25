package uk.co.ogauthority.pathfinder.controller.team.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import uk.co.ogauthority.pathfinder.service.team.teammanagementcontext.TeamManagementPermission;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TeamManagementPermissionCheck {
  TeamManagementPermission[] permissions() default {};
}
