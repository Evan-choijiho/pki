package com.peloton.boilerplate.log;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityLog {
    protected String table;

    public ActivityLog(String table) {
        this.table = table;
    }
}
