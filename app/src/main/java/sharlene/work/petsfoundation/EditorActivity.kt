package sharlene.work. petsfoundation

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import sharlene.work.petsfoundation.data.PetContract.PetEntry

public class EditorActivity: AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor?>{
    private var mNameEditText: EditText?=null
    private var mBreedEditText: EditText?=null
    private var mWeightEditText: EditText?=null
    private var mGenderSpinner: Spinner?=null
    private var mGender=PetEntry.GENDER_UNKNOWN
    private var mPetHasChanged:Boolean = false
    private var mCurrentPetUri:Uri ?=null

    private val mTouchListener:View.OnTouchListener= View.OnTouchListener { view, motionEvent ->
        mPetHasChanged=true
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val intent:Intent=getIntent()
        mCurrentPetUri=intent.data

        if(mCurrentPetUri==null){
            title =getString(R.string.editor_activity_title_new_pet)

            invalidateOptionsMenu()
        }else{
            title=getString(R.string.editor_activity_title_edit_pet)
            loaderManager.initLoader(EXISTING_PET_LOADER,null,this)
        }

        mNameEditText= findViewById(R.id.edit_pet_name)
        mBreedEditText= findViewById<EditText>(R.id.edit_pet_breed)
        mWeightEditText= findViewById<EditText>(R.id.edit_pet_weight)
        mGenderSpinner= findViewById<Spinner>(R.id.spinner_gender)

        mNameEditText!!.setOnTouchListener(mTouchListener)
        mBreedEditText!!.setOnTouchListener(mTouchListener)
        mWeightEditText!!.setOnTouchListener(mTouchListener)
        mGenderSpinner!!.setOnTouchListener(mTouchListener)
        setupSpinner()

    }
    private fun savePet(){
        val nameString=mNameEditText!!.text.toString().trim()
        val breedName=mBreedEditText!!.text.toString().trim()
        val weightString=mWeightEditText!!.text.toString().trim()

        if(mCurrentPetUri==null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(breedName) && TextUtils.isEmpty(weightString) && mGender==PetEntry.GENDER_UNKNOWN){
            return
        }

        val values=ContentValues()
        values.put(PetEntry.COLUMN_PET_NAME,nameString)
        values.put(PetEntry.COLUMN_PET_BREED,breedName)
        values.put(PetEntry.COLUMN_PET_GENDER,mGender)
        var weight=0
        if (TextUtils.isEmpty(weightString)) {
                weight = weightString.toInt()
        }
        values.put(PetEntry.COLUMN_PET_WEIGHT,weight)

        if (mCurrentPetUri==null){
            val newUri=contentResolver.insert(PetEntry.CONTENT_URI,values)

            if(newUri == null){
                Toast.makeText(this, "Error with saving pet", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Pet saved with row is: $newUri", Toast.LENGTH_SHORT).show()
            }

        }else{
            val rowsAffected:Int=contentResolver.update(mCurrentPetUri,values,null,null)
            if(rowsAffected==0){
                Toast.makeText(this,getString(R.string.editor_update_pet_failed),Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,getString(R.string.editor_update_pet_successful),Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun setupSpinner() {
        val genderSpinnerAdapter =ArrayAdapter.createFromResource(this,R.array.array_gender_option,android.R.layout.simple_spinner_item)
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        mGenderSpinner!!.adapter=genderSpinnerAdapter
        mGenderSpinner!!.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view : View, position: Int, id: Long) {
                val selection= parent.getItemAtPosition(position) as String
                if(!TextUtils.isEmpty(selection)){
                    mGender= when (selection) {
                        getString(R.string.gender_male) -> PetEntry.GENDER_MALE
                        getString(R.string.gender_female) -> PetEntry.GENDER_FEMALE
                        else -> PetEntry.GENDER_UNKNOWN
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                mGender=PetEntry.GENDER_UNKNOWN
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editor,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_save->{
                savePet()
                finish()
                return true
            }
            R.id.action_delete->{
                showDeleteConfirmationDialog()
                return true
            }
            android.R.id.home->{
                if(!mPetHasChanged){
                    NavUtils.navigateUpFromSameTask(this)
                    return true
                }
                val discardButtonClickListener=DialogInterface.OnClickListener { dialogInterface, i ->
                    NavUtils.navigateUpFromSameTask(this)
                }
                showUnsavedChangesDialog(discardButtonClickListener )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val EXISTING_PET_LOADER=0
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        if (mCurrentPetUri==null){
            val menuItem= menu?.findItem(R.id.action_delete)
            menuItem.setVisible(false)
        }
        return true
    }

    override fun onCreateLoader(id: Int, bundle: Bundle?): Loader<Cursor?> {
        val projection= arrayOf(
            PetEntry._ID,
            PetEntry.COLUMN_PET_NAME,
            PetEntry.COLUMN_PET_BREED,
            PetEntry.COLUMN_PET_GENDER,
            PetEntry.COLUMN_PET_WEIGHT
        )
        return CursorLoader(this, mCurrentPetUri!!,projection,null,null,null)
    }

    override fun onLoadFinished(loader: Loader<Cursor?>, cursor: Cursor?) {
        if(cursor!!.moveToFirst()){
            val nameColumnIndex= cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME)
            val breedColumnIndex= cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED)
            val genderColumnIndex= cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER)
            val weightColumnIndex= cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT)

            val name= cursor.getString(nameColumnIndex)
            val breed= cursor.getString(breedColumnIndex)
            val gender= cursor.getInt(genderColumnIndex)
            val weight= cursor.getInt(weightColumnIndex)

            mNameEditText!!.setText(name)
            mBreedEditText!!.setText(breed)
            mWeightEditText!!.setText(Integer.toString(weight))
            when(gender){
                PetEntry.GENDER_MALE-> mGenderSpinner!!.setSelection(1)
                PetEntry.GENDER_FEMALE-> mGenderSpinner!!.setSelection(2)
                else-> mGenderSpinner!!.setSelection(0)
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor?>) {
        TODO("Not yet implemented")
    }
    private fun showUnsavedChangesDialog(discardButtonClickListener:DialogInterface.OnClickListener){
        val builder:AlertDialog.Builder=AlertDialog.Builder(this)
        builder.setMessage(R.string.unsaved_changes_dialog_msg)
        builder.setPositiveButton(R.string.discard,discardButtonClickListener)
        builder.setNegativeButton(R.string.keep_editing,DialogInterface.OnClickListener(){dialog, id->
            dialog?.dismiss()
        })
        val alertDialog=builder.create()
        alertDialog.show()
    }

    override fun onBackPressed() {
        if (!mPetHasChanged){
            super.onBackPressed()
            return
        }

        val discardButtonClickListener:DialogInterface.OnClickListener=DialogInterface.OnClickListener { dialog, id ->
            finish()
        }
        showUnsavedChangesDialog(discardButtonClickListener)
    }

    private fun showDeleteConfirmationDialog(){
        val builder=AlertDialog.Builder(this)
        builder.setMessage(R.string.delete_dialog_msg)
        builder.setPositiveButton(R.string.delete,DialogInterface.OnClickListener { dialogInterface, i ->
            deletePet()
        })
        builder.setNegativeButton(R.string.cancel,DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface?.dismiss()
        })
        val alertDialog=builder.create()
        alertDialog.show()
    }
    private fun deletePet(){
        if (mCurrentPetUri!=null){
            val rowsDeleted=contentResolver.delete(mCurrentPetUri!!,null,null)
            if(rowsDeleted==0){
                Toast.makeText(this,getString(R.string.editor_delete_pet_failed),Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(
                    this,
                    getString(R.string.editor_delte_pet_successful),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        finish()
    }


}