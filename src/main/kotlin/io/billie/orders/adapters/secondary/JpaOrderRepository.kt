package io.billie.orders.adapters.secondary

import io.billie.orders.domain.Order
import io.billie.orders.domain.enumByNameIgnoreCase
import io.billie.orders.ports.secondary.OrderRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.Version

@Repository
interface JpaOrderRepository : JpaRepository<OrderDbModel, UUID>, OrderRepository {

    @Transactional(readOnly = true)
    override fun find(id: UUID): Optional<Order> = findById(id).map(OrderDbModel::toDomainModel)

    @Transactional
    override fun save(order: Order): Order = save(OrderDbModel.fromDomainModel(order)).toDomainModel()
}

@Entity
@Table(name = "orders", schema = "organisations_schema")
data class OrderDbModel(
        @Id val id: UUID? = null,
        @Version val ver: Long? = null,
        val createdDatetime: LocalDateTime? = null,
        val orderStatus: String? = null,
        val orderAmount: BigDecimal? = null,
        val shippedAmount: BigDecimal? = null,
        val merchantId: UUID? = null,
) {
    fun toDomainModel(): Order = Order(
            id = id!!,
            version = ver!!,
            createdDatetime = createdDatetime!!,
            orderStatus = enumByNameIgnoreCase(orderStatus, Order.OrderStatus.UNKNOWN),
            orderAmount = orderAmount!!,
            shippedAmount = shippedAmount!!,
            merchantId = merchantId!!
    )

    companion object {
        fun fromDomainModel(order: Order): OrderDbModel {
            return OrderDbModel(
                    id = order.id,
                    ver = order.version,
                    createdDatetime = order.createdDatetime,
                    orderStatus = order.orderStatus.name,
                    orderAmount = order.orderAmount,
                    shippedAmount = order.shippedAmount,
                    merchantId = order.merchantId
            )
        }
    }
}