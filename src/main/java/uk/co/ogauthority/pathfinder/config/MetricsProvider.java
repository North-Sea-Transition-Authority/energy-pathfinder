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
  private final Counter subscribePageHitCounter;
  private final Counter unSubscribePageHitCounter;

  @Autowired
  public MetricsProvider(MeterRegistry registry) {
    this.dashboardTimer = registry.timer("pathfinder.dashboardTimer");
    this.projectStartCounter = registry.counter("pathfinder.projectStartCounter");
    this.subscribePageHitCounter = registry.counter("pathfinder.subscribePageHitCounter");
    this.unSubscribePageHitCounter = registry.counter("pathfinder.unSubscribePageHitCounter");
  }

  public Timer getDashboardTimer() {
    return dashboardTimer;
  }

  public Counter getProjectStartCounter() {
    return projectStartCounter;
  }

  public Counter getSubscribePageHitCounter() {
    return subscribePageHitCounter;
  }

  public Counter getUnSubscribePageHitCounter() {
    return unSubscribePageHitCounter;
  }
}
