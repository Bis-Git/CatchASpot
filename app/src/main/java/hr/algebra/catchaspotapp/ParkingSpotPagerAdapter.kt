package hr.algebra.catchaspotapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import hr.algebra.catchaspotapp.framework.getCurrentCoordinate
import hr.algebra.catchaspotapp.framework.startActivity
import hr.algebra.catchaspotapp.model.ParkingSpot

class ParkingSpotPagerAdapter(private val parkingSpots: MutableList<ParkingSpot>, context: Context)
    :RecyclerView.Adapter<ParkingSpotPagerAdapter.ViewHolder>(){

    val context = context

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val tvCost: TextView = itemView.findViewById(R.id.tvCost)

        fun bind(parkingSpot: ParkingSpot){

            tvAddress.text = parkingSpot.address
            tvCost.text = parkingSpot.price.toString() + " kn/h"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.parking_spot_pager, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val parkingSpot = parkingSpots[position]
        val btnNavigate = holder.itemView.findViewById<Button>(R.id.btnNavigate)
        val btnParked = holder.itemView.findViewById<Button>(R.id.btnParked)

        val currentLocation = LatLng(
                context.getCurrentCoordinate(CURRENT_LATITUDE),
                context.getCurrentCoordinate(CURRENT_LONGITUDE)
        )

        btnNavigate.setOnClickListener{
            val uriString = "http://maps.google.com/maps?saddr="+
                    "${currentLocation.latitude},${currentLocation.longitude}"+
                    "&daddr="+"${parkingSpot.latCoordinate},${parkingSpot.longCoordinate}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(context, intent, null)

            true
        }

        btnParked.setOnClickListener {
            val intent = Intent(context, ParkedActivity::class.java)
            intent.putExtra("price", parkingSpot.price)
            intent.putExtra("address", parkingSpot.address)
            startActivity(context, intent, null)
        }


        holder.bind(parkingSpot)
    }

    override fun getItemCount() = parkingSpots.size
}