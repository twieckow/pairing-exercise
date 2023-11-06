package io.billie.common.infrastructure

import io.billie.common.domain.DomainEvent

import org.springframework.context.ApplicationEventPublisher

class SpringDomainEventPublisher(private val springEventPublisher: ApplicationEventPublisher) : DomainEventPublisher {
    override fun publish(event: DomainEvent) {
        springEventPublisher.publishEvent(event)
    }
}
