package com.smartinventory.api.controller;

import com.smartinventory.api.dto.HealthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final String serviceName;
    private final String serviceVersion;

    public HealthController(
            @Value("${smartinventory.api.service-name}") String serviceName,
            @Value("${smartinventory.api.version}") String serviceVersion) {
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
    }

    @GetMapping
    public HealthResponse getHealth() {
        return new HealthResponse("UP", serviceName, serviceVersion);
    }
}
