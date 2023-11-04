package io.billie.orders.domain

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class Order(
        val id: UUID,
        val version: Long,
        val createdDatetime: LocalDateTime,
        val orderStatus: OrderStatus,
        val orderAmount: BigDecimal,
        val shippedAmount: BigDecimal,
        val merchantId: UUID
) {

    enum class OrderStatus { CREATED, SHIPPED_PARTIALLY, SHIPPED, PAID, UNKNOWN }

    companion object {
        fun initiate(orderAmount: BigDecimal, merchantId: UUID): Order {

            if (orderAmount <= BigDecimal.ZERO) throw OrderValidationException("Order amount must be greater than zero")

            return Order(
                    id = UUID.randomUUID(),
                    version = 1L,
                    createdDatetime = LocalDateTime.now(ZoneOffset.UTC),
                    orderStatus = OrderStatus.CREATED,
                    orderAmount = orderAmount,
                    shippedAmount = BigDecimal.ZERO,
                    merchantId = merchantId)
        }
    }

    fun notifyShipment(shippedAmount: BigDecimal) {

    }
}

inline fun <reified T : Enum<T>> enumByNameIgnoreCase(input: String?, default: T): T {
    return enumValues<T>().firstOrNull { it.name.equals(input, true) } ?: default
}