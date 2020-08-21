package uk.co.ogauthority.pathfinder.service.project.projectinformation;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationForm;
import uk.co.ogauthority.pathfinder.repository.project.projectinformation.ProjectInformationRepository;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class ProjectInformationService {

  private final ProjectInformationRepository projectInformationRepository;
  private final ValidationService validationService;

  @Autowired
  public ProjectInformationService(ProjectInformationRepository projectInformationRepository,
                                   ValidationService validationService) {
    this.projectInformationRepository = projectInformationRepository;
    this.validationService = validationService;
  }

  @Transactional
  public ProjectInformation createOrUpdate(ProjectDetail projectDetail, ProjectInformationForm form) {
    var projectInformation = projectInformationRepository.findByProjectDetail(projectDetail)
        .orElse(new ProjectInformation());

    projectInformation.setProjectDetail(projectDetail);
    projectInformation.setFieldStage(form.getFieldStage());
    projectInformation.setProjectTitle(form.getProjectTitle());
    projectInformation.setProjectSummary(form.getProjectSummary());
    projectInformation.setContactName(form.getName());
    projectInformation.setPhoneNumber(form.getPhoneNumber());
    projectInformation.setJobTitle(form.getJobTitle());
    projectInformation.setEmailAddress(form.getEmailAddress());
    return projectInformationRepository.save(projectInformation);
  }

  public Optional<ProjectInformation> getProjectInformation(ProjectDetail projectDetail) {
    return projectInformationRepository.findByProjectDetail(projectDetail);
  }

  public ProjectInformationForm getForm(ProjectDetail projectDetail) {
    return projectInformationRepository.findByProjectDetail(projectDetail)
        .map(this::getForm).orElse(new ProjectInformationForm());
  }

  private ProjectInformationForm getForm(ProjectInformation projectInformation) {
    var form  = new ProjectInformationForm();
    form.setFieldStage(projectInformation.getFieldStage());
    form.setProjectTitle(projectInformation.getProjectTitle());
    form.setProjectSummary(projectInformation.getProjectSummary());
    form.setName(projectInformation.getContactName());
    form.setPhoneNumber(projectInformation.getPhoneNumber());
    form.setJobTitle(projectInformation.getJobTitle());
    form.setEmailAddress(projectInformation.getEmailAddress());

    return form;
  }

  /**
   * Validate the projectInformationForm, no partial validation just all fields complete or all optional.
   */
  public BindingResult validate(ProjectInformationForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    return validationService.validate(form, bindingResult, validationType);
  }


  public boolean isComplete(ProjectDetail details) {
    var form = getForm(details);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, ValidationType.FULL);
    return !bindingResult.hasErrors();
  }


}
