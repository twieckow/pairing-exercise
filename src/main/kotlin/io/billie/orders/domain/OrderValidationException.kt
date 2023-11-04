package io.billie.orders.domain

class OrderValidationException(override val message: String) : RuntimeException()
