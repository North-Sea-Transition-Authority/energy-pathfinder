package uk.co.ogauthority.pathfinder.model.email.emailproperties.contributor;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;
import uk.co.ogauthority.pathfinder.service.email.notify.CommonEmailMergeField;

public class AddedProjectContributorEmailProperties extends ProjectContributorEmailProperties {

  private final String serviceLoginUrl;

  public AddedProjectContributorEmailProperties(String recipientIdentifier,
                                                ProjectDetail detail,
                                                String serviceLoginUrl,
                                                ProjectOperator projectOperator,
                                                String projectTitle) {
    super(
        recipientIdentifier,
        detail,
        projectOperator,
        projectTitle,
        NotifyTemplate.ADDED_PROJECT_CONTRIBUTOR
    );
    this.serviceLoginUrl = serviceLoginUrl;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    Map<String, Object> emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put(CommonEmailMergeField.SERVICE_LOGIN_URL, serviceLoginUrl);
    return emailPersonalisation;
  }

  public String getServiceLoginUrl() {
    return serviceLoginUrl;
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
    AddedProjectContributorEmailProperties that = (AddedProjectContributorEmailProperties) o;
    return this.getDetail().equals(that.getDetail())
        && this.getProjectOperator().equals(that.getProjectOperator())
        && this.getProjectTitle().equals(that.getProjectTitle())
        && this.getTemplate().equals(that.getTemplate())
        && this.getRecipientIdentifier().equals(that.getRecipientIdentifier())
        && this.getServiceLoginUrl().equals(that.getServiceLoginUrl());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), this.getServiceLoginUrl());
  }
}
