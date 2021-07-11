package hr.algebra.catchaspotapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.algebra.catchaspotapp.framework.getCurrentUserIDProperty
import hr.algebra.catchaspotapp.framework.startActivityAndClearStack
import hr.algebra.catchaspotapp.model.Payment
import kotlinx.android.synthetic.main.activity_payment.*
import java.text.SimpleDateFormat
import java.util.*


class PaymentActivity : AppCompatActivity() {
    private lateinit var time: String
    private var basePrice: Double = 0.0
    private var finalPrice: Double = 0.0
    private var address: String = ""
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        var extras = intent.extras

        if (extras != null){
            time = extras.getString("time").toString()
            basePrice = extras.getDouble("price")
            address = extras.getString("address")!!
        }

        val hour = time.split(":")[0].toInt()

        finalPrice = if (hour < 1){
            basePrice
        }
        else{
            basePrice * (hour + 1)
        }

        tvPrice.text = "$finalPrice kn"
        generateQR()

        setupListeners()

    }

    private fun setupListeners() {
        btnDone.setOnClickListener {
            val date = Calendar.getInstance().time
            val format = SimpleDateFormat.getDateTimeInstance()

            val payment = Payment(getCurrentUserIDProperty(USER_ID)!!, address, finalPrice, format.format(date))

            db.collection("payments").add(
                payment
            )

            startActivityAndClearStack<HostActivity>()
        }
    }

    private fun generateQR() {
        var qrEncoder = QRGEncoder("time:$time, base price:$basePrice, final price:$finalPrice kn", null, QRGContents.Type.TEXT, 400)
        val bitmap = qrEncoder.encodeAsBitmap()
        ivQRCode.setImageBitmap(bitmap)
    }

}