package hr.algebra.catchaspotapp.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.algebra.catchaspotapp.USER_ID
import hr.algebra.catchaspotapp.framework.getCurrentUserIDProperty
import hr.algebra.catchaspotapp.model.ParkingSpot
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CASSender(private val context: Context) {
    private val db = Firebase.firestore
    private var casApi : CASApi
    init{
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        casApi = retrofit.create(CASApi::class.java)
    }

    fun sendNetworkRequest(parkingSpot: ParkingSpot){
        val call = casApi.createParkingSpot(parkingSpot)

        call.enqueue(object: Callback<ParkingSpot>{
            override fun onResponse(call: Call<ParkingSpot>, response: Response<ParkingSpot>) {
                Toast.makeText(context, "Register successful", Toast.LENGTH_LONG).show()

                casApi.fetchParkingSpots().enqueue(object: Callback<List<CASParkingSpot>>{
                    override fun onResponse(
                        call: Call<List<CASParkingSpot>>,
                        response: Response<List<CASParkingSpot>>
                    ) {
                        if (response.body() != null){
                            var idArray = arrayListOf<Long>()
                            response.body()!!.forEach {
                                idArray.add(it.id)
                            }
                            db.collection("users").document(context.getCurrentUserIDProperty(USER_ID)!!)
                                    .update("registered_parking_spots", FieldValue.arrayUnion(idArray.last()))
                        }
                    }

                    override fun onFailure(call: Call<List<CASParkingSpot>>, t: Throwable) {
                        Log.d(javaClass.name, t.message, t)
                    }
                })
            }

            override fun onFailure(call: Call<ParkingSpot>, t: Throwable) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
            }

        })
    }
}