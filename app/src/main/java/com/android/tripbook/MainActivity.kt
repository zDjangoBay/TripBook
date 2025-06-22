
<<<<<<< HEAD
// Importations nécessaires
 import android.os.Bundle 
 import android.view.LayoutInflater 
 import android.view.View 
 import android.view.ViewGroup
 import android.widget.Button 
 import android.widget.EditText 
 import android.widget.RatingBar
 import android.widget.TextView 
 import androidx.appcompat.app.AppCompatActivity
 import androidx.recyclerview.widget.LinearLayoutManager 
 import androidx.recyclerview.widget.RecyclerView 
 import java.text.SimpleDateFormat
 import java.util.* 
=======
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tripbook.ui.theme.TripBookTheme
import com.tripbook.userprofilendedilan.UserProfileNdeDilanEntryPoint
>>>>>>> 4712c46cf27c3ef86b98da24e08e999274614675

 data class Comment(
    val id: String, 
    val text: String, 
    val imageUri: String?,
    val timestamp: String,
    val authorName: String, 
    val rating: Float 
 )

 class MainActivity : AppCompatActivity() {

   
    private lateinit var ratingBar: RatingBar 
    private lateinit var commentEditText: EditText 
    private lateinit var postButton: Button 
    private lateinit var commentsRecyclerView: RecyclerView 
    private lateinit var commentAdapter: CommentAdapter 
    
    private val commentsList = mutableListOf<Comment>()

   
    override fun onCreate(savedInstanceState: Bundle?) {
<<<<<<< HEAD
        super.onCreate(savedInstanceState) 
        setContentView(R.layout.activity_main)

        ratingBar = findViewById(R.id.ratingBar) 
        commentEditText = findViewById(R.id.commentEditText)
        postButton = findViewById(R.id.postButton)
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView) 

        commentAdapter = CommentAdapter(commentsList)
        commentsRecyclerView.adapter = commentAdapter 
        commentsRecyclerView.layoutManager = LinearLayoutManager(this) 
  
        postButton.setOnClickListener {
           
val commentText = commentEditText.text.toString()
            val rating = ratingBar.rating 

            
            if (commentText.isNotEmpty()) {
                
                addComment(commentText, rating) 
                commentEditText.text.clear() 
                ratingBar.rating = 0f
            }
=======
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UserProfileNdeDilanEntryPoint( )
>>>>>>> 4712c46cf27c3ef86b98da24e08e999274614675
        }
    }


    private fun addComment(commentText: String, rating: Float) {

        val newComment = Comment(
            id = UUID.randomUUID().toString(), 
            text = commentText, 
            imageUri = null, 
            timestamp = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(Date()), 
            authorName = "User", 
            rating = rating 
        )

        
        commentsList.add(newComment) 
        commentAdapter.notifyItemInserted(commentsList.size - 1) 

        // Faire défiler vers le bas pour afficher le nouveau commentaire
        commentsRecyclerView.scrollToPosition(commentsList.size - 1) // Faire défiler la RecyclerView jusqu'au dernier élément
    }
<<<<<<< HEAD
 }


 class CommentAdapter(private val comments: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    
    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        val authorNameTextView: TextView = itemView.findViewById(R.id.authorNameTextView) 
        val commentTextView: TextView = itemView.findViewById(R.id.commentTextView) 
        val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView) 
        val ratingTextView: TextView = itemView.findViewById(R.id.ratingTextView) 
    }

   
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
    
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_item, parent, false) 
        return CommentViewHolder(itemView) 
    }

    
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        
val comment = comments[position] 
        holder.authorNameTextView.text = comment.authorName 
        holder.commentTextView.text = comment.text 
        holder.timestampTextView.text = comment.timestamp 
        holder.ratingTextView.text = "Rating: ${comment.rating}" 
    }


    override fun getItemCount() = comments.size 
 }
=======
}
>>>>>>> 4712c46cf27c3ef86b98da24e08e999274614675
