package uk.co.ogauthority.pathfinder.publicdata;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import net.javacrumbs.shedlock.core.LockAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublicDataUploadServiceTest {

  @Mock
  private PublicDataJsonService publicDataJsonService;

  @Mock
  private PublicDataS3Service publicDataS3Service;

  @InjectMocks
  private PublicDataUploadService publicDataUploadService;

  @Test
  void uploadPublicDataJsonFile() {
    LockAssert.TestHelper.makeAllAssertsPass(true);

    var publicDataJson = new PublicDataJson(List.of());

    when(publicDataJsonService.getPublicDataJson()).thenReturn(publicDataJson);

    publicDataUploadService.uploadPublicDataJsonFile();

    verify(publicDataS3Service).uploadPublicDataJsonFile(publicDataJson);
  }
}
