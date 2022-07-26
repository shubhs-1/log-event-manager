package com.creditsuisse.assignment.config;

import com.creditsuisse.assignment.log.model.ApplicationLog;
import com.creditsuisse.assignment.log.model.Log;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * Class to deserialize JSON string into DTO object
 * @author Shubham K
 */
public class LogDeserializer extends StdDeserializer<Log> {
    protected LogDeserializer(Class<?> vc) {
        super(vc);
    }

    /**
     * Parse the JSON string from logfile and deserialize it to ApplicationLog type object
     * @param jsonParser
     * @param deserializationContext
     * @return log entry
     * @throws IOException
     */
    @Override
    public Log deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String id = node.get("id").asText();
        String state = node.get("state").asText();
        long timestamp = node.get("timestamp").asLong();

        boolean applicationLogEntry = node.has("type");
        if (!applicationLogEntry) {
            return new Log(id, state, timestamp);
        }

        String type = node.get("type").asText();
        String host = node.get("host").asText();
        return new ApplicationLog(id, state, timestamp, type, host);
    }
}
