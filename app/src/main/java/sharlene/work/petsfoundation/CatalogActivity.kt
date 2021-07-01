package sharlene.work.petsfoundation

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
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
    private fun displayDatabaseInfo(){
        val db:SQLiteDatabase=mDHelper.readableDatabase
        val cursor:Cursor=db.rawQuery("SELECT * FROM " + PetContract.PetEntry.TABLE_NAME,null)
        cursor.use { cursor ->
            val displayView:TextView=findViewById(R.id.text_view_pet)
            displayView.text = "Number of rows in pets database table:" +cursor.count
        }
        
    }
    private fun insertPet(){
        val db=mDHelper.writableDatabase
        val values=ContentValues().apply {
            put(PetContract.PetEntry.COLUMN_PET_NAME,"Toto")
            put(PetContract.PetEntry.COLUMN_PET_BREED,"Terrier")
            put(PetContract.PetEntry.COLUMN_PET_GENDER,1)
            put(PetContract.PetEntry.COLUMN_PET_WEIGHT,7)
        }
        val newRowId=db?.insert(PetContract.PetEntry.TABLE_NAME,null,values)
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