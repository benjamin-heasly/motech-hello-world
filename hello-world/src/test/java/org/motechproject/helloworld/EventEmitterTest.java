package org.motechproject.helloworld;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.helloworld.domain.EventEmitter;

public class EventEmitterTest {

	@Mock
	private EventRelay eventRelay;

	private EventEmitter eventEmitter;

	@Before
	public void setUp() {
		initMocks(this);
		this.eventEmitter = new EventEmitter(eventRelay);
	}

	@Test
	public void shouldNotBeNullEmitter() {
		assertNotNull(eventEmitter);
	}

	@Test
	public void shouldEmitEvent() {
		ArgumentCaptor<MotechEvent> motechEventCaptor = ArgumentCaptor
				.forClass(MotechEvent.class);
		eventEmitter.emitEvent();
		verify(eventRelay).sendEventMessage(motechEventCaptor.capture());
		MotechEvent motechEvent = motechEventCaptor.getValue();
		assertEquals(motechEvent.getSubject(), eventEmitter.getSubject());
	}
}
