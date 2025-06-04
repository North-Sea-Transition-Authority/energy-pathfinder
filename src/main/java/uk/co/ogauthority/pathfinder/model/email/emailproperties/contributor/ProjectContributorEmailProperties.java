package uk.co.ogauthority.pathfinder.model.email.emailproperties.contributor;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.util.DateUtil;

abstract class ProjectContributorEmailProperties extends EmailProperties {

  public static final String TITLE_PLACEHOLDER = "a %s project created on %s";

  private final ProjectDetail detail;
  private final ProjectOperator projectOperator;
  private final String projectTitle;

  ProjectContributorEmailProperties(String recipientIdentifier,
                                    ProjectDetail detail,
                                    ProjectOperator projectOperator,
                                    String projectTitle,
                                    NotifyTemplate notifyTemplate) {
    super(notifyTemplate, recipientIdentifier);
    this.detail = detail;
    this.projectOperator = projectOperator;
    this.projectTitle = projectTitle;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    Map<String, Object> emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("PROJECT_TYPE_DISPLAY_NAME", ProjectService.getProjectTypeDisplayName(detail));
    emailPersonalisation.put("OWNER_OPERATOR_NAME", resolveProjectOperatorName());
    emailPersonalisation.put("PROJECT_TITLE", resolveProjectTitle());
    return emailPersonalisation;
  }

  public ProjectDetail getDetail() {
    return detail;
  }

  public ProjectOperator getProjectOperator() {
    return projectOperator;
  }

  public String getProjectTitle() {
    return projectTitle;
  }

  private String resolveProjectTitle() {
    if (!projectTitle.isBlank()) {
      return projectTitle;
    }
    var formattedTime = DateUtil.formatInstant(detail.getCreatedDatetime());
    return String.format(TITLE_PLACEHOLDER, detail.getStatus().getDisplayName(), formattedTime);
  }

  private String resolveProjectOperatorName() {
    if (projectOperator.getOrganisationGroup() != null) {
      return projectOperator.getOrganisationGroup().getName();
    }
    return String.format("The %s operator/developer", ProjectService.getProjectTypeDisplayNameLowercase(detail));
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
    ProjectContributorEmailProperties that = (ProjectContributorEmailProperties) o;
    return this.detail.equals(that.detail)
        && this.projectOperator.equals(that.projectOperator)
        && this.projectTitle.equals(that.projectTitle)
        && this.getTemplate().equals(that.getTemplate())
        && this.getRecipientIdentifier().equals(that.getRecipientIdentifier());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), detail, projectOperator, projectTitle);
  }
}
