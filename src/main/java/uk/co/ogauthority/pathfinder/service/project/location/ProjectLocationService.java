package uk.co.ogauthority.pathfinder.service.project.location;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationValidationHint;
import uk.co.ogauthority.pathfinder.model.view.location.ProjectLocationBlockView;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFieldService;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.util.projectcontext.UserToProjectRelationshipUtil;

@Service
public class ProjectLocationService implements ProjectFormSectionService {

  private final ProjectLocationRepository projectLocationRepository;
  private final DevUkFieldService fieldService;
  private final SearchSelectorService searchSelectorService;
  private final ValidationService validationService;
  private final ProjectLocationFormValidator projectLocationFormValidator;
  private final ProjectLocationBlocksService projectLocationBlocksService;
  private final EntityDuplicationService entityDuplicationService;
  private final ProjectInformationService projectInformationService;

  @Autowired
  public ProjectLocationService(
      ProjectLocationRepository projectLocationRepository,
      DevUkFieldService fieldService,
      SearchSelectorService searchSelectorService,
      ValidationService validationService,
      ProjectLocationFormValidator projectLocationFormValidator,
      ProjectLocationBlocksService projectLocationBlocksService,
      EntityDuplicationService entityDuplicationService,
      ProjectInformationService projectInformationService
  ) {
    this.projectLocationRepository = projectLocationRepository;
    this.fieldService = fieldService;
    this.searchSelectorService = searchSelectorService;
    this.validationService = validationService;
    this.projectLocationFormValidator = projectLocationFormValidator;
    this.projectLocationBlocksService = projectLocationBlocksService;
    this.entityDuplicationService = entityDuplicationService;
    this.projectInformationService = projectInformationService;
  }

  @Transactional
  public ProjectLocation createOrUpdate(ProjectDetail detail, ProjectLocationForm form) {
    var projectLocation = getProjectLocationByProjectDetail(detail).orElse(new ProjectLocation(detail));

    var centreOfInterestLatitude = form.getCentreOfInterestLatitude();
    projectLocation.setCentreOfInterestLatitudeDegrees(centreOfInterestLatitude.getDegreesInput().getAsInteger().orElse(null));
    projectLocation.setCentreOfInterestLatitudeMinutes(centreOfInterestLatitude.getMinutesInput().getAsInteger().orElse(null));
    projectLocation.setCentreOfInterestLatitudeSeconds(centreOfInterestLatitude.getSecondsInput().getAsDouble().orElse(null));
    projectLocation.setCentreOfInterestLatitudeHemisphere(centreOfInterestLatitude.getHemisphereInput().getInputValue());

    var centreOfInterestLongitude = form.getCentreOfInterestLongitude();
    projectLocation.setCentreOfInterestLongitudeDegrees(centreOfInterestLongitude.getDegreesInput().getAsInteger().orElse(null));
    projectLocation.setCentreOfInterestLongitudeMinutes(centreOfInterestLongitude.getMinutesInput().getAsInteger().orElse(null));
    projectLocation.setCentreOfInterestLongitudeSeconds(centreOfInterestLongitude.getSecondsInput().getAsDouble().orElse(null));
    projectLocation.setCentreOfInterestLongitudeHemisphere(centreOfInterestLongitude.getHemisphereInput().getInputValue());

    var isOilAndGasProject = projectInformationService.isOilAndGasProject(detail);
    if (isOilAndGasProject) {
      if (form.getField() != null) {
        projectLocation.setField(fieldService.findByIdOrError(Integer.parseInt(form.getField())));
      } else { //The form has no data so clear the existing values
        projectLocation.setField(null);
      }

      projectLocation.setFieldType(form.getFieldType());
      projectLocation.setMaximumWaterDepth(form.getMaximumWaterDepth());

      projectLocation.setApprovedFieldDevelopmentPlan(form.getApprovedFieldDevelopmentPlan());
      projectLocation.setApprovedFdpDate(
          BooleanUtils.isTrue(form.getApprovedFieldDevelopmentPlan())
              ? form.getApprovedFdpDate().createDateOrNull()
              : null
      );

      projectLocation.setApprovedDecomProgram(form.getApprovedDecomProgram());
      projectLocation.setApprovedDecomProgramDate(
          BooleanUtils.isTrue(form.getApprovedDecomProgram())
              ? form.getApprovedDecomProgramDate().createDateOrNull()
              : null
      );
    }

    return projectLocationRepository.save(projectLocation);
  }

  public void createOrUpdateBlocks(List<String> licenceBlockIds, ProjectLocation projectLocation) {
    projectLocationBlocksService.createOrUpdateBlocks(licenceBlockIds, projectLocation);
  }

  public Optional<ProjectLocation> getProjectLocationByProjectDetail(ProjectDetail detail) {
    return projectLocationRepository.findByProjectDetail(detail);
  }

  public Optional<ProjectLocation> getProjectLocationByProjectAndVersion(Project project, Integer version) {
    return projectLocationRepository.findByProjectDetail_ProjectAndProjectDetail_Version(project, version);
  }

