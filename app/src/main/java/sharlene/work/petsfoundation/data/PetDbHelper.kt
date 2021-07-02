
package sharlene.work.petsfoundation.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class PetDbHelper(context: Context?):SQLiteOpenHelper(context,DATABASE_NAME, null, DATABASE_VERSION){
    companion object{
        const val DATABASE_VERSION=1
        const val DATABASE_NAME="shelter.db"
        private const val SQL_CREATE_ENTRIES="CREATE TABLE ${PetContract.PetEntry.TABLE_NAME} ( ${BaseColumns._ID} INTEGER PRIMARY KEY, ${PetContract.PetEntry.COLUMN_PET_NAME} TEXT NOT NULL, ${PetContract.PetEntry.COLUMN_PET_BREED} TEXT, ${PetContract.PetEntry.COLUMN_PET_GENDER} INTEGER NOT NULL, ${PetContract.PetEntry.COLUMN_PET_WEIGHT} INTEGER NOT NULL DEFAULT 0)"
        private const val SQL_DELETE_ENTRIES="DROP TABLE IF EXISTS ${PetContract.PetEntry.TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(Companion.SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int){

    }
}