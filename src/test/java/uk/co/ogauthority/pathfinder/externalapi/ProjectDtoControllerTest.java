package uk.co.ogauthority.pathfinder.externalapi;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.config.ExternalApiWebSecurityConfiguration;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

@RunWith(SpringRunner.class)
@WebMvcTest(ProjectDtoController.class)
@Import(ExternalApiWebSecurityConfiguration.class)
public class ProjectDtoControllerTest extends AbstractControllerTest {

  @MockitoBean
  private ProjectDtoRepository projectDtoRepository;

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final String PRE_SHARED_KEY = "testKey1";

  @Test
  public void searchPathfinderProjects() throws Exception {
    var projectIds = List.of(1, 2);
    var projectStatuses = List.of(ProjectStatus.ARCHIVED, ProjectStatus.PUBLISHED);
    var projectTitle = "project title";
    var projectOperatorOrgGroupId = 11;
    var projectType = ProjectType.INFRASTRUCTURE;
    var result = Collections.singletonList(
        ProjectDtoTestUtil.builder()
            .withProjectId(1)
            .withStatus(ProjectStatus.PUBLISHED)
            .withVersion(5)
            .withProjectTitle(projectTitle)
            .withOperatorOrganisationGroupId(projectOperatorOrgGroupId)
            .withPublishableOrgUnitId(55)
            .withProjectType(ProjectType.INFRASTRUCTURE)
            .build()
    );
    var resultJson = MAPPER.writeValueAsString(result);

    when(projectDtoRepository.searchProjectDtos(
        projectIds, projectStatuses, projectTitle, projectOperatorOrgGroupId, projectType
    )).thenReturn(result);

    mockMvc.perform(get(
        ReverseRouter.route(on(ProjectDtoController.class).searchProjectDtos(
            projectIds,
            projectStatuses,
            projectTitle,
            projectOperatorOrgGroupId,
            projectType
        )))
            .header("Authorization", String.format("Bearer %s", PRE_SHARED_KEY)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(resultJson));
  }

  @Test
  public void searchPathfinderProjects_DraftProjects_AssertBadRequest() throws Exception {
    mockMvc.perform(get(
            ReverseRouter.route(on(ProjectDtoController.class).searchProjectDtos(
                null,
                Collections.singletonList(ProjectStatus.DRAFT),
                null,
                null,
                null
            )))
            .header("Authorization", String.format("Bearer %s", PRE_SHARED_KEY)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  public void searchPathfinderProjects_NoBearerToken_AssertForbidden() throws Exception {
    mockMvc.perform(post(
        ReverseRouter.route(on(ProjectDtoController.class)
            .searchProjectDtos(null, null, null, null, null))))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }
}
