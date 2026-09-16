package com.ntb.bookstore.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ChatBoxIntegrationTest extends BaseIntegrationTest {

    @Test
    void hoiChatbotThanhCong() throws Exception {
        when(chatBoxService.hoi(eq("Có sách Clean Code không?"), eq(false)))
                .thenReturn("Có sách Clean Code trong hệ thống.");

        mockMvc.perform(post("/api/ai/hoi")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("cauHoi", "Có sách Clean Code không?", "isSearchWeb", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu").value("Có sách Clean Code trong hệ thống."));
    }

    @Test
    void cauHoiRongKhongLamCrashController() throws Exception {
        when(chatBoxService.hoi(eq(""), eq(false))).thenReturn("Hiện hệ thống chưa có thông tin này.");

        mockMvc.perform(post("/api/ai/hoi")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("cauHoi", "", "isSearchWeb", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true));
    }

    @Test
    void loiChatbotDuocTraVeDangApiResponse() throws Exception {
        when(chatBoxService.hoi(any(), any())).thenThrow(new IllegalStateException("AI unavailable"));

        mockMvc.perform(post("/api/ai/hoi")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("cauHoi", "test", "isSearchWeb", true))))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.thanhCong").value(false));
    }
}
