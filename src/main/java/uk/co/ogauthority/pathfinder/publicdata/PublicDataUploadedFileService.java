package uk.co.ogauthority.pathfinder.publicdata;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.UploadedFile;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.file.ProjectDetailFileRepository;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;

@Service
class PublicDataUploadedFileService {

  private final ProjectDetailsRepository projectDetailsRepository;
  private final ProjectDetailFileRepository projectDetailFileRepository;

  PublicDataUploadedFileService(
      ProjectDetailsRepository projectDetailsRepository,
      ProjectDetailFileRepository projectDetailFileRepository
  ) {
    this.projectDetailsRepository = projectDetailsRepository;
    this.projectDetailFileRepository = projectDetailFileRepository;
  }

  Set<UploadedFile> getUploadedFilesForPublishedProjects() {
    var publishedProjectDetailIds =
        projectDetailsRepository.getAllPublishedProjectDetailsByProjectTypes(EnumSet.allOf(ProjectType.class))
            .stream()
            .map(ProjectDetail::getId)
            .collect(Collectors.toSet());

    // TODO: When replatforming to use Postgres, switch to findAllByProjectDetail_IdIn. We can't do this with Oracle at the moment
    // due to the 1000 IN clause limit.
    return projectDetailFileRepository.findAll()
        .stream()
        .filter(projectDetailFile -> publishedProjectDetailIds.contains(projectDetailFile.getProjectDetail().getId()))
        .map(ProjectDetailFile::getUploadedFile)
        .collect(Collectors.toSet());
  }
}
