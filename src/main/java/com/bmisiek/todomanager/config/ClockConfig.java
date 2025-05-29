package com.bmisiek.todomanager.config;

import jakarta.validation.ClockProvider;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@Configuration
public class ClockConfig implements ClockProvider {
    @Bean
    public Clock getClock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public ZoneOffset zone() {
        return ZoneOffset.systemDefault().getRules().getOffset(Instant.now(getClock()));
    }

    @Bean
    static LocalValidatorFactoryBean defaultValidator(Clock clock) {
        return new LocalValidatorFactoryBean() {
            @Override
            public void postProcessConfiguration(
                    @NotNull jakarta.validation.Configuration<?> configuration) {
                configuration.clockProvider(() -> clock);
            }
        };
    }
}
