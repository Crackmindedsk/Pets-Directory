package sharlene.work.petsfoundation

import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.content.*
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.google.android.material.floatingactionbutton.FloatingActionButton
import sharlene.work.petsfoundation.data.PetContract

class CatalogActivity : AppCompatActivity(),
    LoaderManager.LoaderCallbacks<Cursor> {

    var mCursorAdapter:PetCursorAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener{
            val intent= Intent(this@CatalogActivity,EditorActivity::class.java)
            startActivity(intent)
        }
//        mDHelper= PetDbHelper(this)
//        displayDatabaseInfo()
        val petListView:ListView=findViewById(R.id.list)
        val emptyView:View=findViewById(R.id.empty_view)
        petListView.emptyView=emptyView
        mCursorAdapter= PetCursorAdapter(this,null)
        petListView.adapter=mCursorAdapter
        petListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            val intent=Intent(this@CatalogActivity,EditorActivity::class.java)
            val currentPetUri=ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI,id)
            intent.data=currentPetUri
            startActivity(intent)
        }
        loaderManager.initLoader(PET_LOADER_,null,this)

    }

    private fun insertPet(){
        val values= ContentValues().apply {
            put(PetContract.PetEntry.COLUMN_PET_NAME,"Toto")
            put(PetContract.PetEntry.COLUMN_PET_BREED,"Terrier")
            put(PetContract.PetEntry.COLUMN_PET_GENDER,PetContract.PetEntry.GENDER_MALE)
            put(PetContract.PetEntry.COLUMN_PET_WEIGHT,7)
        }
        val newUri: Uri? =contentResolver.insert(PetContract.PetEntry.CONTENT_URI,values)

    }

    private fun deleteAllPets(){
        val rowsDeleted=contentResolver.delete(PetContract.PetEntry.CONTENT_URI,null,null)
        Log.v("CatalogActivity","$rowsDeleted rows deleted from pet database")
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
            R.id.action_delete_all_entries -> {
                deleteAllPets()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val projection= arrayOf(PetContract.PetEntry._ID,PetContract.PetEntry.COLUMN_PET_NAME,PetContract.PetEntry.COLUMN_PET_BREED)
        return CursorLoader(this,PetContract.PetEntry.CONTENT_URI,projection,null,null,null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        mCursorAdapter!!.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mCursorAdapter!!.swapCursor(null)
    }

    companion object {
        private const val PET_LOADER_=0
    }

}

