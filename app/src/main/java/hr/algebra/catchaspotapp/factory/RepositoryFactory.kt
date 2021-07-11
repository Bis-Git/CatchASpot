package hr.algebra.catchaspotapp.factory

import android.content.Context
import hr.algebra.catchaspotapp.dao.CASSqlHelper

fun getCASRepository(context: Context?) = CASSqlHelper(context)