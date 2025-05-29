package com.bmisiek.libraries.datetime;

import org.springframework.stereotype.Service;

import java.util.Date;

public interface IDateTimeProvider {
    Date now();
}
