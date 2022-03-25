package uk.co.ogauthority.pathfinder.analytics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@EnableConfigurationProperties(value = {
    AnalyticsProperties.class,
    AnalyticsConfig.class
})
@Import(AnalyticsConfiguration.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableAnalyticsConfiguration { }