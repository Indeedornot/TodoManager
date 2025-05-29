package com.bmisiek.libraries.datetime;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Date;

@Service
public class DateTimeProvider implements IDateTimeProvider {
    private final Clock clock;
    public DateTimeProvider(Clock clock) {
        this.clock = clock;

    }
    public Date now() {
        return Date.from(clock.instant());
    }
}
