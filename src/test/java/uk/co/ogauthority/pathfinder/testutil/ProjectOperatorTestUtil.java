package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;

public class ProjectOperatorTestUtil {

  public static final Integer ORG_GROUP_ID = 1;
  public static final String ORG_GROUP_NAME = "Org Grp";
  public static final String ORG_GROUP_SHORT_NAME = "OrgGrp";
  public static final PortalOrganisationGroup ORG_GROUP = TeamTestingUtil.generateOrganisationGroup(
      ORG_GROUP_ID,
      ORG_GROUP_NAME,
      ORG_GROUP_SHORT_NAME
  );

  public static PortalOrganisationGroup getOrgGroup(String name) {
    return TeamTestingUtil.generateOrganisationGroup(
          ORG_GROUP_ID,
          name,
          ORG_GROUP_SHORT_NAME
      );
  }

  public static ProjectOperator getOperator() {
    return getOperator(
        ProjectUtil.getProjectDetails(),
        ORG_GROUP
    );
  }

  public static ProjectOperator getOperator(PortalOrganisationGroup organisationGroup) {
    return getOperator(
        ProjectUtil.getProjectDetails(),
        organisationGroup
    );
  }

  public static ProjectOperator getOperator(ProjectDetail detail, PortalOrganisationGroup organisationGroup) {
    return new ProjectOperator(
        detail,
        organisationGroup
    );
  }

  public static ProjectOperatorForm getCompleteForm() {
    return new ProjectOperatorForm(ORG_GROUP_ID.toString());
  }


}
