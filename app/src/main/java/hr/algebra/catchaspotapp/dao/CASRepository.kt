package hr.algebra.catchaspotapp.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

interface CASRepository {
    fun delete(selection: String?, selectionArgs: Array<String>?): Int

    fun insert(values: ContentValues?): Long

    fun query(projection: Array<String>?, selection: String?,
              selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor?

    fun update(values: ContentValues?, selection: String?,
               selectionArgs: Array<String>?
    ): Int
}