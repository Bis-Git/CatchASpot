package hr.algebra.catchaspotapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.catchaspotapp.framework.OnItemClickListenerCallback
import hr.algebra.catchaspotapp.framework.startActivity
import hr.algebra.catchaspotapp.model.ParkingSpot

class ParkingSpotAdapter (private val parkingSpots: MutableList<ParkingSpot>, private val context: Context, onClickListener: OnItemClickListenerCallback)
    : RecyclerView.Adapter<ParkingSpotAdapter.ViewHolder>() {

    private val onClickListener = onClickListener

    class ViewHolder(parkingSpotView : View) : RecyclerView.ViewHolder(parkingSpotView) {
        private val ivParkingSpot: ImageView = parkingSpotView.findViewById(R.id.ivParkingSpot)
        private val tvParkingSpot: TextView = parkingSpotView.findViewById(R.id.tvParkingSpot)

        fun bind(parkingSpot: ParkingSpot){

            ivParkingSpot.setImageResource(R.drawable.logo)
            tvParkingSpot.text = parkingSpot.address

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val parkingSpotView = LayoutInflater.from(context)
                .inflate(R.layout.parking_spot, parent, false)
        return ViewHolder(parkingSpotView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener{

            onClickListener.onItemClick(it, position)
        }

        holder.itemView.setOnLongClickListener{
            context.startActivity<ParkingSpotPagerActivity>(ITEM_POSITION, position)
            true
        }

        holder.bind(parkingSpots[position])
    }

    override fun getItemCount() = parkingSpots.size

}