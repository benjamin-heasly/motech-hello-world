package org.motechproject.helloworld.domain;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

import org.motechproject.helloworld.EventKeys;

@Component
public class EventEmitter {

    private static final String SUBJECT = EventKeys.HELLO_SUBJECT;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventRelay eventRelay;

    public EventEmitter() {
    }

    public EventEmitter(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    public void emitEvent() {
        Map<String, Object> helloEventParams = new HashMap<>();
        helloEventParams.put("hello", "world");
        MotechEvent motechEvent = new MotechEvent(SUBJECT, helloEventParams);
        eventRelay.sendEventMessage(motechEvent);

        String message = "Emitted an event with subject " + SUBJECT;
        logger.info(message);
    }

    public String getSubject() {
        return SUBJECT;
    }
}
