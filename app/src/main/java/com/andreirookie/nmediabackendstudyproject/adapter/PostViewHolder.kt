package com.andreirookie.nmediabackendstudyproject.adapter

import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.andreirookie.nmediabackendstudyproject.R
import com.andreirookie.nmediabackendstudyproject.databinding.CardPostBinding
import com.andreirookie.nmediabackendstudyproject.dto.Attachment
import com.andreirookie.nmediabackendstudyproject.dto.AttachmentType
import com.andreirookie.nmediabackendstudyproject.dto.Post
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            // в адаптере
            like.isChecked = post.likedByMe
            like.text = "${post.likes}"

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            // avatars
            val avatarsUrl = "http://10.0.2.2:9999/avatars/"

            val rounding = RoundedCorners(100)

            Glide.with(avatar)
                .load("$avatarsUrl${post.authorAvatar}")
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_error_100dp)
                .transform(rounding)
                .timeout(10_000)
                .into(avatar)

            // attachments
            val attachmentImagesUrl = "http://10.0.2.2:9999/images/"

            Glide.with(attachmentImage)
                .load("$attachmentImagesUrl${post.attachment?.url}")
                .timeout(10_000)
                .into(attachmentImage)

            attachmentDescription.text = post.attachment?.description
        }
    }
}