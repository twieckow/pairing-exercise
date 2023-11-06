package io.billie.common.infrastructure

import io.billie.common.domain.DomainEvent

interface DomainEventPublisher {
    fun publish(event: DomainEvent)
}