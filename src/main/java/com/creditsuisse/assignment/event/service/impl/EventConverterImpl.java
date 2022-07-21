package com.creditsuisse.assignment.event.service.impl;

import com.creditsuisse.assignment.event.model.Event;
import com.creditsuisse.assignment.event.service.EventConverter;
import com.creditsuisse.assignment.log.model.Log;
import com.creditsuisse.assignment.event.service.EventService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Service
public class EventConverterImpl implements EventConverter {
    private ConcurrentHashMap<String, Log> logsMap;

    @Autowired
    private EventService eventService;

    public EventConverterImpl() {
        this.logsMap = new ConcurrentHashMap<>();
    }

    public EventConverterImpl(ConcurrentHashMap<String, Log> logsMap, EventService eventService) {
        this.logsMap = logsMap;
        this.eventService = eventService;
    }

    @Override
    public Event convert(Log logRecord) {
        Log processedLog = logsMap.putIfAbsent(logRecord.getId(), logRecord);
        if (Objects.isNull(processedLog) || processedLog.equals(logRecord))
            return null;
        Event event = convertToEvent(logRecord, processedLog);
        eventService.addEvent(event);
        return event;
    }

    private Event convertToEvent(Log logRecord, Log processedLog) {
        long duration = Math.abs(logRecord.getTimestamp() - processedLog.getTimestamp());
        Event event = new Event(logRecord, duration);
        log.info("Logs {} and {} are transformed to {}", logRecord, processedLog, event);
        return event;
    }
}
