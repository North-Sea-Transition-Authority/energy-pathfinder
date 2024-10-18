package uk.co.ogauthority.pathfinder.publicdata;

import java.util.concurrent.TimeUnit;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
class PublicDataUploadService {

  private final PublicDataJsonService publicDataJsonService;
  private final PublicDataS3Service publicDataS3Service;

  PublicDataUploadService(
      PublicDataJsonService publicDataJsonService,
      PublicDataS3Service publicDataS3Service
  ) {
    this.publicDataJsonService = publicDataJsonService;
    this.publicDataS3Service = publicDataS3Service;
  }

  @Scheduled(fixedDelayString = "${pathfinder.public-data.upload-interval-seconds}", timeUnit = TimeUnit.SECONDS)
  @SchedulerLock(name = "PublicDataUploadService_uploadPublicDataJsonFile")
  void uploadPublicDataJsonFile() {
    LockAssert.assertLocked();

    var publicDataJson = publicDataJsonService.getPublicDataJson();

    publicDataS3Service.uploadPublicDataJsonFile(publicDataJson);
  }
}
