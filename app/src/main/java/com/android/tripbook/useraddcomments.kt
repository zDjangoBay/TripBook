 // importations nécessaires
 import android.os.Bundle
 import android.widget.Button
 import android.widget.EditText
 import androidx.appcompat.app.AppCompatActivity
 import androidx.recyclerview.widget.LinearLayoutManager
 import androidx.recyclerview.widget.RecyclerView
 import java.text.SimpleDateFormat
 import java.util.*

 data class Comment(
    val id: String,
    val text: String,
    val imageUri: String?,
    val timestamp: String,
    val authorName: String
 )

 class MainActivity : AppCompatActivity() {

    private lateinit var commentEditText: EditText
    private lateinit var postButton: Button
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentAdapter: CommentAdapter // créer cet adaptateur

    private val commentsList = mutableListOf<Comment>() 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // avoir le layout activity_main.xml

        commentEditText = findViewById(R.id.commentEditText)
        postButton = findViewById(R.id.postButton)
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView)

        // Config RecyclerView
        commentAdapter = CommentAdapter(commentsList)
        commentsRecyclerView.adapter = commentAdapter
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)

        postButton.setOnClickListener {
            val commentText = commentEditText.text.toString()
            if (commentText.isNotEmpty()) {
                addComment(commentText)
                commentEditText.text.clear() // Effacer le champ de texte après la publication
            }
        }
    }

    private fun addComment(commentText: String) {
        val newComment = Comment(
            id = UUID.randomUUID().toString(), // Générer un ID unique
            text = commentText,
            imageUri = null, 
            timestamp = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(Date()),
            authorName = "User" 
        )
        commentsList.add(newComment)
        commentAdapter.notifyItemInserted(commentsList.size - 1) // Notifier l'adaptateur
        commentsRecyclerView.scrollToPosition(commentsList.size - 1) // Faire défiler vers le nouveau commentaire
    }
}
