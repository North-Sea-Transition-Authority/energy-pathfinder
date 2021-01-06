package uk.co.ogauthority.pathfinder.development;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.development.service.DevelopmentProjectCreatorService;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;

@Component
public class DevelopmentProjectCreatorBean extends QuartzJobBean {

  private final DevelopmentProjectCreatorService developmentProjectCreatorService;

  @Autowired
  public DevelopmentProjectCreatorBean(
      DevelopmentProjectCreatorService developmentProjectCreatorService) {
    this.developmentProjectCreatorService = developmentProjectCreatorService;
  }

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    try {

      JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
      DevelopmentProjectCreatorForm form = (DevelopmentProjectCreatorForm) jobDataMap.get("form");
      AuthenticatedUserAccount user = (AuthenticatedUserAccount) jobDataMap.get("user");
      PortalOrganisationGroup organisationGroup = (PortalOrganisationGroup) jobDataMap.get("orgGroup");

      developmentProjectCreatorService.createProjects(form, user, organisationGroup);

    } catch (Exception e) {
      throw new JobExecutionException(e);
    }
  }
}
