package uk.co.ogauthority.pathfinder.repository.file;

import jakarta.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.ogauthority.pathfinder.model.entity.file.FileLinkStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.FileUploadStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;

public class ProjectDetailFileDtoRepositoryImpl implements ProjectDetailFileDtoRepository {

  private static final List<FileUploadStatus> ALL_FILE_UPLOAD_STATUSES = Arrays.asList(FileUploadStatus.values());
  private static final List<FileUploadStatus> CURRENT_FILE_UPLOAD_STATUSES = List.of(FileUploadStatus.CURRENT);

  private final EntityManager entityManager;


  @Autowired
  public ProjectDetailFileDtoRepositoryImpl(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public List<UploadedFileView> findAllAsFileViewByProjectDetailAndPurposeAndFileLinkStatus(ProjectDetail detail,
                                                                                            ProjectDetailFilePurpose purpose,
                                                                                            FileLinkStatus linkStatus) {

    return entityManager.createQuery("" +
            "SELECT new uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView(" +
            "  pf.uploadedFile.fileId" +
            ", pf.uploadedFile.fileName" +
            ", pf.uploadedFile.fileSize" +
            ", pf.description" +
            ", pf.uploadedFile.uploadDatetime" +
            ", '#' " + //link updated after construction as requires reverse router
            ") " +
            "FROM ProjectDetailFile pf " +
            "WHERE pf.uploadedFile.status = :fileStatus " +
            "AND pf.projectDetail = :projectDetail " +
            "AND pf.purpose = :purpose " +
            "AND (pf.fileLinkStatus = :fileLinkStatus OR :fileLinkStatus = :allFileLinkStatus)",
        UploadedFileView.class)
        .setParameter("projectDetail", detail)
        .setParameter("purpose", purpose)
        .setParameter("fileStatus", FileUploadStatus.CURRENT)
        .setParameter("fileLinkStatus", linkStatus)
        .setParameter("allFileLinkStatus", FileLinkStatus.ALL)
        .getResultList();

  }

  @Override
  public UploadedFileView findAsFileViewByProjectDetailAndFileIdAndPurposeAndFileLinkStatus(ProjectDetail detail,
                                                                                            String fileId,
                                                                                            ProjectDetailFilePurpose purpose,
                                                                                            FileLinkStatus linkStatus) {
    return findFileViewByProjectDetailAndFileIdAndPurposeAndFileLinkStatusAndFileStatusIn(
        detail,
        fileId,
        purpose,
        linkStatus,
        ALL_FILE_UPLOAD_STATUSES
    );
  }

  @Override
  public UploadedFileView findCurrentAsFileViewByProjectDetailAndFileIdAndPurposeAndFileLinkStatus(ProjectDetail detail,
                                                                                                   String fileId,
                                                                                                   ProjectDetailFilePurpose purpose,
                                                                                                   FileLinkStatus linkStatus) {
    return findFileViewByProjectDetailAndFileIdAndPurposeAndFileLinkStatusAndFileStatusIn(
        detail,
        fileId,
        purpose,
        linkStatus,
        CURRENT_FILE_UPLOAD_STATUSES
    );
  }

  private UploadedFileView findFileViewByProjectDetailAndFileIdAndPurposeAndFileLinkStatusAndFileStatusIn(
      ProjectDetail detail,
      String fileId,
      ProjectDetailFilePurpose purpose,
      FileLinkStatus linkStatus,
      List<FileUploadStatus> fileStatusList
  ) {
    return entityManager.createQuery(
            "SELECT new uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView(" +
            "  pf.uploadedFile.fileId" +
            ", pf.uploadedFile.fileName" +
            ", pf.uploadedFile.fileSize" +
            ", pf.description" +
            ", pf.uploadedFile.uploadDatetime" +
            ", '#' " + //link updated after construction as requires reverse router
            ") " +
            "FROM ProjectDetailFile pf " +
            "WHERE pf.uploadedFile.fileId = :fileId " +
            "AND pf.uploadedFile.status IN (:fileStatusList) " +
            "AND pf.projectDetail = :projectDetail " +
            "AND pf.purpose = :purpose " +
            "AND (pf.fileLinkStatus = :fileLinkStatus OR :fileLinkStatus = :allFileLinkStatus)",
        UploadedFileView.class)
        .setParameter("projectDetail", detail)
        .setParameter("purpose", purpose)
        .setParameter("fileId", fileId)
        .setParameter("fileStatusList", fileStatusList)
        .setParameter("fileLinkStatus", linkStatus)
        .setParameter("allFileLinkStatus", FileLinkStatus.ALL)
        .getSingleResult();
  }

  @Override
  public List<ProjectDetailFile> findAllByProjectDetailAndFilePurposeAndIdNotIn(ProjectDetail detail,
                                                                                ProjectDetailFilePurpose purpose,
                                                                                Iterable<Integer> projectDetailFileIdsToExclude) {
    return entityManager.createQuery("" +
        "SELECT pf " +
        "FROM ProjectDetailFile pf " +
        "WHERE pf.projectDetail = :projectDetail " +
        "AND pf.purpose = :purpose " +
        "AND pf.id NOT IN (:projectDetailFileIdsToExclude)", ProjectDetailFile.class)
        .setParameter("projectDetail", detail)
        .setParameter("purpose", purpose)
        .setParameter("projectDetailFileIdsToExclude", projectDetailFileIdsToExclude)
        .getResultList();
  }

}
