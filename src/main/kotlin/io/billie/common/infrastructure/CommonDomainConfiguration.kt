package io.billie.common.infrastructure

import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class CommonDomainConfiguration {
    @Bean
    fun domainEventPublisher(springEventPublisher: ApplicationEventPublisher): DomainEventPublisher {
        return SpringDomainEventPublisher(springEventPublisher)
    }
}