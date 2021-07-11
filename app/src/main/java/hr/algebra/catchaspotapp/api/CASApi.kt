package hr.algebra.catchaspotapp.api

import hr.algebra.catchaspotapp.model.ParkingSpot
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

const val  API_URL = "http://192.168.43.85:3000/"

interface CASApi{
    @GET("parking_spots")
    fun fetchParkingSpots(): Call<List<CASParkingSpot>>

    @POST("parking_spots")
    fun createParkingSpot(@Body parkingSpot: ParkingSpot) : Call<ParkingSpot>
}