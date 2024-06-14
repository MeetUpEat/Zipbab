package com.bestapp.zipbab.data.model.remote

data class ReportForm(
    val userDocumentID: String = "",
    val postDocumentID: String = "",
) {
    constructor() : this("", "")
}
