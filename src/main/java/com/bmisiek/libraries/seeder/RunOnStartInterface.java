package com.bmisiek.libraries.seeder;

public interface RunOnStartInterface {
    void run() throws Exception;
    default int getPriority() {
        return 0;
    }
}
