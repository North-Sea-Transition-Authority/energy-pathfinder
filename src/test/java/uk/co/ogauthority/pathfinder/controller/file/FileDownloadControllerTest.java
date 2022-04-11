package uk.co.ogauthority.pathfinder.controller.file;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.file.FileUploadService;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = FileDownloadController.class
)
public class FileDownloadControllerTest extends AbstractControllerTest {

  @MockBean
  private FileDownloadService fileDownloadService;

  @MockBean
  private FileUploadService fileUploadService;

  private AuthenticatedUserAccount authenticatedUser;
  private AuthenticatedUserAccount unauthenticatedUser;

  @Before
  public void setup() {
    authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(SystemAccessService.CREATE_PROJECT_PRIVILEGES);
    unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();
  }

  @Test
  public void authenticatedUser_canDownloadFile() throws Exception {
    var uploadedFile = UploadedFileUtil.createUploadedFile();
    when(fileUploadService.getUploadedFromReportableProjectByFileId(uploadedFile.getFileId())).thenReturn(
        Optional.of(uploadedFile));

    mockMvc.perform(get(ReverseRouter.route(
            on(FileDownloadController.class).downloadFileById(uploadedFile.getFileId())))
            .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unauthenticatedUser_canDownloadFile() throws Exception {
    var uploadedFile = UploadedFileUtil.createUploadedFile();
    when(fileUploadService.getUploadedFromReportableProjectByFileId(uploadedFile.getFileId())).thenReturn(
        Optional.of(uploadedFile));

    mockMvc.perform(get(ReverseRouter.route(
            on(FileDownloadController.class).downloadFileById(uploadedFile.getFileId())))
            .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unauthenticatedUser_fileNotExist() throws Exception {
    var uploadedFile = UploadedFileUtil.createUploadedFile();
    when(fileUploadService.getUploadedFromReportableProjectByFileId(uploadedFile.getFileId())).thenReturn(
        Optional.empty());

    mockMvc.perform(get(ReverseRouter.route(
            on(FileDownloadController.class).downloadFileById(uploadedFile.getFileId())))
            .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isNotFound());
  }
}