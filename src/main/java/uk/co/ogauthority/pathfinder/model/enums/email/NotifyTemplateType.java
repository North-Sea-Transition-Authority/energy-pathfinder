package uk.co.ogauthority.pathfinder.model.enums.email;

public enum NotifyTemplateType {

  EMAIL_TEMPLATE_TYPE("email");

  private final String typeName;

  NotifyTemplateType(String typeName) {
    this.typeName = typeName;
  }

  public String getTypeName() {
    return typeName;
  }
}