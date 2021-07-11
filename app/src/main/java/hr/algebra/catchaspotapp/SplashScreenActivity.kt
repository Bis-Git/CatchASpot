
package hr.algebra.catchaspotapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import hr.algebra.catchaspotapp.framework.*
import kotlinx.android.synthetic.main.activity_splash_screen.*

private const val PERMISSION_ID = 1

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        startAnimations()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_ID)

        }
        else{
            if (getBooleanProperty(USER_LOGGED_IN)){
                redirect()
            }
            else{
                redirectToLogin()
            }
        }
    }

    private fun startAnimations() {

        val backgroundTranslation = ivBackground.animate().translationY(-2400F)
        backgroundTranslation.duration = 1000
        backgroundTranslation.startDelay = 4000

        val lottieViewTranslation = lottieView.animate().translationY(-2400F)
        lottieViewTranslation.duration = 1000
        lottieViewTranslation.startDelay = 4000

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        redirectToLogin()
    }

    private fun redirectToLogin() {
        startActivityAndClearStack<LoginActivity>()
    }

    private fun redirect() {

        clearTable()

        if (isOnline()){
            Intent(this, CASService::class.java).apply {
                CASService.enqueueWork(this@SplashScreenActivity, this)
            }
        }
        else{
            Toast.makeText(this, "No connection", Toast.LENGTH_LONG).show()
            finish()
        }

    }

}