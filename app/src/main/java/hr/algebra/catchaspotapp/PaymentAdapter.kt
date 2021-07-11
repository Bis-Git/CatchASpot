package hr.algebra.catchaspotapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.catchaspotapp.framework.OnItemClickListenerCallback
import hr.algebra.catchaspotapp.framework.startActivity
import hr.algebra.catchaspotapp.model.Payment

class PaymentAdapter (private val payments: MutableList<Payment>, private val context: Context, onClickListener: OnItemClickListenerCallback)
    : RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {

    private val onClickListener = onClickListener

    class ViewHolder(paymentView : View) : RecyclerView.ViewHolder(paymentView) {
        private val tvDate: TextView = paymentView.findViewById(R.id.tvDate)
        private val tvAmount: TextView = paymentView.findViewById(R.id.tvAmount)

        fun bind(payment: Payment){

            tvDate.text = payment.date
            tvAmount.text = "${payment.pricePayed} kn"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val paymentView = LayoutInflater.from(context)
            .inflate(R.layout.payment, parent, false)
        return ViewHolder(paymentView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener{
            onClickListener.onItemClick(it, position)
        }

        holder.bind(payments[position])
    }

    override fun getItemCount() = payments.size

}