package com.example.aigenerate.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class ChatCompletionResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        @JsonProperty("message")
        private Message message;

        @Data
        public static class Message {
            private String content;
        }
    }
}