  public ProjectLocation getOrError(ProjectDetail detail) {
    return getProjectLocationByProjectDetail(detail).orElseThrow(
        () -> new PathfinderEntityNotFoundException(
            String.format("Unable to find ProjectLocation for projectDetail with id: %d", detail.getId())
        )
    );
  }


  public ProjectLocationForm getForm(ProjectDetail detail) {
    return getProjectLocationByProjectDetail(detail)
        .map(location -> this.getForm(detail, location)).orElse(new ProjectLocationForm());
  }

  /**
   * If the projectLocation has a manual field name set it in the form.
   * If the project location has a field set the id as a String.
   * @param projectLocation ProjectLocation to turn into a form
   * @return completed form object if ProjectLocation has any field data else a new form.
   */
  private ProjectLocationForm getForm(ProjectDetail detail, ProjectLocation projectLocation) {
    var form = new ProjectLocationForm();

    var centreOfInterestLatitude = form.getCentreOfInterestLatitude();
    centreOfInterestLatitude.getDegreesInput().setInputValue(projectLocation.getCentreOfInterestLatitudeDegrees() != null
        ? projectLocation.getCentreOfInterestLatitudeDegrees().toString()
        : null);
    centreOfInterestLatitude.getMinutesInput().setInputValue(projectLocation.getCentreOfInterestLatitudeMinutes() != null
        ? projectLocation.getCentreOfInterestLatitudeMinutes().toString()
        : null);
    centreOfInterestLatitude.getSecondsInput().setInputValue(projectLocation.getCentreOfInterestLatitudeSeconds() != null
        ? projectLocation.getCentreOfInterestLatitudeSeconds().toString()
        : null);

    var centreOfInterestLongitude = form.getCentreOfInterestLongitude();
    centreOfInterestLongitude.getDegreesInput().setInputValue(projectLocation.getCentreOfInterestLongitudeDegrees() != null
        ? projectLocation.getCentreOfInterestLongitudeDegrees().toString()
        : null);
    centreOfInterestLongitude.getMinutesInput().setInputValue(projectLocation.getCentreOfInterestLongitudeMinutes() != null
        ? projectLocation.getCentreOfInterestLongitudeMinutes().toString()
        : null);
    centreOfInterestLongitude.getSecondsInput().setInputValue(projectLocation.getCentreOfInterestLongitudeSeconds() != null
        ? projectLocation.getCentreOfInterestLongitudeSeconds().toString()
        : null);
    centreOfInterestLongitude.getHemisphereInput().setInputValue(projectLocation.getCentreOfInterestLongitudeHemisphere());

    var isOilAndGasProject = projectInformationService.isOilAndGasProject(detail);
    if (isOilAndGasProject) {
      if (projectLocation.getField() != null) {
        form.setField(projectLocation.getField().getFieldId().toString());
      }

      form.setFieldType(projectLocation.getFieldType());
      form.setMaximumWaterDepth(projectLocation.getMaximumWaterDepth());

      form.setApprovedDecomProgram(projectLocation.getApprovedDecomProgram());
      form.setApprovedDecomProgramDate(new ThreeFieldDateInput(projectLocation.getApprovedDecomProgramDate()));

      form.setApprovedFieldDevelopmentPlan(projectLocation.getApprovedFieldDevelopmentPlan());
      form.setApprovedFdpDate(new ThreeFieldDateInput(projectLocation.getApprovedFdpDate()));
      projectLocationBlocksService.addBlocksToForm(form, projectLocation);
    }

    return form;
  }

  /**
   * Validate the projectLocationForm, calls custom validator first.
   * Validates dates if FDP or Decom program questions are true.
   */
  public BindingResult validate(ProjectLocationForm form,
                                BindingResult bindingResult,
                                ProjectDetail projectDetail,
                                ValidationType validationType) {
    var projectLocationValidationHint = new ProjectLocationValidationHint(projectDetail, validationType);
    projectLocationFormValidator.validate(form, bindingResult, projectLocationValidationHint);
    return validationService.validate(form, bindingResult, validationType);
  }

