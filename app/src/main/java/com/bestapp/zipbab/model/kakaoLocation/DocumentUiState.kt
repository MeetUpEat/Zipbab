package com.bestapp.zipbab.model.kakaoLocation

import com.bestapp.zipbab.data.model.remote.kakaomap.DocumentResponse

data class DocumentUiState(
    val address: AddressUiState = AddressUiState(),
    val longitude: String = "",
    val latitude: String = "",
    val addressName: String = "",
    val addressType: String = "",
    val roadAddressResponse: RoadAddressUiState = RoadAddressUiState(),
)

fun DocumentResponse.toUiState() = DocumentUiState(
    address = addressResponse?.toUiState() ?: AddressUiState(),
    longitude = longitude.orEmpty(),
    latitude = latitude.orEmpty(),
    addressName = addressName.orEmpty(),
    addressType = addressType.orEmpty(),
    roadAddressResponse = roadAddressResponse?.toUiState() ?: RoadAddressUiState()
)