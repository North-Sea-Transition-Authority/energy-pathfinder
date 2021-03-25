package uk.co.ogauthority.pathfinder.service.entityduplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EntityDuplicationServiceTest {

  @Mock
  private EntityManager entityManager;

  private EntityDuplicationService entityDuplicationService;

  @Before
  public void setup() {
    entityDuplicationService = new EntityDuplicationService(entityManager);
  }

  @Test
  public void duplicateEntityAndSetNewParent_ensureCorrectDuplicationAndReparent() {

    final var fromParentEntity = new TestParentEntity(1);
    final var toParentEntity = new TestParentEntity(2);

    final var originalChildEntity = new TestChildEntity(1, "value", fromParentEntity);

    final var duplicationResult = entityDuplicationService.duplicateEntityAndSetNewParent(
        originalChildEntity,
        toParentEntity,
        TestChildEntity.class
    );

    assertDuplicatedEntityProperties(
        originalChildEntity,
        duplicationResult,
        toParentEntity
    );

    verify(entityManager, times(1)).persist(duplicationResult);

  }

  @Test
  public void duplicateEntitiesAndSetNewParent_ensureCorrectDuplicationAndReparent() {

    final var fromParentEntity = new TestParentEntity(1);
    final var toParentEntity = new TestParentEntity(2);

    final var originalChildEntities = List.of(
        new TestChildEntity(1, "value1", fromParentEntity),
        new TestChildEntity(2, "value2", fromParentEntity),
        new TestChildEntity(3, "value3", fromParentEntity)
    );

    final var duplicationResults = entityDuplicationService.duplicateEntitiesAndSetNewParent(
        originalChildEntities,
        toParentEntity,
        TestChildEntity.class
    );

    assertThat(duplicationResults).hasSize(originalChildEntities.size());

    duplicationResults.forEach(duplicationResult -> {

      assertDuplicatedEntityProperties(
          duplicationResult.getOriginalEntity(),
          duplicationResult.getDuplicateEntity(),
          toParentEntity
      );

      verify(entityManager, times(1)).persist(duplicationResult.getDuplicateEntity());

    });

  }

  @Test
  public void duplicateEntitiesAndSetNewParent_whenEmptyList_thenEmptySetReturnedAnNoPersistence() {

    final var toParentEntity = new TestParentEntity(1);

    final var duplicationResults = entityDuplicationService.duplicateEntitiesAndSetNewParent(
        List.of(),
        toParentEntity,
        TestChildEntity.class
    );

    assertThat(duplicationResults).isEmpty();

    verify(entityManager, times(0)).persist(any());
  }

  private void assertDuplicatedEntityProperties(TestChildEntity originalChildEntity,
                                                TestChildEntity duplicatedChildEntity,
                                                TestParentEntity toParentEntity) {
    assertThat(duplicatedChildEntity.getId()).isNotEqualTo(originalChildEntity.getId());
    assertThat(duplicatedChildEntity.getStringValue()).isEqualTo(originalChildEntity.getStringValue());
    assertThat(duplicatedChildEntity.getParent().getId()).isEqualTo(toParentEntity.getId());
  }

  @Test
  public void createDuplicatedEntityPairingMap_ensureCorrectResult() {

    final var fromParentEntity = new TestParentEntity(1);
    final var toParentEntity = new TestParentEntity(2);

    final var originalChildEntity1 = new TestChildEntity(1, "value1", fromParentEntity);
    final var duplicatedChildEntity1 = new TestChildEntity(2, originalChildEntity1.getStringValue(), toParentEntity);

    final var originalChildEntity2 = new TestChildEntity(3, "value2", fromParentEntity);
    final var duplicatedChildEntity2 = new TestChildEntity(4, originalChildEntity2.getStringValue(), toParentEntity);

    final var duplicatedEntityPairings = Set.of(
        new DuplicatedEntityPairing<>(originalChildEntity1, duplicatedChildEntity1),
        new DuplicatedEntityPairing<>(originalChildEntity2, duplicatedChildEntity2)
    );

    final var duplicatedEntitiesMap = entityDuplicationService.createDuplicatedEntityPairingMap(
        duplicatedEntityPairings
    );

    assertThat(duplicatedEntitiesMap)
        .hasSize(duplicatedEntityPairings.size())
        .containsEntry(originalChildEntity1, duplicatedChildEntity1)
        .containsEntry(originalChildEntity2, duplicatedChildEntity2);
  }

}