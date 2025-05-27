package com.bmisiek.todomanager.integration.config;

import org.testcontainers.containers.PostgreSQLContainer;

public class CustomPostgreSQLContainer extends PostgreSQLContainer<CustomPostgreSQLContainer> {
    private static final String IMAGE_VERSION = "postgres:15";
    private static CustomPostgreSQLContainer container;

    private CustomPostgreSQLContainer() {
        super(IMAGE_VERSION);
    }

    public static CustomPostgreSQLContainer getInstance() {
        if (container == null) {
            container = new CustomPostgreSQLContainer();
        }
        return container;
    }

    @Override
    public void stop() {
        // Do nothing, JVM handles shut down
    }
}