package hr.algebra.catchaspotapp

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import hr.algebra.catchaspotapp.api.CASFetcher

private const val JOB_ID = 1

class CASService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        CASFetcher(this).fetchParkingSpotsOnStart()
    }

    companion object{
        fun enqueueWork(context: Context, intent: Intent){
            enqueueWork(context, CASService::class.java, JOB_ID, intent)
        }
    }

}