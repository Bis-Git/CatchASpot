package hr.algebra.catchaspotapp

import android.content.*
import android.database.Cursor
import android.net.Uri
import hr.algebra.catchaspotapp.dao.CASRepository
import hr.algebra.catchaspotapp.factory.getCASRepository
import hr.algebra.catchaspotapp.framework.clearTable
import hr.algebra.catchaspotapp.model.ParkingSpot
import java.lang.IllegalArgumentException

private const val AUTHORITY = "hr.algebra.catchaspotapp.api.provider"
private const val PATH = "parking_spots"
val CAS_PROVIDER_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH")

private const val PARKING_SPOTS = 3
private const val PARKING_SPOT_ID = 20
private val URI_MATCHER = with(UriMatcher(UriMatcher.NO_MATCH)) {
    addURI(AUTHORITY, PATH, PARKING_SPOTS)
    addURI(AUTHORITY, "$PATH/#", PARKING_SPOT_ID)
    this
}

private const val CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH
private const val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH


class CASProvider : ContentProvider(){

    private lateinit var repository: CASRepository

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when(URI_MATCHER.match(uri)){
            PARKING_SPOTS ->  return repository.delete(selection, selectionArgs)
            PARKING_SPOT_ID -> {
                val id = uri.lastPathSegment
                if (id != null){
                    return repository.delete("${ParkingSpot::_id.name} = ?", arrayOf(id))
                }
            }
        }

        throw IllegalArgumentException("Wrong URI")
    }

    override fun getType(uri: Uri): String? {

        when(URI_MATCHER.match(uri)){
            PARKING_SPOTS ->  return CONTENT_DIR_TYPE
            PARKING_SPOT_ID -> return CONTENT_ITEM_TYPE
        }

        throw IllegalArgumentException("Wrong URI")

    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id = repository.insert(values)
        return ContentUris.withAppendedId(CAS_PROVIDER_CONTENT_URI, id)
    }

    override fun onCreate(): Boolean {
        repository = getCASRepository(context)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor?
            = repository.query(projection, selection, selectionArgs, sortOrder)

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        when(URI_MATCHER.match(uri)){
            PARKING_SPOTS ->  return repository.update(values, selection, selectionArgs)
            PARKING_SPOT_ID -> {
                val id = uri.lastPathSegment
                if (id != null){
                    return repository.update(values,
                            "${ParkingSpot::_id.name}=?", arrayOf(id))
                }
            }
        }

        throw IllegalArgumentException("Wrong URI")
    }

}
