package uk.co.ogauthority.pathfinder.service.difference;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.exception.DifferenceProcessingException;
import uk.co.ogauthority.pathfinder.model.difference.DifferenceType;
import uk.co.ogauthority.pathfinder.model.difference.DiffedField;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;

@RunWith(MockitoJUnitRunner.class)
public class DifferenceServiceTest {

  private DifferenceService differenceService;

  private SimpleDifferenceTestClass simpleObjectCurrent;
  private SimpleDifferenceTestClass simpleObjectPrevious;

  private DifferenceTestWithSimpleListField diffWithListsCurrent;
  private DifferenceTestWithSimpleListField diffWithListsPrevious;

  private List<SimpleDifferenceTestClass> listOfSimpleDiffsCurrent;
  private List<SimpleDifferenceTestClass> listOfSimpleDiffsPrevious;

  private final List<String> defaultStringList = Arrays.asList("item1", "item2", "item3");
  private final List<Integer> defaultIntegerList = Arrays.asList(100, 200, 300);
  private final List<SimpleDifferenceTestClass> defaultSimpleDifferenceTestClassList = Arrays.asList(
      new SimpleDifferenceTestClass(
          true,
          "item 1",
          1,
          new StringWithTag("No tag"),
          new OtherDiffableAsStringClass("other 1")
      ),
      new SimpleDifferenceTestClass(
          true,
          "item 2",
          2,
          new StringWithTag("No tag"),
          new OtherDiffableAsStringClass("other 2")
      ),
      new SimpleDifferenceTestClass(true,
          "item 3",
          3,
          new StringWithTag("No tag"),
          new OtherDiffableAsStringClass("other 3")
      )
  );

  @Before
  public void setup() {
    differenceService = new DifferenceService();

    String defaultStringValue = "string";
    Integer defaultIntegerValue = 100;
    simpleObjectCurrent = new SimpleDifferenceTestClass(true,
        defaultStringValue,
        defaultIntegerValue,
        new StringWithTag("No tag"),
        new OtherDiffableAsStringClass(defaultStringValue));
    simpleObjectPrevious = new SimpleDifferenceTestClass(true,
        defaultStringValue,
        defaultIntegerValue,
        new StringWithTag("No tag"),
        new OtherDiffableAsStringClass(defaultStringValue));

    diffWithListsCurrent = new DifferenceTestWithSimpleListField(defaultStringList, defaultIntegerList);
    diffWithListsPrevious = new DifferenceTestWithSimpleListField(defaultStringList, defaultIntegerList);

    listOfSimpleDiffsCurrent = new ArrayList<>();
    listOfSimpleDiffsCurrent.addAll(defaultSimpleDifferenceTestClassList);
    listOfSimpleDiffsPrevious = new ArrayList<>();
    // we need two lists containing objects different objects which have matching values
    defaultSimpleDifferenceTestClassList.forEach(simpleDifferenceTestClass -> listOfSimpleDiffsPrevious.add(
        new SimpleDifferenceTestClass(true,
            simpleDifferenceTestClass.getStringField(),
            simpleDifferenceTestClass.getIntegerField(),
            simpleDifferenceTestClass.getStringWithTagField(),
            simpleDifferenceTestClass.getDiffableAsString())));

  }

  @Test
  public void differentiate_whenUsingSimpleObjects_thenResultContainsAllFields() {
    Map<String, Object> diffResult = differenceService.differentiate(simpleObjectCurrent, simpleObjectPrevious);

    Set<String> resultKeySet = diffResult.keySet();

    List<String> expectedDiffResultKeySet = FieldUtils.getAllFieldsList(SimpleDifferenceTestClass.class).stream().filter(
        f -> !f.isSynthetic()).map(f -> SimpleDifferenceTestClass.class.getSimpleName() + "_" + f.getName()).collect(
        toList());

    assertThat(resultKeySet).containsExactlyInAnyOrderElementsOf(expectedDiffResultKeySet);

  }

  @Test
  public void differentiate_whenUsingSimpleObjects_andObjectsAreEquivalent_thenAllDiffedResultObjectsAreTypeUNCHANGED() {
    Map<String, Object> diffResult = differenceService.differentiate(simpleObjectCurrent, simpleObjectPrevious);

    Set<DiffedField> resultDiffedFields = diffResult.values().stream().map(o -> ((DiffedField) o)).collect(
        Collectors.toSet());

    // types of all entries is UNCHANGED
    assertThat(resultDiffedFields.stream().allMatch(
        diffedField -> diffedField.getDifferenceType().equals(DifferenceType.UNCHANGED))).isTrue();
    // currentValue and previousValue match for each field
    assertThat(resultDiffedFields.stream().allMatch(
        dangerField -> dangerField.getCurrentValue().equals(dangerField.getPreviousValue()))).isTrue();
  }

