package uk.co.ogauthority.pathfinder.service.scheduler.newsletters;

import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import uk.co.ogauthority.pathfinder.service.newsletters.NewsletterService;

@Component
public class MonthlyNewsletterJob extends QuartzJobBean {

  private final NewsletterService newsletterService;

  public MonthlyNewsletterJob(NewsletterService newsletterService) {
    this.newsletterService = newsletterService;
  }

  @Override
  protected void executeInternal(JobExecutionContext context) {
    newsletterService.sendNewsletterToSubscribers();
  }
}
