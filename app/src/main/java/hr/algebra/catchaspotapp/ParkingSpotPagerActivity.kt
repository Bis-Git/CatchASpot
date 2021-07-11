package hr.algebra.catchaspotapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import hr.algebra.catchaspotapp.framework.fetchParkingSpots
import hr.algebra.catchaspotapp.model.ParkingSpot
import kotlinx.android.synthetic.main.activity_parkin_spot_pager.*

const val ITEM_POSITION = "hr.algebra.catchaspot.item_position"

class ParkingSpotPagerActivity : AppCompatActivity(){

    private lateinit var items: MutableList<ParkingSpot>
    private var itemPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parkin_spot_pager)

        init()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun init() {
        items = fetchParkingSpots()
        var parkingSpots = mutableListOf<ParkingSpot>()

        items.forEach{
            if (!it.isUserRegistered){
                parkingSpots.add(it)
            }
        }

        itemPosition = intent.getIntExtra(ITEM_POSITION, 0)
        viewPager.adapter = ParkingSpotPagerAdapter(parkingSpots, this)
        viewPager.currentItem = itemPosition
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}