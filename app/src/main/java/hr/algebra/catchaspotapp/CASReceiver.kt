package hr.algebra.catchaspotapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hr.algebra.catchaspotapp.framework.setBooleanProperty
import hr.algebra.catchaspotapp.framework.startActivity

class CASReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.startActivity<HostActivity>()
    }
}