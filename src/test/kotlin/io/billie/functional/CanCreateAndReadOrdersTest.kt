package io.billie.functional

import com.fasterxml.jackson.databind.ObjectMapper
import io.billie.functional.data.Fixtures.orderCreationRequestJson
import io.billie.functional.data.Fixtures.orderCreationRequestJsonOrderAmountMissing
import io.billie.functional.data.Fixtures.orderFixture
import io.billie.orders.adapters.primary.OrdersRestApi
import io.billie.orders.adapters.secondary.JpaOrderRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = DEFINED_PORT)
class CanCreateAndReadOrdersTest {

    @LocalServerPort
    private val port = 8080

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var orderRepository: JpaOrderRepository


    @Test
    fun shouldFailCreateOrderWhenOrderAmountIsMissing() {
        mockMvc.perform(
            post("/orders").contentType(APPLICATION_JSON).content(orderCreationRequestJsonOrderAmountMissing())
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun shouldCreateOrder() {
        //given
        val orderCreationRequestJson = orderCreationRequestJson()

        //when
        val result = mockMvc.perform(
            post("/orders").contentType(APPLICATION_JSON).content(orderCreationRequestJson)
        )
        //then
        .andExpect(status().isOk)
        .andReturn()

        val response = mapper.readValue(result.response.contentAsString, OrdersRestApi.OrderResponse::class.java)

        val orderFromDb = orderRepository.find(response.id)
        assertThat(orderFromDb).isPresent
        assertThat(response).isEqualTo(OrdersRestApi.OrderResponse.fromDomainModel(orderFromDb.get()))
    }

}
