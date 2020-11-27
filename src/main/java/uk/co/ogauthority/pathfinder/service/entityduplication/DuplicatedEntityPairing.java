package uk.co.ogauthority.pathfinder.service.entityduplication;

public class DuplicatedEntityPairing<C> {

  private final C originalEntity;

  private final C duplicateEntity;

  public DuplicatedEntityPairing(C originalEntity,
                                 C duplicateEntity) {
    this.originalEntity = originalEntity;
    this.duplicateEntity = duplicateEntity;
  }

  public C getOriginalEntity() {
    return originalEntity;
  }

  public C getDuplicateEntity() {
    return duplicateEntity;
  }
}
