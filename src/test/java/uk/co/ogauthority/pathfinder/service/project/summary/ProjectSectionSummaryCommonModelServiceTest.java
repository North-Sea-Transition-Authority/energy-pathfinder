package uk.co.ogauthority.pathfinder.service.project.summary;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSectionSummaryCommonModelServiceTest {

  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Before
  public void setup() {
    projectSectionSummaryCommonModelService = new ProjectSectionSummaryCommonModelService();
  }

  @Test
  public void getCommonSummaryModelMap_projectTypeSmokeTest_assertCommonMapProperties() {

    Arrays.asList(ProjectType.values()).forEach(projectType -> {

      final var projectDetail = ProjectUtil.getProjectDetails();
      projectDetail.setProjectType(projectType);

      final var sectionName = "section name";
      final var sectionId = "section id";

      final var resultingMap = projectSectionSummaryCommonModelService.getCommonSummaryModelMap(
          projectDetail,
          sectionName,
          sectionId
      );

      final var expectedMapEntries = Map.of(
          ProjectSectionSummaryCommonModelService.SECTION_TITLE_ATTR_NAME, sectionName,
          ProjectSectionSummaryCommonModelService.SECTION_ID_ATTR_NAME, sectionId,
          ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR, projectType.getDisplayName(),
          ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR, projectType.getLowercaseDisplayName()
      );

      assertThat(resultingMap).containsExactlyInAnyOrderEntriesOf(expectedMapEntries);
    });

  }

}