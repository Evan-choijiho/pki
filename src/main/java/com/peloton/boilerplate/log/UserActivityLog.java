package com.peloton.boilerplate.log;

import com.peloton.boilerplate.util.WebLogUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserActivityLog extends ActivityLog {
    protected Long userSid = WebLogUtils.getAuthUserSid();
    protected String userIp = WebLogUtils.getRequestIp();

    public UserActivityLog(String table) {
        super(table);
    }
}
