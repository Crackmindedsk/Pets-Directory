package sharlene.work.petsfoundation

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import sharlene.work.petsfoundation.data.PetContract

class CatalogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        val fab= findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener{
            val intent= Intent(this@CatalogActivity,EditorActivity::class.java)
            startActivity(intent)
        }
//        mDHelper= PetDbHelper(this)
//        displayDatabaseInfo()
        val petListView:ListView=findViewById(R.id.list)
        val emptyView:View=findViewById(R.id.empty_view)
        petListView.emptyView=emptyView
    }

    override fun onStart() {
        super.onStart()
        displayDatabaseInfo()
    }

    private fun displayDatabaseInfo(){
        val projection= arrayOf(BaseColumns._ID,PetContract.PetEntry.COLUMN_PET_NAME, PetContract.PetEntry.COLUMN_PET_BREED, PetContract.PetEntry.COLUMN_PET_GENDER,PetContract.PetEntry.COLUMN_PET_WEIGHT)
//        val cursor=db.query(PetContract.PetEntry.TABLE_NAME,projection,null,null,null,null,null)
        val cursor=contentResolver.query(PetContract.PetEntry.CONTENT_URI,projection,null,null,null)
        val displayView:ListView=findViewById(R.id.list)

        val adapter=PetCursorAdapter(this,cursor)
        displayView.adapter = adapter

        
    }
    private fun insertPet(){
        val values= ContentValues().apply {
            put(PetContract.PetEntry.COLUMN_PET_NAME,"Toto")
            put(PetContract.PetEntry.COLUMN_PET_BREED,"Terrier")
            put(PetContract.PetEntry.COLUMN_PET_GENDER,PetContract.PetEntry.GENDER_MALE)
            put(PetContract.PetEntry.COLUMN_PET_WEIGHT,7)
        }
//        val newUri: Uri? =contentResolver.insert(PetContract.PetEntry.CONTENT_URI,values)

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
        when(item.itemId) {
            R.id.action_insert_dummy_data -> {
                insertPet()

                return true
            }
            R.id.action_delete_all_entries -> return true
        }
        return super.onOptionsItemSelected(item)
    }
}