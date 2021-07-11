package hr.algebra.catchaspotapp.api

import com.google.gson.annotations.SerializedName

data class CASParkingSpot(
        @SerializedName("id") val id : Long,
        @SerializedName("address") val address : String,
        @SerializedName("price") val price : Double,
        @SerializedName("latCoordinate") val latCoordinate : Double,
        @SerializedName("longCoordinate") val longCoordinate : Double
)


