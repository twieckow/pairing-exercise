package io.billie.orders.ports.primary

import io.billie.orders.ports.secondary.OrderRepository
import io.billie.orders.domain.Order
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class CreateOrderUseCase(val orderRepository: OrderRepository) {

    data class Input(val orderAmount: BigDecimal, val merchantId: UUID)

    fun createNewOrder(input: Input): Order {
        val order = Order.initiate(input.orderAmount, input.merchantId)
        return orderRepository.save(order)
    }
}