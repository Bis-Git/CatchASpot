package hr.algebra.catchaspotapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hr.algebra.catchaspotapp.framework.fetchParkingSpots
import hr.algebra.catchaspotapp.model.ParkingSpot
import hr.algebra.catchaspotapp.model.Payment
import kotlinx.android.synthetic.main.activity_parkin_spot_pager.*

class PaymentPagerActivity() : AppCompatActivity() {
    private lateinit var payments: MutableList<Payment>
    private var paymentPosition: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_pager)

        init()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun init() {
        val extras = intent.getSerializableExtra("payments") as ArrayList<Payment>

        payments = extras
        paymentPosition = intent.getIntExtra(ITEM_POSITION, 0)
        viewPager.adapter = PaymentPagerAdapter(payments, this)
        viewPager.currentItem = paymentPosition
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}