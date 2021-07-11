package hr.algebra.catchaspotapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.catchaspotapp.model.Payment

class PaymentPagerAdapter(private val payments: MutableList<Payment>, context: Context)
    : RecyclerView.Adapter<PaymentPagerAdapter.ViewHolder>(){

    val context = context

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(payment: Payment){
            tvAddress.text = payment.address
            tvPrice.text = "${payment.pricePayed} kn"
            tvDate.text = payment.date
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.payment_pager, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val payment = payments[position]

        holder.bind(payment)
    }

    override fun getItemCount() = payments.size
}