  @Test
  public void differentiate_whenUsingSimpleObjects_andAllFieldsHaveChanged_thenAllDiffedResultObjectsAreTypeUPDATED() {
    simpleObjectCurrent.setBooleanField(false);
    simpleObjectCurrent.setIntegerField(999);
    simpleObjectCurrent.setStringField("Updated String");
    simpleObjectCurrent.setStringWithTagField(new StringWithTag("Updated String"));
    simpleObjectCurrent.setDiffableAsString(new OtherDiffableAsStringClass("Updated String"));

    Map<String, Object> diffResult = differenceService.differentiate(simpleObjectCurrent, simpleObjectPrevious);

    Set<DiffedField> resultDiffedFields = diffResult.values().stream().map(o -> ((DiffedField) o)).collect(
        Collectors.toSet());

    // type of all entries is UPDATED
    assertThat(resultDiffedFields.stream().allMatch(
        diffedField -> diffedField.getDifferenceType().equals(DifferenceType.UPDATED))).isTrue();

    // currentValue and previousValue do not match for each field
    assertThat(resultDiffedFields.stream().noneMatch(
        diffedField -> diffedField.getCurrentValue().equals(diffedField.getPreviousValue()))).isTrue();

  }

  @Test
  public void differentiate_whenUsingSimpleObjects_andAllFieldsHaveChanged_thenAllDiffedResultObjectsAreTypeDELETED() {
    simpleObjectCurrent.setBooleanField(null);
    simpleObjectCurrent.setIntegerField(null);
    simpleObjectCurrent.setStringField(null);
    simpleObjectCurrent.setStringWithTagField(new StringWithTag());
    simpleObjectCurrent.setDiffableAsString(null);

    Map<String, Object> diffResult = differenceService.differentiate(simpleObjectCurrent, simpleObjectPrevious);

    Set<DiffedField> resultDiffedFields = diffResult.values().stream().map(o -> ((DiffedField) o)).collect(
        Collectors.toSet());

    // type of all entries is DELETED
    assertThat(resultDiffedFields.stream().allMatch(
        diffedField -> diffedField.getDifferenceType().equals(DifferenceType.DELETED))).isTrue();

    // currentValue is blank and previous value has value each field
    assertThat(resultDiffedFields.stream().allMatch(diffedField -> StringUtils.isBlank(diffedField.getCurrentValue()))).isTrue();
    assertThat(
        resultDiffedFields.stream().allMatch(diffedField -> StringUtils.isNotBlank(diffedField.getPreviousValue()))).isTrue();

  }


  @Test(expected = DifferenceProcessingException.class)
  public void differentiate_whenObjectWithUnsupportedFieldType_thenThrowsException() {
    UndiffableObject undiffableObject = new UndiffableObject(Instant.now());
    differenceService.differentiate(undiffableObject, undiffableObject);
  }

  @Test(expected = DifferenceProcessingException.class)
  public void differentiate_whenObjectHasUnsupportedLists_thenThrowsException() {
    DifferenceTestWithUnsupportedListField undiffableObject = new DifferenceTestWithUnsupportedListField();
    differenceService.differentiate(undiffableObject, undiffableObject);
  }

  @Test
  public void differentiate_whenObjectHasSupportedLists_thenResultContainsListOfDiffs() {

    Map<String, Object> diffResult = differenceService.differentiate(diffWithListsCurrent, diffWithListsPrevious);

    for (Object diffResultObject : diffResult.values()) {
      // test that a list fields produce a mapped list
      assertThat(diffResultObject).isInstanceOf(List.class);
      // all objects in list are of the expected type
      assertThat(((List) diffResultObject).stream().allMatch(o -> o instanceof DiffedField)).isTrue();

    }

  }

  @Test
  public void differentiate_whenObjectHasSupportedListsA_andNoListItemsAreDifferent_thenResultListForFieldsAreAllUNCHANGED() {

    Map<String, Object> diffResult = differenceService.differentiate(diffWithListsCurrent, diffWithListsPrevious);

    for (Object diffResultObject : diffResult.values()) {

      List<Object> resultObjectList = ((List) diffResultObject);
      List<DiffedField> diffedFieldList = resultObjectList.stream().filter(o -> o instanceof DiffedField).map(
          o -> ((DiffedField) o)).collect(toList());

      assertThat(diffedFieldList.size()).isPositive();
      // all objects in list are of the expected type
      assertThat(diffedFieldList).allMatch(diffedField -> diffedField.getDifferenceType().equals(DifferenceType.UNCHANGED));
      // all objects in list have equal previous and current values
      assertThat(diffedFieldList).allMatch(
          diffedField -> diffedField.getCurrentValue().equals(diffedField.getPreviousValue()));

    }

  }

