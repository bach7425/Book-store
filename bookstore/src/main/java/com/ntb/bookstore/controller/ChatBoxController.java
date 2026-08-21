package com.ntb.bookstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.ChatBox.ChatRequest;
import com.ntb.bookstore.service.AI_tool.ChatBoxService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class ChatBoxController {
    private final ChatBoxService chatBoxService;

    @PostMapping("/hoi")
    public ResponseEntity<ApiResponse<String>> hoi(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(ApiResponse.of(true,
                "Thành công", LocalDateTime.now(),
                chatBoxService.hoi(request.cauHoi(), request.isSearchWeb())));
    }

}
