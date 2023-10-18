package com.db.logger;

import java.sql.Timestamp;

public class Log {
    private Integer id;
    private String action;
    private Timestamp date;
    private String description;

    public Log(Integer id, String action, Timestamp date, String description) {
        this.id = id;
        this.action = action;
        this.date = date;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}

