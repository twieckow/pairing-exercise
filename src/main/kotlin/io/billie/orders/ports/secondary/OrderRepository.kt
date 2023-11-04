package io.billie.orders.ports.secondary

import io.billie.orders.domain.Order
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface OrderRepository {

    @Transactional(readOnly = true)
    fun find(id: UUID): Optional<Order>

    @Transactional
    fun save(order: Order): Order
}
