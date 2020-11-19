package uk.co.ogauthority.pathfinder.service.project.projectassessment;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.ManageProjectController;
import uk.co.ogauthority.pathfinder.controller.project.projectassessment.ProjectAssessmentController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectassessment.ProjectAssessment;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.projectassessment.ProjectQuality;
import uk.co.ogauthority.pathfinder.model.form.project.projectassessment.ProjectAssessmentForm;
import uk.co.ogauthority.pathfinder.model.form.project.projectassessment.ProjectAssessmentFormValidator;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.project.projectassessment.ProjectAssessmentRepository;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class ProjectAssessmentService {

  private final ProjectAssessmentRepository projectAssessmentRepository;
  private final ValidationService validationService;
  private final ProjectAssessmentFormValidator projectAssessmentFormValidator;

  @Autowired
  public ProjectAssessmentService(ProjectAssessmentRepository projectAssessmentRepository,
                                  ValidationService validationService,
                                  ProjectAssessmentFormValidator projectAssessmentFormValidator) {
    this.projectAssessmentRepository = projectAssessmentRepository;
    this.validationService = validationService;
    this.projectAssessmentFormValidator = projectAssessmentFormValidator;
  }

  @Transactional
  public ProjectAssessment createProjectAssessment(ProjectDetail projectDetail,
                                                   AuthenticatedUserAccount assessor,
                                                   ProjectAssessmentForm form) {
    var projectAssessment = new ProjectAssessment();
    projectAssessment.setProjectDetail(projectDetail);
    projectAssessment.setProjectQuality(form.getProjectQuality());
    projectAssessment.setReadyToBePublished(form.getReadyToBePublished());
    projectAssessment.setUpdateRequired(form.getUpdateRequired());
    projectAssessment.setAssessedInstant(Instant.now());
    projectAssessment.setAssessorWuaId(assessor.getWuaId());
    projectAssessmentRepository.save(projectAssessment);
    return projectAssessment;
  }

  public Optional<ProjectAssessment> getProjectAssessment(ProjectDetail projectDetail) {
    return projectAssessmentRepository.findByProjectDetail(projectDetail);
  }

  public BindingResult validate(ProjectAssessmentForm form, BindingResult bindingResult) {
    projectAssessmentFormValidator.validate(form, bindingResult);
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  public ModelAndView getProjectAssessmentModelAndView(Integer projectId, ProjectAssessmentForm form) {
    return new ModelAndView("project/projectassessment/projectAssessment")
        .addObject("pageName", ProjectAssessmentController.PAGE_NAME)
        .addObject("form", form)
        .addObject("projectQualities", ProjectQuality.getAllAsMap())
        .addObject("cancelUrl", ReverseRouter.route(on(ManageProjectController.class).getProject(projectId, null, null)));
  }
}
