package sharlene.work.petsfoundation.data

import android.provider.BaseColumns

object PetContract {
    object PetEntry:BaseColumns{
        const val TABLE_NAME="pets"
        const val _ID=BaseColumns._ID
        const val COLUMN_PET_NAME="name"
        const val COLUMN_PET_BREED="breed"
        const val COLUMN_PET_GENDER="gender"
        const val COLUMN_PET_WEIGHT="weight"

        const val GENDER_UNKNOWN=0
        const val GENDER_MALE=1
        const val GENDER_FEMALE=2
    }
}