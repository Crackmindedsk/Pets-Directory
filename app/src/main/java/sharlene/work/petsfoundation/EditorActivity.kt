package sharlene.work.petsfoundation

import android.content.ContentValues
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import sharlene.work.petsfoundation.data.PetContract.PetEntry

public class EditorActivity: AppCompatActivity() {
    private var mNameEditText: EditText?=null
    private var mBreedEditText: EditText?=null
    private var mWeightEditText: EditText?=null
    private var mGenderSpinner: Spinner?=null
    private var mGender=PetEntry.GENDER_UNKNOWN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        mNameEditText= findViewById(R.id.edit_pet_name)
        mBreedEditText= findViewById<EditText>(R.id.edit_pet_breed)
        mWeightEditText= findViewById<EditText>(R.id.edit_pet_weight)
        mGenderSpinner= findViewById<Spinner>(R.id.spinner_gender)

        setupSpinner()
    }
    private fun insertPet(){
        val nameString=mNameEditText!!.text.toString().trim()
        val breedName=mBreedEditText!!.text.toString().trim()
        val weightString=mWeightEditText!!.text.toString().trim()
        val weight=weightString.toInt()

        val values=ContentValues().apply {
            put(PetEntry.COLUMN_PET_NAME,nameString)
            put(PetEntry.COLUMN_PET_BREED,breedName)
            put(PetEntry.COLUMN_PET_GENDER,mGender)
            put(PetEntry.COLUMN_PET_WEIGHT,weight)
        }
        val newUri=contentResolver.insert(PetEntry.CONTENT_URI,values)

        if(newUri == null){
            Toast.makeText(this, "Error with saving pet", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Pet saved with row is: $newUri", Toast.LENGTH_SHORT).show()
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
                insertPet()
                finish()
                return true
            }
            R.id.action_delete->return true
            android.R.id.home->{
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}