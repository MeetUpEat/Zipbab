package com.bestapp.zipbab.data.model.remote

data class Post(
    val postDocumentID: String,
    val images: List<String>,
) {

    constructor() : this("", listOf())
}