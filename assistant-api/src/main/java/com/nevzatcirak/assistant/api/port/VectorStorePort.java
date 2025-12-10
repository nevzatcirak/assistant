package com.nevzatcirak.assistant.api.port;
import java.util.List;
public interface VectorStorePort {
    void save(List<String> contents);
    List<String> findSimilar(String query, int limit);
}
