package uk.co.ogauthority.pathfinder.service.project.subseainfrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.subseainfrastructure.SubseaInfrastructure;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure.SubseaInfrastructureType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.ConcreteMattressForm;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.OtherSubseaStructureForm;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.SubseaInfrastructureForm;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.SubseaInfrastructureFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.SubseaStructureForm;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.concretemattress.ConcreteMattressFullValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.concretemattress.ConcreteMattressPartialValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.othersubseastructure.OtherSubseaStructureFullValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.othersubseastructure.OtherSubseaStructurePartialValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.subseastructure.SubseaStructureFullValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.subseastructure.SubseaStructurePartialValidation;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.subseainfrastructure.SubseaInfrastructureRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFacilitiesService;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.DevUkTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.SubseaInfrastructureTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class SubseaInfrastructureServiceTest {

  private static final Integer SUBSEA_INFRASTRUCTURE_ID = 1;

  @Mock
  private DevUkFacilitiesService devUkFacilitiesService;

  @Mock
  private SubseaInfrastructureRepository subseaInfrastructureRepository;

  @Mock
  private ValidationService validationService;

  @Mock
  private SubseaInfrastructureFormValidator subseaInfrastructureFormValidator;

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private SubseaInfrastructureService subseaInfrastructureService;

  private ProjectDetail projectDetail;

  @Before
  public void setup() {
    var searchSelectorService = new SearchSelectorService();
    subseaInfrastructureService = new SubseaInfrastructureService(
        devUkFacilitiesService,
        subseaInfrastructureRepository,
        searchSelectorService,
        validationService,
        subseaInfrastructureFormValidator,
        projectSetupService,
        entityDuplicationService
    );

    projectDetail = ProjectUtil.getProjectDetails();

    when(subseaInfrastructureRepository.save(any(SubseaInfrastructure.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  private void checkCommonSubseaInfrastructureFormFields(SubseaInfrastructureForm form,
                                                         SubseaInfrastructure subseaInfrastructure) {
    assertThat(form.getDescription()).isEqualTo(subseaInfrastructure.getDescription());
    assertThat(form.getStatus()).isEqualTo(subseaInfrastructure.getStatus());
    assertThat(form.getInfrastructureType()).isEqualTo(subseaInfrastructure.getInfrastructureType());
    assertThat(form.getDecommissioningDate().getMinYear()).isEqualTo(String.valueOf(subseaInfrastructure.getEarliestDecommissioningStartYear()));
    assertThat(form.getDecommissioningDate().getMaxYear()).isEqualTo(String.valueOf(subseaInfrastructure.getLatestDecommissioningCompletionYear()));
  }

  private void checkCommonSubseaInfrastructureEntityFields(SubseaInfrastructure subseaInfrastructure,
                                                           SubseaInfrastructureForm form) {
    assertThat(subseaInfrastructure.getDescription()).isEqualTo(form.getDescription());
    assertThat(subseaInfrastructure.getStatus()).isEqualTo(form.getStatus());
    assertThat(subseaInfrastructure.getInfrastructureType()).isEqualTo(form.getInfrastructureType());
    assertThat(subseaInfrastructure.getEarliestDecommissioningStartYear()).isEqualTo(Integer.parseInt(form.getDecommissioningDate().getMinYear()));
    assertThat(subseaInfrastructure.getLatestDecommissioningCompletionYear()).isEqualTo(Integer.parseInt(form.getDecommissioningDate().getMaxYear()));
  }

  private void assertConcreteMattressFormFieldsAreNull(ConcreteMattressForm form) {
    assertThat(form.getNumberOfMattresses()).isNull();
    assertThat(form.getTotalEstimatedMattressMass()).isNull();
  }

  private void assertSubseaStructureFormFieldsAreNull(SubseaStructureForm form) {
    assertThat(form.getTotalEstimatedSubseaMass()).isNull();
  }

  private void assertOtherInfrastructureFormFieldsAreNull(OtherSubseaStructureForm form) {
    assertThat(form.getTypeOfStructure()).isNull();
    assertThat(form.getTotalEstimatedMass()).isNull();
  }

  private void assertThatHiddenFormsAreNotPopulated(SubseaInfrastructureForm form) {

    var infrastructureType = form.getInfrastructureType();

    if (infrastructureType == null) {
      assertConcreteMattressFormFieldsAreNull(form.getConcreteMattressForm());
      assertSubseaStructureFormFieldsAreNull(form.getSubseaStructureForm());
      assertOtherInfrastructureFormFieldsAreNull(form.getOtherSubseaStructureForm());
    } else if (infrastructureType.equals(SubseaInfrastructureType.CONCRETE_MATTRESSES)) {
      assertSubseaStructureFormFieldsAreNull(form.getSubseaStructureForm());
      assertOtherInfrastructureFormFieldsAreNull(form.getOtherSubseaStructureForm());
    } else if (infrastructureType.equals(SubseaInfrastructureType.SUBSEA_STRUCTURE)) {
      assertConcreteMattressFormFieldsAreNull(form.getConcreteMattressForm());
      assertOtherInfrastructureFormFieldsAreNull(form.getOtherSubseaStructureForm());
    } else if (infrastructureType.equals(SubseaInfrastructureType.OTHER)) {
      assertConcreteMattressFormFieldsAreNull(form.getConcreteMattressForm());
      assertSubseaStructureFormFieldsAreNull(form.getSubseaStructureForm());
    }
  }

  private void assertCorrectValidation(SubseaInfrastructureForm form, ValidationType validationType, List<Object> hintClass) {

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    subseaInfrastructureService.validate(
        form,
        bindingResult,
        validationType
    );

    verify(subseaInfrastructureFormValidator, times(1)).validate(any(), any(), any());
    verify(validationService, times(1)).validate(form, bindingResult, validationType, hintClass);
  }

  private void assertConcreteMattressEntityFieldsAreNull(SubseaInfrastructure subseaInfrastructure) {
    assertThat(subseaInfrastructure.getNumberOfMattresses()).isNull();
    assertThat(subseaInfrastructure.getTotalEstimatedMattressMass()).isNull();
  }

  private void assertSubseaStructureEntityFieldsAreNull(SubseaInfrastructure subseaInfrastructure) {
    assertThat(subseaInfrastructure.getTotalEstimatedSubseaMass()).isNull();
  }

  private void assertOtherInfrastructureEntityFieldsAreNull(SubseaInfrastructure subseaInfrastructure) {
    assertThat(subseaInfrastructure.getOtherInfrastructureType()).isNull();
    assertThat(subseaInfrastructure.getTotalEstimatedOtherMass()).isNull();
  }

  private void assertThatHiddenEntityFieldsAreNotPopulated(SubseaInfrastructure subseaInfrastructure) {

    var infrastructureType = subseaInfrastructure.getInfrastructureType();

    if (infrastructureType == null) {
      assertConcreteMattressEntityFieldsAreNull(subseaInfrastructure);
      assertSubseaStructureEntityFieldsAreNull(subseaInfrastructure);
      assertOtherInfrastructureEntityFieldsAreNull(subseaInfrastructure);
    } else if (infrastructureType.equals(SubseaInfrastructureType.CONCRETE_MATTRESSES)) {
      assertSubseaStructureEntityFieldsAreNull(subseaInfrastructure);
      assertOtherInfrastructureEntityFieldsAreNull(subseaInfrastructure);
    } else if (infrastructureType.equals(SubseaInfrastructureType.SUBSEA_STRUCTURE)) {
      assertConcreteMattressEntityFieldsAreNull(subseaInfrastructure);
      assertOtherInfrastructureEntityFieldsAreNull(subseaInfrastructure);
    } else if (infrastructureType.equals(SubseaInfrastructureType.OTHER)) {
      assertConcreteMattressEntityFieldsAreNull(subseaInfrastructure);
      assertSubseaStructureEntityFieldsAreNull(subseaInfrastructure);
    }
  }

  @Test
  public void getForm_withStructureFromList() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withDevUkFacility();

    when(subseaInfrastructureRepository.findByIdAndProjectDetail(SUBSEA_INFRASTRUCTURE_ID, projectDetail))
        .thenReturn(Optional.of(subseaInfrastructure));

    var form = subseaInfrastructureService.getForm(SUBSEA_INFRASTRUCTURE_ID, projectDetail);

    checkCommonSubseaInfrastructureFormFields(form, subseaInfrastructure);

    assertThat(form.getStructure()).isEqualTo(String.valueOf(subseaInfrastructure.getFacility().getId()));
  }

  @Test
  public void getForm_withManualEntryStructure() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();

    when(subseaInfrastructureRepository.findByIdAndProjectDetail(SUBSEA_INFRASTRUCTURE_ID, projectDetail))
        .thenReturn(Optional.of(subseaInfrastructure));

    var form = subseaInfrastructureService.getForm(SUBSEA_INFRASTRUCTURE_ID, projectDetail);

    checkCommonSubseaInfrastructureFormFields(form, subseaInfrastructure);

    assertThat(form.getStructure()).isEqualTo(
        SearchSelectablePrefix.FREE_TEXT_PREFIX + subseaInfrastructure.getManualFacility()
    );
  }

  @Test
  public void getForm_withConcreteMattressType() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withConcreteMattresses();

    when(subseaInfrastructureRepository.findByIdAndProjectDetail(SUBSEA_INFRASTRUCTURE_ID, projectDetail))
        .thenReturn(Optional.of(subseaInfrastructure));

    var form = subseaInfrastructureService.getForm(SUBSEA_INFRASTRUCTURE_ID, projectDetail);

    checkCommonSubseaInfrastructureFormFields(form, subseaInfrastructure);

    var concreteMattressForm = form.getConcreteMattressForm();

    assertThat(concreteMattressForm.getNumberOfMattresses()).isEqualTo(subseaInfrastructure.getNumberOfMattresses());
    assertThat(concreteMattressForm.getTotalEstimatedMattressMass()).isEqualTo(subseaInfrastructure.getTotalEstimatedMattressMass());
    assertThatHiddenFormsAreNotPopulated(form);
  }

  @Test
  public void getForm_withSubseaStructureType() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withSubseaStructure();

    when(subseaInfrastructureRepository.findByIdAndProjectDetail(SUBSEA_INFRASTRUCTURE_ID, projectDetail))
        .thenReturn(Optional.of(subseaInfrastructure));

    var form = subseaInfrastructureService.getForm(SUBSEA_INFRASTRUCTURE_ID, projectDetail);

    checkCommonSubseaInfrastructureFormFields(form, subseaInfrastructure);

    var subseaStructureForm = form.getSubseaStructureForm();

    assertThat(subseaStructureForm.getTotalEstimatedSubseaMass()).isEqualTo(subseaInfrastructure.getTotalEstimatedSubseaMass());
    assertThatHiddenFormsAreNotPopulated(form);
  }

  @Test
  public void getForm_withOtherSubseaStructureType() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withOtherInfrastructure();

    when(subseaInfrastructureRepository.findByIdAndProjectDetail(SUBSEA_INFRASTRUCTURE_ID, projectDetail))
        .thenReturn(Optional.of(subseaInfrastructure));

    var form = subseaInfrastructureService.getForm(SUBSEA_INFRASTRUCTURE_ID, projectDetail);

    checkCommonSubseaInfrastructureFormFields(form, subseaInfrastructure);

    var otherSubseaStructureForm = form.getOtherSubseaStructureForm();

    assertThat(otherSubseaStructureForm.getTypeOfStructure()).isEqualTo(subseaInfrastructure.getOtherInfrastructureType());
    assertThat(otherSubseaStructureForm.getTotalEstimatedMass()).isEqualTo(subseaInfrastructure.getTotalEstimatedOtherMass());
    assertThatHiddenFormsAreNotPopulated(form);
  }

  @Test
  public void getForm_withNullInfrastructureType() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withDevUkFacility();
    subseaInfrastructure.setInfrastructureType(null);

    when(subseaInfrastructureRepository.findByIdAndProjectDetail(SUBSEA_INFRASTRUCTURE_ID, projectDetail))
        .thenReturn(Optional.of(subseaInfrastructure));

    var form = subseaInfrastructureService.getForm(SUBSEA_INFRASTRUCTURE_ID, projectDetail);

    checkCommonSubseaInfrastructureFormFields(form, subseaInfrastructure);
    assertThatHiddenFormsAreNotPopulated(form);
  }

  @Test
  public void getFacilityRestUrl() {
    subseaInfrastructureService.getFacilityRestUrl();
    verify(devUkFacilitiesService, times(1)).getFacilitiesRestUrl();
  }

  @Test
  public void getPreSelectedFacility() {
    var form = new SubseaInfrastructureForm();
    form.setStructure("test");

    subseaInfrastructureService.getPreSelectedFacility(form);
    verify(devUkFacilitiesService, times(1)).getPreSelectedFacility(form.getStructure());
  }

  @Test
  public void validate_whenConcreteTypeAndPartial() {
    var form = new SubseaInfrastructureForm();
    form.setInfrastructureType(SubseaInfrastructureType.CONCRETE_MATTRESSES);
    assertCorrectValidation(form, ValidationType.PARTIAL, List.of(ConcreteMattressPartialValidation.class));
  }

  @Test
  public void validate_whenConcreteTypeAndFull() {
    var form = new SubseaInfrastructureForm();
    form.setInfrastructureType(SubseaInfrastructureType.CONCRETE_MATTRESSES);
    assertCorrectValidation(form, ValidationType.FULL, List.of(ConcreteMattressFullValidation.class));
  }

  @Test
  public void validate_whenSubseaStructureTypeAndPartial() {
    var form = new SubseaInfrastructureForm();
    form.setInfrastructureType(SubseaInfrastructureType.SUBSEA_STRUCTURE);
    assertCorrectValidation(form, ValidationType.PARTIAL, List.of(SubseaStructurePartialValidation.class));
  }

  @Test
  public void validate_whenSubseaStructureTypeAndFull() {
    var form = new SubseaInfrastructureForm();
    form.setInfrastructureType(SubseaInfrastructureType.SUBSEA_STRUCTURE);
    assertCorrectValidation(form, ValidationType.FULL, List.of(SubseaStructureFullValidation.class));
  }

  @Test
  public void validate_whenOtherInfrastructureTypeAndPartial() {
    var form = new SubseaInfrastructureForm();
    form.setInfrastructureType(SubseaInfrastructureType.OTHER);
    assertCorrectValidation(form, ValidationType.PARTIAL, List.of(OtherSubseaStructurePartialValidation.class));
  }

  @Test
  public void validate_whenOtherInfrastructureTypeAndFull() {
    var form = new SubseaInfrastructureForm();
    form.setInfrastructureType(SubseaInfrastructureType.OTHER);
    assertCorrectValidation(form, ValidationType.FULL, List.of(OtherSubseaStructureFullValidation.class));
  }

  @Test
  public void validate_whenNoTypeSetAndPartial() {
    var form = new SubseaInfrastructureForm();
    form.setInfrastructureType(null);
    assertCorrectValidation(form, ValidationType.PARTIAL, List.of());
  }

  @Test
  public void validate_whenNoTypeSetAndFull() {
    var form = new SubseaInfrastructureForm();
    form.setInfrastructureType(null);
    assertCorrectValidation(form, ValidationType.FULL, List.of());
  }

  @Test
  public void createSubseaInfrastructure_whenFacilityFromDevUk() {

    final var devUkFacilityId = 123;
    final var devUkFacility = DevUkTestUtil.getDevUkFacility(devUkFacilityId, "test");

    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm();
    form.setStructure(String.valueOf(devUkFacilityId));



    when(devUkFacilitiesService.getFacilityAsList(form.getStructure())).thenReturn(List.of(devUkFacility));

    var persistedSubseaInfrastructure = subseaInfrastructureService.createSubseaInfrastructure(projectDetail, form);

    assertThat(persistedSubseaInfrastructure.getFacility()).isEqualTo(devUkFacility);
    assertThat(persistedSubseaInfrastructure.getManualFacility()).isNull();
    checkCommonSubseaInfrastructureEntityFields(persistedSubseaInfrastructure, form);
  }

  @Test
  public void createSubseaInfrastructure_whenFacilityIsManual() {

    final var manualEntryFacility = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my manual facility";

    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm();
    form.setStructure(manualEntryFacility);

    var persistedSubseaInfrastructure = subseaInfrastructureService.createSubseaInfrastructure(projectDetail, form);

    assertThat(persistedSubseaInfrastructure.getFacility()).isNull();
    assertThat(persistedSubseaInfrastructure.getManualFacility()).isEqualTo(SearchSelectorService.removePrefix(manualEntryFacility));
    checkCommonSubseaInfrastructureEntityFields(persistedSubseaInfrastructure, form);
  }

  @Test
  public void createSubseaInfrastructure_whenConcreteMattress() {

    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm(SubseaInfrastructureType.CONCRETE_MATTRESSES);
    form.setStructure(SearchSelectablePrefix.FREE_TEXT_PREFIX + "manual entry");

    var concreteMattressForm = form.getConcreteMattressForm();

    var persistedSubseaInfrastructure = subseaInfrastructureService.createSubseaInfrastructure(projectDetail, form);

    assertThat(persistedSubseaInfrastructure.getNumberOfMattresses()).isEqualTo(concreteMattressForm.getNumberOfMattresses());
    assertThat(persistedSubseaInfrastructure.getTotalEstimatedMattressMass()).isEqualTo(concreteMattressForm.getTotalEstimatedMattressMass());
    assertThatHiddenEntityFieldsAreNotPopulated(persistedSubseaInfrastructure);
    checkCommonSubseaInfrastructureEntityFields(persistedSubseaInfrastructure, form);
  }

  @Test
  public void createSubseaInfrastructure_whenSubseaStructure() {

    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm(SubseaInfrastructureType.SUBSEA_STRUCTURE);
    form.setStructure(SearchSelectablePrefix.FREE_TEXT_PREFIX + "manual entry");

    var subseaStructureForm = form.getSubseaStructureForm();

    var persistedSubseaInfrastructure = subseaInfrastructureService.createSubseaInfrastructure(projectDetail, form);

    assertThat(persistedSubseaInfrastructure.getTotalEstimatedSubseaMass()).isEqualTo(subseaStructureForm.getTotalEstimatedSubseaMass());
    assertThatHiddenEntityFieldsAreNotPopulated(persistedSubseaInfrastructure);
    checkCommonSubseaInfrastructureEntityFields(persistedSubseaInfrastructure, form);
  }

  @Test
  public void createSubseaInfrastructure_whenOtherSubseaInfrastructure() {

    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm(SubseaInfrastructureType.OTHER);
    form.setStructure(SearchSelectablePrefix.FREE_TEXT_PREFIX + "manual entry");

    var otherSubseaStructureForm = form.getOtherSubseaStructureForm();

    var persistedSubseaInfrastructure = subseaInfrastructureService.createSubseaInfrastructure(projectDetail, form);

    assertThat(persistedSubseaInfrastructure.getOtherInfrastructureType()).isEqualTo(otherSubseaStructureForm.getTypeOfStructure());
    assertThat(persistedSubseaInfrastructure.getTotalEstimatedOtherMass()).isEqualTo(otherSubseaStructureForm.getTotalEstimatedMass());
    assertThatHiddenEntityFieldsAreNotPopulated(persistedSubseaInfrastructure);
    checkCommonSubseaInfrastructureEntityFields(persistedSubseaInfrastructure, form);
  }

  @Test
  public void createSubseaInfrastructure_whenNullInfrastructureType() {

    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm();
    form.setStructure(SearchSelectablePrefix.FREE_TEXT_PREFIX + "manual entry");
    form.setInfrastructureType(null);

    var persistedSubseaInfrastructure = subseaInfrastructureService.createSubseaInfrastructure(projectDetail, form);

    assertThatHiddenEntityFieldsAreNotPopulated(persistedSubseaInfrastructure);
    checkCommonSubseaInfrastructureEntityFields(persistedSubseaInfrastructure, form);
  }

  @Test
  public void updateSubseaInfrastructure_whenFacilityFromDevUk() {

    final var devUkFacilityId = 123;
    final var devUkFacility = DevUkTestUtil.getDevUkFacility(devUkFacilityId, "test");

    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm();
    form.setStructure(String.valueOf(devUkFacilityId));

    when(devUkFacilitiesService.getFacilityAsList(form.getStructure())).thenReturn(List.of(devUkFacility));

    when(subseaInfrastructureRepository.findByIdAndProjectDetail(SUBSEA_INFRASTRUCTURE_ID, projectDetail))
        .thenReturn(Optional.of(new SubseaInfrastructure()));

    var persistedSubseaInfrastructure = subseaInfrastructureService.updateSubseaInfrastructure(
        SUBSEA_INFRASTRUCTURE_ID,
        projectDetail,
        form
    );

    assertThat(persistedSubseaInfrastructure.getFacility()).isEqualTo(devUkFacility);
    assertThat(persistedSubseaInfrastructure.getManualFacility()).isNull();
    checkCommonSubseaInfrastructureEntityFields(persistedSubseaInfrastructure, form);
  }

  @Test
  public void updateSubseaInfrastructure_whenFacilityIsManual() {

    final var manualEntryFacility = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my manual facility";

    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm();
    form.setStructure(manualEntryFacility);

    when(subseaInfrastructureRepository.findByIdAndProjectDetail(SUBSEA_INFRASTRUCTURE_ID, projectDetail))
        .thenReturn(Optional.of(new SubseaInfrastructure()));

    var persistedSubseaInfrastructure = subseaInfrastructureService.updateSubseaInfrastructure(
        SUBSEA_INFRASTRUCTURE_ID,
        projectDetail,
        form
    );

    assertThat(persistedSubseaInfrastructure.getFacility()).isNull();
    assertThat(persistedSubseaInfrastructure.getManualFacility()).isEqualTo(SearchSelectorService.removePrefix(manualEntryFacility));
    checkCommonSubseaInfrastructureEntityFields(persistedSubseaInfrastructure, form);
  }

  @Test
  public void updateSubseaInfrastructure_whenConcreteMattress() {

    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm(SubseaInfrastructureType.CONCRETE_MATTRESSES);
    form.setStructure(SearchSelectablePrefix.FREE_TEXT_PREFIX + "manual entry");

    var concreteMattressForm = form.getConcreteMattressForm();

    when(subseaInfrastructureRepository.findByIdAndProjectDetail(SUBSEA_INFRASTRUCTURE_ID, projectDetail))
        .thenReturn(Optional.of(new SubseaInfrastructure()));

    var persistedSubseaInfrastructure = subseaInfrastructureService.updateSubseaInfrastructure(
        SUBSEA_INFRASTRUCTURE_ID,
        projectDetail,
        form
    );

    assertThat(persistedSubseaInfrastructure.getNumberOfMattresses()).isEqualTo(concreteMattressForm.getNumberOfMattresses());
    assertThat(persistedSubseaInfrastructure.getTotalEstimatedMattressMass()).isEqualTo(concreteMattressForm.getTotalEstimatedMattressMass());
    assertThatHiddenEntityFieldsAreNotPopulated(persistedSubseaInfrastructure);
    checkCommonSubseaInfrastructureEntityFields(persistedSubseaInfrastructure, form);
  }

  @Test
  public void updateSubseaInfrastructure_whenSubseaStructure() {

    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm(SubseaInfrastructureType.SUBSEA_STRUCTURE);
    form.setStructure(SearchSelectablePrefix.FREE_TEXT_PREFIX + "manual entry");

    var subseaStructureForm = form.getSubseaStructureForm();

    when(subseaInfrastructureRepository.findByIdAndProjectDetail(SUBSEA_INFRASTRUCTURE_ID, projectDetail))
        .thenReturn(Optional.of(new SubseaInfrastructure()));

    var persistedSubseaInfrastructure = subseaInfrastructureService.updateSubseaInfrastructure(
        SUBSEA_INFRASTRUCTURE_ID,
        projectDetail,
        form
    );

    assertThat(persistedSubseaInfrastructure.getTotalEstimatedSubseaMass()).isEqualTo(subseaStructureForm.getTotalEstimatedSubseaMass());
    assertThatHiddenEntityFieldsAreNotPopulated(persistedSubseaInfrastructure);
    checkCommonSubseaInfrastructureEntityFields(persistedSubseaInfrastructure, form);
  }

  @Test
  public void updateSubseaInfrastructure_whenOtherSubseaInfrastructure() {

    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm(SubseaInfrastructureType.OTHER);
    form.setStructure(SearchSelectablePrefix.FREE_TEXT_PREFIX + "manual entry");

    var otherSubseaStructureForm = form.getOtherSubseaStructureForm();

    when(subseaInfrastructureRepository.findByIdAndProjectDetail(SUBSEA_INFRASTRUCTURE_ID, projectDetail))
        .thenReturn(Optional.of(new SubseaInfrastructure()));

    var persistedSubseaInfrastructure = subseaInfrastructureService.updateSubseaInfrastructure(
        SUBSEA_INFRASTRUCTURE_ID,
        projectDetail,
        form
    );

    assertThat(persistedSubseaInfrastructure.getOtherInfrastructureType()).isEqualTo(otherSubseaStructureForm.getTypeOfStructure());
    assertThat(persistedSubseaInfrastructure.getTotalEstimatedOtherMass()).isEqualTo(otherSubseaStructureForm.getTotalEstimatedMass());
    assertThatHiddenEntityFieldsAreNotPopulated(persistedSubseaInfrastructure);
    checkCommonSubseaInfrastructureEntityFields(persistedSubseaInfrastructure, form);
  }

  @Test
  public void updateSubseaInfrastructure_whenNullInfrastructureType() {

    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm();
    form.setStructure(SearchSelectablePrefix.FREE_TEXT_PREFIX + "manual entry");
    form.setInfrastructureType(null);

    when(subseaInfrastructureRepository.findByIdAndProjectDetail(SUBSEA_INFRASTRUCTURE_ID, projectDetail))
        .thenReturn(Optional.of(new SubseaInfrastructure()));

    var persistedSubseaInfrastructure = subseaInfrastructureService.updateSubseaInfrastructure(
        SUBSEA_INFRASTRUCTURE_ID,
        projectDetail,
        form
    );

    assertThatHiddenEntityFieldsAreNotPopulated(persistedSubseaInfrastructure);
    checkCommonSubseaInfrastructureEntityFields(persistedSubseaInfrastructure, form);
  }

  @Test
  public void getSubseaInfrastructures_whenExist_thenReturnList() {
    var subseaInfrastructure1 = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withSubseaStructure();
    var subseaInfrastructure2 = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withConcreteMattresses();

    when(subseaInfrastructureRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(List.of(subseaInfrastructure1, subseaInfrastructure2));

    var subseaInfrastructures = subseaInfrastructureService.getSubseaInfrastructures(projectDetail);
    assertThat(subseaInfrastructures).containsExactly(subseaInfrastructure1, subseaInfrastructure2);
  }

  @Test
  public void getSubseaInfrastructures_whenNoneExist_thenEmptyList() {

    when(subseaInfrastructureRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(List.of());

    var subseaInfrastructures = subseaInfrastructureService.getSubseaInfrastructures(projectDetail);
    assertThat(subseaInfrastructures).isEmpty();
  }

  @Test
  public void deleteSubseaInfrastructure() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withSubseaStructure();
    subseaInfrastructureService.deleteSubseaInfrastructure(subseaInfrastructure);
    verify(subseaInfrastructureRepository, times(1)).delete(subseaInfrastructure);
  }

  @Test
  public void getSubseaInfrastructure_whenExists_thenReturn() {

    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();

    when(subseaInfrastructureRepository.findByIdAndProjectDetail(SUBSEA_INFRASTRUCTURE_ID, projectDetail)).thenReturn(
        Optional.of(subseaInfrastructure)
    );

    var result = subseaInfrastructureService.getSubseaInfrastructure(SUBSEA_INFRASTRUCTURE_ID, projectDetail);

    assertThat(result.getId()).isEqualTo(subseaInfrastructure.getId());
    assertThat(result.getProjectDetail().getId()).isEqualTo(subseaInfrastructure.getProjectDetail().getId());
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getSubseaInfrastructure_whenNotFound_thenException() {

    when(subseaInfrastructureRepository.findByIdAndProjectDetail(SUBSEA_INFRASTRUCTURE_ID, projectDetail)).thenReturn(
        Optional.empty()
    );

    subseaInfrastructureService.getSubseaInfrastructure(SUBSEA_INFRASTRUCTURE_ID, projectDetail);
  }

  @Test
  public void canShowInTaskList_true() {
    when(projectSetupService.taskSelectedForProjectDetail(projectDetail, ProjectTask.SUBSEA_INFRASTRUCTURE)).thenReturn(true);
    assertThat(subseaInfrastructureService.canShowInTaskList(projectDetail)).isTrue();
  }

  @Test
  public void canShowInTaskList_false() {
    when(projectSetupService.taskSelectedForProjectDetail(projectDetail, ProjectTask.SUBSEA_INFRASTRUCTURE)).thenReturn(false);
    assertThat(subseaInfrastructureService.canShowInTaskList(projectDetail)).isFalse();
  }

  @Test
  public void removeSectionData_verifyInteractions() {
    subseaInfrastructureService.removeSectionData(projectDetail);

    verify(subseaInfrastructureRepository, times(1)).deleteAllByProjectDetail(projectDetail);
  }

  @Test
  public void copySectionData_verifyDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    final var subseaInfrastructures = List.of(
        SubseaInfrastructureTestUtil.createSubseaInfrastructure_withConcreteMattresses()
    );
    when(subseaInfrastructureRepository.findByProjectDetailOrderByIdAsc(fromProjectDetail)).thenReturn(subseaInfrastructures);

    subseaInfrastructureService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        subseaInfrastructures,
        toProjectDetail,
        SubseaInfrastructure.class
    );

  }

}