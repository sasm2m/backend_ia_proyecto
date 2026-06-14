package com.smartinventory.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartinventory.api.dto.request.PurchaseOrderCancelDTO;
import com.smartinventory.api.dto.request.PurchaseOrderRequestDTO;
import com.smartinventory.api.dto.response.PurchaseOrderResponseDTO;
import com.smartinventory.api.enums.OrderStatus;
import com.smartinventory.api.exception.BusinessException;
import com.smartinventory.api.service.PurchaseOrderService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PurchaseOrderController.class)
@TestPropertySource(properties = {
        "smartinventory.features.purchase-orders.enabled=true",
        "server.servlet.context-path=/api"
})
@Import(PurchaseOrderControllerTest.LocalConfig.class)
class PurchaseOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PurchaseOrderService purchaseOrderService;

    @Test
    void create_withValidData_shouldReturn201() throws Exception {
        PurchaseOrderResponseDTO response = PurchaseOrderResponseDTO.builder()
                .id(1L)
                .productId(10L)
                .productName("Keyboard")
                .supplierId(20L)
                .supplierName("Fast Supplier")
                .quantity(5)
                .status(OrderStatus.DRAFT)
                .automatic(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(purchaseOrderService.create(any(PurchaseOrderRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/purchase-orders")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 10,
                                  "supplierId": 20,
                                  "quantity": 5
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    @Disabled("Authentication is out of scope per constitution; 401 would require adding security")
    void create_withoutAuth_shouldReturn401() {
    }

    @Test
    void cancel_withInvalidTransition_shouldReturn400() throws Exception {
        when(purchaseOrderService.cancel(eq(99L), any(PurchaseOrderCancelDTO.class)))
                .thenThrow(new BusinessException("Invalid purchase order transition from RECEIVED to CANCELLED"));

        mockMvc.perform(patch("/purchase-orders/99/cancel")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "reason": "Invalid transition"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid purchase order transition from RECEIVED to CANCELLED"));
    }

    @TestConfiguration
    static class LocalConfig {

        @Bean
        ObjectMapper objectMapper() {
            return Jackson2ObjectMapperBuilder.json().build();
        }
    }
}
