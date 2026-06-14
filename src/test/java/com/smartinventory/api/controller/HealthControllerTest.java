package com.smartinventory.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.smartinventory.api.dto.HealthResponse;
import org.junit.jupiter.api.Test;

class HealthControllerTest {

    @Test
    void shouldReturnHealthInformation() {
        HealthController controller = new HealthController("SmartInventory API", "1.0.0");

        HealthResponse response = controller.getHealth();

        assertEquals("UP", response.getStatus());
        assertEquals("SmartInventory API", response.getService());
        assertEquals("1.0.0", response.getVersion());
    }
}
