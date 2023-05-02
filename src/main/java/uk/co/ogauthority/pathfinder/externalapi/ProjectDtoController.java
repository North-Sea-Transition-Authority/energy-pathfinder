package uk.co.ogauthority.pathfinder.externalapi;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

@RequestMapping(ProjectDtoController.ENERGY_PORTAL_API_BASE_PATH)
@RestController
public class ProjectDtoController {

  public static final String ENERGY_PORTAL_API_BASE_PATH = "/api/external/v1";
  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectDtoController.class);

  private final ProjectDtoRepository projectDtoRepository;

  @Autowired
  ProjectDtoController(ProjectDtoRepository projectDtoRepository) {
    this.projectDtoRepository = projectDtoRepository;
  }

  @GetMapping("/projects")
  List<ProjectDto> searchProjectDtos(@RequestParam(name = "projectId", required = false)
                                     List<Integer> projectIds,
                                     @RequestParam(name = "projectStatus", required = false)
                                     List<ProjectStatus> projectStatuses,
                                     @RequestParam(required = false)
                                     String projectTitle,
                                     @RequestParam(required = false)
                                     Integer operatorOrganisationGroupId,
                                     @RequestParam(required = false)
                                     ProjectType projectType) {
    if (Objects.requireNonNullElse(projectStatuses, Collections.emptyList()).contains(ProjectStatus.DRAFT)) {
      var errorMessage = "Got External API request for DRAFT project";
      LOGGER.warn(errorMessage);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
    }

    return projectDtoRepository.searchProjectDtos(
        projectIds,
        projectStatuses,
        projectTitle,
        operatorOrganisationGroupId,
        projectType
    );
  }
}
