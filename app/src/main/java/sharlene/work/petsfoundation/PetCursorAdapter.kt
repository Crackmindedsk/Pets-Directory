package sharlene.work.petsfoundation

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import sharlene.work.petsfoundation.data.PetContract

class PetCursorAdapter(context: Context?, c: Cursor?) : CursorAdapter(context, c) {
    override fun newView(context: Context?, c: Cursor?, parent: ViewGroup?): View? {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false)
    }

    override fun bindView(view: View?, context: Context?, c: Cursor?) {
        val title:TextView= view?.findViewById(R.id.name)!!
        val breedtitle:TextView= view.findViewById(R.id.summary)

        val nameColumnIndex= c?.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME)
        val breedColumnIndex= c?.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED)

        val petName= c?.getString(nameColumnIndex!!)
        val breedName= c?.getString(breedColumnIndex!!)

        title.setText(petName)
        breedtitle.setText(breedName)
    }
}


