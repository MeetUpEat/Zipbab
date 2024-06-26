package com.bestapp.zipbab.data.upload

import com.bestapp.zipbab.data.model.UploadStateEntity
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

class UploadStateEntityAdapter {

    @ToJson
    fun toJson(writer: JsonWriter, value: UploadStateEntity?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.beginObject()
        writer.name("type").value(value::class.java.simpleName)
        writer.name("data")

        when (value) {
            is UploadStateEntity.Pending -> {
                writer.beginObject()
                writer.name("tempPostDocumentID").value(value.tempPostDocumentID)
                writer.endObject()
            }
            is UploadStateEntity.ProcessImage -> {
                writer.beginObject()
                writer.name("tempPostDocumentID").value(value.tempPostDocumentID)
                writer.name("currentProgressOrder").value(value.currentProgressOrder)
                writer.name("maxOrder").value(value.maxOrder)
                writer.endObject()
            }
            is UploadStateEntity.ProcessPost -> {
                writer.beginObject()
                writer.name("tempPostDocumentID").value(value.tempPostDocumentID)
                writer.endObject()
            }
            is UploadStateEntity.Fail -> {
                writer.beginObject()
                writer.name("tempPostDocumentID").value(value.tempPostDocumentID)
                writer.endObject()
            }
            is UploadStateEntity.SuccessPost -> {
                writer.beginObject()
                writer.name("tempPostDocumentID").value(value.tempPostDocumentID)
                writer.name("postDocumentID").value(value.postDocumentID)
                writer.endObject()
            }
        }
        writer.endObject()
    }

    @FromJson
    fun fromJson(reader: JsonReader): UploadStateEntity? {
        var type: String? = null
        var tempPostDocumentID: String? = null
        var currentProgressOrder: Int? = null
        var maxOrder: Int? = null
        var postDocumentID: String? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "type" -> type = reader.nextString()
                "data" -> {
                    reader.beginObject()
                    while (reader.hasNext()) {
                        when (reader.nextName()) {
                            "tempPostDocumentID" -> tempPostDocumentID = reader.nextString()
                            "currentProgressOrder" -> currentProgressOrder = reader.nextInt()
                            "maxOrder" -> maxOrder = reader.nextInt()
                            "postDocumentID" -> postDocumentID = reader.nextString()
                            else -> reader.skipValue()
                        }
                    }
                    reader.endObject()
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return when (type) {
            UploadStateEntity.Pending::class.java.simpleName -> tempPostDocumentID?.let { UploadStateEntity.Pending(it) }
            UploadStateEntity.ProcessImage::class.java.simpleName -> {
                if (tempPostDocumentID != null && currentProgressOrder != null && maxOrder != null) {
                    UploadStateEntity.ProcessImage(tempPostDocumentID, currentProgressOrder, maxOrder)
                } else null
            }
            UploadStateEntity.ProcessPost::class.java.simpleName -> tempPostDocumentID?.let { UploadStateEntity.ProcessPost(it) }
            UploadStateEntity.Fail::class.java.simpleName -> tempPostDocumentID?.let { UploadStateEntity.Fail(it) }
            UploadStateEntity.SuccessPost::class.java.simpleName -> {
                if (tempPostDocumentID != null && postDocumentID != null) {
                    UploadStateEntity.SuccessPost(tempPostDocumentID, postDocumentID)
                } else null
            }
            else -> null
        }
    }
}