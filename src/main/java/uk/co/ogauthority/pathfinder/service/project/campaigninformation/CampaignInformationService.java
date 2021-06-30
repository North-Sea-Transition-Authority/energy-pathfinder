package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.campaigninformation.CampaignInformationController;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationForm;
import uk.co.ogauthority.pathfinder.repository.project.campaigninformation.CampaignInformationRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class CampaignInformationService implements ProjectFormSectionService {

  public static final String FORM_TEMPLATE_PATH = "project/campaigninformation/campaignInformationForm";

  private final ProjectSetupService projectSetupService;
  private final CampaignInformationRepository campaignInformationRepository;
  private final ValidationService validationService;
  private final EntityDuplicationService entityDuplicationService;
  private final BreadcrumbService breadcrumbService;

  public CampaignInformationService(
      ProjectSetupService projectSetupService,
      CampaignInformationRepository campaignInformationRepository,
      ValidationService validationService,
      EntityDuplicationService entityDuplicationService,
      BreadcrumbService breadcrumbService
  ) {
    this.projectSetupService = projectSetupService;
    this.campaignInformationRepository = campaignInformationRepository;
    this.validationService = validationService;
    this.entityDuplicationService = entityDuplicationService;
    this.breadcrumbService = breadcrumbService;
  }

  @Transactional
  public CampaignInformation createOrUpdateCampaignInformation(CampaignInformationForm form,
                                                               ProjectDetail projectDetail) {
    var campaignInformation = campaignInformationRepository.findByProjectDetail(projectDetail)
        .orElse(new CampaignInformation());
    campaignInformation.setProjectDetail(projectDetail);
    campaignInformation.setScopeDescription(form.getScopeDescription());
    campaignInformation.setPublishedCampaign(form.isPublishedCampaign());
    return campaignInformationRepository.save(campaignInformation);
  }

  public CampaignInformationForm getForm(ProjectDetail projectDetail) {
    return campaignInformationRepository.findByProjectDetail(projectDetail)
        .map(this::populateForm)
        .orElse(new CampaignInformationForm());
  }

  private CampaignInformationForm populateForm(CampaignInformation campaignInformation) {
    var form = new CampaignInformationForm();
    form.setScopeDescription(campaignInformation.getScopeDescription());
    form.setPublishedCampaign(campaignInformation.isPublishedCampaign());

    return form;
  }

  public CampaignInformation getOrError(ProjectDetail projectDetail) {
    return campaignInformationRepository.findByProjectDetail(projectDetail).orElseThrow(() ->
        new PathfinderEntityNotFoundException(
            String.format("Unable to find campaign information for project detail with id: %s", projectDetail.getId())
        )
    );
  }

  public ModelAndView getCampaignInformationModelAndView(ProjectDetail projectDetail, CampaignInformationForm form) {

    final var modelAndView = new ModelAndView(FORM_TEMPLATE_PATH)
        .addObject("pageTitle", CampaignInformationController.PAGE_NAME)
        .addObject("form", form);

    ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(modelAndView, projectDetail);

    breadcrumbService.fromTaskList(
        projectDetail.getProject().getId(),
        modelAndView,
        CampaignInformationController.PAGE_NAME
    );

    return modelAndView;
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {
    var form = getForm(detail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, ValidationType.FULL);
    return !bindingResult.hasErrors();
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.CAMPAIGN_INFORMATION);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    entityDuplicationService.duplicateEntityAndSetNewParent(
        getOrError(fromDetail),
        toDetail,
        CampaignInformation.class
    );
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    campaignInformationRepository.deleteByProjectDetail(projectDetail);
  }

  public BindingResult validate(
      CampaignInformationForm form,
      BindingResult bindingResult,
      ValidationType validationType
  ) {
    return validationService.validate(form, bindingResult, validationType);
  }
}