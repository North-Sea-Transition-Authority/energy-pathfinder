package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignProject;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationForm;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.campaigninformation.CampaignInformationRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class CampaignInformationService implements ProjectFormSectionService {

  private final ProjectSetupService projectSetupService;
  private final CampaignInformationRepository campaignInformationRepository;
  private final ValidationService validationService;
  private final EntityDuplicationService entityDuplicationService;
  private final CampaignInformationFormValidator campaignInformationFormValidator;
  private final CampaignProjectService campaignProjectService;

  public CampaignInformationService(
      ProjectSetupService projectSetupService,
      CampaignInformationRepository campaignInformationRepository,
      ValidationService validationService,
      EntityDuplicationService entityDuplicationService,
      CampaignInformationFormValidator campaignInformationFormValidator,
      CampaignProjectService campaignProjectService
  ) {
    this.projectSetupService = projectSetupService;
    this.campaignInformationRepository = campaignInformationRepository;
    this.validationService = validationService;
    this.entityDuplicationService = entityDuplicationService;
    this.campaignInformationFormValidator = campaignInformationFormValidator;
    this.campaignProjectService = campaignProjectService;
  }

  @Transactional
  public CampaignInformation createOrUpdateCampaignInformation(CampaignInformationForm form,
                                                               ProjectDetail projectDetail) {
    var campaignInformation = campaignInformationRepository.findByProjectDetail(projectDetail)
        .orElse(new CampaignInformation());
    campaignInformation.setProjectDetail(projectDetail);
    campaignInformation.setScopeDescription(form.getScopeDescription());
    campaignInformation.setIsPartOfCampaign(form.isPartOfCampaign());
    campaignInformation = campaignInformationRepository.save(campaignInformation);
    campaignProjectService.persistCampaignProjects(campaignInformation, form.getProjectsIncludedInCampaign());
    return campaignInformation;
  }

  public CampaignInformationForm getForm(ProjectDetail projectDetail) {

    final var campaignProjects = campaignProjectService.getCampaignProjects(projectDetail);

    return campaignInformationRepository.findByProjectDetail(projectDetail)
        .map(campaignInformation -> populateForm(campaignInformation, campaignProjects))
        .orElse(new CampaignInformationForm());
  }

  private CampaignInformationForm populateForm(CampaignInformation campaignInformation,
                                               List<CampaignProject> campaignProjects) {
    var form = new CampaignInformationForm();
    form.setScopeDescription(campaignInformation.getScopeDescription());
    form.setIsPartOfCampaign(campaignInformation.isPartOfCampaign());

    final var publishedProjectIds = campaignProjects
        .stream()
        .map(campaignProject -> campaignProject.getProject().getProjectId())
        .collect(Collectors.toList());

    form.setProjectsIncludedInCampaign(publishedProjectIds);

    return form;
  }

  public CampaignInformation getOrError(ProjectDetail projectDetail) {
    return getCampaignInformationByProjectDetail(projectDetail).orElseThrow(() ->
        new PathfinderEntityNotFoundException(
            String.format("Unable to find campaign information with project detail with id: %s", projectDetail.getId())
        )
    );
  }

  protected Optional<CampaignInformation> getCampaignInformationByProjectDetail(ProjectDetail projectDetail) {
    return campaignInformationRepository.findByProjectDetail(projectDetail);
  }

  protected Optional<CampaignInformation> getCampaignInformationByProjectAndVersion(Project project, int version) {
    return campaignInformationRepository.findByProjectDetail_ProjectAndProjectDetail_Version(project, version);
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {
    var form = getForm(detail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, ValidationType.FULL, detail);
    return !bindingResult.hasErrors();
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.CAMPAIGN_INFORMATION);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {

    final var fromCampaignInformation = getOrError(fromDetail);

    final var toCampaignInformation = entityDuplicationService.duplicateEntityAndSetNewParent(
        fromCampaignInformation,
        toDetail,
        CampaignInformation.class
    );

    campaignProjectService.copyCampaignProjectsToNewCampaign(
        fromCampaignInformation,
        toCampaignInformation
    );
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.INFRASTRUCTURE);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    campaignProjectService.deleteAllCampaignProjects(projectDetail);
    campaignInformationRepository.deleteByProjectDetail(projectDetail);
  }

  public BindingResult validate(CampaignInformationForm form,
                                BindingResult bindingResult,
                                ValidationType validationType,
                                ProjectDetail projectDetail) {
    final var campaignInformationValidationHint = new CampaignInformationValidationHint(validationType, projectDetail);
    campaignInformationFormValidator.validate(form, bindingResult, campaignInformationValidationHint);
    return validationService.validate(form, bindingResult, validationType);
  }
}