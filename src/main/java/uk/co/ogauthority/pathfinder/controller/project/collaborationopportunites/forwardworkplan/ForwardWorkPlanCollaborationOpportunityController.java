package uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityFileLinkService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityModelService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@ProjectTypeCheck(types = ProjectType.FORWARD_WORK_PLAN)
@RequestMapping("/project/{projectId}/forward-work-plan/collaboration-opportunities")
public class ForwardWorkPlanCollaborationOpportunityController extends PathfinderFileUploadController {

  private final ForwardWorkPlanCollaborationOpportunityModelService forwardWorkPlanCollaborationOpportunityModelService;
  private final ForwardWorkPlanCollaborationOpportunityFileLinkService forwardWorkPlanCollaborationOpportunityFileLinkService;
  private final ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public ForwardWorkPlanCollaborationOpportunityController(
      ForwardWorkPlanCollaborationOpportunityModelService forwardWorkPlanCollaborationOpportunityModelService,
      ForwardWorkPlanCollaborationOpportunityFileLinkService forwardWorkPlanCollaborationOpportunityFileLinkService,
      ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService,
      ProjectDetailFileService projectDetailFileService,
      ControllerHelperService controllerHelperService
  ) {
    super(projectDetailFileService);
    this.forwardWorkPlanCollaborationOpportunityModelService = forwardWorkPlanCollaborationOpportunityModelService;
    this.forwardWorkPlanCollaborationOpportunityFileLinkService = forwardWorkPlanCollaborationOpportunityFileLinkService;
    this.forwardWorkPlanCollaborationOpportunityService = forwardWorkPlanCollaborationOpportunityService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView viewCollaborationOpportunities(@PathVariable("projectId") Integer projectId,
                                                     ProjectContext projectContext) {
    return forwardWorkPlanCollaborationOpportunityModelService.getViewCollaborationOpportunitiesModelAndView(
        projectContext.getProjectDetails(),
        ValidationResult.NOT_VALIDATED
    );
  }

  @PostMapping
  public ModelAndView saveCollaborationOpportunities(@PathVariable("projectId") Integer projectId,
                                                     ProjectContext projectContext) {
    return forwardWorkPlanCollaborationOpportunityModelService.getSaveCollaborationOpportunitySummaryModelAndView(
        projectContext.getProjectDetails()
    );
  }

  @GetMapping("/collaboration-opportunity")
  public ModelAndView addCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                  ProjectContext projectContext) {
    final var form = new ForwardWorkPlanCollaborationOpportunityForm();
    return getCollaborationOpportunityModelAndView(form, projectContext.getProjectDetails());
  }

