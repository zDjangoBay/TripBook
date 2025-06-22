package com.android.tripbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tohpoh.R
import com.example.tohpoh.models.Post

class PostAdapter(
    private val postList: List<Post>,
    private val onLikeClick: (Int) -> Unit,
    private val onCommentClick: (Int) -> Unit,
    private val onShareClick: (Int) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.profileImage)
        val username: TextView = view.findViewById(R.id.username)
        val postContent: TextView = view.findViewById(R.id.postContent)
        val postImage: ImageView = view.findViewById(R.id.postImage)
        val likeButton: Button = view.findViewById(R.id.likeButton)
        val commentButton: Button = view.findViewById(R.id.commentButton)
        val shareButton: Button = view.findViewById(R.id.shareButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.username.text = post.user?.username
        holder.postContent.text = post.content

        // Gestion des images (si elles existent)
        if (post.imageUrl != null) {
            holder.postImage.visibility = View.VISIBLE
        } else {
            holder.postImage.visibility = View.GONE
        }

        // Actions des boutons : on passe uniquement l'ID du post
        holder.likeButton.setOnClickListener { onLikeClick(post.id) }
        holder.commentButton.setOnClickListener { onCommentClick(post.id) }
        holder.shareButton.setOnClickListener { onShareClick(post.id) }
    }

    override fun getItemCount() = postList.size
}
