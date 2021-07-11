package hr.algebra.catchaspotapp.framework

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.algebra.catchaspotapp.CAS_PROVIDER_CONTENT_URI
import hr.algebra.catchaspotapp.CURRENT_LATITUDE
import hr.algebra.catchaspotapp.CURRENT_LONGITUDE
import hr.algebra.catchaspotapp.USER_ID
import hr.algebra.catchaspotapp.dao.CASSqlHelper
import hr.algebra.catchaspotapp.model.ParkingSpot


fun Context.getCurrentCoordinate(key: String)
    = java.lang.Double.longBitsToDouble(
        PreferenceManager.getDefaultSharedPreferences(this).getLong(
                key,
                0
        )
)

fun Context.setCurrentCoordinate(key: String, value: Double)
    = PreferenceManager.getDefaultSharedPreferences(this)
    .edit()
    .putLong(key, java.lang.Double.doubleToRawLongBits(value))
    .apply()

fun Context.getCurrentUserIDProperty(key: String)
        = PreferenceManager.getDefaultSharedPreferences(this).getString(key, "")

fun Context.setCurrentUserIDProperty(key: String, value: String)
    = PreferenceManager.getDefaultSharedPreferences(this)
    .edit()
    .putString(key, value)
    .apply()


fun Context.getBooleanProperty(key: String)
    = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(key, false)

fun Context.setBooleanProperty(key: String, value: Boolean)
    = PreferenceManager.getDefaultSharedPreferences(this)
    .edit()
    .putBoolean(key, value)
    .apply()

inline fun <reified T : Activity> Context.startActivity() = startActivity(Intent(
        this,
        T::class.java
).apply {
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(this)
})

inline fun <reified T : Activity> Context.startActivityAndClearStack() = startActivity(Intent(
        this,
        T::class.java
).apply {
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    startActivity(this)
})


inline fun <reified T : Activity> Context.startActivity(key: String, value: Int) = startActivity(
        Intent(
                this,
                T::class.java
        ).apply {
            putExtra(key, value)
        })

fun Context.isOnline() : Boolean{
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork
    if (activeNetwork != null){
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        if (networkCapabilities != null){
            return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }
    }
    return false
}

inline fun <reified T : BroadcastReceiver> Context.sendBroadcast() = sendBroadcast(
        Intent(
                this,
                T::class.java
        )
)

fun Context.fetchParkingSpots() : MutableList<ParkingSpot>{
    val parkingSpots = mutableListOf<ParkingSpot>()
    val cursor = contentResolver?.query(
            CAS_PROVIDER_CONTENT_URI,
            null, null, null, null
    )

    if (cursor != null){
        while (cursor.moveToNext()){
            parkingSpots.add(
                    ParkingSpot(
                            cursor.getLong(cursor.getColumnIndex(ParkingSpot::_id.name)),
                            cursor.getString(cursor.getColumnIndex(ParkingSpot::address.name)),
                            cursor.getDouble(cursor.getColumnIndex(ParkingSpot::price.name)),
                            cursor.getDouble(cursor.getColumnIndex(ParkingSpot::latCoordinate.name)),
                            cursor.getDouble(cursor.getColumnIndex(ParkingSpot::longCoordinate.name)),
                            cursor.getInt(cursor.getColumnIndex(ParkingSpot::isUserRegistered.name)) > 0
                    )
            )
        }
    }
    return parkingSpots
}

@SuppressWarnings@SuppressLint("MissingPermission")
fun FusedLocationProviderClient.prepareMapAndSetCurrentLocation(
        gMap: GoogleMap,
        context: Context,
        showLocation: Boolean
){

    gMap.isMyLocationEnabled = showLocation
    gMap.uiSettings.isMyLocationButtonEnabled = showLocation

    this.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null){
                    val sydney = LatLng(location.latitude, location.longitude)
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16F))

                    context.setCurrentCoordinate(
                            CURRENT_LATITUDE,
                            location.latitude
                    )

                    context.setCurrentCoordinate(
                            CURRENT_LONGITUDE,
                            location.longitude
                    )
                }
            }
}

fun Context.getCustomMarkerIcon(icon: Int): Bitmap {
    val height = 70
    val width = 50
    val b = BitmapFactory.decodeResource(resources, icon)
    return Bitmap.createScaledBitmap(b, width, height, false)
}

fun Context.clearTable(){
    val helper = CASSqlHelper(this)
    helper.clearTable(helper.writableDatabase)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


