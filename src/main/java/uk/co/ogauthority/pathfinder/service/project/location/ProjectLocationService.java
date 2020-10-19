package uk.co.ogauthority.pathfinder.service.project.location;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationValidationHint;
import uk.co.ogauthority.pathfinder.model.view.projectlocation.ProjectLocationBlockView;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFieldService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class ProjectLocationService {

  private final ProjectLocationRepository projectLocationRepository;
  private final DevUkFieldService fieldService;
  private final SearchSelectorService searchSelectorService;
  private final ValidationService validationService;
  private final ProjectLocationFormValidator projectLocationFormValidator;
  private final ProjectLocationBlocksService projectLocationBlocksService;

  @Autowired
  public ProjectLocationService(ProjectLocationRepository projectLocationRepository,
                                DevUkFieldService fieldService,
                                SearchSelectorService searchSelectorService,
                                ValidationService validationService,
                                ProjectLocationFormValidator projectLocationFormValidator,
                                ProjectLocationBlocksService projectLocationBlocksService) {
    this.projectLocationRepository = projectLocationRepository;
    this.fieldService = fieldService;
    this.searchSelectorService = searchSelectorService;
    this.validationService = validationService;
    this.projectLocationFormValidator = projectLocationFormValidator;
    this.projectLocationBlocksService = projectLocationBlocksService;
  }

  @Transactional
  public ProjectLocation createOrUpdate(ProjectDetail detail, ProjectLocationForm form) {
    var projectLocation = findByProjectDetail(detail).orElse(new ProjectLocation(detail));

    if (SearchSelectorService.isManualEntry(form.getField())) {
      projectLocation.setManualFieldName(SearchSelectorService.removePrefix(form.getField()));
      projectLocation.setField(null);
    } else if (form.getField() != null) {
      projectLocation.setField(fieldService.findById(Integer.parseInt(form.getField())));
      projectLocation.setManualFieldName(null);
    } else { //The form has no data so clear the existing values
      projectLocation.setField(null);
      projectLocation.setManualFieldName(null);
    }

    projectLocation.setFieldType(form.getFieldType());
    projectLocation.setWaterDepth(form.getWaterDepth());

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
    projectLocation.setUkcsArea(form.getUkcsArea());

    return projectLocationRepository.save(projectLocation);
  }

  public void createOrUpdateBlocks(List<String> licenceBlockIds, ProjectLocation projectLocation) {
    projectLocationBlocksService.createOrUpdateBlocks(licenceBlockIds, projectLocation);
  }

  public Optional<ProjectLocation> findByProjectDetail(ProjectDetail detail) {
    return projectLocationRepository.findByProjectDetail(detail);
  }

  public ProjectLocation getOrError(ProjectDetail detail) {
    return findByProjectDetail(detail).orElseThrow(
        () -> new PathfinderEntityNotFoundException(
            String.format("Unable to find ProjectLocation for projectDetail with id: %d", detail.getId())
        )
    );
  }


  public ProjectLocationForm getForm(ProjectDetail detail) {
    return findByProjectDetail(detail)
        .map(this::getForm).orElse(new ProjectLocationForm());
  }

  /**
   * If the projectLocation has a manual field name set it in the form.
   * If the project location has a field set the id as a String.
   * @param projectLocation ProjectLocation to turn into a form
   * @return completed form object if ProjectLocation has any field data else a new form.
   */
  private ProjectLocationForm getForm(ProjectLocation projectLocation) {
    var form = new ProjectLocationForm();

    if (projectLocation.getManualFieldName() != null) {
      form.setField(SearchSelectorService.getValueWithManualEntryPrefix(projectLocation.getManualFieldName()));
    } else if (projectLocation.getField() != null) {
      form.setField(projectLocation.getField().getFieldId().toString());
    }

    form.setFieldType(projectLocation.getFieldType());
    form.setWaterDepth(projectLocation.getWaterDepth());

    form.setApprovedDecomProgram(projectLocation.getApprovedDecomProgram());
    form.setApprovedDecomProgramDate(new ThreeFieldDateInput(projectLocation.getApprovedDecomProgramDate()));

    form.setApprovedFieldDevelopmentPlan(projectLocation.getApprovedFieldDevelopmentPlan());
    form.setApprovedFdpDate(new ThreeFieldDateInput(projectLocation.getApprovedFdpDate()));
    form.setUkcsArea(projectLocation.getUkcsArea());
    projectLocationBlocksService.addBlocksToForm(form, projectLocation);

    return form;
  }

  /**
   * Validate the projectLocationForm, calls custom validator first.
   * Validates dates if FDP or Decom program questions are true.
   */
  public BindingResult validate(ProjectLocationForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    var projectLocationValidationHint = createProjectLocationValidationHint(validationType);
    projectLocationFormValidator.validate(form, bindingResult, projectLocationValidationHint);
    return validationService.validate(form, bindingResult, validationType);
  }

  private ProjectLocationValidationHint createProjectLocationValidationHint(ValidationType validationType) {
    return new ProjectLocationValidationHint(validationType);
  }

  public boolean isComplete(ProjectDetail details) {
    var form = getForm(details);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, ValidationType.FULL);
    return !bindingResult.hasErrors();
  }

  /**
   * If there's data in the form turn it back into a format the searchselector can parse.
   * @param form valid or invalid ProjectLocationForm
   * @return id and display name of the search selector items empty map if there's no form data.
   */
  public Map<String, String> getPreSelectedField(ProjectLocationForm form) {
    if (form.getField() != null) {
      return SearchSelectorService.isManualEntry(form.getField())
        ? searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(form.getField()),
            Map.of(form.getField(), form.getField())
          )
        : searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(form.getField()),
            Map.of(form.getField(), fieldService.findById(Integer.parseInt(form.getField())).getFieldName())
          );

    }
    return Map.of();
  }

  public List<ProjectLocationBlockView> getUnvalidatedBlockViewsForLocation(ProjectDetail detail) {
    return projectLocationBlocksService.getBlockViewsForLocation(getOrError(detail), ValidationType.NO_VALIDATION);
  }

  public List<ProjectLocationBlockView> getUnvalidatedBlockViewsFromForm(ProjectLocationForm form) {
    return projectLocationBlocksService.getBlockViewsFromForm(form, ValidationType.NO_VALIDATION);
  }

  public List<ProjectLocationBlockView> getValidatedBlockViewsForLocation(ProjectDetail detail) {
    return projectLocationBlocksService.getBlockViewsForLocation(getOrError(detail), ValidationType.FULL);
  }

  public List<ProjectLocationBlockView> getValidatedBlockViewsFromForm(ProjectLocationForm form) {
    return projectLocationBlocksService.getBlockViewsFromForm(form, ValidationType.FULL);
  }

}
