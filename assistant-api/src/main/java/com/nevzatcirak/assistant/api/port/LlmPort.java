package com.nevzatcirak.assistant.api.port;
import com.nevzatcirak.assistant.api.model.AssistantResponse;
import com.nevzatcirak.assistant.api.model.UserQuery;

public interface LlmPort {
    AssistantResponse generate(String systemPrompt, UserQuery query);
}
