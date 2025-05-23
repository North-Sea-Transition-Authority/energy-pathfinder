package uk.co.ogauthority.pathfinder.repository.file;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;

@Repository
public interface ProjectDetailFileRepository extends ListCrudRepository<ProjectDetailFile, Integer>,
    ProjectDetailFileDtoRepository {

  List<ProjectDetailFile> findAllByProjectDetailAndPurpose(ProjectDetail projectDetail, ProjectDetailFilePurpose purpose);

  Optional<ProjectDetailFile> findByProjectDetailAndUploadedFile_FileId(ProjectDetail projectDetail, String fileId);

}
