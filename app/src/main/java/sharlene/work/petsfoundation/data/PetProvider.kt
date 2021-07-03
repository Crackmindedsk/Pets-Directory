package sharlene. work.petsfoundation.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentUris.*
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.text.Selection
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
                val selectionArgs= (parseId(uri)).toString()
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
        val name= values?.getAsString(PetContract.PetEntry.COLUMN_PET_NAME)
        if (name==null)
            throw IllegalArgumentException("Pet requires a name")

        val breed=values.getAsString(PetContract.PetEntry.COLUMN_PET_BREED)
        if(breed==null)
            throw IllegalArgumentException("Pet requires a breed name")

        val gender=values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER)
        if(gender==null ||!PetContract.PetEntry.isValidGender(gender))
            throw IllegalArgumentException("Pet requires a gender")

        val weight=values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT)
        if(weight!=null && weight<0)
            throw IllegalArgumentException("Pet requires a weight")

        val db=mDbHelper.writableDatabase

        val newRowId=db.insert(PetContract.PetEntry.TABLE_NAME,null,values)
        if(newRowId==-1L){
            Log.d("PetProvider","Failed to insert row for $uri")
            return null
        }
        return withAppendedId(uri,newRowId)
    }

    override fun delete(uri: Uri, s: String?, strings: Array<out String>?): Int {
        val db=mDbHelper.writableDatabase
        val match= sUriMatcher.match(uri)
        when(match){
            1->return db.delete(PetContract.PetEntry.TABLE_NAME,s,strings)
            2->{
                s=PetContract.PetEntry._ID+"=?"
                strings= parseId(uri)
                return db.delete(PetContract.PetEntry.TABLE_NAME,s,strings)
            }
            else->throw IllegalArgumentException("Deletion is not supported for $uri")
        }
    }

    override fun update(uri: Uri, values: ContentValues?, s: String?, strings: Array<out String>?): Int {
        val match= sUriMatcher.match(uri)
        when(match){
            1->return updatePet(uri,values,s,strings)
            2->{
                s=PetContract.PetEntry._ID+"=?"
                strings= parseId(uri)
                return updatePet(uri,values,s,strings)
            }
            else-> throw IllegalArgumentException("Update is not supported for $uri")
        }
    }
    private fun updatePet(uri: Uri,values: ContentValues,selection: String,selectionArgs:Array<out String>){
        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_NAME)){
            val name=values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME)
            if (name==null)
                throw IllegalArgumentException("Pet requires a name")
        }

        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_GENDER)){
            val gender=values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER)
            if (gender==null || !PetContract.PetEntry.isValidGender())
                throw IllegalArgumentException("Pet requires valid gender")
        }

        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_WEIGHT)){
            val weight=values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT)
            if(weight!=null && weight<0)
                throw IllegalArgumentException("Pet requires valid weight")
        }

        if (values.size() ==0){
            return
        }
        val db=mDbHelper.writableDatabase
        return db.update(PetContract.PetEntry.TABLE_NAME,values,selection,selectionArgs)
    }


}