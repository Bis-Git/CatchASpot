package hr.algebra.catchaspotapp


import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.algebra.catchaspotapp.framework.setBooleanProperty
import hr.algebra.catchaspotapp.framework.setCurrentUserIDProperty
import hr.algebra.catchaspotapp.framework.startActivityAndClearStack
import hr.algebra.catchaspotapp.model.User
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        setupListeners()
    }


    private fun setupListeners() {

        btnRegister.setOnClickListener {

            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val name = etName.text.toString()

            if(validateFields(email, password, name)){

                val user = User(email, password, name, null)
                registerUser(user)


            }
        }
    }

    private fun registerUser(user: User) {
        mAuth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    FirebaseAuth.getInstance().currentUser?.let {
                        Firebase.firestore.collection("users")
                            .document(it.uid)
                            .set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()

                                setBooleanProperty(USER_LOGGED_IN, true)
                                setCurrentUserIDProperty(USER_ID, mAuth.currentUser?.uid.toString())
                                startActivityAndClearStack<SplashScreenActivity>()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Big OOF", Toast.LENGTH_SHORT).show()
                            }
                    }

                } else {
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    etEmail.error = "E-mail already in use!"
                    etEmail.requestFocus()
                }
            }
    }

    private fun validateFields(email: String, password: String, name: String):Boolean {
        if (email.isEmpty()){
            etEmail.error = "Email required"
            etEmail.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.error = "Please insert valid email"
            etEmail.requestFocus()
            return false
        }
        if (password.isEmpty()){
            etPassword.error = "Password required"
            etPassword.requestFocus()
            return false
        }
        if (password.length < 6){
            etPassword.error = "Password must be at least 6 characters long"
            etPassword.requestFocus()
            return false
        }
        if (name.isEmpty()){
            etName.error = "Name required"
            etName.requestFocus()
            return false
        }
        return true
    }

}