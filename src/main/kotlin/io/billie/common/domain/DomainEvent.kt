package io.billie.common.domain

import java.time.LocalDateTime

interface DomainEvent {
    fun eventDateTime(): LocalDateTime
}