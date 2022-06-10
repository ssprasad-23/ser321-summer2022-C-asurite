package Assigment3.session;

import java.time.LocalDateTime;

public class SessionInfo {
    private LocalDateTime expireDate;
    private String name;

    public SessionInfo(LocalDateTime expireDate, String username) {
        this.expireDate = expireDate;
        this.name = username;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public String getName() {
        return name;
    }
}
