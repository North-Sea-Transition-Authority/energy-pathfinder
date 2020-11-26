package uk.co.ogauthority.pathfinder.service.entityduplication;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.exception.EntityDuplicationException;

@Service
public class EntityDuplicationService {

  private final EntityManager entityManager;

  @Autowired
  public EntityDuplicationService(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * Duplicate a single entity, retarget the parent, and return new entity.
   * @param entityToDuplicate the entity to duplicate
   * @param newParentEntity the new parent to associate to the duplicated entity
   * @param duplicationEntityClass the class of entity to duplicate
   * @param <I> the class which represents the ID of Child
   * @param <P> the class represents the PARENT of Child
   * @param <C> the class of the entity we are duplicating
   * @return newly persisted and re-parented ChildEntity which is a duplicate of entityToDuplicate
   */
  public <I, P extends ParentEntity, C extends ChildEntity<I, P>> C duplicateEntityAndSetNewParent(
      C entityToDuplicate,
      P newParentEntity,
      Class<C> duplicationEntityClass
  ) {
    return duplicateEntityAndSetNewParentHelper(entityToDuplicate, newParentEntity, duplicationEntityClass);
  }

  /**
   * Duplicate a collection entity, retarget the parent, and return new entity.
   * @param entitiesToDuplicate the collection of entities to duplicate
   * @param newParentEntity the new parent to associate to the duplicated entities
   * @param duplicationEntityClass the class of entity to duplicate
   * @param <I> the class which represents the ID of Child
   * @param <P> the class represents the PARENT of Child
   * @param <C> the class of the entity we are duplicating
   * @return a set of DuplicatedEntityPairing of the original entity and the re-parented duplicate
   */
  public <I, P extends ParentEntity, C extends ChildEntity<I, P>> Set<DuplicatedEntityPairing<C>> duplicateEntitiesAndSetNewParent(
      List<C> entitiesToDuplicate,
      P newParentEntity,
      Class<C> duplicationEntityClass
  ) {

    var duplicateEntityPairings = new HashSet<DuplicatedEntityPairing<C>>();

    entitiesToDuplicate.forEach(entityToDuplicate -> {

      final var duplicateEntity = duplicateEntityAndSetNewParentHelper(
          entityToDuplicate,
          newParentEntity,
          duplicationEntityClass
      );

      final var duplicateEntityPairing = createDuplicatedEntityPairing(
          entityToDuplicate,
          duplicateEntity
      );

      duplicateEntityPairings.add(duplicateEntityPairing);

    });

    return duplicateEntityPairings;
  }

  /**
   * Convert a set of DuplicatedEntityPairing into a map with the original entity as the key.
   * @param duplicatedEntityPairings a set of DuplicatedEntityPairing objects to covert to a map
   * @param <I> the class which represents the ID of Child
   * @param <P> the class represents the PARENT of Child
   * @param <C> the class of the entity we are duplicating
   * @return a map with the key as the original entity and the value as the re-parented duplicate
   */
  public <I, P extends ParentEntity, C extends ChildEntity<I, P>> Map<C, C> createDuplicatedEntityPairingMap(
      Set<DuplicatedEntityPairing<C>> duplicatedEntityPairings
  ) {
    return duplicatedEntityPairings
        .stream()
        .collect(Collectors.toMap(
            DuplicatedEntityPairing::getOriginalEntity,
            DuplicatedEntityPairing::getDuplicateEntity
        ));
  }

  /**
   * Helper method to duplicate and reparent an entity.
   * @param entityToDuplicate the entity to duplicate
   * @param newParentEntity the new parent to associate to the duplicated entity
   * @param duplicationEntityClass the class of entity to duplicate
   * @param <I> the class which represents the ID of Child
   * @param <P> the class represents the PARENT of Child
   * @param <C> the class of the entity we are duplicating
   * @return a newly instantiated entity which is a duplicate of entityToDuplicate re-parented to newParentEntity
   */
  private <I, P extends ParentEntity, C extends ChildEntity<I, P>> C duplicateEntityAndSetNewParentHelper(
      C entityToDuplicate,
      P newParentEntity,
      Class<C> duplicationEntityClass
  ) {

    try {

      var newInstance = duplicationEntityClass.getConstructor().newInstance();

      for (Field field : FieldUtils.getAllFields(duplicationEntityClass)) {
        if (!field.isSynthetic()) {
          var value = FieldUtils.readField(field, entityToDuplicate, true);
          FieldUtils.writeField(field, newInstance, value, true);
        }
      }

      newInstance.clearId();
      newInstance.setParent(newParentEntity);
      entityManager.persist(newInstance);

      return newInstance;

    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
      throw new EntityDuplicationException(
          String.format(
              "Could not duplicate entity of class %s with id: %s ",
              duplicationEntityClass.getSimpleName(),
              entityToDuplicate.getId().toString()
          ),
          ex
      );
    }
  }

  private <I, P extends ParentEntity, C extends ChildEntity<I, P>> DuplicatedEntityPairing<C> createDuplicatedEntityPairing(
      C originalEntity,
      C duplicateEntity
  ) {
    return new DuplicatedEntityPairing<>(originalEntity, duplicateEntity);
  }

}
