package sharlene.work.petsfoundation

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

public class EditorActivity: AppCompatActivity() {
    private val mNameEditText: EditText
    private var mGender=0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

    }
}