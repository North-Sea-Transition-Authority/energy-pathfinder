package uk.co.ogauthority.pathfinder.controller.test.file;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.config.file.FileDeleteResult;
import uk.co.ogauthority.pathfinder.config.file.FileUploadResult;
import uk.co.ogauthority.pathfinder.controller.file.PathfinderFileUploadController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.file.FileUpdateMode;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;

@Controller
@Profile("development")
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/file-upload")
public class TestFileUploadController extends PathfinderFileUploadController {

  private static final ProjectDetailFilePurpose FILE_PURPOSE = ProjectDetailFilePurpose.PLACEHOLDER;

  private final ControllerHelperService controllerHelperService;

  @Autowired
  public TestFileUploadController(ProjectDetailFileService projectDetailFileService,
                                  ControllerHelperService controllerHelperService) {
    super(projectDetailFileService);
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView getTestFileUpload(@PathVariable("projectId") Integer projectId,
                                        ProjectContext projectContext) {
    final var form = new TestFileUploadForm();
    final var projectDetail = projectContext.getProjectDetails();

    projectDetailFileService.mapFilesToForm(
        form,
        projectDetail,
        FILE_PURPOSE
    );

    var modelAndView = getTestModelAndView(form, projectDetail);
    modelAndView.addObject("form", form);

    return modelAndView;
  }

  @PostMapping
  public ModelAndView saveTestFileUpload(@PathVariable("projectId") Integer projectId,
                                         @Valid @ModelAttribute("form") TestFileUploadForm form,
                                         BindingResult bindingResult,
                                         ValidationType validationType,
                                         ProjectContext projectContext) {
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getTestModelAndView(form, projectContext.getProjectDetails()),
        form,
        () -> {
          projectDetailFileService.updateFiles(
              form,
              projectContext.getProjectDetails(),
              FILE_PURPOSE,
              FileUpdateMode.DELETE_UNLINKED_FILES,
              projectContext.getUserAccount()
          );

          return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
        }
    );
  }

  @PostMapping("/files/upload")
  @ResponseBody
  public FileUploadResult handleUpload(@PathVariable("projectId") Integer projectId,
                                       @RequestParam("file") MultipartFile file,
                                       ProjectContext projectContext) {

    return projectDetailFileService.processInitialUpload(
        file,
        projectContext.getProjectDetails(),
        FILE_PURPOSE,
        projectContext.getUserAccount()
    );

  }

  @GetMapping("/files/download/{fileId}")
  @ResponseBody
  public ResponseEntity<Resource> handleDownload(@PathVariable("projectId") Integer projectId,
                                                 @PathVariable("fileId") String fileId,
                                                 ProjectContext projectContext) {
    var file = projectDetailFileService.getProjectDetailFileByProjectDetailAndFileId(
        projectContext.getProjectDetails(),
        fileId
    );
    return serveFile(file);
  }

  @PostMapping("/files/delete/{fileId}")
  @ResponseBody
  public FileDeleteResult handleDelete(@PathVariable("projectId") Integer projectId,
                                       @PathVariable("fileId") String fileId,
                                       ProjectContext projectContext) {
    var file = projectDetailFileService.getProjectDetailFileByProjectDetailAndFileId(
        projectContext.getProjectDetails(),
        fileId
    );
    return projectDetailFileService.processFileDeletion(file, projectContext.getUserAccount());
  }

  private ModelAndView getTestModelAndView(TestFileUploadForm fileUploadForm,
                                           ProjectDetail projectDetail) {
    return this.createModelAndView(
        "test/file/fileUpload",
        projectDetail,
        FILE_PURPOSE,
        fileUploadForm
    );
  }
}
