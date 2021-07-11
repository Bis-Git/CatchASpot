package hr.algebra.catchaspotapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.algebra.catchaspotapp.api.CASSender
import hr.algebra.catchaspotapp.framework.*
import hr.algebra.catchaspotapp.model.ParkingSpot
import hr.algebra.catchaspotapp.model.User
import kotlinx.android.synthetic.main.fragment_register_spot.*


class RegisterSpotFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var gMap: GoogleMap
    private lateinit var movableMarker: MarkerOptions
    private var db = Firebase.firestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_spot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        setupListeners()
    }

    private fun setupListeners() {


        btnRegisterSpot.setOnClickListener{

            val position = movableMarker.position

            val parkingSpot = ParkingSpot(
                    null,
                    etAddress.text.toString(),
                    etCost.text.toString().toDouble(),
                    position.latitude,
                    position.longitude,
                    true
            )

            val casSender = CASSender(requireContext())
            casSender.sendNetworkRequest(parkingSpot)



            etAddress.text.clear()
            etParkingSpotNum.text.clear()
            etCost.text.clear()

            requireContext().hideKeyboard(it)
        }

    }


    override fun onMapReady(map: GoogleMap) {
        gMap = map

        fusedLocationClient.prepareMapAndSetCurrentLocation(gMap, requireContext(), false)

        var pos = LatLng(requireContext().getCurrentCoordinate(CURRENT_LATITUDE),
                requireContext().getCurrentCoordinate(CURRENT_LONGITUDE))

        movableMarker = MarkerOptions().position(pos).icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).draggable(true)

        gMap.addMarker(movableMarker)
        gMap.setOnMarkerDragListener(this)
    }

    override fun onMarkerDragStart(marker: Marker?) { }

    override fun onMarkerDrag(marker: Marker?) { }

    override fun onMarkerDragEnd(marker: Marker?) {
        if (marker != null) {
            movableMarker.position(marker.position)
        }
    }


}