package uk.co.ogauthority.pathfinder.service.quarterlystatistics;

public class ProjectUpdateStatistic {

  private final String statisticPrompt;

  private final int totalProjects;

  private final int totalProjectsUpdated;

  private final double percentageOfProjectsUpdated;

  public ProjectUpdateStatistic(String statisticPrompt,
                                int totalProjects,
                                int totalProjectsUpdated) {
    this.statisticPrompt = statisticPrompt;
    this.totalProjects = totalProjects;
    this.totalProjectsUpdated = totalProjectsUpdated;
    this.percentageOfProjectsUpdated = computeUpdatePercentage();
  }

  public String getStatisticPrompt() {
    return statisticPrompt;
  }

  public int getTotalProjects() {
    return totalProjects;
  }

  public int getTotalProjectsUpdated() {
    return totalProjectsUpdated;
  }

  public double getPercentageOfProjectsUpdated() {
    return percentageOfProjectsUpdated;
  }

  private double computeUpdatePercentage() {
    if (totalProjects == 0) {
      return 0.0;
    } else {
      return ((double) totalProjectsUpdated / (double) totalProjects) * 100.0;
    }
  }
}
