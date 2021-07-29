package sharlene.work.petsfoundation

import android.content.Context
import android.database.Cursor
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import sharlene.work.petsfoundation.data.PetContract

class PetCursorAdapter(context: Context?, c: Cursor?) : CursorAdapter(context,c,0) {
    override fun newView(context: Context?, c: Cursor?, parent: ViewGroup?): View? {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false)
    }

    override fun bindView(view: View, context: Context, c: Cursor) {
        val title:TextView= view.findViewById(R.id.name)!!
        val breedTitle:TextView= view.findViewById(R.id.summary)

        val nameColumnIndex= c.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME)
        val breedColumnIndex= c.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED)

        val petName= c.getString(nameColumnIndex)
        var breedName= c.getString(breedColumnIndex)

        if(TextUtils.isEmpty(breedName)){
            breedName=context.getString(R.string.unknown_breed)
        }

        title.text = petName
        breedTitle.text = breedName
    }
}


