package uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline;

import java.util.Map;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedpipeline.DecommissionedPipeline;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineForm;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.decommissionedpipeline.DecommissionedPipelineRepository;
import uk.co.ogauthority.pathfinder.service.pipeline.PipelinesService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class DecommissionedPipelineService {

  private final PipelinesService pipelinesService;
  private final DecommissionedPipelineRepository decommissionedPipelineRepository;
  private final DecommissionedPipelineFormValidator decommissionedPipelineFormValidator;
  private final ValidationService validationService;

  @Autowired
  public DecommissionedPipelineService(PipelinesService pipelinesService,
                                       DecommissionedPipelineRepository decommissionedPipelineRepository,
                                       DecommissionedPipelineFormValidator decommissionedPipelineFormValidator,
                                       ValidationService validationService) {
    this.pipelinesService = pipelinesService;
    this.decommissionedPipelineRepository = decommissionedPipelineRepository;
    this.decommissionedPipelineFormValidator = decommissionedPipelineFormValidator;
    this.validationService = validationService;
  }

  public String getPipelinesRestUrl() {
    return pipelinesService.getPipelinesRestUrl();
  }

  public Map<String, String> getPreSelectedPipeline(DecommissionedPipelineForm form) {
    return pipelinesService.getPreSelectedPipeline(form.getPipeline());
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

  @Transactional
  DecommissionedPipeline createOrUpdateDecommissionedPipeline(DecommissionedPipeline decommissionedPipeline,
                                                     ProjectDetail projectDetail,
                                                     DecommissionedPipelineForm form) {
    setCommonEntityFields(decommissionedPipeline, projectDetail, form);
    return decommissionedPipelineRepository.save(decommissionedPipeline);
  }

  private void setCommonEntityFields(DecommissionedPipeline decommissionedPipeline,
                                     ProjectDetail projectDetail,
                                     DecommissionedPipelineForm form) {
    decommissionedPipeline.setProjectDetail(projectDetail);

    if (form.getPipeline() != null) {
      decommissionedPipeline.setPipeline(pipelinesService.getPipelineByIdOrError(Integer.parseInt(form.getPipeline())));
    } else {
      decommissionedPipeline.setPipeline(null);
    }

    decommissionedPipeline.setMaterialType(form.getMaterialType());
    decommissionedPipeline.setStatus(form.getStatus());
    decommissionedPipeline.setEarliestRemovalYear(form.getDecommissioningYears().getMinYear());
    decommissionedPipeline.setLatestRemovalYear(form.getDecommissioningYears().getMaxYear());
    decommissionedPipeline.setRemovalPremise(form.getRemovalPremise());
  }
}
