package uk.co.ogauthority.pathfinder.service.project.projectinformation;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetails;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationForm;
import uk.co.ogauthority.pathfinder.repository.project.projectinformation.ProjectInformationRepository;

@Service
public class ProjectInformationService {

  private final ProjectInformationRepository projectInformationRepository;
  private final SpringValidatorAdapter validator;

  @Autowired
  public ProjectInformationService(ProjectInformationRepository projectInformationRepository,
                                   SpringValidatorAdapter validator) {
    this.projectInformationRepository = projectInformationRepository;
    this.validator = validator;
  }

  @Transactional
  public ProjectInformation createOrUpdate(ProjectDetails projectDetail, ProjectInformationForm form) {
    var projectInformationOpt = projectInformationRepository.findByProjectDetail(projectDetail);
    var projectInformation = projectInformationOpt.orElse(new ProjectInformation());

    projectInformation.setProjectDetail(projectDetail);
    projectInformation.setFieldStage(form.getFieldStage());
    projectInformation.setProjectTitle(form.getProjectTitle());
    projectInformation.setProjectSummary(form.getProjectSummary());
    return projectInformationRepository.save(projectInformation);
  }

  public Optional<ProjectInformation> getProjectInformation(ProjectDetails projectDetails) {
    return projectInformationRepository.findByProjectDetail(projectDetails);
  }

  public ProjectInformationForm getForm(ProjectDetails projectDetails) {
    return projectInformationRepository.findByProjectDetail(projectDetails)
        .map(this::getForm).orElse(new ProjectInformationForm());
  }

  private ProjectInformationForm getForm(ProjectInformation projectInformation) {
    var form  = new ProjectInformationForm();
    form.setFieldStage(projectInformation.getFieldStage());
    form.setProjectTitle(projectInformation.getProjectTitle());
    form.setProjectSummary(projectInformation.getProjectSummary());

    return form;
  }

  /**
   * Validate the projectInformationForm, no partial validation just all fields complete or all optional.
   */
  public BindingResult validate(ProjectInformationForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    if (validationType.equals(ValidationType.FULL)) {
      validator.validate(form, bindingResult, ProjectInformationForm.Full.class);
    }

    return bindingResult;
  }


  public boolean isComplete(ProjectDetails details) {
    var form = getForm(details);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, ValidationType.FULL);
    return !bindingResult.hasErrors();
  }


}
