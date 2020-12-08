package uk.co.ogauthority.pathfinder.service.entityduplication;

public class TestParentEntity implements ParentEntity {

  private Integer id;

  public TestParentEntity(Integer id) {
    this.id = id;
  }

  public Integer getId() {
    return id;
  }
}