  /**
   * If there's data in the form turn it back into a format the searchselector can parse.
   * @param form valid or invalid ProjectLocationForm
   * @return id and display name of the search selector items empty map if there's no form data.
   */
  public Map<String, String> getPreSelectedField(ProjectLocationForm form) {
    if (form.getField() != null) {
      var field = fieldService.findByIdOrError(Integer.parseInt(form.getField()));
      return SearchSelectorService.isManualEntry(form.getField())
        ? searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(form.getField()),
            Map.of(form.getField(), form.getField())
          )
        : searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(form.getField()),
            Map.of(form.getField(), field.getFieldName())
          );

    }
    return Map.of();
  }

  public List<ProjectLocationBlockView> getUnvalidatedBlockViewsForLocation(ProjectDetail detail) {
    return getBlockViewsForLocation(detail, ValidationType.NO_VALIDATION);
  }

  public List<ProjectLocationBlockView> getUnvalidatedBlockViewsFromForm(ProjectLocationForm form, ProjectDetail detail) {
    return getBlockViewsFromForm(form, detail, ValidationType.NO_VALIDATION);
  }

  public List<ProjectLocationBlockView> getValidatedBlockViewsForLocation(ProjectDetail detail) {
    return getBlockViewsForLocation(detail, ValidationType.FULL);
  }

  private List<ProjectLocationBlockView> getBlockViewsForLocation(ProjectDetail detail, ValidationType validationType) {
    var location = getProjectLocationByProjectDetail(detail);
    return location.isPresent()
        ? projectLocationBlocksService.getBlockViewsForLocation(location.get(), validationType)
        : Collections.emptyList();
  }

  public List<ProjectLocationBlockView> getValidatedBlockViewsFromForm(ProjectLocationForm form, ProjectDetail detail) {
    return getBlockViewsFromForm(form, detail, ValidationType.FULL);
  }

  private List<ProjectLocationBlockView> getBlockViewsFromForm(ProjectLocationForm form,
                                                               ProjectDetail detail,
                                                               ValidationType validationType
  ) {
    var views = projectLocationBlocksService.getBlockViewsFromForm(form, validationType);

    if (views.size() != form.getLicenceBlocks().size()) {
      getMissingPortalBlockViewsFromForm(form, detail, views, validationType);
    }

    views.sort(Comparator.comparing(ProjectLocationBlockView::getSortKey));

    return views;
  }

  /**
   * If a block is now invalid it won't have been found in the portal data (licensing hard deletes them).
   * Find the corresponding block(s) from ProjectLocationLicenceBlocks and add it (them)
   * @param form Project location form (with more licence block keys than found in the portal data).
   * @param detail project detail.
   * @param views the views from the respective projectDetails' ProjectLocationLicenceBlocks
   */
  private void getMissingPortalBlockViewsFromForm(ProjectLocationForm form,
                                                  ProjectDetail detail,
                                                  List<ProjectLocationBlockView> views,
                                                  ValidationType validationType
  ) {
    getProjectLocationByProjectDetail(detail).ifPresent(location -> {
      var missingViews = form.getLicenceBlocks().stream() //find out which ones are missing
          // (this will also avoid looking for duplicates)
          .filter(ref -> views.stream().noneMatch(view -> view.getCompositeKey().equals(ref)))
          .collect(Collectors.toList());

      views.addAll(
          projectLocationBlocksService.getBlockViewsByProjectLocationAndCompositeKeyIn(location, missingViews, validationType)
      );
    });

  }

  @Override
  public boolean isComplete(ProjectDetail details) {
    var form = getForm(details);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, details, ValidationType.FULL);
    return !bindingResult.hasErrors();
  }

  @Override
  public boolean isTaskValidForProjectDetail(ProjectDetail detail) {
    return ProjectService.isInfrastructureProject(detail);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    getProjectLocationByProjectDetail(projectDetail).ifPresent(projectLocation -> {
      projectLocationBlocksService.deleteBlocks(projectLocation);
      projectLocationRepository.delete(projectLocation);
    });
  }

  @Override
  public void removeSectionDataIfNotRelevant(ProjectDetail projectDetail) {
    getProjectLocationByProjectDetail(projectDetail).ifPresent(projectLocation -> {
      var isOilAndGasProject = projectInformationService.isOilAndGasProject(projectDetail);
      if (!isOilAndGasProject) {
        projectLocation.setField(null);
        projectLocation.setFieldType(null);
        projectLocation.setMaximumWaterDepth(null);
        projectLocation.setApprovedFieldDevelopmentPlan(null);
        projectLocation.setApprovedFdpDate(null);
        projectLocation.setApprovedDecomProgram(null);
        projectLocation.setApprovedDecomProgramDate(null);

        projectLocationRepository.save(projectLocation);

        projectLocationBlocksService.deleteBlocks(projectLocation);
      }
    });
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {

    final var fromLocation = getOrError(fromDetail);

    // duplicate ProjectLocation entity and reparent to toDetail
    final var toLocation = entityDuplicationService.duplicateEntityAndSetNewParent(
        fromLocation,
        toDetail,
        ProjectLocation.class
    );

    final var blocksLinkedToFromLocation =  projectLocationBlocksService.getBlocks(fromLocation);

    // duplicate the ProjectLocationBlock entities linked to the fromLocation and reparent to toLocation
    entityDuplicationService.duplicateEntitiesAndSetNewParent(
        blocksLinkedToFromLocation,
        toLocation,
        ProjectLocationBlock.class
    );

  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail, Set<UserToProjectRelationship> userToProjectRelationships) {
    return isTaskValidForProjectDetail(detail)
        && UserToProjectRelationshipUtil.canAccessProjectTask(ProjectTask.PROJECT_LOCATION, userToProjectRelationships);
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.INFRASTRUCTURE);
  }
}
