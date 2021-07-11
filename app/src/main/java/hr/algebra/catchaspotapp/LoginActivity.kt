package hr.algebra.catchaspotapp

import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.algebra.catchaspotapp.framework.setBooleanProperty
import hr.algebra.catchaspotapp.framework.setCurrentUserIDProperty
import hr.algebra.catchaspotapp.framework.startActivity
import hr.algebra.catchaspotapp.framework.startActivityAndClearStack
import kotlinx.android.synthetic.main.activity_login.*


const val USER_LOGGED_IN = "hr.algebra.catchaspotapp_user_logged_in"
const val USER_ID = "hr.algebra.catchaspotapp_user_id"

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        setupListeners()
    }


    private fun setupListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if(validateFields(email, password)){
                loginUser(email, password)
            }

        }

        btnRegister.setOnClickListener {
            startActivity<RegisterActivity>()
        }

    }

    private fun loginUser(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    setBooleanProperty(USER_LOGGED_IN, true)

                    FirebaseAuth.getInstance().currentUser?.let {fu ->
                        Firebase.firestore.collection("users")
                            .document(fu.uid)
                            .get()
                            .addOnSuccessListener {
                                setCurrentUserIDProperty(USER_ID, it.id)
                                startActivityAndClearStack<SplashScreenActivity>()
                            }
                    }
                } else {
                    etEmail.error = "User doesn't exist or password mismatch"
                    etEmail.requestFocus()
                }
            }
    }

    private fun validateFields(email: String, password: String):Boolean {
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
        return true
    }
}