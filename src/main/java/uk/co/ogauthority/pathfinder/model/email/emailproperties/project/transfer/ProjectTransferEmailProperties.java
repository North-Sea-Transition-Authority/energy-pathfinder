package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

class ProjectTransferEmailProperties extends EmailProperties {

  private final String transferReason;

  public ProjectTransferEmailProperties(NotifyTemplate template,
                                        String transferReason) {
    super(template);
    this.transferReason = transferReason;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("TRANSFER_REASON", transferReason);
    return emailPersonalisation;
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }
    if (this == o) {
      return true;
    }
    if (getClass() != o.getClass()) {
      return false;
    }
    ProjectTransferEmailProperties that = (ProjectTransferEmailProperties) o;
    return Objects.equals(transferReason, that.transferReason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), transferReason);
  }
}
