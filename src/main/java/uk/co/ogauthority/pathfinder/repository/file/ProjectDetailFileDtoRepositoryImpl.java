package uk.co.ogauthority.pathfinder.repository.file;

import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.ogauthority.pathfinder.model.entity.file.FileLinkStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.FileUploadStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;

public class ProjectDetailFileDtoRepositoryImpl implements ProjectDetailFileDtoRepository {

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
            "  uf.fileId" +
            ", uf.fileName" +
            ", uf.fileSize" +
            ", pf.description" +
            ", uf.uploadDatetime" +
            ", '#' " + //link updated after construction as requires reverse router
            ") " +
            "FROM ProjectDetailFile pf " +
            "JOIN UploadedFile uf ON pf.fileId = uf.fileId " +
            "WHERE uf.status = :fileStatus " +
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
    return entityManager.createQuery("" +
            "SELECT new uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView(" +
            "  uf.fileId" +
            ", uf.fileName" +
            ", uf.fileSize" +
            ", pf.description" +
            ", uf.uploadDatetime" +
            ", '#' " + //link updated after construction as requires reverse router
            ") " +
            "FROM ProjectDetailFile pf " +
            "JOIN UploadedFile uf ON pf.fileId = uf.fileId " +
            "WHERE pf.fileId = :fileId " +
            "AND uf.status = :fileStatus " +
            "AND pf.projectDetail = :projectDetail " +
            "AND pf.purpose = :purpose " +
            "AND (pf.fileLinkStatus = :fileLinkStatus OR :fileLinkStatus = :allFileLinkStatus)",
        UploadedFileView.class)
        .setParameter("projectDetail", detail)
        .setParameter("purpose", purpose)
        .setParameter("fileId", fileId)
        .setParameter("fileStatus", FileUploadStatus.CURRENT)
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
