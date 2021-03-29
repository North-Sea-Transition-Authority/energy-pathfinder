package uk.co.ogauthority.pathfinder.testutil;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.noop.NoopCounter;
import io.micrometer.core.instrument.noop.NoopTimer;

public class MetricsProviderTestUtil {

  public static Timer getNoOpTimer(){
    return new NoopTimer(new Meter.Id("foo", Tags.empty(), null, null, Meter.Type.TIMER));
  }

  public static Counter getNoOpCounter(){
    return new NoopCounter(new Meter.Id("foo", Tags.empty(), null, null, Meter.Type.COUNTER));
  }
}
