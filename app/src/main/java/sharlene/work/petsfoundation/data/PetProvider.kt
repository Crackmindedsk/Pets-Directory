package sharlene.work.petsfoundation.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log

private val sUriMatcher=UriMatcher(UriMatcher.NO_MATCH).apply {
    addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS,1)
    addURI(PetContract.CONTENT_AUTHORITY,"${PetContract.PATH_PETS}/#",2)
}
class PetProvider: ContentProvider() {
//    companion object {
//        const val CONTENT_AUTHORITY="sharlene.work.petsfoundation"
//        final val BASE_CONTENT_URI:Uri= Uri.parse("content://$CONTENT_AUTHORITY")
//        const val PATH_PETS="pets"
//        final val CONTENT_URI:Uri= Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS)
//    }
    private lateinit var mDbHelper:PetDbHelper

    override fun onCreate(): Boolean {
        mDbHelper=PetDbHelper(context)

        return true
    }

    override fun query(
        uri: Uri,
        strings: Array<out String>?,
        s: String?,
        strings1: Array<out String>?,
        s1: String?,
    ): Cursor? {
        val db=mDbHelper.readableDatabase
        val match= sUriMatcher.match(uri)
        lateinit var cursor:Cursor
        val projection= arrayOf(PetContract.PetEntry._ID,PetContract.PetEntry.COLUMN_PET_NAME)
        when(match){
            1->{
                cursor=db.query(PetContract.PetEntry.TABLE_NAME,projection,null,null,null,null,null)
            }
            2->{
                val selection=PetContract.PetEntry._ID+"=?"
                val selectionArgs= (ContentUris.parseId(uri)).toString()
                cursor=db.query(PetContract.PetEntry.TABLE_NAME,projection,selection,
                    arrayOf(selectionArgs),null,null,null)
            }
            else->throw IllegalArgumentException("Cannot query unknown URI $uri")
        }
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val match = sUriMatcher.match(uri)
        when(match){
            1-> return insertPet(uri,values)
            else->throw IllegalArgumentException("Insertion is not supported for $uri")
        }
    }
    private fun insertPet(uri:Uri, values: ContentValues?): Uri? {
        val db=mDbHelper.writableDatabase

        val newRowId=db.insert(PetContract.PetEntry.TABLE_NAME,null,values)
        if(newRowId==-1L){
            Log.d("PetProvider","Failed to insert row for $uri")
            return null
        }
        return ContentUris.withAppendedId(uri,newRowId)
    }

    override fun delete(uri: Uri, s: String?, strings: Array<out String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, s: String?, strings: Array<out String>?): Int {
        return 0
    }



}