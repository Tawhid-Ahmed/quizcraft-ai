package com.tawhid.quizcraft.ai.model;

import lombok.Data;

@Data
public class OllamaResponse {
    private String model;
    private String response;
    private long createdAt;
    private boolean done;
}
