package io.billie.orders.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.*

class OrderTest {

    @Test
    fun shouldInitiateOrder() {
        //given
        val orderAmount = BigDecimal.TEN
        val merchantId = UUID.randomUUID()

        //when
        val result = Order.initiate(orderAmount, merchantId)

        //then
        assertThat(result).isNotNull
    }

    @Test
    fun shouldInitiateOrderWithProperValues() {
        //given
        val orderAmount = BigDecimal.TEN
        val merchantId = UUID.randomUUID()

        //when
        val result = Order.initiate(orderAmount, merchantId)

        //then
        assertThat(result.id).isNotNull()
        assertThat(result.orderAmount).isEqualTo(orderAmount)
        assertThat(result.shippedAmount).isEqualTo(BigDecimal.ZERO)
        assertThat(result.orderStatus).isEqualTo(Order.OrderStatus.CREATED)
        assertThat(result.merchantId).isEqualTo(merchantId)
    }

    @Test
    fun shouldFailWhileInitiatingOrderWhenOrderAmountIsZero() {
        //given
        val orderAmount = BigDecimal.ZERO
        val merchantId = UUID.randomUUID()

        //when & then
        assertThrows<OrderValidationException> { Order.initiate(orderAmount, merchantId) }
    }

    @Test
    fun shouldFailWhileInitiatingOrderWhenOrderAmountIsNegative() {
        //given
        val orderAmount = BigDecimal.valueOf(-2.2)
        val merchantId = UUID.randomUUID()

        //when & then
        assertThrows<OrderValidationException> { Order.initiate(orderAmount, merchantId) }
    }


    @Test
    fun shouldFailShipmentNotificationWhenNegativeShippedAmount() {
        //given
        val order = Order.initiate(BigDecimal.TEN, UUID.randomUUID())
        val shipped = BigDecimal.valueOf(-3.2)

        //when & then
        assertThrows<OrderShipmentException> { order.notifyShipment(shipped) }
    }

    @Test
    fun shouldFailShipmentNotificationWhenZeroShippedAmount() {
        //given
        val order = Order.initiate(BigDecimal.TEN, UUID.randomUUID())
        val shipped = BigDecimal.ZERO

        //when & then
        assertThrows<OrderShipmentException> { order.notifyShipment(shipped) }
    }

    @Test
    fun shouldChangeStatusWhenWhenPartialShipmentNotification(){
        //given
        val order = Order.initiate(BigDecimal.TEN, UUID.randomUUID())
        val shipped = BigDecimal.valueOf(2.2)

        //when
        order.notifyShipment(shipped)

        //then
        assertThat(order).isNotNull()
        assertThat(order.orderAmount).isEqualTo(BigDecimal.TEN)
        assertThat(order.shippedAmount).isEqualTo(shipped)
        assertThat(order.orderStatus).isEqualTo(Order.OrderStatus.SHIPPED_PARTIALLY)
    }

    @Test
    fun shouldChangeStatusToShippedWhenWhenTwoPartialShipmentNotificationsMatchTotalAmount(){
        //given
        val order = Order.initiate(BigDecimal.TEN, UUID.randomUUID())
        val shipped1 = BigDecimal.valueOf(2.2)
        val shipped2 = BigDecimal.valueOf(7.8)

        //when
        order.notifyShipment(shipped1)
        order.notifyShipment(shipped2)

        //then
        assertThat(order.orderAmount).isEqualTo(BigDecimal.TEN)
        assertThat(order.shippedAmount).isEqualTo(BigDecimal.TEN)
        assertThat(order.orderStatus).isEqualTo(Order.OrderStatus.SHIPPED)
    }

    @Test
    fun shouldFailShipmentNotificationWhenShippedAmountExceedsOrderAmount() {
        //given
        val order = Order.initiate(BigDecimal.TEN, UUID.randomUUID())
        val shipped = BigDecimal.valueOf(10.1)

        //when & then
        assertThrows<OrderShipmentException> { order.notifyShipment(shipped) }
    }

}