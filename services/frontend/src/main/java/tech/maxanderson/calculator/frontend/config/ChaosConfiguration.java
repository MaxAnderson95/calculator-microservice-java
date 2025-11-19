package tech.maxanderson.calculator.frontend.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Slf4j
public class ChaosConfiguration {

    @Value("${chaos.enabled:false}")
    private boolean chaosEnabled;

    @Value("${chaos.percent:#{null}}")
    private Integer chaosPercent;

    private static final int DEFAULT_CHAOS_PERCENT = 5;

    @PostConstruct
    public void init() {
        if (chaosEnabled) {
            if (chaosPercent == null) {
                chaosPercent = DEFAULT_CHAOS_PERCENT;
                log.warn("Chaos engineering is ENABLED but CHAOS_PERCENT is not set. Using default value of {}%", DEFAULT_CHAOS_PERCENT);
            } else if (chaosPercent < 0 || chaosPercent > 100) {
                log.error("Invalid CHAOS_PERCENT value: {}. Must be between 0 and 100. Disabling chaos engineering.", chaosPercent);
                chaosEnabled = false;
            } else {
                log.info("Chaos engineering is ENABLED with failure rate of {}%", chaosPercent);
            }
        } else {
            log.info("Chaos engineering is DISABLED");
        }
    }

    public double getFailureRate() {
        return chaosPercent / 100.0;
    }
}
