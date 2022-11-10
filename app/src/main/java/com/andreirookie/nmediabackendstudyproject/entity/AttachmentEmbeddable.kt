package com.andreirookie.nmediabackendstudyproject.entity

import com.andreirookie.nmediabackendstudyproject.dto.Attachment
import com.andreirookie.nmediabackendstudyproject.dto.AttachmentType

data class AttachmentEmbeddable(
    val url: String,
    val description: String?,
    val type: AttachmentType
) {
    fun toDto() = Attachment(url, description, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.description, it.type)
        }
    }
}