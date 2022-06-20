package uk.co.ogauthority.pathfinder.service.entityduplication;

public class TestChildEntity implements ChildEntity<Integer, TestParentEntity> {

  private final static int finalField = 123456;

  private Integer id;

  private String stringValue;

  private TestParentEntity parentEntity;

  public TestChildEntity() {}

  public TestChildEntity(Integer id, String stringValue, TestParentEntity parentEntity) {
    this.id = id;
    this.stringValue = stringValue;
    this.parentEntity = parentEntity;
  }

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }

  @Override
  public Integer getId() {
    return id;
  }

  @Override
  public void clearId() {
    this.id = null;
  }

  @Override
  public void setParent(TestParentEntity parentEntity) {
    this.parentEntity = parentEntity;
  }

  @Override
  public TestParentEntity getParent() {
    return parentEntity;
  }
}
