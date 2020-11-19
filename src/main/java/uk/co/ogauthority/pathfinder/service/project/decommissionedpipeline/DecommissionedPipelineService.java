package uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline;

import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedpipeline.DecommissionedPipeline;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineForm;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.decommissionedpipeline.DecommissionedPipelineRepository;
import uk.co.ogauthority.pathfinder.service.pipeline.PipelineService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class DecommissionedPipelineService implements ProjectFormSectionService {

  private final PipelineService pipelineService;
  private final DecommissionedPipelineRepository decommissionedPipelineRepository;
  private final DecommissionedPipelineFormValidator decommissionedPipelineFormValidator;
  private final ValidationService validationService;
  private final ProjectSetupService projectSetupService;

  @Autowired
  public DecommissionedPipelineService(PipelineService pipelineService,
                                       DecommissionedPipelineRepository decommissionedPipelineRepository,
                                       DecommissionedPipelineFormValidator decommissionedPipelineFormValidator,
                                       ValidationService validationService,
                                       ProjectSetupService projectSetupService) {
    this.pipelineService = pipelineService;
    this.decommissionedPipelineRepository = decommissionedPipelineRepository;
    this.decommissionedPipelineFormValidator = decommissionedPipelineFormValidator;
    this.validationService = validationService;
    this.projectSetupService = projectSetupService;
  }

  public DecommissionedPipelineForm getForm(Integer decommissionedPipelineId, ProjectDetail projectDetail) {
    var decommissionedPipeline = getDecommissionedPipelineOrError(decommissionedPipelineId, projectDetail);
    return getForm(decommissionedPipeline);
  }

  private DecommissionedPipelineForm getForm(DecommissionedPipeline decommissionedPipeline) {
    var form = new DecommissionedPipelineForm();

    String pipeline = null;

    if (decommissionedPipeline.getPipeline() != null) {
      pipeline = decommissionedPipeline.getPipeline().getSelectionId();
    }

    form.setPipeline(pipeline);
    form.setMaterialType(decommissionedPipeline.getMaterialType());
    form.setStatus(decommissionedPipeline.getStatus());
    form.setDecommissioningDate(new MinMaxDateInput(
        decommissionedPipeline.getEarliestRemovalYear(),
        decommissionedPipeline.getLatestRemovalYear())
    );
    form.setRemovalPremise(decommissionedPipeline.getRemovalPremise());

    return form;
  }

  public String getPipelineRestUrl() {
    return pipelineService.getPipelineRestUrl();
  }

  public Map<String, String> getPreSelectedPipeline(DecommissionedPipelineForm form) {
    return pipelineService.getPreSelectedPipeline(form.getPipeline());
  }

  public BindingResult validate(DecommissionedPipelineForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    var decommissionedPipelineValidationHint = new DecommissionedPipelineValidationHint(validationType);
    decommissionedPipelineFormValidator.validate(form, bindingResult, decommissionedPipelineValidationHint);
    return validationService.validate(form, bindingResult, validationType);
  }

  public DecommissionedPipeline createDecommissionedPipeline(ProjectDetail projectDetail,
                                                             DecommissionedPipelineForm form) {
    var decommissionedPipeline = new DecommissionedPipeline();
    return createOrUpdateDecommissionedPipeline(decommissionedPipeline, projectDetail, form);
  }

  public DecommissionedPipeline updateDecommissionedPipeline(Integer decommissionedPipelineId,
                                                             ProjectDetail projectDetail,
                                                             DecommissionedPipelineForm form) {
    var decommissionedPipeline = getDecommissionedPipelineOrError(decommissionedPipelineId, projectDetail);
    return createOrUpdateDecommissionedPipeline(decommissionedPipeline, projectDetail, form);
  }

  @Transactional
  DecommissionedPipeline createOrUpdateDecommissionedPipeline(DecommissionedPipeline decommissionedPipeline,
                                                              ProjectDetail projectDetail,
                                                              DecommissionedPipelineForm form) {
    setCommonEntityFields(decommissionedPipeline, projectDetail, form);
    return decommissionedPipelineRepository.save(decommissionedPipeline);
  }

  public List<DecommissionedPipeline> getDecommissionedPipelines(ProjectDetail projectDetail) {
    return decommissionedPipelineRepository.findByProjectDetailOrderByIdAsc(projectDetail);
  }

  public boolean isValid(DecommissionedPipeline decommissionedPipeline, ValidationType validationType) {
    var form = getForm(decommissionedPipeline);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  @Transactional
  public void deleteDecommissionedPipeline(DecommissionedPipeline decommissionedPipeline) {
    decommissionedPipelineRepository.delete(decommissionedPipeline);
  }

  private void setCommonEntityFields(DecommissionedPipeline decommissionedPipeline,
                                     ProjectDetail projectDetail,
                                     DecommissionedPipelineForm form) {
    decommissionedPipeline.setProjectDetail(projectDetail);

    if (form.getPipeline() != null) {
      decommissionedPipeline.setPipeline(pipelineService.getPipelineByIdOrError(Integer.parseInt(form.getPipeline())));
    } else {
      decommissionedPipeline.setPipeline(null);
    }

    decommissionedPipeline.setMaterialType(form.getMaterialType());
    decommissionedPipeline.setStatus(form.getStatus());
    decommissionedPipeline.setEarliestRemovalYear(form.getDecommissioningDate().getMinYear());
    decommissionedPipeline.setLatestRemovalYear(form.getDecommissioningDate().getMaxYear());
    decommissionedPipeline.setRemovalPremise(form.getRemovalPremise());
  }

  public DecommissionedPipeline getDecommissionedPipelineOrError(Integer decommissionedPipelineId, ProjectDetail projectDetail) {
    return decommissionedPipelineRepository.findByIdAndProjectDetail(decommissionedPipelineId, projectDetail)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(String.format(
            "Could not find DecommissionedPipeline with ID %d for ProjectDetail with ID %s",
            decommissionedPipelineId,
            projectDetail.getId()
        )));
  }

  @Override
  public boolean isComplete(ProjectDetail projectDetail) {
    var decommissionedPipelines = getDecommissionedPipelines(projectDetail);
    return !decommissionedPipelines.isEmpty() && decommissionedPipelines.stream()
        .allMatch(decommissionedPipeline -> isValid(decommissionedPipeline, ValidationType.FULL));
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return projectSetupService.taskSelectedForProjectDetail(detail, ProjectTask.PIPELINES);
  }
}
