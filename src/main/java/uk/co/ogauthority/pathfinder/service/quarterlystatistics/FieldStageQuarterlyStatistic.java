package uk.co.ogauthority.pathfinder.service.quarterlystatistics;

public class FieldStageQuarterlyStatistic {

  private final String fieldStage;

  private final int totalProjects;

  private final int totalProjectsUpdateThisQuarter;

  private final double percentageOfProjectsUpdated;

  public FieldStageQuarterlyStatistic(String fieldStage,
                                      int totalProjects,
                                      int totalProjectsUpdateThisQuarter) {
    this.fieldStage = fieldStage;
    this.totalProjects = totalProjects;
    this.totalProjectsUpdateThisQuarter = totalProjectsUpdateThisQuarter;
    this.percentageOfProjectsUpdated = computeUpdatePercentage();
  }

  public String getFieldStage() {
    return fieldStage;
  }

  public int getTotalProjects() {
    return totalProjects;
  }

  public int getTotalProjectsUpdateThisQuarter() {
    return totalProjectsUpdateThisQuarter;
  }

  public double getPercentageOfProjectsUpdated() {
    return percentageOfProjectsUpdated;
  }

  private double computeUpdatePercentage() {
    if (totalProjects == 0) {
      return 0.0;
    } else {
      return ((double) totalProjectsUpdateThisQuarter / (double) totalProjects) * 100.0;
    }
  }
}
