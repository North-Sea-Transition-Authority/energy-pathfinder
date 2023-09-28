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
  private final Counter manageSubscriptionCounter;
  private final Counter updateSubscriptionHitCounter;
  private final Counter updateSubscriptionPostCounter;

  public MetricsProvider(MeterRegistry registry) {
    this.dashboardTimer = registry.timer("pathfinder.dashboardTimer");
    this.projectStartCounter = registry.counter("pathfinder.projectStartCounter");
    this.subscribePageHitCounter = registry.counter("pathfinder.subscribePageHitCounter");
    this.unSubscribePageHitCounter = registry.counter("pathfinder.unSubscribePageHitCounter");
    this.subscribePagePostCounter = registry.counter("pathfinder.subscribePagePostCounter");
    this.unsubscribePagePostCounter = registry.counter("pathfinder.unsubscribePagePostCounter");
    this.manageSubscriptionCounter = registry.counter("pathfinder.manageSubscriptionCounter");
    this.updateSubscriptionHitCounter = registry.counter("pathfinder.updateSubscriptionHitCounter");
    this.updateSubscriptionPostCounter = registry.counter("pathfinder.updateSubscriptionPostCounter");
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

  public Counter getManageSubscriptionCounter() {
    return manageSubscriptionCounter;
  }

  public Counter getUpdateSubscriptionHitCounter() {
    return updateSubscriptionHitCounter;
  }

  public Counter getUpdateSubscriptionPostCounter() {
    return updateSubscriptionPostCounter;
  }
}
