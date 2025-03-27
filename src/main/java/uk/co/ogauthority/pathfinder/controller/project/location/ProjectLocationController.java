package uk.co.ogauthority.pathfinder.controller.project.location;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.fivium.formlibrary.input.CoordinateInputLongitudeHemisphere;
import uk.co.ogauthority.pathfinder.controller.project.ProjectFormPageController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.controller.rest.DevUkRestController;
import uk.co.ogauthority.pathfinder.controller.rest.LicenceBlocksRestController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.MeasurementUnits;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.model.view.location.ProjectLocationBlockView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.location.ProjectLocationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
@RequestMapping("/project/{projectId}/location")
public class ProjectLocationController extends ProjectFormPageController {
  public static final String PAGE_NAME = "Location";

  private final ProjectLocationService locationService;
  private final ProjectInformationService projectInformationService;

  @Autowired
  public ProjectLocationController(BreadcrumbService breadcrumbService,
                                   ProjectLocationService locationService,
                                   ControllerHelperService controllerHelperService,
                                   ProjectInformationService projectInformationService) {
    super(breadcrumbService, controllerHelperService);
    this.locationService = locationService;
    this.projectInformationService = projectInformationService;
  }

  @GetMapping
  public ModelAndView getLocationDetails(@PathVariable("projectId") Integer projectId,
                                         ProjectContext projectContext) {
    var projectDetail = projectContext.getProjectDetails();
    return getLocationModelAndView(
        projectId,
        projectDetail,
        locationService.getForm(projectDetail),
        locationService.getValidatedBlockViewsForLocation(projectDetail)
    );
  }

  @PostMapping
  public ModelAndView saveProjectLocation(@PathVariable("projectId") Integer projectId,
                                          @Valid @ModelAttribute("form") ProjectLocationForm form,
                                          BindingResult bindingResult,
                                          ValidationType validationType,
                                          ProjectContext projectContext) {
    var projectDetail = projectContext.getProjectDetails();
    bindingResult = locationService.validate(form, bindingResult, projectDetail, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getLocationModelAndView(projectId, projectDetail, form, locationService.getValidatedBlockViewsFromForm(
            form,
            projectDetail
          )
        ),
        form,
        () -> {
          var projectLocation = locationService.createOrUpdate(projectDetail, form);
          locationService.createOrUpdateBlocks(form.getLicenceBlocks(), projectLocation);

          AuditService.audit(
              AuditEvent.LOCATION_INFORMATION_UPDATED,
              String.format(
                  AuditEvent.LOCATION_INFORMATION_UPDATED.getMessage(),
                  projectDetail.getId()
              )
          );
          return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
        });
  }


  public ModelAndView getLocationModelAndView(
      Integer projectId,
      ProjectDetail projectDetail,
      ProjectLocationForm form,
      List<ProjectLocationBlockView> blockViews
  ) {
    var modelAndView = new ModelAndView("project/location/location")
        .addObject("isOilAndGasProject", projectInformationService.isOilAndGasProject(projectDetail))
        .addObject("longitudeHemispheres", CoordinateInputLongitudeHemisphere.getFdsSelectMap())
        .addObject("fieldsRestUrl", SearchSelectorService.route(on(DevUkRestController.class).searchFields(null)))
        .addObject("blocksRestUrl", SearchSelectorService.route(on(LicenceBlocksRestController.class).searchLicenceBlocks(null)))
        .addObject("form", form)
        .addObject("fieldTypeMap", FieldType.getAllAsMap())
        .addObject("alreadyAddedBlocks", blockViews)
        .addObject("preselectedField", locationService.getPreSelectedField(form))
        .addObject("waterDepthUnit", MeasurementUnits.METRES);

    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);
    return modelAndView;
  }
}
