package uk.co.ogauthority.pathfinder.development.service;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;

import java.time.Instant;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.development.DevelopmentProjectCreatorBean;
import uk.co.ogauthority.pathfinder.development.DevelopmentProjectCreatorForm;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;

@Service
public class DevelopmentProjectCreatorSchedulerService {

  private final Scheduler scheduler;

  public DevelopmentProjectCreatorSchedulerService(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public void scheduleProjectCreation(
      DevelopmentProjectCreatorForm form,
      AuthenticatedUserAccount user,
      PortalOrganisationGroup organisationGroup
  ) {
    try {

      JobKey jobKey = jobKey("ProjectCreator" + Instant.now().toString());
      JobDetail jobDetail = newJob(DevelopmentProjectCreatorBean.class)
          .withIdentity(jobKey)
          .build();

      jobDetail.getJobDataMap().put("form", form);
      jobDetail.getJobDataMap().put("user", user);
      jobDetail.getJobDataMap().put("orgGroup", organisationGroup);

      Trigger trigger = TriggerBuilder.newTrigger().startNow().build();

      scheduler.scheduleJob(jobDetail, trigger);


    } catch (SchedulerException e) {
      throw new RuntimeException("Error scheduling project creator", e);
    }
  }
}