  @Test
  public void differentiate_whenObjectHasSupportedListsA_andPreviousListWasEmpty_thenAllResultsAreADDED() {

    diffWithListsPrevious.setIntegerList(Collections.emptyList());
    diffWithListsPrevious.setStringList(Collections.emptyList());

    Map<String, Object> diffResult = differenceService.differentiate(diffWithListsCurrent, diffWithListsPrevious);

    for (Object diffResultObject : diffResult.values()) {

      List<Object> resultObjectList = ((List) diffResultObject);
      List<DiffedField> diffedFieldList = resultObjectList.stream().filter(o -> o instanceof DiffedField).map(
          o -> ((DiffedField) o)).collect(toList());

      assertThat(diffedFieldList.size()).isEqualTo(defaultStringList.size());
      // all objects in list are of the expected type
      assertThat(diffedFieldList).allMatch(diffedField -> diffedField.getDifferenceType().equals(DifferenceType.ADDED));
      // all objects in list have a current value but no previous value
      assertThat(diffedFieldList).allMatch(diffedField -> StringUtils.isNotBlank(diffedField.getCurrentValue()));
      assertThat(diffedFieldList).allMatch(diffedField -> StringUtils.isBlank(diffedField.getPreviousValue()));

    }

  }

  @Test
  public void differentiate_whenObjectHasSupportedListsA_andCurrentListIsEmpty_andPreviousListHasContent_thenAllResultsAreDELETED() {

    diffWithListsCurrent.setIntegerList(Collections.emptyList());
    diffWithListsCurrent.setStringList(Collections.emptyList());

    Map<String, Object> diffResult = differenceService.differentiate(diffWithListsCurrent, diffWithListsPrevious);

    for (Object diffResultObject : diffResult.values()) {

      List<Object> resultObjectList = ((List) diffResultObject);
      List<DiffedField> diffedFieldList = resultObjectList.stream().filter(o -> o instanceof DiffedField).map(
          o -> ((DiffedField) o)).collect(toList());

      assertThat(diffedFieldList.size()).isEqualTo(defaultStringList.size());
      // all objects in list are of the expected type
      assertThat(diffedFieldList).allMatch(diffedField -> diffedField.getDifferenceType().equals(DifferenceType.DELETED));
      // all objects in list have blank current value but non-blank previous value
      assertThat(diffedFieldList).allMatch(diffedField -> StringUtils.isBlank(diffedField.getCurrentValue()));
      assertThat(diffedFieldList).allMatch(diffedField -> StringUtils.isNotBlank(diffedField.getPreviousValue()));

    }

  }

  @Test
  public void differentiateComplexLists_whenListsObjectsCompletelyMap_andThereAreNoUpdatedObjects_thenResultListContainsOnlyUNCHANGEDDiffs() {
    List<Map<String, ?>> diffResultList = differenceService.differentiateComplexLists(listOfSimpleDiffsCurrent,
        listOfSimpleDiffsPrevious, this::simpleDiffTestClassMappingFunction, this::simpleDiffTestClassMappingFunction);

    assertThat(diffResultList.size()).isEqualTo(defaultSimpleDifferenceTestClassList.size());
    for (Map<String, ?> diffResultMap : diffResultList) {
      // this test works for the simple class where we know no List is contained within the object
      assertThat(diffResultMap.values()).allMatch(o -> o instanceof DiffedField);
      List<DiffedField> diffedFieldList = diffResultMap.values().stream().filter(o -> o instanceof DiffedField).map(
          o -> (DiffedField) o).collect(toList());

      assertThat(diffedFieldList).isNotEmpty().allMatch(diffedField -> diffedField.getDifferenceType().equals(DifferenceType.UNCHANGED));
    }
  }

  @Test
  public void differentiateComplexLists_whenListsObjectsCompletelyMap_andEachObjectHasHadANonMappingFieldUpdated_thenResultListContainsUPDATEDDiffs() {
    for (SimpleDifferenceTestClass simpleDifferenceTestClass : listOfSimpleDiffsCurrent) {
      simpleDifferenceTestClass.setStringField("Updated Item");
    }

    List<Map<String, ?>> diffResultList = differenceService.differentiateComplexLists(listOfSimpleDiffsCurrent,
        listOfSimpleDiffsPrevious, this::simpleDiffTestClassMappingFunction, this::simpleDiffTestClassMappingFunction);

    assertThat(diffResultList.size()).isEqualTo(defaultSimpleDifferenceTestClassList.size());
    for (Map<String, ?> diffResultMap : diffResultList) {
      // this test works for the simple class where we know no List is contained within the object
      assertThat(diffResultMap.values()).allMatch(o -> o instanceof DiffedField);
      List<DiffedField> diffedFieldList = diffResultMap.values().stream().filter(o -> o instanceof DiffedField).map(
          o -> (DiffedField) o).collect(toList());
      assertThat(diffedFieldList).anyMatch(diffedField -> diffedField.getDifferenceType().equals(DifferenceType.UPDATED));
    }
  }

