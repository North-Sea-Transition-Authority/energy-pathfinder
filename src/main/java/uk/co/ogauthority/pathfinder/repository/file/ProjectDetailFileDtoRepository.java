package uk.co.ogauthority.pathfinder.repository.file;

import java.util.List;
import uk.co.ogauthority.pathfinder.model.entity.file.FileLinkStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;

public interface ProjectDetailFileDtoRepository {

  List<UploadedFileView> findAllAsFileViewByProjectDetailAndPurposeAndFileLinkStatus(ProjectDetail detail,
                                                                                     ProjectDetailFilePurpose purpose,
                                                                                     FileLinkStatus linkStatus);

  UploadedFileView findAsFileViewByProjectDetailAndFileIdAndPurposeAndFileLinkStatus(ProjectDetail detail,
                                                                                     String fileId,
                                                                                     ProjectDetailFilePurpose purpose,
                                                                                     FileLinkStatus linkStatus);

  UploadedFileView findCurrentAsFileViewByProjectDetailAndFileIdAndPurposeAndFileLinkStatus(ProjectDetail detail,
                                                                                            String fileId,
                                                                                            ProjectDetailFilePurpose purpose,
                                                                                            FileLinkStatus linkStatus);

  List<ProjectDetailFile> findAllByProjectDetailAndFilePurposeAndIdNotIn(ProjectDetail detail,
                                                                         ProjectDetailFilePurpose purpose,
                                                                         Iterable<Integer> projectDetailFileIdsToExclude);

}
