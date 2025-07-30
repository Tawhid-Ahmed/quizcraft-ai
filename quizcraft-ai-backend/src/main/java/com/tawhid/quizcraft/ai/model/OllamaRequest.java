package com.tawhid.quizcraft.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OllamaRequest {
    private String model;
    private String prompt;
    private boolean stream = false;
    
    public OllamaRequest(String model, String prompt) {
        this.model = model;
        this.prompt = prompt;
    }
}