  @Test
  public void differentiateComplexLists_whenPreviousListIsEmpty_thenResultListContainsADDEDDiffsOnly() {
    listOfSimpleDiffsPrevious = Collections.emptyList();

    List<Map<String, ?>> diffResultList = differenceService.differentiateComplexLists(listOfSimpleDiffsCurrent,
        listOfSimpleDiffsPrevious, this::simpleDiffTestClassMappingFunction, this::simpleDiffTestClassMappingFunction);

    assertThat(diffResultList.size()).isEqualTo(defaultSimpleDifferenceTestClassList.size());
    for (Map<String, ?> diffResultMap : diffResultList) {
      // this test works for the simple class where we know no List is contained within the object
      assertThat(diffResultMap.values()).allMatch(o -> o instanceof DiffedField);
      List<DiffedField> diffedFieldList = diffResultMap.values().stream().filter(o -> o instanceof DiffedField).map(
          o -> (DiffedField) o).collect(toList());

      assertThat(diffedFieldList).isNotEmpty().allMatch(diffedField -> diffedField.getDifferenceType().equals(DifferenceType.ADDED));
    }
  }

  @Test
  public void differentiateComplexLists_whenCurrentListIsEmpty_thenResultListContainsDeletedDiffsOnly() {
    listOfSimpleDiffsCurrent = Collections.emptyList();

    List<Map<String, ?>> diffResultList = differenceService.differentiateComplexLists(listOfSimpleDiffsCurrent,
        listOfSimpleDiffsPrevious, this::simpleDiffTestClassMappingFunction, this::simpleDiffTestClassMappingFunction);

    assertThat(diffResultList.size()).isEqualTo(defaultSimpleDifferenceTestClassList.size());
    for (Map<String, ?> diffResultMap : diffResultList) {
      // this test works for the simple class where we know no List is contained within the object
      assertThat(diffResultMap.values()).allMatch(o -> o instanceof DiffedField);
      List<DiffedField> diffedFieldList = diffResultMap.values().stream().filter(o -> o instanceof DiffedField).map(
          o -> (DiffedField) o).collect(toList());

      assertThat(diffedFieldList).isNotEmpty().allMatch(diffedField -> diffedField.getDifferenceType().equals(DifferenceType.DELETED));
    }
  }

  private Integer simpleDiffTestClassMappingFunction(SimpleDifferenceTestClass simpleDifferenceTestClass) {
    return simpleDifferenceTestClass.getIntegerField();
  }

  @Test
  public void allSupportedDiffClassesAreIncludedInTestedObject() {
    Set<Class> supportedClassSet = new HashSet<>();
    Set<Class> testedClassMemberClassSet = new HashSet<>(
        Arrays.asList(FieldUtils.getAllFields(SimpleDifferenceTestClass.class)))
        .stream()
        .map(Field::getType)
        .collect(Collectors.toSet());

    for (ComparisonType comparisonType : ComparisonType.values()) {
      if (!comparisonType.equals(ComparisonType.LIST)) {
        supportedClassSet.addAll(comparisonType.getSupportedClasses());
      }
    }

    // Assert that every supported class is the class of a field in SimpleDiffTestClass. This ensures we dont add new comparison types and forget to test them
    try {
      assertThat(supportedClassSet).allMatch(testedClassMemberClassSet::contains);
    } catch (AssertionError e) {
      throw new AssertionError("All supported diff comparison classes  need to added to the SimpleDiffTestClass!", e);
    }

  }

  @Test
  public void differentiate_ignoresFieldsWithinIgnoreSet(){

    var allFieldNames = Arrays.stream(FieldUtils.getAllFields(SimpleDifferenceTestClass.class))
        .map(Field::getName)
        .collect(toSet());

    Map<String, Object> diffResult = differenceService.differentiate(simpleObjectCurrent, simpleObjectPrevious, allFieldNames);

    assertThat(diffResult).isEmpty();

  }

  @Test
  public void differentiateComplexLists_ignoresFieldsWithinIgnoreSet(){

    var allFieldNames = Arrays.stream(FieldUtils.getAllFields(SimpleDifferenceTestClass.class))
        .map(Field::getName)
        .collect(toSet());

    List<Map<String, ?>> diffResult = differenceService.differentiateComplexLists(
        List.of(simpleObjectCurrent),
        List.of(simpleObjectPrevious),
        allFieldNames,
        SimpleDifferenceTestClass::getIntegerField,
        SimpleDifferenceTestClass::getIntegerField
    );

    assertThat(diffResult.get(0)).isEmpty();

  }

}