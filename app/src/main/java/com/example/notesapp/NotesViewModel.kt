import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.notesapp.Note

class NotesViewModel : ViewModel() {
    private val _textInput = MutableStateFlow("")
    val textInput: StateFlow<String> = _textInput

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    fun onTextChanged(newText: String) {
        _textInput.value = newText
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNote() {
        if (_textInput.value.isNotBlank()) {
            val newNote = Note(
                content = _textInput.value,
                category = "cat1",
                isSecret = false
            )
            _notes.value = listOf(newNote) + _notes.value // Add new note to the top
            _textInput.value = "" // Clear input field
        }
    }
}
