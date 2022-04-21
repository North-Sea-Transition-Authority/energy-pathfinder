package uk.co.ogauthority.pathfinder.model.entity.project.workplanprojectcontribution;

import javax.persistence.Entity;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;

@Entity
@Table(name = "work_plan_contributor_details")
public class ForwardWorkPlanContributorDetails extends ProjectDetailEntity {

  Boolean hasProjectContributors;

  public ForwardWorkPlanContributorDetails() {

  }

  public ForwardWorkPlanContributorDetails(ProjectDetail projectDetail,
                                           Boolean hasProjectContributors) {
    this.projectDetail = projectDetail;
    this.hasProjectContributors = hasProjectContributors;
  }

  public Boolean getHasProjectContributors() {
    return hasProjectContributors;
  }

  public void setHasProjectContributors(Boolean hasProjectContributors) {
    this.hasProjectContributors = hasProjectContributors;
  }
}
