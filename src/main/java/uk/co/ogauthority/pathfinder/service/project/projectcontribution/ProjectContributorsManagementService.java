package uk.co.ogauthority.pathfinder.service.project.projectcontribution;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.projectcontributor.ProjectContributorsController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorValidationHint;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsForm;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsFormValidator;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class ProjectContributorsManagementService {

  private final BreadcrumbService breadcrumbService;
  private final ValidationService validationService;
  private final ProjectContributorsCommonService projectContributorsCommonService;
  private final ProjectContributorsFormValidator projectContributorsFormValidator;

  @Autowired
  public ProjectContributorsManagementService(
      BreadcrumbService breadcrumbService,
      ValidationService validationService,
      ProjectContributorsCommonService projectContributorsCommonService,
      ProjectContributorsFormValidator projectContributorsFormValidator) {
    this.breadcrumbService = breadcrumbService;
    this.validationService = validationService;
    this.projectContributorsCommonService = projectContributorsCommonService;
    this.projectContributorsFormValidator = projectContributorsFormValidator;
  }

  public ModelAndView getProjectContributorsFormModelAndView(ProjectContributorsForm form,
                                                             ProjectDetail projectDetail,
                                                             List<FieldError> errorList) {
    var modelAndView = new ModelAndView("project/projectcontributors/projectContributors");

    projectContributorsCommonService.setModelAndViewCommonObjects(
        modelAndView,
        projectDetail,
        form,
        ProjectContributorsController.FORM_PAGE_NAME,
        errorList
    );

    breadcrumbService.fromTaskList(
        projectDetail.getProject().getId(),
        modelAndView,
        ProjectContributorsController.TASK_LIST_NAME
    );

    return modelAndView;

  }

  public void saveProjectContributors(ProjectContributorsForm form, ProjectDetail projectDetail) {
    projectContributorsCommonService.saveProjectContributors(form, projectDetail);
  }

  public boolean isValid(ProjectDetail projectDetail, ValidationType validationType) {
    var form = getForm(projectDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType, projectDetail);
    return !bindingResult.hasErrors();
  }

  public BindingResult validate(ProjectContributorsForm form,
                                BindingResult bindingResult,
                                ValidationType validationType,
                                ProjectDetail detail) {
    final var projectContributorValidationHint = new ProjectContributorValidationHint(validationType, detail);
    projectContributorsFormValidator.validate(form, bindingResult, projectContributorValidationHint);
    return validationService.validate(form, bindingResult, validationType);
  }

  public ProjectContributorsForm getForm(ProjectDetail projectDetail) {
    var form = new ProjectContributorsForm();
    projectContributorsCommonService.setContributorsInForm(form, projectDetail);
    return form;
  }

  void removeProjectContributorsForDetail(ProjectDetail detail) {
    projectContributorsCommonService.deleteProjectContributors(detail);
  }
}
