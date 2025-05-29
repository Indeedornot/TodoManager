package com.bmisiek.todomanager.areas.data.dto.info;

import com.fasterxml.jackson.annotation.JsonInclude;

public class BugInfo extends TaskInfo {
    public BugInfo() {
        this.type = "Bug";
    }
}
