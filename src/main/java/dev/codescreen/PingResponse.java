package dev.codescreen;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PingResponse {
    private final String serverTime;

    /**
     * Inner class to store the ping response, according to the prescribed schema from service.yml
     */
    public PingResponse() {
        this.serverTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    //getters
    public String getServerTime() {
        return serverTime;
    }
}
