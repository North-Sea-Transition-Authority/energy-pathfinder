package uk.co.ogauthority.pathfinder.service.project.location;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.fivium.formlibrary.input.CoordinateInputLatitudeHemisphere;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFieldService;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.LicenceBlockTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectFormSectionServiceTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectLocationServiceTest {

  @Mock
  private ProjectLocationRepository projectLocationRepository;

  @Mock
  private DevUkFieldService fieldService;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private ValidationService validationService;

  @Mock
  private ProjectLocationFormValidator projectLocationFormValidator;

  @Mock
  private ProjectLocationBlocksService projectLocationBlocksService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @Mock
  private ProjectInformationService projectInformationService;

  private ProjectLocationService projectLocationService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  private ProjectLocation projectLocation;

  @Before
  public void setUp() {
    projectLocationService = new ProjectLocationService(
        projectLocationRepository,
        fieldService,
        searchSelectorService,
        validationService,
        projectLocationFormValidator,
        projectLocationBlocksService,
        entityDuplicationService,
        projectInformationService
    );
    when(projectLocationRepository.save(any(ProjectLocation.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createOrUpdate_newLocation_oilAndGasProject() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    when(projectInformationService.isOilAndGasProject(details)).thenReturn(true);
    when(fieldService.findByIdOrError(ProjectLocationTestUtil.FIELD_ID)).thenReturn(ProjectLocationTestUtil.FIELD);
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLatitude().getHemisphereInput().setInputValue(CoordinateInputLatitudeHemisphere.NORTH.name());
    projectLocation = projectLocationService.createOrUpdate(details, form);
    assertThat(projectLocation.getProjectDetail()).isEqualTo(details);
    assertThat(projectLocation.getField()).isEqualTo(ProjectLocationTestUtil.FIELD);
    checkCommonFieldsMatch(projectLocation);
    checkOilAndGasFieldsMatch(projectLocation);
  }

  @Test
  public void createOrUpdate_newLocation_notOilAndGasProject() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    when(projectInformationService.isOilAndGasProject(details)).thenReturn(false);
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.getCentreOfInterestLatitude().getHemisphereInput().setInputValue(CoordinateInputLatitudeHemisphere.NORTH.name());
    projectLocation = projectLocationService.createOrUpdate(details, form);
    assertThat(projectLocation.getProjectDetail()).isEqualTo(details);
    assertThat(projectLocation.getField()).isNull();
    checkCommonFieldsMatch(projectLocation);
    checkOilAndGasFieldsAreNull(projectLocation);
  }

  @Test
  public void createOrUpdate_existingLocation_dateNotSetWhenLinkedQuestionIsFalse() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    form.setApprovedFieldDevelopmentPlan(false);
    projectLocation = ProjectLocationTestUtil.getProjectLocation(details);
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(
        Optional.of(
            projectLocation
        ));
    when(projectInformationService.isOilAndGasProject(details)).thenReturn(true);

    //before call fdp date set
    assertThat(projectLocation.getApprovedFieldDevelopmentPlan()).isTrue();
    assertThat(projectLocation.getApprovedFdpDate()).isNotNull();

    //update with new form details
    projectLocation = projectLocationService.createOrUpdate(details, form);

    //after call fdp date null
    assertThat(projectLocation.getProjectDetail()).isEqualTo(details);
    assertThat(projectLocation.getApprovedFieldDevelopmentPlan()).isFalse();
    assertThat(projectLocation.getApprovedFdpDate()).isNull();
  }

  @Test
  public void createOrUpdate_existingLocation_blankForm() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(
        Optional.of(
            ProjectLocationTestUtil.getProjectLocation(details)
        ));
    when(projectInformationService.isOilAndGasProject(details)).thenReturn(true);
    projectLocation = projectLocationService.createOrUpdate(details, ProjectLocationTestUtil.getBlankForm());
    assertThat(projectLocation.getProjectDetail()).isEqualTo(details);

    assertThat(projectLocation.getCentreOfInterestLatitudeDegrees()).isNull();
    assertThat(projectLocation.getCentreOfInterestLatitudeMinutes()).isNull();
    assertThat(projectLocation.getCentreOfInterestLatitudeSeconds()).isNull();
    assertThat(projectLocation.getCentreOfInterestLatitudeHemisphere()).isNull();

    assertThat(projectLocation.getCentreOfInterestLongitudeDegrees()).isNull();
    assertThat(projectLocation.getCentreOfInterestLongitudeMinutes()).isNull();
    assertThat(projectLocation.getCentreOfInterestLongitudeSeconds()).isNull();
    assertThat(projectLocation.getCentreOfInterestLongitudeHemisphere()).isNull();

    assertThat(projectLocation.getField()).isNull();
    assertThat(projectLocation.getFieldType()).isNull();
    assertThat(projectLocation.getMaximumWaterDepth()).isNull();
    assertThat(projectLocation.getApprovedFieldDevelopmentPlan()).isNull();
    assertThat(projectLocation.getApprovedFdpDate()).isNull();
    assertThat(projectLocation.getApprovedDecomProgram()).isNull();
  }

  @Test
  public void getForm_existingLocation_withField_oilAndGasProject() {
    projectLocation = ProjectLocationTestUtil.getProjectLocation(details);
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(
        Optional.of(projectLocation)
    );
    when(projectInformationService.isOilAndGasProject(details)).thenReturn(true);
    var form = projectLocationService.getForm(details);
    assertThat(form.getField()).isEqualTo(ProjectLocationTestUtil.FIELD_ID.toString());
    checkCommonFormFieldsMatch(projectLocation, form);

    assertThat(form.getFieldType()).isEqualTo(projectLocation.getFieldType());
    assertThat(form.getMaximumWaterDepth()).isEqualTo(projectLocation.getMaximumWaterDepth());
    assertThat(form.getApprovedFieldDevelopmentPlan()).isEqualTo(projectLocation.getApprovedFieldDevelopmentPlan());
    assertThat(form.getApprovedFdpDate()).isEqualTo(new ThreeFieldDateInput(projectLocation.getApprovedFdpDate()));
    assertThat(form.getApprovedDecomProgram()).isEqualTo(projectLocation.getApprovedDecomProgram());
    assertThat(form.getApprovedDecomProgramDate()).isEqualTo(new ThreeFieldDateInput(projectLocation.getApprovedDecomProgramDate()));
  }

  @Test
  public void getForm_existingLocation_notOilAndGasProject() {
    projectLocation = ProjectLocationTestUtil.getProjectLocation(details);
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(
        Optional.of(projectLocation)
    );
    when(projectInformationService.isOilAndGasProject(details)).thenReturn(false);
    var form = projectLocationService.getForm(details);
    assertThat(form.getField()).isNull();
    checkCommonFormFieldsMatch(projectLocation, form);

    assertThat(form.getFieldType()).isNull();
    assertThat(form.getMaximumWaterDepth()).isNull();
    assertThat(form.getApprovedFieldDevelopmentPlan()).isNull();
    assertThat(form.getApprovedFdpDate()).isNull();
    assertThat(form.getApprovedDecomProgram()).isNull();
    assertThat(form.getApprovedDecomProgramDate()).isNull();
  }

  @Test
  public void getForm_noExistingLocation() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    var form = projectLocationService.getForm(details);

    assertThat(form.getCentreOfInterestLatitude().getDegreesInput().getAsInteger()).isEmpty();
    assertThat(form.getCentreOfInterestLatitude().getMinutesInput().getAsInteger()).isEmpty();
    assertThat(form.getCentreOfInterestLatitude().getSecondsInput().getAsDouble()).isEmpty();
    assertThat(form.getCentreOfInterestLatitude().getHemisphereInput().getInputValue()).isNull();

    assertThat(form.getCentreOfInterestLongitude().getDegreesInput().getAsInteger()).isEmpty();
    assertThat(form.getCentreOfInterestLongitude().getMinutesInput().getAsInteger()).isEmpty();
    assertThat(form.getCentreOfInterestLongitude().getSecondsInput().getAsDouble()).isEmpty();
    assertThat(form.getCentreOfInterestLongitude().getHemisphereInput().getInputValue()).isNull();

    assertThat(form.getField()).isNull();
    assertThat(form.getFieldType()).isNull();
    assertThat(form.getMaximumWaterDepth()).isNull();
    assertThat(form.getApprovedFieldDevelopmentPlan()).isNull();
    assertThat(form.getApprovedFdpDate()).isNull();
    assertThat(form.getApprovedDecomProgram()).isNull();
  }

  @Test
  public void validate_partial() {
    var form = new ProjectLocationForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectLocationService.validate(
        form,
        bindingResult,
        details,
        ValidationType.PARTIAL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void validate_full() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectLocationService.validate(
        form,
        bindingResult,
        details,
        ValidationType.FULL
    );

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void getPreSelectedLocation_withField() {
    when(searchSelectorService.buildPrePopulatedSelections(any(), any())).thenCallRealMethod();
    when(fieldService.findByIdOrError(ProjectLocationTestUtil.FIELD_ID)).thenReturn(ProjectLocationTestUtil.FIELD);
    var form = ProjectLocationTestUtil.getCompletedForm();
    var preSelectedLocation = projectLocationService.getPreSelectedField(form);
    assertThat(preSelectedLocation).containsOnly(
        entry(ProjectLocationTestUtil.FIELD_ID.toString(), ProjectLocationTestUtil.FIELD_NAME)
    );
  }

  @Test
  public void getPreSelectedLocation_emptyForm() {
    var preSelectedLocation = projectLocationService.getPreSelectedField(new ProjectLocationForm());
    assertThat(preSelectedLocation).isEmpty();
  }

  @Test
  public void getUnvalidatedBlockViewsForLocation() {
    //Just a wrapper call really - logic tested in ProjectLocationBlockServiceTest.
    projectLocation = ProjectLocationTestUtil.getProjectLocation(details);
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.of(projectLocation));
    when(projectLocationBlocksService.getBlockViewsForLocation(projectLocation, ValidationType.NO_VALIDATION))
        .thenReturn(Collections.singletonList(LicenceBlockTestUtil.getBlockView(true)));
    var blocks = projectLocationService.getUnvalidatedBlockViewsForLocation(details);
    assertThat(blocks.size()).isEqualTo(1);
    assertThat(blocks.get(0).getBlockReference()).isEqualTo(LicenceBlockTestUtil.BLOCK_REFERENCE);
  }

  @Test
  public void getUnValidatedBlockViewsForLocation_noneLinked() {
    //Just a wrapper call really - logic tested in ProjectLocationBlockServiceTest.
    projectLocation = ProjectLocationTestUtil.getProjectLocation(details);
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.of(projectLocation));
    var blocks = projectLocationService.getUnvalidatedBlockViewsForLocation(details);
    assertThat(blocks).isEmpty();
  }

  @Test
  public void getUnValidatedBlockViewsForLocation_noProjectLocation() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    var blocks = projectLocationService.getUnvalidatedBlockViewsForLocation(details);
    assertThat(blocks).isEmpty();
  }

  @Test
  public void getValidatedBlockViewsForLocation_noneLinked() {
    //Just a wrapper call really - logic tested in ProjectLocationBlockServiceTest.
    projectLocation = ProjectLocationTestUtil.getProjectLocation(details);
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.of(projectLocation));
    var blocks = projectLocationService.getValidatedBlockViewsForLocation(details);
    assertThat(blocks).isEmpty();
  }

  @Test
  public void getValidatedBlockViewsForLocation_noProjectLocation() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    var blocks = projectLocationService.getValidatedBlockViewsForLocation(details);
    assertThat(blocks).isEmpty();
  }

  @Test
  public void getValidatedBlockViewsForLocation() {
    projectLocation = ProjectLocationTestUtil.getProjectLocation(details);
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.of(projectLocation));

    when(projectLocationBlocksService.getBlockViewsForLocation(projectLocation, ValidationType.FULL))
        .thenReturn(Collections.singletonList(LicenceBlockTestUtil.getBlockView(true)));

    var blocks = projectLocationService.getValidatedBlockViewsForLocation(details);
    assertThat(blocks.size()).isEqualTo(1);
    assertThat(blocks.get(0).getBlockReference()).isEqualTo(LicenceBlockTestUtil.BLOCK_REFERENCE);
  }

  @Test
  public void getUnvalidatedBlockViewsFromForm() {
    //Just a wrapper call really - logic tested in ProjectLocationBlockServiceTest.
    projectLocation = ProjectLocationTestUtil.getProjectLocation(details);
    when(projectLocationBlocksService.getBlockViewsFromForm(any(), any()))
        .thenReturn(Collections.singletonList(LicenceBlockTestUtil.getBlockView(true)));
    var blocks = projectLocationService.getUnvalidatedBlockViewsFromForm(ProjectLocationTestUtil.getCompletedForm(), details);
    assertThat(blocks.size()).isEqualTo(1);
    assertThat(blocks.get(0).getBlockReference()).isEqualTo(LicenceBlockTestUtil.BLOCK_REFERENCE);
  }

  @Test
  public void getValidatedBlockViewsFromForm() {
    projectLocation = ProjectLocationTestUtil.getProjectLocation(details);
    when(projectLocationBlocksService.getBlockViewsFromForm(any(), any()))
        .thenReturn(Collections.singletonList(LicenceBlockTestUtil.getBlockView(true)));
    when(projectLocationRepository.findByProjectDetail(any())).thenReturn(Optional.of(projectLocation));
    var blocks = projectLocationService.getValidatedBlockViewsFromForm(ProjectLocationTestUtil.getCompletedForm(), details);
    assertThat(blocks.size()).isEqualTo(1);
    assertThat(blocks.get(0).getBlockReference()).isEqualTo(LicenceBlockTestUtil.BLOCK_REFERENCE);
  }

  @Test
  public void getValidatedBlockViewsFromForm_notAllReturnedFromPortal() {
    var form = ProjectLocationTestUtil.getCompletedForm();
    var returnedBlockView = LicenceBlockTestUtil.getBlockView(true);
    returnedBlockView.setCompositeKey(ProjectLocationTestUtil.LICENCE_BLOCKS.get(0)); //One returned block

    projectLocation = ProjectLocationTestUtil.getProjectLocation(details);
    when(projectLocationBlocksService.getBlockViewsFromForm(any(), any()))
        .thenReturn(Collections.singletonList(returnedBlockView));
    when(projectLocationRepository.findByProjectDetail(any())).thenReturn(Optional.of(projectLocation));

    var blocks = projectLocationService.getValidatedBlockViewsFromForm(form, details);

    verify(projectLocationBlocksService, times(1)) //The second licenceBlock id will be missing from the original result set,
        // expect one call to fetch it from Pathfinder data
        .getBlockViewsByProjectLocationAndCompositeKeyIn(projectLocation, List.of(ProjectLocationTestUtil.LICENCE_BLOCKS.get(1)), ValidationType.FULL);
    assertThat(blocks.size()).isEqualTo(1);
    assertThat(blocks.get(0).getBlockReference()).isEqualTo(returnedBlockView.getBlockReference());
  }

  @Test
  public void createOrUpdateBlocks_verifyServiceInteraction() {

    final var licenceBlocks = List.of("1/1", "1/2", "1/3");

    projectLocationService.createOrUpdateBlocks(licenceBlocks, projectLocation);

    verify(projectLocationBlocksService, times(1)).createOrUpdateBlocks(licenceBlocks, projectLocation);
  }

  @Test
  public void getProjectLocationByProjectDetail_whenFound_thenReturn() {
    projectLocation = ProjectLocationTestUtil.getProjectLocation(details);

    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.of(projectLocation));

    assertThat(projectLocationService.getProjectLocationByProjectDetail(details)).contains(projectLocation);
  }

  @Test
  public void getProjectLocationByProjectDetail_whenNotFound_thenReturnEmpty() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());

    assertThat(projectLocationService.getProjectLocationByProjectDetail(details)).isEmpty();
  }

  @Test
  public void getProjectLocationByProjectAndVersion_whenFound_thenReturn() {
    projectLocation = ProjectLocationTestUtil.getProjectLocation(details);

    var project = details.getProject();
    var version = details.getVersion() -  1;

    when(projectLocationRepository.findByProjectDetail_ProjectAndProjectDetail_Version(project, version))
        .thenReturn(Optional.of(projectLocation));

    assertThat(projectLocationService.getProjectLocationByProjectAndVersion(project, version)).contains(projectLocation);
  }

  @Test
  public void getProjectLocationByProjectAndVersion_whenNotFound_thenReturnEmpty() {
    var project = details.getProject();
    var version = details.getVersion() -  1;

    when(projectLocationRepository.findByProjectDetail_ProjectAndProjectDetail_Version(project, version))
        .thenReturn(Optional.empty());

    assertThat(projectLocationService.getProjectLocationByProjectAndVersion(project, version)).isEmpty();
  }

  @Test
  public void removeSectionData_whenLocationNotFound_thenNoDelete() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());

    projectLocationService.removeSectionData(details);

    verify(projectLocationBlocksService, never()).deleteBlocks(any());
    verify(projectLocationRepository, never()).delete(any());
  }

  @Test
  public void removeSectionData_whenLocationFound_thenDelete() {
    projectLocation = ProjectLocationTestUtil.getProjectLocation(details);

    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.of(projectLocation));

    projectLocationService.removeSectionData(details);

    verify(projectLocationBlocksService, times(1)).deleteBlocks(projectLocation);
    verify(projectLocationRepository, times(1)).delete(projectLocation);
  }

  @Test
  public void removeSectionDataIfNotRelevant_whenLocationNotFound_thenNonRelevantDataNotRemoved() {
    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());

    projectLocationService.removeSectionDataIfNotRelevant(details);

    verify(projectLocationRepository, never()).delete(any());
    verify(projectLocationBlocksService, never()).deleteBlocks(any());
  }

  @Test
  public void removeSectionDataIfNotRelevant_whenLocationFound_oilAndGasProject_thenNonRelevantDataNotRemoved() {
    projectLocation = ProjectLocationTestUtil.getProjectLocation(details);

    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.of(projectLocation));
    when(projectInformationService.isOilAndGasProject(details)).thenReturn(true);

    projectLocationService.removeSectionDataIfNotRelevant(details);

    checkOilAndGasFieldsMatch(projectLocation);

    verify(projectLocationRepository, never()).save(projectLocation);
    verify(projectLocationBlocksService, never()).deleteBlocks(projectLocation);
  }

  @Test
  public void removeSectionDataIfNotRelevant_whenLocationFound_notOilAndGasProject_thenNonRelevantDataRemoved() {
    projectLocation = ProjectLocationTestUtil.getProjectLocation(details);

    when(projectLocationRepository.findByProjectDetail(details)).thenReturn(Optional.of(projectLocation));
    when(projectInformationService.isOilAndGasProject(details)).thenReturn(false);

    projectLocationService.removeSectionDataIfNotRelevant(details);

    checkOilAndGasFieldsAreNull(projectLocation);

    verify(projectLocationRepository).save(projectLocation);
    verify(projectLocationBlocksService).deleteBlocks(projectLocation);
  }

  @Test
  public void copySectionData_verifyDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    final var fromLocation = ProjectLocationTestUtil.getProjectLocation(fromProjectDetail);
    final var toLocation = ProjectLocationTestUtil.getProjectLocation(toProjectDetail);

    when(projectLocationRepository.findByProjectDetail(fromProjectDetail)).thenReturn(Optional.of(fromLocation));

    final var licenceBlocks = List.of(
        LicenceBlockTestUtil.getProjectLocationBlock(fromLocation, "1/1"),
        LicenceBlockTestUtil.getProjectLocationBlock(fromLocation, "1/2")
    );

    when(projectLocationBlocksService.getBlocks(fromLocation)).thenReturn(licenceBlocks);

    when(entityDuplicationService.duplicateEntityAndSetNewParent(
        fromLocation,
        toProjectDetail,
        ProjectLocation.class
    )).thenReturn(toLocation);

    projectLocationService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntityAndSetNewParent(
        fromLocation,
        toProjectDetail,
        ProjectLocation.class
    );

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        licenceBlocks,
        toLocation,
        ProjectLocationBlock.class
    );
  }

  @Test
  public void copySectionData_whenNoProjectLocationEntityFound() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    when(projectLocationRepository.findByProjectDetail(fromProjectDetail))
        .thenReturn(Optional.empty());

    projectLocationService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(projectLocationBlocksService, never()).getBlocks(any());
    verify(entityDuplicationService, never()).duplicateEntityAndSetNewParent(any(), any(), any());
    verify(entityDuplicationService, never()).duplicateEntitiesAndSetNewParent(any(), any(), any());
  }

  @Test
  public void canShowInTaskList_whenInfrastructureProject_thenTrue() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);

    assertThat(projectLocationService.canShowInTaskList(projectDetail, Set.of(UserToProjectRelationship.OPERATOR))).isTrue();
  }

  @Test
  public void canShowInTaskList_whenNotInfrastructureProject_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    assertThat(projectLocationService.canShowInTaskList(projectDetail, Set.of(UserToProjectRelationship.OPERATOR))).isFalse();
  }

  @Test
  public void canShowInTaskList_whenNullProjectType_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails();
    projectDetail.setProjectType(null);
    assertThat(projectLocationService.canShowInTaskList(projectDetail, Set.of(UserToProjectRelationship.OPERATOR))).isFalse();
  }

  @Test
  public void canShowInTaskList_userToProjectRelationshipSmokeTest() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);

    ProjectFormSectionServiceTestUtil.canShowInTaskList_userToProjectRelationshipSmokeTest(
        projectLocationService,
        projectDetail,
        Set.of(UserToProjectRelationship.OPERATOR)
    );
  }

  @Test
  public void isTaskValidForProjectDetail_whenInfrastructureProject_thenTrue() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);
    assertThat(projectLocationService.isTaskValidForProjectDetail(projectDetail)).isTrue();
  }

  @Test
  public void isTaskValidForProjectDetail_whenNotInfrastructureProject_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    assertThat(projectLocationService.isTaskValidForProjectDetail(projectDetail)).isFalse();
  }

  @Test
  public void getSupportedProjectTypes_verifyInfrastructure() {
    assertThat(projectLocationService.getSupportedProjectTypes()).containsExactly(ProjectType.INFRASTRUCTURE);
  }

  @Test
  public void alwaysCopySectionData_verifyFalse() {
    assertThat(projectLocationService.alwaysCopySectionData(details)).isFalse();
  }

  @Test
  public void allowSectionDataCleanUp_verifyIsTrue() {
    final var allowSectionDateCleanUp = projectLocationService.allowSectionDataCleanUp(details);
    assertThat(allowSectionDateCleanUp).isTrue();
  }

  private void checkCommonFieldsMatch(ProjectLocation projectLocation) {
    assertThat(projectLocation.getCentreOfInterestLatitudeDegrees()).isEqualTo(ProjectLocationTestUtil.CENTRE_OF_INTEREST_LATITUDE_DEGREES);
    assertThat(projectLocation.getCentreOfInterestLatitudeMinutes()).isEqualTo(ProjectLocationTestUtil.CENTRE_OF_INTEREST_LATITUDE_MINUTES);
    assertThat(projectLocation.getCentreOfInterestLatitudeSeconds()).isEqualTo(ProjectLocationTestUtil.CENTRE_OF_INTEREST_LATITUDE_SECONDS);
    assertThat(projectLocation.getCentreOfInterestLatitudeHemisphere()).isEqualTo(ProjectLocationTestUtil.CENTRE_OF_INTEREST_LATITUDE_HEMISPHERE);

    assertThat(projectLocation.getCentreOfInterestLongitudeDegrees()).isEqualTo(ProjectLocationTestUtil.CENTRE_OF_INTEREST_LONGITUDE_DEGREES);
    assertThat(projectLocation.getCentreOfInterestLongitudeMinutes()).isEqualTo(ProjectLocationTestUtil.CENTRE_OF_INTEREST_LONGITUDE_MINUTES);
    assertThat(projectLocation.getCentreOfInterestLongitudeSeconds()).isEqualTo(ProjectLocationTestUtil.CENTRE_OF_INTEREST_LONGITUDE_SECONDS);
    assertThat(projectLocation.getCentreOfInterestLongitudeHemisphere()).isEqualTo(ProjectLocationTestUtil.CENTRE_OF_INTEREST_LONGITUDE_HEMISPHERE);
  }

  private void checkOilAndGasFieldsMatch(ProjectLocation projectLocation) {
    assertThat(projectLocation.getFieldType()).isEqualTo(ProjectLocationTestUtil.FIELD_TYPE);
    assertThat(projectLocation.getMaximumWaterDepth()).isEqualTo(ProjectLocationTestUtil.WATER_DEPTH);
    assertThat(projectLocation.getApprovedFieldDevelopmentPlan()).isEqualTo(ProjectLocationTestUtil.APPROVED_FDP_PLAN);
    assertThat(projectLocation.getApprovedFdpDate()).isEqualTo(ProjectLocationTestUtil.APPROVED_FDP_DATE);
    assertThat(projectLocation.getApprovedDecomProgram()).isEqualTo(ProjectLocationTestUtil.APPROVED_DECOM_PROGRAM);
  }

  private void checkOilAndGasFieldsAreNull(ProjectLocation projectLocation) {
    assertThat(projectLocation.getFieldType()).isNull();
    assertThat(projectLocation.getMaximumWaterDepth()).isNull();
    assertThat(projectLocation.getApprovedFieldDevelopmentPlan()).isNull();
    assertThat(projectLocation.getApprovedFdpDate()).isNull();
    assertThat(projectLocation.getApprovedDecomProgram()).isNull();
  }

  private void checkCommonFormFieldsMatch(ProjectLocation projectLocation, ProjectLocationForm form) {
    assertThat(form.getCentreOfInterestLatitude().getDegreesInput().getAsInteger())
        .isEqualTo(Optional.ofNullable(projectLocation.getCentreOfInterestLatitudeDegrees()));
    assertThat(form.getCentreOfInterestLatitude().getMinutesInput().getAsInteger())
        .isEqualTo(Optional.ofNullable(projectLocation.getCentreOfInterestLatitudeMinutes()));
    assertThat(form.getCentreOfInterestLatitude().getSecondsInput().getAsDouble())
        .isEqualTo(Optional.ofNullable(projectLocation.getCentreOfInterestLatitudeSeconds()));

    assertThat(form.getCentreOfInterestLongitude().getDegreesInput().getAsInteger())
        .isEqualTo(Optional.ofNullable(projectLocation.getCentreOfInterestLongitudeDegrees()));
    assertThat(form.getCentreOfInterestLongitude().getMinutesInput().getAsInteger())
        .isEqualTo(Optional.ofNullable(projectLocation.getCentreOfInterestLongitudeMinutes()));
    assertThat(form.getCentreOfInterestLongitude().getSecondsInput().getAsDouble())
        .isEqualTo(Optional.ofNullable(projectLocation.getCentreOfInterestLongitudeSeconds()));
    assertThat(form.getCentreOfInterestLongitude().getHemisphereInput().getInputValue())
        .isEqualTo(projectLocation.getCentreOfInterestLongitudeHemisphere());
  }
}
