package uk.co.ogauthority.pathfinder.config;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

public class MetricsProvider {
  private final Timer dashboardTimer;
  private final Counter projectStartCounter;
  private final Counter subscribePageHitCounter;
  private final Counter subscribePagePostCounter;
  private final Counter unSubscribePageHitCounter;
  private final Counter unsubscribePagePostCounter;

  public MetricsProvider(MeterRegistry registry) {
    this.dashboardTimer = registry.timer("pathfinder.dashboardTimer");
    this.projectStartCounter = registry.counter("pathfinder.projectStartCounter");
    this.subscribePageHitCounter = registry.counter("pathfinder.subscribePageHitCounter");
    this.unSubscribePageHitCounter = registry.counter("pathfinder.unSubscribePageHitCounter");
    this.subscribePagePostCounter = registry.counter("pathfinder.subscribePagePostCounter");
    this.unsubscribePagePostCounter = registry.counter("pathfinder.unsubscribePagePostCounter");
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

  public Counter getSubscribePagePostCounter() {
    return subscribePagePostCounter;
  }

  public Counter getUnsubscribePagePostCounter() {
    return unsubscribePagePostCounter;
  }
}
