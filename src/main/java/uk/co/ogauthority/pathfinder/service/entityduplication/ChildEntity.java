package uk.co.ogauthority.pathfinder.service.entityduplication;

/**
 * Interface to identify entities which have a parent and allow manipulation of that relationship.
 *
 * @param <I> type used for child entity ID
 * @param <P> type of entity's Parent.
 */
public interface ChildEntity<I, P extends ParentEntity> {

  I getId();

  void clearId();

  void setParent(P parentEntity);

  P getParent();

}
