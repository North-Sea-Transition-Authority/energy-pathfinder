package uk.co.ogauthority.pathfinder.service.project.workoplanprojectcontribution;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.workplanprojectcontributor.ForwardWorkPlanProjectContributorsController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanprojectcontribution.ForwardWorkPlanContributorDetails;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorValidationHint;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsForm;
import uk.co.ogauthority.pathfinder.model.form.project.workoplanprojectcontribution.ForwardWorkPlanProjectContributorsForm;
import uk.co.ogauthority.pathfinder.model.form.project.workoplanprojectcontribution.ForwardWorkPlanProjectContributorsFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.workplanprojectcontributor.ForwardWorkPlanContributorDetailsRepository;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.projectcontribution.ProjectContributorsCommonService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class ForwardWorkPlanProjectContributorManagementService {

  private final ProjectContributorsCommonService projectContributorsCommonService;
  private final ForwardWorkPlanContributorDetailsRepository forwardWorkPlanContributorDetailsRepository;
  private final BreadcrumbService breadcrumbService;
  private final ValidationService validationService;
  private final ForwardWorkPlanProjectContributorsFormValidator forwardWorkPlanProjectContributorsFormValidator;

  @Autowired
  public ForwardWorkPlanProjectContributorManagementService(
      ProjectContributorsCommonService projectContributorsCommonService,
      ForwardWorkPlanContributorDetailsRepository forwardWorkPlanContributorDetailsRepository,
      BreadcrumbService breadcrumbService,
      ValidationService validationService,
      ForwardWorkPlanProjectContributorsFormValidator forwardWorkPlanProjectContributorsFormValidator) {
    this.projectContributorsCommonService = projectContributorsCommonService;
    this.forwardWorkPlanContributorDetailsRepository = forwardWorkPlanContributorDetailsRepository;
    this.breadcrumbService = breadcrumbService;
    this.validationService = validationService;
    this.forwardWorkPlanProjectContributorsFormValidator = forwardWorkPlanProjectContributorsFormValidator;
  }

  public ModelAndView getProjectContributorsFormModelAndView(ForwardWorkPlanProjectContributorsForm form,
                                                             ProjectDetail projectDetail,
                                                             List<FieldError> errorList) {
    var modelAndView = new ModelAndView(
        "project/workplanprojectcontributors/workPlanProjectContributors");

    projectContributorsCommonService.setModelAndViewCommonObjects(
        modelAndView,
        projectDetail,
        form,
        ForwardWorkPlanProjectContributorsController.PAGE_NAME,
        errorList
    );

    breadcrumbService.fromTaskList(
        projectDetail.getProject().getId(),
        modelAndView,
        ForwardWorkPlanProjectContributorsController.TASK_LIST_NAME
    );

    return modelAndView;
  }

  public ForwardWorkPlanProjectContributorsForm getForm(ProjectDetail detail) {
    ForwardWorkPlanProjectContributorsForm form = new ForwardWorkPlanProjectContributorsForm();
    forwardWorkPlanContributorDetailsRepository.findByProjectDetail(detail)
        .ifPresent(workPlanProjectContributor -> {
          form.setHasProjectContributors(workPlanProjectContributor.getHasProjectContributors());
          projectContributorsCommonService.setContributorsInForm(form, detail);
        });
    return form;
  }

  @Transactional
  public void saveForwardWorkPlanProjectContributors(ForwardWorkPlanProjectContributorsForm form,
                                                     ProjectDetail projectDetail) {
    if (form.getHasProjectContributors() != null) {
      var forwardWorkPlanProjectContributor =
          forwardWorkPlanContributorDetailsRepository.findByProjectDetail(projectDetail)
              .orElse(new ForwardWorkPlanContributorDetails(projectDetail, null));

      forwardWorkPlanProjectContributor.setHasProjectContributors(form.getHasProjectContributors());

      forwardWorkPlanContributorDetailsRepository.save(forwardWorkPlanProjectContributor);

      if (form.getHasProjectContributors()) {
        projectContributorsCommonService.saveProjectContributors(form, projectDetail);
      } else {
        projectContributorsCommonService.deleteProjectContributors(projectDetail);
      }
    }
  }

  public BindingResult validate(ProjectContributorsForm form,
                                BindingResult bindingResult,
                                ValidationType validationType,
                                ProjectDetail detail) {
    var projectContributorValidationHint = new ProjectContributorValidationHint(validationType, detail);
    forwardWorkPlanProjectContributorsFormValidator.validate(form, bindingResult, projectContributorValidationHint);
    return validationService.validate(form, bindingResult, validationType);
  }

  boolean isValid(ProjectDetail projectDetail, ValidationType validationType) {
    var form = getForm(projectDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType, projectDetail);
    return !bindingResult.hasErrors();
  }

  ForwardWorkPlanContributorDetails getForwardProjectContributorForDetail(ProjectDetail detail) {
    return forwardWorkPlanContributorDetailsRepository.findByProjectDetail(detail)
        .orElse(new ForwardWorkPlanContributorDetails(detail, null));
  }

  void removeForwardProjectContributorsForDetail(ProjectDetail detail) {
    forwardWorkPlanContributorDetailsRepository.deleteByProjectDetail(detail);
    projectContributorsCommonService.deleteProjectContributors(detail);
  }
}
