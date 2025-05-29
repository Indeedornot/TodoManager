package com.bmisiek.todomanager.areas.data.dto.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;

@Data
public class TaskInfo {
    @Getter
    protected String type;
}
