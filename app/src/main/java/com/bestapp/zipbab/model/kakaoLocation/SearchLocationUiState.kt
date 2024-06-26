package com.bestapp.zipbab.model.kakaoLocation

import com.bestapp.zipbab.data.model.remote.kakaomap.SearchLocationResponse

data class SearchLocationUiState(
    val documentUiState: List<DocumentUiState> = emptyList(),
    val metaUiState: MetaUiState = MetaUiState(),
)

fun SearchLocationResponse.toUiState() = SearchLocationUiState(
    documentUiState = documentResponses?.map { it.toUiState() } ?: emptyList(),
    metaUiState = metaResponse?.toUiState() ?: MetaUiState()
)