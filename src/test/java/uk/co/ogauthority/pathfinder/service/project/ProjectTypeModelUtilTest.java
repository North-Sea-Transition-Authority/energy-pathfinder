package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Arrays;
import java.util.HashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTypeModelUtilTest {

  @Test
  public void addProjectTypeDisplayNameAttributesToModel_whenModelAndViewVariant_assertCorrectModelProperties() {

    Arrays.asList(ProjectType.values()).forEach(projectType -> {

      final var projectDetail = ProjectUtil.getProjectDetails();
      projectDetail.setProjectType(projectType);

      final var modelAndView = new ModelAndView();

      ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(modelAndView, projectDetail);

      assertThat(modelAndView.getModel()).containsExactly(
          entry(ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR, projectType.getDisplayName()),
          entry(ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR, projectType.getLowercaseDisplayName())
      );
    });

  }

  @Test
  public void addProjectTypeDisplayNameAttributesToModel_whenMapVariant_assertCorrectModelProperties() {

    Arrays.asList(ProjectType.values()).forEach(projectType -> {

      final var projectDetail = ProjectUtil.getProjectDetails();
      projectDetail.setProjectType(projectType);

      final var modelMap = new HashMap<String, Object>();

      ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(modelMap, projectDetail);

      assertThat(modelMap).containsExactly(
          entry(ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR, projectType.getDisplayName()),
          entry(ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR, projectType.getLowercaseDisplayName())
      );
    });

  }

}