package sharlene. work.petsfoundation.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log


class PetProvider: ContentProvider() {
    companion object{
        private val sUriMatcher= UriMatcher(UriMatcher.NO_MATCH)
        init {
            sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS,1)
            sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS+"/#",2)
        }
    }
//    companion object {
//        const val CONTENT_AUTHORITY="sharlene.work.petsfoundation"
//        final val BASE_CONTENT_URI:Uri= Uri.parse("content://$CONTENT_AUTHORITY")
//        const val PATH_PETS="pets"
//        final val CONTENT_URI:Uri= Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS)
//    }
    private var mDbHelper: PetDbHelper?=null

    override fun onCreate(): Boolean {
        mDbHelper=PetDbHelper(context)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {

        var selection:String?=selection
        var selectionArgs:Array<out String>?=selectionArgs
        val db= mDbHelper!!.readableDatabase
        val cursor:Cursor
        val match= sUriMatcher.match(uri)
        val projection= arrayOf(PetContract.PetEntry._ID,PetContract.PetEntry.COLUMN_PET_NAME)
        when(match){
            1->{
                cursor=db.query(PetContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder)
            }
            2->{
                selection=PetContract.PetEntry._ID+"=?"
                selectionArgs= arrayOf(ContentUris.parseId(uri).toString())
                cursor=db.query(PetContract.PetEntry.TABLE_NAME,projection,selection,
                    selectionArgs,null,null,sortOrder)
            }
            else->throw IllegalArgumentException("Cannot query unknown URI $uri")
        }
        cursor.setNotificationUri(context?.contentResolver,uri)
        return cursor
    }

    override fun getType(uri: Uri): String {
        val match= sUriMatcher.match(uri)
        return when(match){
            1-> PetContract.PetEntry.CONTENT_LIST_TYPE
            2-> PetContract.PetEntry.CONTENT_ITEM_TYPE
            else->throw IllegalArgumentException("Unknown URI $uri with $match")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when(sUriMatcher.match(uri)){
            1-> return insertPet(uri, values)
            else->throw IllegalArgumentException("Insertion is not supported for $uri")
        }
    }
    private fun insertPet(uri:Uri, values: ContentValues?): Uri? {
        val name= values?.getAsString(PetContract.PetEntry.COLUMN_PET_NAME)
            ?: throw IllegalArgumentException("Pet requires a name")

        val gender=values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER)
        if(gender==null ||!PetContract.PetEntry.isValidGender(gender))
            throw IllegalArgumentException("Pet requires a gender")

        val weight=values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT)
        if(weight!=null && weight<0)
            throw IllegalArgumentException("Pet requires a weight")

        val db= mDbHelper?.writableDatabase

        val newRowId=db?.insert(PetContract.PetEntry.TABLE_NAME,null,values)
        if(newRowId==-1L){
            Log.d("PetProvider","Failed to insert row for $uri")
            return null
        }
        context?.contentResolver?.notifyChange(uri,null)
        return ContentUris.withAppendedId(uri, newRowId!!)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        var selection:String?=selection
        var selectionArgs: Array<out String>? =selectionArgs
        val db= mDbHelper!!.writableDatabase
        val match= sUriMatcher.match(uri)
        var rowsDeleted:Int
        when(match){
            1->{
                rowsDeleted=db.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs)
                if (rowsDeleted!=0) {
                    context?.contentResolver?.notifyChange(uri, null)
                }
                return rowsDeleted
            }
            2->{
                selection=PetContract.PetEntry._ID+"=?"
                selectionArgs= arrayOf(ContentUris.parseId(uri).toString())
                rowsDeleted=db.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs)
                if (rowsDeleted!=0) {
                    context?.contentResolver?.notifyChange(uri, null)
                }
                return rowsDeleted
            }
            else->throw IllegalArgumentException("Deletion is not supported for $uri")
        }

    }


    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        var selection:String?=selection
        var selectionArgs:Array<out String>?=selectionArgs
        val match= sUriMatcher.match(uri)
        when(match){
            1->return updatePet(uri, values, selection,selectionArgs)
            2->{
                selection=PetContract.PetEntry._ID+"=?"
                selectionArgs= arrayOf(ContentUris.parseId(uri).toString())
                return updatePet(uri, values,selection,selectionArgs)
            }
            else-> throw IllegalArgumentException("Update is not supported for $uri")
        }
    }
    private fun updatePet(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ):Int{
        if(values!!.containsKey(PetContract.PetEntry.COLUMN_PET_NAME)){
            val name= values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME)
                ?: throw IllegalArgumentException("Pet requires a name")
        }

        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_GENDER)){
            val gender=values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER)
            if (gender==null || !PetContract.PetEntry.isValidGender(gender))
                throw IllegalArgumentException("Pet requires valid gender")
        }

        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_WEIGHT)){
            val weight=values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT)
            if(weight!=null && weight<0)
                throw IllegalArgumentException("Pet requires valid weight")
        }

        if (values.size() ==0){
            return 0
        }
        val db= mDbHelper!!.writableDatabase
        val rowUpdated= db.update(PetContract.PetEntry.TABLE_NAME,values,selection,selectionArgs)
        if (rowUpdated!=0){
            context?.contentResolver?.notifyChange(uri,null)
        }
        return rowUpdated
    }


}
