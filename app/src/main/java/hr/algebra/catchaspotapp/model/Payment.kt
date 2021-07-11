package hr.algebra.catchaspotapp.model

import java.io.Serializable

data class Payment(
            val payerID: String,
            val address: String,
            val pricePayed: Double,
            val date: String
        ) : Serializable