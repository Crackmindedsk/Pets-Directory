package sharlene.work.petsfoundation

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils

public class EditorActivity: AppCompatActivity() {
    private var mNameEditText: EditText?=null
    private var mBreedEditText: EditText?=null
    private var mWeightEditText: EditText?=null
    private var mGenderSpinner: Spinner?=null
    private var mGender=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        mNameEditText= findViewById(R.id.edit_pet_name) as EditText
        mBreedEditText= findViewById(R.id.edit_pet_breed) as EditText
        mWeightEditText= findViewById(R.id.edit_pet_weight) as EditText
        mGenderSpinner=findViewById(R.id.spinner_gender) as Spinner

        setupSpinner()
    }

    private fun setupSpinner() {
        val genderSpinnerAdapter =ArrayAdapter.createFromResource(this,R.array.array_gender_option,android.R.layout.simple_spinner_item)
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        mGenderSpinner!!.adapter=genderSpinnerAdapter
        mGenderSpinner!!.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view : View, position: Int, id: Long) {
                val selection= parent.getItemAtPosition(position) as String
                if(!TextUtils.isEmpty(selection)){
                    mGender=if (selection==getString(R.string.gender_male))
                        1
                    else if(selection==getString(R.string.gender_female))
                        2
                    else
                        0
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                mGender=0
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editor,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return  when(item.itemId){
            R.id.action_save->true
            R.id.action_delete->true
            android.R.id.home->{
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
            else->super.onOptionsItemSelected(item)
        }
    }

}