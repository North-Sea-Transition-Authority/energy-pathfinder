package uk.co.ogauthority.pathfinder.repository.file;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.file.UploadedFile;

@Repository
public interface UploadedFileRepository extends CrudRepository<UploadedFile, String> {

  @Query("SELECT uf " +
      "FROM ReportableProject rp " +
      "JOIN ProjectDetailFile pdf ON pdf.projectDetail.id = rp.projectDetailId " +
      "JOIN UploadedFile uf ON uf.fileId = pdf.fileId " +
      "WHERE uf.fileId = :fileId")
  Optional<UploadedFile> findFileOnReportableProject(@Param("fileId") String fileId);

}
