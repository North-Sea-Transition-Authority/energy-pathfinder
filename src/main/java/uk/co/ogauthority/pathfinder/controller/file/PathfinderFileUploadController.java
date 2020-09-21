package uk.co.ogauthority.pathfinder.controller.file;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.config.file.FileDeleteResult;
import uk.co.ogauthority.pathfinder.config.file.FileUploadResult;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.entity.file.UploadedFile;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.forminput.file.UploadMultipleFilesWithDescriptionForm;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.util.file.FileDownloadUtil;

@FileUploadController
public abstract class PathfinderFileUploadController {

  protected final ProjectDetailFileService projectDetailFileService;

  @Autowired
  public PathfinderFileUploadController(ProjectDetailFileService projectDetailFileService) {
    this.projectDetailFileService = projectDetailFileService;
  }

  /**
   * Create model and view with all file urls populated in model.
   */
  protected ModelAndView createModelAndView(String templatePath,
                                            String uploadFileUrl,
                                            String downloadUrl,
                                            String deleteUrl,
                                            List<UploadedFileView> uploadedFileViews) {
    return new ModelAndView(templatePath)
        .addObject("uploadedFileViewList", uploadedFileViews)
        .addObject("uploadUrl", uploadFileUrl)
        .addObject("downloadUrl", downloadUrl)
        .addObject("deleteUrl", deleteUrl);
  }

  protected ModelAndView createModelAndView(String templatePath,
                                            ProjectDetail projectDetail,
                                            ProjectDetailFilePurpose purpose,
                                            UploadMultipleFilesWithDescriptionForm uploadForm) {
    return createModelAndView(
        templatePath,
        ReverseRouter.route(on(purpose.getFileControllerClass()).handleUpload(
            projectDetail.getProject().getId(),
            null,
            null
        )),
        ReverseRouter.route(on(purpose.getFileControllerClass()).handleDownload(
            projectDetail.getProject().getId(),
            null,
            null
        )),
        ReverseRouter.route(on(purpose.getFileControllerClass()).handleDelete(
            projectDetail.getProject().getId(),
            null,
            null
        )),
        projectDetailFileService.getFilesLinkedToForm(uploadForm, projectDetail, purpose)
    );
  }

  /**
   * Serves file for download.
   *
   * @param uploadedFile file we want to trigger download for
   * @return the ResponseEntity object containing the downloaded file
   */
  protected ResponseEntity<Resource> serveFile(UploadedFile uploadedFile) {
    Resource resource = FileDownloadUtil.fetchFileAsStream(uploadedFile.getFileName(), uploadedFile.getFileData());
    MediaType mediaType = MediaType.parseMediaType(uploadedFile.getContentType());
    return FileDownloadUtil.getResourceAsResponse(resource, mediaType, uploadedFile.getFileName(),
        uploadedFile.getFileSize());
  }

  protected ResponseEntity<Resource> serveFile(ProjectDetailFile projectDetailFile) {
    return serveFile(projectDetailFileService.getUploadedFileById(projectDetailFile.getFileId()));
  }

  public abstract FileUploadResult handleUpload(@PathVariable("projectId") Integer projectId,
                                                @RequestParam("file") MultipartFile file,
                                                ProjectContext projectContext);

  public abstract ResponseEntity<Resource> handleDownload(@PathVariable("projectId") Integer projectId,
                                                          @PathVariable("fileId") String fileId,
                                                          ProjectContext projectContext);

  public abstract FileDeleteResult handleDelete(@PathVariable("projectId") Integer projectId,
                                                @PathVariable("fileId") String fileId,
                                                ProjectContext projectContext);

}
