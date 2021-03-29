package uk.co.ogauthority.pathfinder.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetricsProvider {
  private final Timer dashboardTimer;
  private final Counter projectStartCounter;

  @Autowired
  public MetricsProvider(MeterRegistry registry) {
    this.dashboardTimer = registry.timer("pathfinder.dashboardTimer");
    this.projectStartCounter = registry.counter("pathfinder.projectStartCounter");
  }

  public Timer getDashboardTimer() {
    return dashboardTimer;
  }

  public Counter getProjectStartCounter() {
    return projectStartCounter;
  }
}
