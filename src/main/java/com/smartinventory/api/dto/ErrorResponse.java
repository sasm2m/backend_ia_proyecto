package com.smartinventory.api.dto;

import java.time.Instant;
import java.util.Map;

public class ErrorResponse {

    private Instant timestamp;
    private String path;
    private String message;
    private Map<String, String> errors;

    public ErrorResponse() {
    }

    public ErrorResponse(Instant timestamp, String path, String message, Map<String, String> errors) {
        this.timestamp = timestamp;
        this.path = path;
        this.message = message;
        this.errors = errors;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
