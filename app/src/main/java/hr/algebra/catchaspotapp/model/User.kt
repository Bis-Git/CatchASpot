package hr.algebra.catchaspotapp.model

data class User (
    val email: String,
    val password: String,
    val name: String,
    val registered_parking_spots: List<Int>?
)