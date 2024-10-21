package com.example.threadproject.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ErrorWriter<T> {

    private static final String ERROR_LOG_FILE = "error-log.json";
    private final List<ErrorLog> errorLogs = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Method to add an error log record
    public void writeErrorLog(ErrorLog errorLog) {
        log.debug("Adding error log: {}", errorLog);
        errorLogs.add(errorLog);
    }
    // Method to save all errors to the JSON file after batch processing is complete
    public void saveErrors() throws IOException {
        if (!errorLogs.isEmpty()) {
            log.debug("Writing error logs: {}", errorLogs);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Enabling pretty printing
            objectMapper.writeValue(new File(ERROR_LOG_FILE), errorLogs);
        }
    }

    // Clears all accumulated errors after writing to the file, if needed
    public void clearErrors() {
        errorLogs.clear();
    }
}
