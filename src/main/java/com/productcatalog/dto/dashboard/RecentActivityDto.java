package com.productcatalog.dto.dashboard;

import java.time.LocalDateTime;

public class RecentActivityDto {

    private String type;
    private String title;
    private String description;
    private String user;
    private LocalDateTime timestamp;

    public RecentActivityDto() {
    }

    public RecentActivityDto(String type, String title, String description, String user, LocalDateTime timestamp) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.user = user;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
