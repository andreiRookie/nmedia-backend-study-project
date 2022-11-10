package com.andreirookie.nmediabackendstudyproject.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val authorAvatar: String = "",
    val attachment: Attachment? = null


)

data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType
)

enum class AttachmentType {
    IMAGE
}