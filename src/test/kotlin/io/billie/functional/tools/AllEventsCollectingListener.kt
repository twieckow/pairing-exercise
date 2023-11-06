package io.billie.functional.tools

import io.billie.common.domain.DomainEvent
import org.springframework.context.event.EventListener


class AllEventsCollectingListener {
    private val allEvents: MutableSet<DomainEvent> = HashSet()
    @EventListener
    fun handleEvent(domainEvent: DomainEvent) {
        allEvents.add(domainEvent)
    }

    fun allEvents(): Set<DomainEvent> {
        return allEvents
    }

    fun clearEvents() {
        allEvents.clear()
    }
}