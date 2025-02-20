package uk.co.ogauthority.pathfinder.publicdata;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import net.javacrumbs.shedlock.core.LockAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;

@ExtendWith(MockitoExtension.class)
class PublicDataUploadServiceTest {

  @Mock
  private PublicDataJsonService publicDataJsonService;

  @Mock
  private PublicDataUploadedFileService publicDataUploadedFileService;

  @Mock
  private PublicDataS3Service publicDataS3Service;

  @InjectMocks
  private PublicDataUploadService publicDataUploadService;

  @Test
  void uploadPublicData() {
    LockAssert.TestHelper.makeAllAssertsPass(true);

    var publicDataJson = PublicDataJsonTestUtil.newBuilder().build();
    var uploadedFiles = Set.of(
        UploadedFileUtil.createUploadedFile("file-1"),
        UploadedFileUtil.createUploadedFile("file-2")
    );

    when(publicDataJsonService.getPublicDataJson()).thenReturn(publicDataJson);
    when(publicDataUploadedFileService.getUploadedFilesForPublishedProjects()).thenReturn(uploadedFiles);

    publicDataUploadService.uploadPublicData();

    verify(publicDataS3Service).uploadPublicData(publicDataJson, uploadedFiles);
  }
}
