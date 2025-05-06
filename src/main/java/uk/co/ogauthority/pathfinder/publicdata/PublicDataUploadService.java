package uk.co.ogauthority.pathfinder.publicdata;

import java.util.concurrent.TimeUnit;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
class PublicDataUploadService {

  private final PublicDataJsonService publicDataJsonService;
  private final PublicDataUploadedFileService publicDataUploadedFileService;
  private final PublicDataS3Service publicDataS3Service;

  PublicDataUploadService(
      PublicDataJsonService publicDataJsonService,
      PublicDataUploadedFileService publicDataUploadedFileService,
      PublicDataS3Service publicDataS3Service
  ) {
    this.publicDataJsonService = publicDataJsonService;
    this.publicDataUploadedFileService = publicDataUploadedFileService;
    this.publicDataS3Service = publicDataS3Service;
  }

  @Scheduled(fixedDelayString = "${pathfinder.public-data.upload-interval-seconds}", timeUnit = TimeUnit.SECONDS)
  @SchedulerLock(name = "PublicDataUploadService_uploadPublicData")
  void uploadPublicData() {
    LockAssert.assertLocked();

    var publicDataJson = publicDataJsonService.getPublicDataJson();
    var uploadedFiles = publicDataUploadedFileService.getUploadedFilesForPublishedProjects();

    publicDataS3Service.uploadPublicData(publicDataJson, uploadedFiles);
  }
}
