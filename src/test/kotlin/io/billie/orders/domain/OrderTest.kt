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
}