package com.ntb.bookstore.dto.ChatBox;

public record ChatRequest(String cauHoi, Boolean isSearchWeb) {
    public ChatRequest {
        if (isSearchWeb == null) {
            isSearchWeb = false;
        }
    }
}
