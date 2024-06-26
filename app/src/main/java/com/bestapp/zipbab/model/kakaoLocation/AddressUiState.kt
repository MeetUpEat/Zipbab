package com.bestapp.zipbab.model.kakaoLocation

import com.bestapp.zipbab.data.model.remote.kakaomap.AddressResponse

data class AddressUiState(
    val addressName: String = "",
    val longitude: String = "",
    val latitude: String = "",
    val bCode: String = "",
    val hCode: String = "",
    val mainAddressNo: String = "",
    val subAddressNo: String = "",
    val mountainYn: String = "",
    val regionDepthName1: String = "",
    val regionDepthName2: String = "",
    val regionDepthName3h: String = "",
    val regionDepthName3: String = "",
)

fun AddressResponse.toUiState() = AddressUiState(
    addressName = addressName.orEmpty(),
    longitude = longitude.orEmpty(),
    latitude = latitude.orEmpty(),
    bCode = bCode.orEmpty(),
    hCode = hCode.orEmpty(),
    mainAddressNo = mainAddressNo.orEmpty(),
    subAddressNo = subAddressNo.orEmpty(),
    mountainYn = mountainYn.orEmpty(),
    regionDepthName1 = regionDepthName1.orEmpty(),
    regionDepthName2 = regionDepthName2.orEmpty(),
    regionDepthName3h = regionDepthName3h.orEmpty(),
    regionDepthName3 = regionDepthName3.orEmpty()
)