  @PostMapping("/collaboration-opportunity")
  public ModelAndView saveCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                   @Valid @ModelAttribute("form") ForwardWorkPlanCollaborationOpportunityForm form,
                                                   BindingResult bindingResult,
                                                   ValidationType validationType,
                                                   ProjectContext projectContext) {
    bindingResult = forwardWorkPlanCollaborationOpportunityService.validate(form, bindingResult, validationType);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getCollaborationOpportunityModelAndView(form, projectContext.getProjectDetails()),
        form,
        () -> {
          var collaborationOpportunity = forwardWorkPlanCollaborationOpportunityService.createCollaborationOpportunity(
              projectContext.getProjectDetails(),
              form,
              projectContext.getUserAccount()
          );
          AuditService.audit(
              AuditEvent.WORK_PLAN_COLLABORATION_UPDATED,
              String.format(
                  AuditEvent.WORK_PLAN_COLLABORATION_UPDATED.getMessage(),
                  collaborationOpportunity.getId(),
                  projectContext.getProjectDetails().getId()
              )
          );

          return ReverseRouter.redirect(on(ForwardWorkPlanCollaborationOpportunityController.class)
              .viewCollaborationOpportunities(projectId, null));
        }
    );
  }

  @GetMapping("/collaboration-opportunity/{opportunityId}/edit")
  public ModelAndView editCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                   @PathVariable("opportunityId") Integer opportunityId,
                                                   ProjectContext projectContext) {
    var opportunity = forwardWorkPlanCollaborationOpportunityService.getOrError(opportunityId);
    return getCollaborationOpportunityModelAndView(
        (ForwardWorkPlanCollaborationOpportunityForm) forwardWorkPlanCollaborationOpportunityService.getForm(opportunity),
        projectContext.getProjectDetails()
    );
  }

  @PostMapping("/collaboration-opportunity/{opportunityId}/edit")
  public ModelAndView updateCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                     @PathVariable("opportunityId") Integer opportunityId,
                                                     @Valid @ModelAttribute("form") ForwardWorkPlanCollaborationOpportunityForm form,
                                                     BindingResult bindingResult,
                                                     ValidationType validationType,
                                                     ProjectContext projectContext) {
    var opportunity = forwardWorkPlanCollaborationOpportunityService.getOrError(opportunityId);
    bindingResult = forwardWorkPlanCollaborationOpportunityService.validate(form, bindingResult, validationType);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getCollaborationOpportunityModelAndView(form, projectContext.getProjectDetails()),
        form,
        () -> {
          var collaboration = forwardWorkPlanCollaborationOpportunityService.updateCollaborationOpportunity(
              opportunity,
              form,
              projectContext.getUserAccount()
          );
          AuditService.audit(
              AuditEvent.WORK_PLAN_COLLABORATION_UPDATED,
              String.format(
                  AuditEvent.WORK_PLAN_COLLABORATION_UPDATED.getMessage(),
                  collaboration.getId(),
                  projectContext.getProjectDetails().getId()
              )
          );

          return ReverseRouter.redirect(on(ForwardWorkPlanCollaborationOpportunityController.class)
              .viewCollaborationOpportunities(projectId, null));
        }
    );
  }

  @GetMapping("/collaboration-opportunity/{opportunityId}/remove/{displayOrder}")
  public ModelAndView removeCollaborationOpportunityConfirm(@PathVariable("projectId") Integer projectId,
                                                            @PathVariable("opportunityId") Integer opportunityId,
                                                            @PathVariable("displayOrder") Integer displayOrder,
                                                            ProjectContext projectContext) {
    return forwardWorkPlanCollaborationOpportunityModelService.getRemoveCollaborationOpportunityConfirmationModelAndView(
        projectId,
        opportunityId,
        displayOrder
    );
  }

  @PostMapping("/collaboration-opportunity/{opportunityId}/remove/{displayOrder}")
  public ModelAndView removeCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                     @PathVariable("opportunityId") Integer opportunityId,
                                                     @PathVariable("displayOrder") Integer displayOrder,
                                                     ProjectContext projectContext) {
    final var opportunity = forwardWorkPlanCollaborationOpportunityService.getOrError(opportunityId);
    forwardWorkPlanCollaborationOpportunityService.delete(opportunity);

    AuditService.audit(
        AuditEvent.WORK_PLAN_COLLABORATION_REMOVED,
        String.format(
            AuditEvent.WORK_PLAN_COLLABORATION_REMOVED.getMessage(),
            opportunityId,
            projectContext.getProjectDetails().getId()
        )
    );
    return ReverseRouter.redirect(on(ForwardWorkPlanCollaborationOpportunityController.class)
        .viewCollaborationOpportunities(projectId, null));
  }

  @PostMapping("/collaboration-opportunity/files/upload")
  @ResponseBody
  public FileUploadResult handleUpload(@PathVariable("projectId") Integer projectId,
                                       @RequestParam("file") MultipartFile file,
                                       ProjectContext projectContext) {

    return projectDetailFileService.processInitialUpload(
        file,
        projectContext.getProjectDetails(),
        ForwardWorkPlanCollaborationOpportunityFileLinkService.FILE_PURPOSE,
        projectContext.getUserAccount()
    );

  }

  @GetMapping("{projectVersion}/collaboration-opportunity/files/download/{fileId}")
  @ProjectStatusCheck(status = {ProjectStatus.DRAFT, ProjectStatus.QA, ProjectStatus.PUBLISHED, ProjectStatus.ARCHIVED})
  @ResponseBody
  public ResponseEntity<Resource> handleDownload(@PathVariable("projectId") Integer projectId,
                                                 @PathVariable("projectVersion") Integer projectVersion,
                                                 @PathVariable("fileId") String fileId,
                                                 ProjectContext projectContext) {
    var file = projectDetailFileService.getProjectDetailFileByProjectDetailVersionAndFileId(
        projectContext.getProjectDetails().getProject(),
        projectVersion,
        fileId
    );
    return serveFile(file);
  }

  @PostMapping("/collaboration-opportunity/files/delete/{fileId}")
  @ResponseBody
  public FileDeleteResult handleDelete(@PathVariable("projectId") Integer projectId,
                                       @PathVariable("fileId") String fileId,
                                       ProjectContext projectContext) {
    return forwardWorkPlanCollaborationOpportunityFileLinkService.deleteCollaborationOpportunityFile(
        fileId,
        projectContext.getProjectDetails(),
        projectContext.getUserAccount()
    );
  }

  private ModelAndView getCollaborationOpportunityModelAndView(ForwardWorkPlanCollaborationOpportunityForm form,
                                                               ProjectDetail projectDetail) {

    final var modelAndView = createModelAndView(
        ForwardWorkPlanCollaborationOpportunityModelService.FORM_TEMPLATE_PATH,
        projectDetail,
        ForwardWorkPlanCollaborationOpportunityFileLinkService.FILE_PURPOSE,
        form
    );
    return forwardWorkPlanCollaborationOpportunityModelService.getCollaborationOpportunityModelAndView(
        modelAndView,
        form,
        projectDetail.getProject().getId()
    );
  }

}
