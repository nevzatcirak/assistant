package com.nevzatcirak.assistant.api.port;
import java.util.List;
public interface DocumentReaderPort {
    List<String> read(String sourcePath);
}
