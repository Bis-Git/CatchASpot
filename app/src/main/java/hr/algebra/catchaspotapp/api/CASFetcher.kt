package hr.algebra.catchaspotapp.api

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.algebra.catchaspotapp.CASReceiver
import hr.algebra.catchaspotapp.CAS_PROVIDER_CONTENT_URI
import hr.algebra.catchaspotapp.USER_ID
import hr.algebra.catchaspotapp.framework.fetchParkingSpots
import hr.algebra.catchaspotapp.framework.getCurrentUserIDProperty
import hr.algebra.catchaspotapp.framework.prepareMapAndSetCurrentLocation
import hr.algebra.catchaspotapp.framework.sendBroadcast
import hr.algebra.catchaspotapp.model.ParkingSpot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CASFetcher(private val context: Context) {
    private val db = Firebase.firestore
    var casApi : CASApi
    init{

        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        casApi = retrofit.create(CASApi::class.java)
    }

    fun populateSpotsOnRefresh(casParkingSpot: List<CASParkingSpot>, documentSnapshot: DocumentSnapshot) {
        val work = GlobalScope.launch {
            var registeredSpots: ArrayList<Long>? = null
            documentSnapshot.data?.values?.forEach {
                if (it is ArrayList<*>) {
                    registeredSpots = it as ArrayList<Long>
                }
            }

            casParkingSpot.forEach {

                val values = ContentValues().apply {
                    put(ParkingSpot::address.name, it.address)
                    put(ParkingSpot::price.name, it.price)
                    put(ParkingSpot::latCoordinate.name, it.latCoordinate)
                    put(ParkingSpot::longCoordinate.name, it.longCoordinate)

                    Log.d("Tag", it.toString())


                    if (registeredSpots != null) {
                        if (registeredSpots!!.contains(it.id)) {
                            put(ParkingSpot::isUserRegistered.name, true)
                        } else {
                            put(ParkingSpot::isUserRegistered.name, false)
                        }
                    } else {
                        put(ParkingSpot::isUserRegistered.name, false)
                    }

                }
                context.contentResolver.insert(CAS_PROVIDER_CONTENT_URI, values)
            }
        }
        runBlocking {
            work.join()
        }
    }

    fun fetchParkingSpotsOnStart(){
        val request = casApi.fetchParkingSpots()

        request.enqueue(object: Callback<List<CASParkingSpot>>{
            override fun onResponse(
                    call: Call<List<CASParkingSpot>>,
                    response: Response<List<CASParkingSpot>>
            ) {
                if (response.body() != null){

                    context.getCurrentUserIDProperty(USER_ID)?.let { id ->
                        db.collection("users")
                                .document(id)
                                .get()
                                .addOnSuccessListener {
                                    populateSpotsOnStart(response.body()!!, it)
                                }
                    }
                }
            }

            override fun onFailure(call: Call<List<CASParkingSpot>>, t: Throwable) {
                Log.d(javaClass.name, t.message, t)
            }
        })
    }

    private fun populateSpotsOnStart(casParkingSpot: List<CASParkingSpot>, documentSnapshot: DocumentSnapshot) {
        GlobalScope.launch {
            var registeredSpots: ArrayList<Long>? = null
            documentSnapshot.data?.values?.forEach{
                if (it is ArrayList<*>){
                    registeredSpots = it as ArrayList<Long>
                }
            }

            casParkingSpot.forEach{

                val values = ContentValues().apply {
                    put(ParkingSpot::address.name, it.address)
                    put(ParkingSpot::price.name, it.price)
                    put(ParkingSpot::latCoordinate.name, it.latCoordinate)
                    put(ParkingSpot::longCoordinate.name, it.longCoordinate)

                    Log.d("Tag", it.toString())


                    if (registeredSpots != null){
                        if (registeredSpots!!.contains(it.id)){
                            put(ParkingSpot::isUserRegistered.name, true)
                        }
                        else{
                            put(ParkingSpot::isUserRegistered.name, false)
                        }
                    }else{
                        put(ParkingSpot::isUserRegistered.name, false)
                    }

                }
                context.contentResolver.insert(CAS_PROVIDER_CONTENT_URI, values)
            }
            context.sendBroadcast<CASReceiver>()
        }
    }


}