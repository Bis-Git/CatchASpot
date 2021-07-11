package hr.algebra.catchaspotapp.model

import java.io.Serializable

data class ParkingSpot(
    var _id: Long?,
    val address: String,
    val price: Double,
    val latCoordinate: Double,
    val longCoordinate: Double,
    @Transient val isUserRegistered: Boolean
) : Serializable
