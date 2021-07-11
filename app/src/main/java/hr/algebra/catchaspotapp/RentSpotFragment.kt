package hr.algebra.catchaspotapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.ui.IconGenerator
import hr.algebra.catchaspotapp.api.CASFetcher
import hr.algebra.catchaspotapp.api.CASParkingSpot
import hr.algebra.catchaspotapp.framework.*
import hr.algebra.catchaspotapp.model.ParkingSpot
import kotlinx.android.synthetic.main.fragment_rent_spot.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


const val CURRENT_LATITUDE = "hr.algebra.catchaspotapp_latitude"
const val CURRENT_LONGITUDE = "hr.algebra.catchaspotapp_longitude"

class RentSpotFragment : Fragment(), OnMapReadyCallback, OnItemClickListenerCallback {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var gMap: GoogleMap
    private var parkingSpots = mutableListOf<ParkingSpot>()
    private lateinit var eligibleParkingSpots: MutableList<ParkingSpot>
    private var db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val allParkingSpots = requireContext().fetchParkingSpots()

        allParkingSpots.forEach{
            if (!it.isUserRegistered){
                parkingSpots.add(it)
            }
        }

        return inflater.inflate(R.layout.fragment_rent_spot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout = view.findViewById(R.id.swipeContainer)
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        eligibleParkingSpots = mutableListOf()
        parkingSpots.forEach{
            if (!it.isUserRegistered)
            {
                eligibleParkingSpots.add(it)
            }
        }

        val parkingSpotAdapter = ParkingSpotAdapter(eligibleParkingSpots, requireContext(), this)
        rvParkingSpots.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = parkingSpotAdapter
        }
    }

    private fun refresh() {

        requireContext().clearTable()
        val fetcher = CASFetcher(requireContext())
        val request = fetcher.casApi.fetchParkingSpots()

        requireContext().getCurrentUserIDProperty(USER_ID)?.let { id ->
            db.collection("users")
                    .document(id)
                    .get()
                    .addOnSuccessListener {
                        request.enqueue(object: Callback<List<CASParkingSpot>> {
                            override fun onResponse(
                                    call: Call<List<CASParkingSpot>>,
                                    response: Response<List<CASParkingSpot>>
                            ) {
                                if (response.body() != null){
                                    val work = GlobalScope.launch {
                                        fetcher.populateSpotsOnRefresh(response.body()!!, it)
                                    }
                                    runBlocking {
                                        work.join()
                                        parkingSpots = requireContext().fetchParkingSpots()
                                        gMap.clear()
                                        fusedLocationClient.prepareMapAndSetCurrentLocation(gMap, requireContext(), true)
                                        addMarkers()
                                        swipeRefreshLayout.isRefreshing = false
                                    }
                                }
                            }
                            override fun onFailure(call: Call<List<CASParkingSpot>>, t: Throwable) {
                                Log.d(javaClass.name, t.message, t)
                            }
                        })
                    }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        gMap = map
        fusedLocationClient.prepareMapAndSetCurrentLocation(gMap, requireContext(), true)
        addMarkers()
    }

    private fun addMarkers(){

        var iconFactory = IconGenerator(requireContext())

        parkingSpots.forEach {
            if (!it.isUserRegistered){
                val pos = LatLng(it.latCoordinate, it.longCoordinate)
                var marker = MarkerOptions()
                        .position(pos)
                        .icon(
                                BitmapDescriptorFactory
                                        .fromBitmap(iconFactory.makeIcon(it.price.toString() + "kn/h"))
                        )
                        .anchor(iconFactory.anchorU, iconFactory.anchorV)

                //val customMarkerIcon = requireContext().getCustomMarkerIcon(R.drawable.parking_meter)

                gMap.addMarker(marker)
            }
        }
    }


    override fun onItemClick(v: View, position: Int) {
        val parkingSpot = eligibleParkingSpots[position]

        val pos = LatLng(parkingSpot.latCoordinate, parkingSpot.longCoordinate)

        val cameraUpdate = CameraUpdateFactory.newLatLng(pos)
        gMap.animateCamera(cameraUpdate)
    }

}
