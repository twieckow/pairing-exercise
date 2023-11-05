package io.billie.functional

import com.fasterxml.jackson.databind.ObjectMapper
import io.billie.orders.adapters.primary.OrdersRestApi
import io.billie.orders.adapters.secondary.JpaOrderRepository
import io.billie.orders.domain.Order
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.util.*

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = DEFINED_PORT)
class ShipmentNotificationUseCaseIT {

    @LocalServerPort
    private val port = 8080

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    val merchantId = UUID.randomUUID()
    val orderTotalAmount = BigDecimal.valueOf(123)


    @Test
    fun shouldCreateOrderAndNotifyAboutShipment() {
        //given
        val orderCreationRequestJson = //language=JSON
                """{
                      "order_amount": ${orderTotalAmount},
                      "merchant_id": "${merchantId}"
                    }"""
        val firstShipmentNotificationJson = //language=JSON
                """{
                      "shipment_amount_notification": 100.1
                    }"""

        //when
        val creationResult = mockMvc.perform(post("/orders").contentType(APPLICATION_JSON).content(orderCreationRequestJson))
                //then
                .andExpect(status().isOk).andReturn()

        val response = mapper.readValue(creationResult.response.contentAsString, OrdersRestApi.OrderResponse::class.java)

        assertThat(response.orderAmount).isEqualTo(orderTotalAmount)
        assertThat(response.shippedAmount).isEqualTo(BigDecimal.ZERO)
        assertThat(response.orderStatus).isEqualTo(Order.OrderStatus.CREATED.name)
        assertThat(response.version).isEqualTo(1)


        //when
        val firstShipmentNotificationResult = mockMvc.perform(patch("/orders/${response.id}").contentType(APPLICATION_JSON).content(firstShipmentNotificationJson))
                //then
                .andExpect(status().isOk).andReturn()

        val firstShipmentNotificationResponse = mapper.readValue(firstShipmentNotificationResult.response.contentAsString, OrdersRestApi.OrderResponse::class.java)

        assertThat(firstShipmentNotificationResponse.orderAmount).isEqualTo(orderTotalAmount)
        assertThat(firstShipmentNotificationResponse.shippedAmount).isEqualTo(BigDecimal.valueOf(100.1))
        assertThat(firstShipmentNotificationResponse.orderStatus).isEqualTo(Order.OrderStatus.SHIPPED_PARTIALLY.name)
        assertThat(firstShipmentNotificationResponse.version).isEqualTo(2)

    }

}
