package com.ntb.bookstore.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ntb.bookstore.service.AI_tool.ToolSearchWeb;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class ToolSearchWebController {
    private final ToolSearchWeb toolSearchWeb;

    @GetMapping("/search")
    public String search(@RequestParam String q) {
        return toolSearchWeb.timKiemtrenWeb(q);
    }
}