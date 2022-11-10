package com.andreirookie.nmediabackendstudyproject.entity


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andreirookie.nmediabackendstudyproject.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val authorAvatar: String,
    @Embedded
    val attachment: AttachmentEmbeddable?
) {
    fun toDto() = Post(id, author, content, published, likedByMe, likes, authorAvatar, attachment?.toDto())

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id,
                dto.author,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                dto.authorAvatar,
                AttachmentEmbeddable.fromDto(dto.attachment))
    }
}
