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
    final var projectOperator = new ProjectOperator(
        detail,
        organisationGroup
    );

    projectOperator.setIsPublishedAsOperator(true);

    return projectOperator;
  }

  public static ProjectOperatorForm getCompleteForm() {
    final var form = new ProjectOperatorForm();
    form.setOperator(ORG_GROUP_ID.toString());
    form.setIsPublishedAsOperator(true);
    return form;
  }


}
