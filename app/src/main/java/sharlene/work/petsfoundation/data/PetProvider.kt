package sharlene.work.petsfoundation.data

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

class PetProvider: ContentProvider() {
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
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, s: String?, strings: Array<out String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, s: String?, strings: Array<out String>?): Int {
        return 0
    }

}