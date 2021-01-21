package uk.co.ogauthority.pathfinder.controller.quarterlystatistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.service.quarterlystatistics.QuarterlyStatisticsService;

@Controller
@RequestMapping("/quarterly-statistics")
public class QuarterlyStatisticsController {

  public static final String QUARTERLY_STATISTICS_TITLE = "Quarterly statistics";

  private final QuarterlyStatisticsService quarterlyStatisticsService;

  @Autowired
  public QuarterlyStatisticsController(QuarterlyStatisticsService quarterlyStatisticsService) {
    this.quarterlyStatisticsService = quarterlyStatisticsService;
  }

  @GetMapping
  public ModelAndView getQuarterlyStatistics(AuthenticatedUserAccount userAccount) {
    return quarterlyStatisticsService.getQuarterlyStatisticsModelAndView();
  }
}
