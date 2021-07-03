package sharlene.work.petsfoundation.data

import android.net.Uri
import android.provider.BaseColumns


object PetContract {
    public const val CONTENT_AUTHORITY="sharlene.work.petsfoundation"
    public val BASE_CONTENT_URI:Uri= Uri.parse("content://$CONTENT_AUTHORITY")
    public const val PATH_PETS="pets"

    object PetEntry:BaseColumns{
        public val CONTENT_URI:Uri= Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS)
        const val TABLE_NAME="pets"
        const val _ID=BaseColumns._ID
        const val COLUMN_PET_NAME="name"
        const val COLUMN_PET_BREED="breed"
        const val COLUMN_PET_GENDER="gender"
        const val COLUMN_PET_WEIGHT="weight"

        const val GENDER_UNKNOWN=0
        const val GENDER_MALE=1
        const val GENDER_FEMALE=2

        public fun isValidGender(gender:Int):Boolean{
            if(gender== GENDER_UNKNOWN || gender== GENDER_FEMALE||gender== GENDER_MALE)
            {   return true}
            return false
        }
    }
}