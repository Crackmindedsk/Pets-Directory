package sharlene.work.petsfoundation

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import sharlene.work.petsfoundation.data.PetContract
import sharlene.work.petsfoundation.data.PetDbHelper

class CatalogActivity : AppCompatActivity() {
    private lateinit var mDHelper:PetDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        val fab= findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(){
            val intent= Intent(this@CatalogActivity,EditorActivity::class.java)
            startActivity(intent)
        }
        mDHelper= PetDbHelper(this)
        displayDatabaseInfo()
    }

    override fun onStart() {
        super.onStart()
        displayDatabaseInfo()
    }

    private fun displayDatabaseInfo(){
        val db:SQLiteDatabase=mDHelper.readableDatabase
        val projection= arrayOf(BaseColumns._ID,PetContract.PetEntry.COLUMN_PET_NAME, PetContract.PetEntry.COLUMN_PET_BREED, PetContract.PetEntry.COLUMN_PET_GENDER,PetContract.PetEntry.COLUMN_PET_WEIGHT)
        val cursor=db.query(PetContract.PetEntry.TABLE_NAME,projection,null,null,null,null,null)

        cursor.use { cursor ->
            val displayView:TextView=findViewById(R.id.text_view_pet)
            displayView.text = "The pets table contains ${cursor.count} pets\n\n"
            displayView.append("${PetContract.PetEntry._ID} - ${PetContract.PetEntry.COLUMN_PET_NAME} - ${PetContract.PetEntry.COLUMN_PET_BREED} - ${PetContract.PetEntry.COLUMN_PET_GENDER} - ${PetContract.PetEntry.COLUMN_PET_WEIGHT}\n")

            val idColumnIndex=cursor.getColumnIndex(PetContract.PetEntry._ID)
            val nameColumnIndex=cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME)
            val breedColumnIndex=cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED)
            val genderColumnIndex=cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER)
            val weightColumnIndex=cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT)
            while (cursor.moveToNext()){
                val curentID=cursor.getInt(idColumnIndex)
                val currentname=cursor.getString(nameColumnIndex)
                val currentbreed=cursor.getString(breedColumnIndex)
                val currentgender=cursor.getInt(genderColumnIndex)
                val currentweight=cursor.getInt(weightColumnIndex)
                displayView.append("\n $curentID - $currentname - $currentbreed - $currentgender - $currentweight")
            }
        }
        cursor.close()
        
    }
    private fun insertPet(){
        val db=mDHelper.writableDatabase
        val values=ContentValues().apply {
            put(PetContract.PetEntry.COLUMN_PET_NAME,"Toto")
            put(PetContract.PetEntry.COLUMN_PET_BREED,"Terrier")
            put(PetContract.PetEntry.COLUMN_PET_GENDER,PetContract.PetEntry.GENDER_MALE)
            put(PetContract.PetEntry.COLUMN_PET_WEIGHT,7)
        }
        val newRowId=db?.insert(PetContract.PetEntry.TABLE_NAME,null,values)
        Log.d("CatalogActivity","New Row Id $newRowId")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_catalog,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_insert_dummy_data -> {
                insertPet()
                displayDatabaseInfo()
                true
            }
            R.id.action_delete_all_entries -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}