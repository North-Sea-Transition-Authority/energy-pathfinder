package uk.co.ogauthority.pathfinder.repository.file;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.file.UploadedFile;

@Repository
public interface UploadedFileRepository extends CrudRepository<UploadedFile, String> {}
