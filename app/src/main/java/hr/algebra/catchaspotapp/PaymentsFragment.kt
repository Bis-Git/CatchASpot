package hr.algebra.catchaspotapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.algebra.catchaspotapp.framework.OnItemClickListenerCallback
import hr.algebra.catchaspotapp.framework.getCurrentUserIDProperty
import hr.algebra.catchaspotapp.framework.startActivity
import hr.algebra.catchaspotapp.model.Payment
import kotlinx.android.synthetic.main.fragment_payments.*
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class PaymentsFragment : Fragment(), OnItemClickListenerCallback {

    private var db = Firebase.firestore
    private lateinit var payments: MutableList<Payment>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        getPayments()
        return inflater.inflate(R.layout.fragment_payments, container, false)
    }

    private fun getPayments() {
        val refPayments = db.collection("payments")
        refPayments.whereIn("payerID", listOf(requireContext().getCurrentUserIDProperty(USER_ID)))
            .get()
            .addOnSuccessListener { qs ->
                payments = mutableListOf<Payment>()
                qs.forEach {
                    val payment = Payment(
                        it.data["payerID"].toString(),
                        it.data["address"].toString(),
                        it.data["pricePayed"].toString().toDouble(),
                        it.data["date"].toString()
                    )

                    payments.add(payment)
                }

                val paymentAdapter = PaymentAdapter(payments, requireContext(), this)
                rvPayments.apply {
                    layoutManager = LinearLayoutManager(activity)
                    adapter = paymentAdapter
                }
            }

    }

    override fun onItemClick(v: View, position: Int) {
        val intent = Intent(requireContext(), PaymentPagerActivity::class.java)
        intent.putExtra("payments", payments as ArrayList<Payment>)
        startActivity(intent)
    }

}